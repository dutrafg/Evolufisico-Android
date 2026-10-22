package br.com.evolufisico.nativo

import android.Manifest
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var repo: AppRepository
    private var message by mutableStateOf("")
    private var pdf: ByteArray? = null
    private var csvBytes:ByteArray?=null
    private var exportPassword: CharArray? = null
    private var restorePassword: CharArray? = null
    private var pendingBackup: ByteArray? = null
    private var legacyPassword: CharArray? = null

    private val permissionReminders = registerForActivityResult(ActivityResultContracts.RequestPermission()){ granted ->
        if(granted)enableReminders() else message="Permissão negada; os lembretes permanecerão desligados."
    }
    private fun enableReminders(){
        AppointmentNotifications.configure(this,true)
        AppointmentNotifications.channel(this)
        repo.appointments.forEach{AppointmentNotifications.schedule(this,it)}
        val lead=AppointmentNotifications.leadMinutes(this)
        message="Lembretes de agenda ativados (aproximadamente ${if(lead==1440)"24 horas" else if(lead==60)"1 hora" else "$lead min"} antes)."
    }
    private val csvPicker=registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")){uri->
        val bytes=csvBytes;csvBytes=null
        try {
            if(uri!=null){
                val data=bytes ?: error("Sem dados para exportar.")
                contentResolver.openOutputStream(uri)?.use{it.write(data);it.flush()} ?: error("Destino indisponível.")
                message="CSV salvo com sucesso."
            }
        } catch(e:Exception){
            if(uri!=null)runCatching{contentResolver.delete(uri,null,null)}
            message="Não foi possível salvar CSV: ${e.localizedMessage}"
        } finally {bytes?.fill(0)}
    }
    private val pdfPicker = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val data = pdf
        if (uri != null) try {
            val bytes = data ?: error("Sem documento para salvar")
            contentResolver.openOutputStream(uri)?.use { it.write(bytes); it.flush() } ?: error("Destino indisponível")
            message = "PDF salvo com sucesso."
        } catch (e: Exception) {
            runCatching{contentResolver.delete(uri,null,null)}
            message = "Falha ao salvar PDF: ${e.localizedMessage}"
        }
        finally { data?.fill(0); pdf = null }
        else { data?.fill(0); pdf = null }
    }
    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            getPreferences(MODE_PRIVATE).edit().putString("backup-folder", uri.toString()).apply()
            getSharedPreferences("native-backup", MODE_PRIVATE).edit().putString("folder", uri.toString()).apply()
            message = "Pasta de backup selecionada."
        } catch (e: Exception) { message = "Não foi possível acessar pasta: ${e.localizedMessage}" }
    }
    private val openBackup = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val password = restorePassword
        restorePassword = null
        var content: ByteArray? = null
        if (uri != null && password != null) try {
            content = contentResolver.openInputStream(uri)?.use { stream ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                while (true) {
                    val n = stream.read(buffer)
                    if (n < 0) break
                    if (output.size() + n > 24_000_000) error("Backup excede 24 MB")
                    output.write(buffer, 0, n)
                }
                output.toByteArray()
            } ?: error("Arquivo inacessível")
            val count = repo.store.importEncrypted(requireNotNull(content), password)
            repo.refresh();repo.rescheduleReminders()
            message = "$count registros recuperados."
        } catch (e: Exception) { message = "Restauração não efetuada: ${e.localizedMessage}" }
        finally { content?.fill(0); password.fill('\u0000') }
        if (uri == null) password?.fill('\u0000')
    }
    private val legacyPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val password=legacyPassword
        legacyPassword=null
        var raw: ByteArray? = null
        if (uri != null) try {
            raw = contentResolver.openInputStream(uri)?.use { stream ->
                val bytes=java.io.ByteArrayOutputStream();val buf=ByteArray(8192)
                while(true) { val count=stream.read(buf);if(count<0)break
                    require(bytes.size()+count<=24_000_000) { "Arquivo maior que 24 MB" }
                    bytes.write(buf,0,count) }
                bytes.toByteArray()
            } ?: error("Arquivo indisponível")
            val stage = LegacyImporter.parse(requireNotNull(raw),password)
            raw?.fill(0)
            require(stage.total>0) { "Backup sem registros" }
            val count=repo.store.importLegacy(stage)
            repo.refresh();repo.rescheduleReminders()
            message="Importação legado concluída: $count registros (${stage.version}). Cópia original preservada."
        } catch (e:Exception) { message="Importação legado cancelada: ${e.localizedMessage}" }
        finally { raw?.fill(0); password?.fill('\u0000') }
        if(uri==null)password?.fill('\u0000')
    }
    private val deviceBackupPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if(uri!=null)try {
            val bytes=contentResolver.openInputStream(uri)?.use { it.readLimited(24_000_000) }?:error("Arquivo inválido")
            try {
                val count=repo.store.importDeviceEncrypted(bytes);repo.refresh();repo.rescheduleReminders()
                message="$count registros do backup automático restaurados."
            } finally { bytes.fill(0) }
        } catch(e:Exception) { message="Falha na restauração automática: ${e.localizedMessage}" }
    }
    private val createBackup = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        val bytes = pendingBackup
        if (uri != null) try {
            val payload = bytes ?: error("Nenhum backup preparado")
            contentResolver.openOutputStream(uri)?.use { it.write(payload); it.flush() } ?: error("Destino não disponível")
            message = "Backup criptografado exportado. Guarde a senha em local seguro."
        } catch (e: Exception) { message = "Falha no backup: ${e.localizedMessage}" }
        finally { bytes?.fill(0); pendingBackup = null; exportPassword?.fill('\u0000'); exportPassword = null }
        else { bytes?.fill(0); pendingBackup = null; exportPassword?.fill('\u0000'); exportPassword = null }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = AppRepository(applicationContext)
        setContent {
            NativeApp(
                repository = repo, message = message, onDismissMessage = { message = "" },
                onCreateEvaluationPdf = { e -> createPdf("Avaliacao_${e.date}.pdf") { Reports.evaluation(e) } },
                onCreateEvolutionPdf = { name, list -> createPdf("Evolucao_${LocalDate.now()}.pdf") { Reports.evolution(name, list) } },
                onExportCsv={name,text->
                    runCatching{
                        require(text.length<=8_000_000){"CSV muito grande. Reduza o período."}
                        csvBytes?.fill(0);csvBytes=text.toByteArray(Charsets.UTF_8)
                        csvPicker.launch(name)
                    }.onFailure{csvBytes?.fill(0);csvBytes=null;message=it.message ?: "Falha ao exportar CSV."}
                },
                onCreateBillingPdf = { createPdf("Cobrancas_${LocalDate.now()}.pdf") { Reports.billing(repo.charges) } },
                onCreateTrainingPdf = { item -> createPdf("Treino_${item.date}.pdf") { Reports.training(item) } },
                onChooseFolder = { folderPicker.launch(null) },
                onBackup = { password ->
                    try {
                        exportPassword = password.toCharArray()
                        pendingBackup = repo.store.exportEncrypted(exportPassword!!)
                        exportPassword?.fill('\u0000'); exportPassword = null
                        val str = getPreferences(MODE_PRIVATE).getString("backup-folder", null)
                        if (str != null) {
                            val dir = androidx.documentfile.provider.DocumentFile.fromTreeUri(this, Uri.parse(str))
                                ?: error("Pasta de backup indisponível")
                            val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                            val file = dir.createFile("application/octet-stream", "EvoluFisico-$stamp.efbackup")
                                ?: error("Não foi possível criar o arquivo de backup")
                            val bytes = pendingBackup ?: error("Backup vazio")
                            try {
                                val out = contentResolver.openOutputStream(file.uri) ?: error("Sem permissão de escrita")
                                out.use { it.write(bytes); it.flush() }
                                check(file.length() == bytes.size.toLong()) { "Backup incompleto no destino" }
                                message = "Backup salvo na pasta escolhida."
                            } catch (e: Exception) { file.delete(); throw e }
                            finally { bytes.fill(0); pendingBackup = null }
                        } else createBackup.launch("EvoluFisico-${LocalDate.now()}.efbackup")
                    } catch (e: Exception) {
                        message = "Não foi possível gerar backup: ${e.localizedMessage}"
                        pendingBackup?.fill(0); pendingBackup = null; exportPassword?.fill('\u0000'); exportPassword = null
                    }
                },
                onImportLegacy = { password ->
                    legacyPassword=password.toCharArray()
                    legacyPicker.launch(arrayOf("application/json","application/octet-stream","*/*"))
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
                    restorePassword = password.toCharArray()
                    openBackup.launch(arrayOf("application/octet-stream", "application/json", "*/*"))
                }
            )
        }
    }
    private fun createPdf(name:String,build:()->ByteArray) {
        try { output(name,build()) }
        catch(e:Exception){pdf?.fill(0);pdf=null;message="Não foi possível gerar PDF: ${e.localizedMessage}"}
    }
    private fun output(name: String, bytes: ByteArray) {
        pdf?.fill(0);pdf=bytes
        try{pdfPicker.launch(name)}catch(e:Exception){bytes.fill(0);pdf=null;throw e}
    }
    override fun onDestroy() {
        csvBytes?.fill(0);csvBytes=null
        exportPassword?.fill('\u0000'); restorePassword?.fill('\u0000'); legacyPassword?.fill('\u0000')
        pendingBackup?.fill(0); pdf?.fill(0)
        exportPassword = null; restorePassword = null; legacyPassword = null; pendingBackup = null; pdf = null
        super.onDestroy()
    }
}
