package br.com.evolufisico.nativo

import android.Manifest
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.readLimited
import br.com.evolufisico.nativo.data.LegacyImporter
import br.com.evolufisico.nativo.system.NativeBackupWorker
import br.com.evolufisico.nativo.system.AppointmentNotifications
import br.com.evolufisico.nativo.system.Reports
import br.com.evolufisico.nativo.ui.NativeApp
import java.time.LocalDate
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var repo: AppRepository
    private var message by mutableStateOf("")
    private var dataBusy by mutableStateOf(false)
    private var pdf: ByteArray? = null
    private var csvBytes:ByteArray?=null
    private var restorePassword: CharArray? = null
    private var pendingBackup: ByteArray? = null
    private var legacyPassword: CharArray? = null

    private val permissionReminders = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
        if(granted)enableReminders() else message="Permissão negada; os lembretes permanecerão desligados."
    }
    private fun enableReminders(){
        if(!AppointmentNotifications.permitted(this)) {
            message="Notificações bloqueadas no Android. Libere o aplicativo e o canal de agenda nas configurações do sistema."
            return
        }
        AppointmentNotifications.configure(this,true)
        AppointmentNotifications.channel(this)
        repo.appointments.forEach{AppointmentNotifications.schedule(this,it)}
        val lead=AppointmentNotifications.leadMinutes(this)
        message="Lembretes de agenda ativados (aproximadamente ${if(lead==1440)"24 horas" else if(lead==60)"1 hora" else "$lead min"} antes)."
    }
    private val csvPicker=registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){uri->
        val bytes=csvBytes;csvBytes=null
        saveExport(uri,bytes,"CSV")
    }
    private val pdfPicker = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val bytes=pdf;pdf=null
        saveExport(uri,bytes,"PDF")
    }
    private fun saveExport(uri:Uri?,bytes:ByteArray?,label:String) {
        if(uri==null){bytes?.fill(0);return}
        if(bytes==null){message="Documento indisponível. Gere o arquivo novamente.";return}
        if(dataBusy){bytes.fill(0);message="Aguarde e gere o documento novamente.";return}
        runDataOperation("Falha ao salvar $label") {
            try {
                withContext(Dispatchers.IO) {
                    try {
                        contentResolver.openOutputStream(uri)?.use{it.write(bytes);it.flush()} ?: error("Destino indisponível")
                    } catch(e:Exception){runCatching{contentResolver.delete(uri,null,null)};throw e}
                }
                message="$label salvo com sucesso."
            } finally {bytes.fill(0)}
        }
    }
    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            getPreferences(MODE_PRIVATE).edit().putString("backup-folder", uri.toString()).apply()
            getSharedPreferences("native-backup", MODE_PRIVATE).edit().putString("folder", uri.toString()).apply()
            message = "Pasta de backup selecionada."
        } catch (e: Exception) { message = "Não foi possível acessar pasta: ${e.localizedMessage}" }
    }
    private val organizationPicker=registerForActivityResult(ActivityResultContracts.OpenDocument()){uri->
        if(uri!=null && !dataBusy)runDataOperation("Importação da organização não efetuada") {
            var bytes:ByteArray?=null
            try {
                val profiles=repo.evaluations.toList()
                val count=withContext(Dispatchers.IO){bytes=contentResolver.openInputStream(uri)?.use{it.readLimited(3_000_000)} ?: error("Arquivo inacessível");repo.store.importOrganizationJson(requireNotNull(bytes),profiles)}
                showRestoreResult("$count registros de organização importados. Dados existentes preservados.")
            } finally {bytes?.fill(0)}
        }
    }
    private val openBackup = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val password=restorePassword;restorePassword=null
        if(uri==null || password==null || dataBusy) {
            password?.fill('\u0000')
            if(uri!=null)message="Restauração indisponível. Tente selecionar o arquivo novamente."
        } else runDataOperation("Restauração não efetuada") {
            var content:ByteArray?=null
            try {
                val count=withContext(Dispatchers.IO) {
                    content=contentResolver.openInputStream(uri)?.use{it.readLimited(24_000_000)} ?: error("Arquivo inacessível")
                    repo.store.importEncrypted(requireNotNull(content),password)
                }
                showRestoreResult("$count registros recuperados.")
            } finally { content?.fill(0);password.fill('\u0000') }
        }
    }
    private val legacyPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val password=legacyPassword;legacyPassword=null
        if(uri==null || dataBusy)password?.fill('\u0000')
        else runDataOperation("Importação legado cancelada") {
            var raw:ByteArray?=null
            try {
                val result=withContext(Dispatchers.IO) {
                    raw=contentResolver.openInputStream(uri)?.use{it.readLimited(24_000_000)} ?: error("Arquivo indisponível")
                    val stage=LegacyImporter.parse(requireNotNull(raw),password)
                    require(stage.total>0){"Backup sem registros"}
                    val count=repo.store.importLegacy(stage)
                    count to stage.version
                }
                showRestoreResult("Importação legado concluída: ${result.first} registros (${result.second}). Cópia original preservada.")
            } finally { raw?.fill(0);password?.fill('\u0000') }
        }
    }
    private val deviceBackupPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri!=null && !dataBusy)runDataOperation("Falha na restauração automática") {
            var bytes:ByteArray?=null
            try {
                val count=withContext(Dispatchers.IO) {
                    bytes=contentResolver.openInputStream(uri)?.use{it.readLimited(24_000_000)} ?: error("Arquivo inválido")
                    repo.store.importDeviceEncrypted(requireNotNull(bytes))
                }
                showRestoreResult("$count registros do backup automático restaurados.")
            } finally { bytes?.fill(0) }
        }
    }
    private val createBackup = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        val bytes=pendingBackup;pendingBackup=null
        if(uri==null)bytes?.fill(0)
        else if(bytes==null)message="Backup não disponível. Gere o arquivo novamente."
        else if(dataBusy) { bytes.fill(0);message="Aguarde e gere o backup novamente." }
        else runDataOperation("Falha ao salvar backup") {
            try {
                withContext(Dispatchers.IO) {
                    try {
                        contentResolver.openOutputStream(uri)?.use{it.write(bytes);it.flush()} ?: error("Destino indisponível")
                    } catch(e:Exception) { runCatching{contentResolver.delete(uri,null,null)};throw e }
                }
                message="Backup criptografado exportado. Guarde a senha em local seguro."
            } finally { bytes.fill(0) }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repo = AppRepository(applicationContext)
        setContent {
            NativeApp(
                repository = repo, dataBusy=dataBusy, message = message, onDismissMessage = { message = "" },
                onCreateEvaluationPdf = { e -> createPdf("Avaliacao_${e.date}.pdf") { Reports.evaluation(e) } },
                onCreateEvolutionPdf = { name, list -> createPdf("Evolucao_${LocalDate.now()}.pdf") { Reports.evolution(name, list) } },
                onExportCsv={name,text->
                    runCatching{
                        require(text.length<=8_000_000){"CSV muito grande. Reduza o período."}
                        csvBytes?.fill(0);csvBytes=text.toByteArray(Charsets.UTF_8)
                        csvPicker.launch(name)
                    }.onFailure{csvBytes?.fill(0);csvBytes=null;message=it.message ?: "Falha ao exportar CSV."}
                },
                onMonthlyBillingPdf = { rows,period ->
                    val professional=getSharedPreferences("professional",MODE_PRIVATE).let{listOf(it.getString("name","").orEmpty(),it.getString("cref","").orEmpty(),it.getString("workplace","").orEmpty()).filter{v->v.isNotBlank()}.joinToString(" · ")}
                    createPdf("Caixa_${period.ifBlank{"completo"}}.pdf"){Reports.monthlyBilling(rows,period,professional)}
                },
                onCreateBillingPdf = { createPdf("Cobrancas_${LocalDate.now()}.pdf") { Reports.billing(repo.charges) } },
                onIntakePdf = { item -> createPdf("Anamnese_${item.date}.pdf") { Reports.intake(item) } },
                onAnalysisPdf = { name,dates,text -> createPdf("Analises_${LocalDate.now()}.pdf") { Reports.analysis(name,dates,text) } },
                onImportOrganizationJson = { organizationPicker.launch(arrayOf("application/json","text/plain","*/*")) },
                onOrganizationBackup = { password -> prepareBackup(password,organizationOnly=true) },
                onIndividualBackup = { id,password -> prepareBackup(password,id) },
                onChooseFolder = { folderPicker.launch(null) },
                onBackup = { password -> prepareBackup(password) },
                onImportLegacy = { password ->
                    legacyPassword?.fill('\u0000');legacyPassword=password.toCharArray()
                    try { legacyPicker.launch(arrayOf("application/json","application/octet-stream","*/*")) }
                    catch(e:Exception) { legacyPassword?.fill('\u0000');legacyPassword=null;message="Não foi possível abrir o seletor: ${e.localizedMessage}" }
                },
                onRestoreDevice = { deviceBackupPicker.launch(arrayOf("application/octet-stream","*/*")) },
                onScheduleBackup = { time ->
                    try {
                        val fields=time.split(":");require(fields.size==2)
                        val folder=getSharedPreferences("native-backup",MODE_PRIVATE).getString("folder",null)
                        require(folder != null) { "Escolha primeiro uma pasta de backup." }
                        NativeBackupWorker.schedule(this,fields[0].toInt(),fields[1].toInt())
                        message="Backup diário configurado (horário aproximado, sujeito ao Android)."
                    } catch(e:Exception) { message="Horário/pasta inválidos: ${e.localizedMessage}" }
                },
                onDisableBackup = { NativeBackupWorker.disable(this);message="Backup automático desativado." },
                onEnableReminders = {
                    if(Build.VERSION.SDK_INT>=33 && !AppointmentNotifications.permitted(this))
                        permissionReminders.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else enableReminders()
                },
                onDisableReminders = {
                    AppointmentNotifications.configure(this,false)
                    repo.appointments.forEach{AppointmentNotifications.cancel(this,it.id)}
                    message="Lembretes desativados."
                },
                onRestore = { password ->
                    restorePassword?.fill('\u0000');restorePassword=password.toCharArray()
                    try { openBackup.launch(arrayOf("application/octet-stream", "application/json", "*/*")) }
                    catch(e:Exception) { restorePassword?.fill('\u0000');restorePassword=null;message="Não foi possível abrir o seletor: ${e.localizedMessage}" }
                }
            )
        }
    }
    private suspend fun showRestoreResult(success:String) {
        try {
            withContext(Dispatchers.IO) { repo.refresh() }
        } catch(e:CancellationException) { throw e }
        catch(e:Exception) {
            message="$success Os dados foram gravados, mas a tela não pôde ser atualizada: ${e.localizedMessage}. Reabra o aplicativo."
            return
        }
        repo.rescheduleReminders()
        message=success
    }
    private fun runDataOperation(label:String,operation:suspend ()->Unit) {
        if(dataBusy) { message="Aguarde a operação de dados em andamento.";return }
        dataBusy=true
        lifecycleScope.launch {
            try { operation() }
            catch(e:CancellationException) { throw e }
            catch(e:Exception) { message="$label: ${e.localizedMessage ?: "erro desconhecido"}" }
            finally { dataBusy=false }
        }
    }
    private fun prepareBackup(password:String,clientId:String?=null,organizationOnly:Boolean=false) {
        runDataOperation("Não foi possível preparar o backup") {
            val secret=password.toCharArray()
            var bytes:ByteArray?=null
            try {
                withContext(Dispatchers.IO) { bytes=repo.store.exportEncrypted(secret,clientId,organizationOnly) }
                val payload=requireNotNull(bytes)
                val folder=if(clientId==null && !organizationOnly)getPreferences(MODE_PRIVATE).getString("backup-folder",null) else null
                if(folder!=null) {
                    withContext(Dispatchers.IO) {
                        val dir=androidx.documentfile.provider.DocumentFile.fromTreeUri(this@MainActivity,Uri.parse(folder))
                            ?: error("Pasta indisponível; selecione-a novamente em Configurações.")
                        val stamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                        val file=dir.createFile("application/octet-stream","EvoluFisico-$stamp.efbackup")
                            ?: error("Não foi possível criar o arquivo de backup")
                        try {
                            contentResolver.openOutputStream(file.uri)?.use{it.write(payload);it.flush()} ?: error("Sem permissão de escrita")
                            check(file.length()==payload.size.toLong()){ "Backup incompleto no destino" }
                        } catch(e:Exception) { runCatching{file.delete()};throw e }
                    }
                    message="Backup criptografado salvo na pasta escolhida. Guarde a senha."
                } else {
                    pendingBackup?.fill(0);pendingBackup=payload
                    try { createBackup.launch("EvoluFisico-${if(organizationOnly)"Organizacao" else if(clientId==null)"Completo" else "Cliente"}-${LocalDate.now()}.efbackup") }
                    catch(e:Exception) { pendingBackup=null;throw e }
                    bytes=null // SAF callback now owns the encrypted buffer, including cancellation cleanup.
                }
            } finally { bytes?.fill(0);secret.fill('\u0000') }
        }
    }
    private fun createPdf(name:String,build:()->ByteArray) {
        runDataOperation("Não foi possível gerar PDF") {
            var bytes:ByteArray?=null
            try {
                withContext(Dispatchers.Default){bytes=build()}
                output(name,requireNotNull(bytes));bytes=null
            } finally {bytes?.fill(0)}
        }
    }
    private fun output(name: String, bytes: ByteArray) {
        pdf?.fill(0);pdf=bytes
        try{pdfPicker.launch(name)}catch(e:Exception){bytes.fill(0);pdf=null;throw e}
    }
    override fun onDestroy() {
        csvBytes?.fill(0);csvBytes=null
        restorePassword?.fill('\u0000'); legacyPassword?.fill('\u0000')
        pendingBackup?.fill(0); pdf?.fill(0)
        restorePassword = null; legacyPassword = null; pendingBackup = null; pdf = null
        super.onDestroy()
    }
}
