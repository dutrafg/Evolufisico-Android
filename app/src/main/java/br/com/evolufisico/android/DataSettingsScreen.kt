package br.com.evolufisico.android

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                            context.contentResolver.openOutputStream(uri, "w")?.use { stream ->
                                stream.write(bytes)
                                stream.flush()
                            } ?: throw IllegalStateException("Destino selecionado não permite gravação.")
                            "Backup cifrado salvo no local escolhido."
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
            }
            message = if (outcome.isSuccess) "Pasta de backups escolhida."
            else "Não foi possível manter acesso à pasta. Selecione outra."
        }
    }

    fun exportToFolder() {
        val folder = folderUri ?: return
        val credential = password.toCharArray()
        password = ""
        if (credential.size < 10) {
            credential.fill('\u0000')
            message = "A senha deve conter pelo menos 10 caracteres."
            return
        }
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
                        val timestamp = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
                        )
                        val parent = DocumentsContract.buildDocumentUriUsingTree(
                            tree, DocumentsContract.getTreeDocumentId(tree)
                        )
                        val file = DocumentsContract.createDocument(
                            context.contentResolver, parent,
                            "application/octet-stream",
                            "EvoluFisico_${timestamp}.efbak"
                        ) ?: error("Pasta não permite criar arquivos.")
                        try {
                            context.contentResolver.openOutputStream(file, "w")?.use {
                                it.write(bytes); it.flush()
                            } ?: error("Falha ao abrir o arquivo de backup.")
                        } catch (error: Exception) {
                            runCatching { DocumentsContract.deleteDocument(context.contentResolver, file) }
                            throw error
                        }
                        "Backup criptografado criado na pasta selecionada."
                    }
                } finally { credential.fill('\u0000') }
            }
            message = outcome.getOrElse { "Falha ao salvar na pasta: ${it.message}" }
            busy = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Preferências", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Modo escuro", style = MaterialTheme.typography.titleMedium)
            Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
        }
        HorizontalDivider()
        Text("Dados e backups", style = MaterialTheme.typography.titleLarge)
        Text("Clientes, avaliações, formulários e cobranças são criptografados no dispositivo. " +
            "O backup portátil usa senha própria, necessária para restaurar em outro aparelho.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = password, onValueChange = { password = it.take(128) },
            label = { Text("Senha do backup (mínimo 10 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            enabled = !busy && password.length >= 10,
            onClick = {
                pendingPassword = password.toCharArray()
                onDocumentFlow(true)
                createDocument.launch("EvoluFisico_${LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.efbak")
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Exportar backup criptografado") }
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
            enabled = !busy && folderUri != null && password.length >= 10,
            onClick = { exportToFolder() }, modifier = Modifier.fillMaxWidth()
        ) { Text("Salvar novo backup na pasta") }
        HorizontalDivider()
        Text("Verificar backup antes de atualizar", style = MaterialTheme.typography.titleMedium)
        Text("Verifica o arquivo e sua senha sem alterar o banco do dispositivo. " +
            "É recomendado antes de atualizar ou reinstalar o aplicativo.",
            style = MaterialTheme.typography.bodySmall)
        OutlinedButton(
            enabled = !busy && password.length >= 10,
            onClick = {
                pendingPassword = password.toCharArray()
                onDocumentFlow(true)
                inspectDocument.launch(arrayOf("*/*"))
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Verificar arquivo .efbak") }
        HorizontalDivider()
        Text("Restaurar arquivo de backup", style = MaterialTheme.typography.titleMedium)
        Text(
            "Por segurança, a restauração só é permitida se clientes, avaliações, formulários e cobranças locais " +
                "estiverem vazios. Ela nunca sobrescreve uma base existente. " +
                "Guarde o arquivo e a senha; sem a senha, não é possível recuperar o conteúdo.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            enabled = !busy && password.isNotEmpty(),
            onClick = { showRestoreConfirmation = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Restaurar de arquivo (.efbak)") }
        if (busy) Text("Processando arquivo. Não feche o aplicativo.")
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        HorizontalDivider()
        OutlinedButton(
            enabled = !busy, onClick = {
                busy = true
                scope.launch {
                    val state = withContext(Dispatchers.IO) { runCatching { backupManager.summary() } }
                    message = state.fold(
                        { "Integridade: ${it.clients.size} clientes, ${it.assessments.size} avaliações, ${it.forms.size} formulários e ${it.charges.size} cobranças válidas." },
                        { "Falha na verificação: ${it.message}" }
                    )
                    busy = false
                }
            }, modifier = Modifier.fillMaxWidth()
        ) { Text("Verificar integridade dos dados") }
        OutlinedButton(enabled = !busy, onClick = onLock,
            modifier = Modifier.fillMaxWidth()) { Text("Bloquear aplicativo agora") }
        InAppUpdatePanel(onDocumentFlow = onDocumentFlow)
        Text("Versão 0.20.0-alpha (Fases 16 a 20)", style = MaterialTheme.typography.bodySmall)
        Text(
            "Sem backup automático: a senha nunca é guardada e os backups são iniciados manualmente. " +
            "O aplicativo não possui integração própria com nuvem.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
    if (showRestoreConfirmation) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmation = false },
            title = { Text("Restaurar backup protegido?") },
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
