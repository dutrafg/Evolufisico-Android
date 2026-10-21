package br.com.evolufisico.android

import android.app.TimePickerDialog
import android.Manifest
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.backup.BackupFileVerification
import br.com.evolufisico.android.backup.DailyBackupScheduler
import br.com.evolufisico.android.agenda.AgendaReminderScheduler
import br.com.evolufisico.android.updates.InAppUpdatePanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DataSettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    paletteId:String,
    onPaletteChange:(String)->Unit,
    backupManager: SecureBackupManager,
    onDocumentFlow: (Boolean) -> Unit,
    onLock: () -> Unit,
    onRestored: (BackupSnapshot) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("backup_settings", 0) }
    var folderUri by remember { mutableStateOf(prefs.getString("folder_uri", null)) }
    var password by remember { mutableStateOf("") }
    var pendingPassword by remember { mutableStateOf<CharArray?>(null) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showRestoreConfirmation by remember { mutableStateOf(false) }
    var settingsPage by remember { mutableStateOf("geral") }
    val dailyScheduler = remember(context) { DailyBackupScheduler(context.applicationContext) }
    var daily by remember { mutableStateOf(dailyScheduler.state()) }
    var preferredHour by remember { mutableStateOf(daily.hour) }
    var preferredMinute by remember { mutableStateOf(daily.minute) }
    var localRecords by remember { mutableStateOf<Int?>(null) }
    var localDataProblem by remember { mutableStateOf<String?>(null) }
    val reminderScheduler=remember(context) { AgendaReminderScheduler(context.applicationContext) }
    var remindersEnabled by remember { mutableStateOf(reminderScheduler.isEnabled()) }
    var reminderIssue by remember { mutableStateOf<String?>(null) }
    val reminderPermission=rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if(granted) {
            runCatching { reminderScheduler.setEnabled(true) }
                .onSuccess { remindersEnabled=true;reminderIssue=null }
                .onFailure { reminderIssue=it.message ?: "Falha ao ativar lembretes." }
        } else reminderIssue="Autorize as notificações do EvoluFísico no Android."
    }

    LaunchedEffect(backupManager) {
        val outcome = withContext(Dispatchers.IO) { runCatching { backupManager.summary() } }
        outcome.onSuccess {
            localRecords = it.recordCount
            localDataProblem = null
        }.onFailure {
            localRecords = null
            localDataProblem = "Não foi possível verificar a base atual; restauração bloqueada."
        }
    }

    val createDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val credential = pendingPassword
        pendingPassword = null
        password = ""
        if (uri != null && credential != null) {
            busy = true
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        runCatching {
                            val bytes = backupManager.export(credential)
                            try {
                                context.contentResolver.openOutputStream(uri, "w")?.use { stream ->
                                    stream.write(bytes)
                                    stream.flush()
                                } ?: error("Destino selecionado não permite gravação.")
                                val saved=context.contentResolver.openInputStream(uri)
                                    ?: error("O provedor salvou, mas não permite reler o arquivo.")
                                check(BackupFileVerification.matches(saved,bytes)) {
                                    "Backup salvo com conteúdo diferente do original. Gere outro arquivo."
                                }
                            } finally { bytes.fill(0) }
                            "Backup JSON gravado e verificado por releitura (sem senha)."
                        }
                    } finally { credential.fill('\u0000') }
                }
                message = result.getOrElse {
                    "Falha ao exportar: ${it.message ?: "Erro de gravação."}"
                }
                busy = false
            }
        } else {
            credential?.fill('\u0000')
        }
    }

    val inspectDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val credential = pendingPassword
        pendingPassword = null
        password = ""
        if (uri != null && credential != null) {
            busy = true
            scope.launch {
                val check = withContext(Dispatchers.IO) {
                    try {
                        runCatching {
                            val input = context.contentResolver.openInputStream(uri)
                                ?: error("Não foi possível abrir o backup escolhido.")
                            backupManager.inspectBackup(input, credential)
                        }
                    } finally { credential.fill('\u0000') }
                }
                message = check.fold(
                    onSuccess = { data ->
                        "Backup íntegro e senha correta: " +
                            "${data.clients.size} clientes, " +
                            "${data.assessments.size} avaliações, " +
                            "${data.forms.size} formulários e " +
                            "${data.charges.size} cobranças. Nada foi restaurado."
                    },
                    onFailure = {
                        "Backup não pôde ser verificado: " +
                            (it.localizedMessage ?: "Senha, formato ou integridade inválidos.")
                    }
                )
                busy = false
            }
        } else credential?.fill('\u0000')
    }

    val openDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val credential = pendingPassword
        pendingPassword = null
        password = ""
        if (uri != null && credential != null) {
            busy = true
            scope.launch {
                val outcome = withContext(Dispatchers.IO) {
                    try {
                        runCatching {
                            val input = context.contentResolver.openInputStream(uri)
                                ?: error("Arquivo de backup não pôde ser aberto.")
                            backupManager.restore(input, credential)
                        }
                    } finally { credential.fill('\u0000') }
                }
                outcome.onSuccess {
                    message = "Restauração concluída: ${it.clients.size} clientes, " +
                        "${it.assessments.size} avaliações, ${it.forms.size} formulários, " +
                        "${it.charges.size} cobranças."
                    localRecords = it.recordCount
                    localDataProblem = null
                    onRestored(it)
                }.onFailure {
                    message = "Falha na restauração: ${it.message ?: "Arquivo inválido, senha incorreta ou erro de armazenamento."}"
                }
                busy = false
            }
        } else { credential?.fill('\u0000') }
    }

    val openTree = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        onDocumentFlow(false)
        if (uri != null) {
            val outcome = runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                prefs.edit().putString("folder_uri", uri.toString()).apply()
                folderUri = uri.toString()
                if (daily.active && daily.folderUri != uri.toString()) {
                    message = "A pasta do backup automático permanece a anterior. " +
                        "Reconfigure o agendamento para usar esta pasta."
                }
            }
            message = if (outcome.isSuccess) "Pasta de backups escolhida."
            else "Não foi possível manter acesso à pasta. Selecione outra."
        }
    }

    fun exportToFolder() {
        val folder = folderUri ?: return
        val credential = password.toCharArray()
        password = ""
        busy = true
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                try {
                    runCatching {
                        val tree = Uri.parse(folder)
                        require(context.contentResolver.persistedUriPermissions.any {
                            it.uri == tree && it.isWritePermission
                        }) { "A permissão da pasta expirou. Selecione-a novamente." }
                        val bytes = backupManager.export(credential)
                        try {
                            val timestamp = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
                            )
                            val parent = DocumentsContract.buildDocumentUriUsingTree(
                                tree, DocumentsContract.getTreeDocumentId(tree)
                            )
                            val file = DocumentsContract.createDocument(
                                context.contentResolver, parent,
                                "application/octet-stream",
                                "EvoluFisico_${timestamp}.json"
                            ) ?: error("Pasta não permite criar arquivos.")
                            try {
                                context.contentResolver.openOutputStream(file, "w")?.use {
                                    it.write(bytes); it.flush()
                                } ?: error("Falha ao abrir o arquivo de backup.")
                                val saved=context.contentResolver.openInputStream(file)
                                    ?: error("O arquivo foi gravado, mas não pôde ser relido.")
                                check(BackupFileVerification.matches(saved,bytes)) {
                                    "O backup na pasta não corresponde ao arquivo original."
                                }
                            } catch (error: Exception) {
                                runCatching { DocumentsContract.deleteDocument(context.contentResolver, file) }
                                throw error
                            }
                        } finally { bytes.fill(0) }
                        "Backup JSON criado e verificado na pasta selecionada (sem senha)."
                    }
                } finally { credential.fill('\u0000') }
            }
            message = outcome.getOrElse { "Falha ao salvar na pasta: ${it.message}" }
            busy = false
        }
    }

    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    Column(
        modifier=Modifier.widthIn(max=VisualTokens.contentMaxWidthDp.dp)
            .fillMaxWidth().fillMaxHeight()
            .verticalScroll(rememberScrollState()).padding(VisualLayout.edgePaddingDp(
                LocalConfiguration.current.screenWidthDp).dp),
        verticalArrangement=Arrangement.spacedBy(18.dp)
    ) {
        Text("Gerencie o ambiente, proteja seus dados e execute a manutenção.",
            style=MaterialTheme.typography.bodyMedium,
            color=MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("geral" to "Geral","dados" to "Dados e privacidade",
                "manutencao" to "Manutenção").forEach { (id,label) ->
                FilterChip(selected=settingsPage==id,onClick={settingsPage=id},
                    label={Text(label)})
            }
        }
        HorizontalDivider()
        if (settingsPage=="geral") {
            Text("Geral", style = MaterialTheme.typography.titleLarge)
            Card(modifier=Modifier.fillMaxWidth(),
                shape=androidx.compose.foundation.shape.RoundedCornerShape(
                    VisualTokens.cardRadiusDp.dp),
                colors=androidx.compose.material3.CardDefaults.cardColors(
                    containerColor=MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,
                    verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
                        Text("Modo escuro", style=MaterialTheme.typography.titleMedium)
                        Text("Ajusta o contraste de toda a interface.",
                            style=MaterialTheme.typography.bodySmall,
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.size(12.dp))
                    Switch(checked=darkMode,onCheckedChange=onDarkModeChange)
                }
            }
            Card(modifier=Modifier.fillMaxWidth(),
                shape=androidx.compose.foundation.shape.RoundedCornerShape(
                    VisualTokens.cardRadiusDp.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement=Arrangement.SpaceBetween,
                    verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                        Text("Avisos da Agenda",style=MaterialTheme.typography.titleMedium)
                        Text("Lembretes aproximados na hora anterior aos atendimentos. " +
                            "Não exibimos o nome dos clientes nas notificações.",
                            style=MaterialTheme.typography.bodySmall,
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.size(12.dp))
                    Switch(checked=remindersEnabled,onCheckedChange={enabled ->
                        if(enabled && Build.VERSION.SDK_INT>=33 &&
                            !reminderScheduler.hasPermission()) {
                            reminderPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            runCatching { reminderScheduler.setEnabled(enabled) }
                                .onSuccess { remindersEnabled=enabled;reminderIssue=null }
                                .onFailure {
                                    reminderIssue=it.localizedMessage ?: "Falha no agendamento."
                                }
                        }
                    })
                }
            }
            reminderIssue?.let {
                Text(it,style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.error)
            }
            Text("O Android pode atrasar avisos em modo economia de bateria.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Paleta de cores",style=MaterialTheme.typography.titleMedium)
            Text("Escolha entre 10 cores. A preferência é salva automaticamente.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            val screenDp=LocalConfiguration.current.screenWidthDp
            val scale=androidx.compose.ui.platform.LocalDensity.current.fontScale
            val colorColumns=when {
                screenDp<360 || scale>=1.45f && screenDp<700 -> 1
                screenDp>=840 && scale<=1.2f -> 5
                screenDp>=700 -> 3
                else -> 2
            }
            CorporatePalettes.options.chunked(colorColumns).forEach { paletteRow ->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    paletteRow.forEach { palette ->
                        FilterChip(
                            selected=paletteId==palette.id,
                            onClick={onPaletteChange(palette.id)},
                            modifier=Modifier.weight(1f),
                            label={Text(palette.label,maxLines=1)},
                            leadingIcon={
                                Box(Modifier.size(16.dp).background(palette.bright,CircleShape))
                            }
                        )
                    }
                    repeat(colorColumns-paletteRow.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            HorizontalDivider()
            Card(onClick = { settingsPage="dados" }, modifier = Modifier.fillMaxWidth(),
                shape=androidx.compose.foundation.shape.RoundedCornerShape(
                    VisualTokens.cardRadiusDp.dp),
                colors=androidx.compose.material3.CardDefaults.cardColors(
                    containerColor=VisualTokens.panel(
                        VisualTokens.dark(MaterialTheme.colorScheme)))) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Dados e Privacidade", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary)
                    Text("Dados locais, backup manual e diário, restauração, " +
                        "diagnósticos e bloqueio do aplicativo.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Abrir Dados e Privacidade →",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
            HorizontalDivider()
            Text("Atualizações", style = MaterialTheme.typography.titleLarge)
            InAppUpdatePanel(onDocumentFlow = onDocumentFlow)
            Text("Configurações · versão de testes",
                style = MaterialTheme.typography.bodySmall)
        } else {
        if(settingsPage=="dados") {
        Text("Dados e privacidade",style=MaterialTheme.typography.titleLarge)
        Text("Armazenamento local", style = MaterialTheme.typography.titleLarge)
        Text("Versão alpha: clientes, avaliações, formulários e cobranças são salvos em " +
            "JSON local legível. Novos backups JSON não usam senha. Evite dados sensíveis reais.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider()
        Text("Backup manual", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = password, onValueChange = { password = it.take(128) },
            label = { Text("Senha (apenas backups antigos .efbak)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            enabled = !busy,
            onClick = {
                pendingPassword = password.toCharArray()
                onDocumentFlow(true)
                createDocument.launch("EvoluFisico_${LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.json")
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar backup JSON") }
        OutlinedButton(
            enabled = !busy,
            onClick = { onDocumentFlow(true); openTree.launch(null) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Escolher pasta para backups") }
        Text(
            if (folderUri == null) "Pasta: nenhuma selecionada"
            else "Pasta selecionada; novos arquivos serão criados sem sobrescrever os anteriores.",
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedButton(
            enabled = !busy && folderUri != null,
            onClick = { exportToFolder() }, modifier = Modifier.fillMaxWidth()
        ) { Text("Salvar novo backup na pasta") }
        HorizontalDivider()
        Text("Backup automático diário", style = MaterialTheme.typography.titleLarge)
        Text("Escolha a hora e uma pasta permanente. O Android agenda a execução " +
            "em segundo plano, inclusive quando o aplicativo estiver fechado. " +
            "A economia de bateria pode atrasar o horário escolhido.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Horário escolhido: " +
            preferredHour.toString().padStart(2,'0') + ":" +
            preferredMinute.toString().padStart(2,'0'))
        OutlinedButton(modifier = Modifier.fillMaxWidth(), enabled = !busy,
            onClick = {
                TimePickerDialog(context, { _, hour, minute ->
                    preferredHour = hour; preferredMinute = minute
                }, preferredHour, preferredMinute, true).show()
            }) { Text("Escolher horário do backup") }
        Text("Pasta: " + (if (folderUri == null) "não selecionada"
            else "selecionada pelo Android"),
            style = MaterialTheme.typography.bodySmall)
        Button(
            enabled = !busy && folderUri != null,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val secret = password.toCharArray()
                password = ""
                busy = true
                scope.launch {
                    val operation = withContext(Dispatchers.IO) {
                        try {
                            runCatching {
                                dailyScheduler.activate(folderUri!!, preferredHour,
                                    preferredMinute, secret)
                            }
                        } finally { secret.fill('\u0000') }
                    }
                    daily = dailyScheduler.state()
                    message = operation.fold(
                        onSuccess = { "Backup JSON diário ativado. O Android pode atrasar a execução." },
                        onFailure = { "Não foi possível agendar: " + (it.localizedMessage ?: "Erro.") }
                    )
                    busy = false
                }
            }
        ) { Text(if (daily.active) "Atualizar programação e senha" else "Ativar backup diário") }
        OutlinedButton(enabled = !busy && daily.active,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                dailyScheduler.disable()
                daily = dailyScheduler.state()
                message = "Backup automático desativado; arquivos anteriores mantidos."
            }) { Text("Desativar backup automático") }
        Text(if (daily.active)
            "Agendado diariamente para " + daily.hour.toString().padStart(2,'0') +
                ":" + daily.minute.toString().padStart(2,'0') + " (aproximadamente)"
            else "Backup automático desativado.",
            color = MaterialTheme.colorScheme.primary)
        if (daily.lastSuccessMillis > 0L) {
            val date = java.time.Instant.ofEpochMilli(daily.lastSuccessMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            Text("Último backup automático confirmado: " + date)
        } else {
            Text("Nenhum backup automático confirmado até o momento.")
        }
        daily.lastError?.let { Text("Última falha: " + it, color = MaterialTheme.colorScheme.error) }
        OutlinedButton(enabled = !busy, modifier = Modifier.fillMaxWidth(),
            onClick = { daily = dailyScheduler.state() }) {
            Text("Atualizar status do backup")
        }
        Text("O backup automático grava arquivos JSON sem senha. Os arquivos antigos " +
            ".efbak protegidos continuam exigindo a senha usada na criação.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider()
        Text("Verificar backup e dados", style = MaterialTheme.typography.titleLarge)
        Text("Verifica o arquivo e sua senha sem alterar o banco do dispositivo. " +
            "É recomendado antes de atualizar ou reinstalar o aplicativo.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedButton(
            enabled = !busy,
            onClick = {
                pendingPassword = password.toCharArray()
                onDocumentFlow(true)
                inspectDocument.launch(arrayOf("*/*"))
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Verificar backup .json ou .efbak") }
        HorizontalDivider()
        Text("Restaurar arquivo de backup", style = MaterialTheme.typography.titleMedium)
        Text(
            "Por segurança, a restauração só é permitida se clientes, avaliações, formulários e cobranças locais " +
                "estiverem vazios. Ela nunca sobrescreve uma base existente. " +
                "Backups JSON novos não exigem senha; arquivos .efbak antigos precisam dela.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (localRecords != null && localRecords!! > 0) {
            Text("Restauração indisponível: há " + localRecords +
                " registro(s) no aparelho. Use 'Verificar arquivo .efbak' sem sobrescrever " +
                "nenhuma informação. Após uma instalação nova com base vazia, a restauração poderá ser feita.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        localDataProblem?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        OutlinedButton(
            enabled = !busy && localRecords == 0,
            onClick = { showRestoreConfirmation = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Restaurar backup .json ou .efbak") }
        if (busy) Text("Processando arquivo. Não feche o aplicativo.")
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
        if(settingsPage=="manutencao") {
        Text("Manutenção e diagnóstico",style=MaterialTheme.typography.titleLarge)
        Text("Verifique a integridade dos registros e controle o bloqueio local.",
            color=MaterialTheme.colorScheme.onSurfaceVariant)
        BetaReadinessPanel()

        HorizontalDivider()
        OutlinedButton(
            enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    val state = withContext(Dispatchers.IO) { runCatching { backupManager.summary() } }
                    state.onSuccess {
                        localRecords = it.recordCount
                        localDataProblem = null
                    }.onFailure {
                        localRecords = null
                        localDataProblem = "Não foi possível verificar os dados locais."
                    }
                    message = state.fold(
                        { "Integridade: ${it.clients.size} clientes, ${it.assessments.size} avaliações, ${it.forms.size} formulários e ${it.charges.size} cobranças válidas." },
                        { "Falha na verificação: ${it.message}" }
                    )
                    busy = false
                }
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Verificar integridade dos dados") }
        HorizontalDivider()
        Text("Segurança", style = MaterialTheme.typography.titleLarge)
        Text("Bloqueio local da sessão. Durante o desenvolvimento, o JSON é legível.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(enabled = !busy, onClick = onLock,
            modifier = Modifier.fillMaxWidth()) { Text("Bloquear aplicativo agora") }
        Text("Manutenção · EvoluFísico Android",style=MaterialTheme.typography.bodySmall)
        Text(
            "Backup automático usa WorkManager e JSON sem senha durante a fase alpha. " +
            "Não existe sincronização própria com nuvem ou garantia de execução no minuto exato.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        }
        }
    }
    }
    if (showRestoreConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmation = false },
            title = { Text("Restaurar backup?") },
            text = {
                Text("A restauração só é permitida se não existirem clientes, avaliações, formulários ou cobranças " +
                    "na base local. O aplicativo validará o arquivo e a senha antes de gravar. " +
                    "Mantenha o arquivo original até verificar todos os registros.")
            },
            confirmButton = {
                Button(onClick = {
                    showRestoreConfirmation = false
                    pendingPassword = password.toCharArray()
                    onDocumentFlow(true)
                    openDocument.launch(arrayOf("*/*"))
                }) { Text("Selecionar backup") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmation = false }) { Text("Cancelar") }
            }
        )
    }

}
