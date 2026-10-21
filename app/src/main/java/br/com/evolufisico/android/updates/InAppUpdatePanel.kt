package br.com.evolufisico.android.updates

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.backup.BackupSnapshot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun InAppUpdatePanel(onDocumentFlow: (Boolean) -> Unit) {
    val context = LocalContext.current
    val service = remember(context) { AppUpdateService(context.applicationContext) }
    val scope = rememberCoroutineScope()
    var checking by remember { mutableStateOf(false) }
    var available by remember { mutableStateOf<AvailableRelease?>(null) }
    var selectedApk by remember { mutableStateOf<File?>(null) }
    var summary by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var backupPassword by remember { mutableStateOf("") }
    var backupVerified by remember { mutableStateOf(false) }
    var verifiedSnapshot by remember { mutableStateOf<BackupSnapshot?>(null) }
    var backupReport by remember { mutableStateOf<String?>(null) }
    var confirmInstall by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        onDocumentFlow(false)
        if (uri != null) {
            checking = true
            scope.launch {
                val validation = withContext(Dispatchers.IO) {
                    runCatching {
                        val file = service.importFromDocument(uri)
                        file to service.validate(file)
                    }
                }
                validation.onSuccess {
                    selectedApk = it.first
                    summary = it.second
                    message = null
                }.onFailure {
                    selectedApk = null
                    summary = null
                    message = it.message ?: "Arquivo APK inválido."
                }
                checking = false
            }
        }
    }

    val backupPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        onDocumentFlow(false)
        backupVerified = false
        verifiedSnapshot = null
        backupReport = null
        val credential = backupPassword.toCharArray()
        backupPassword = ""
        if (uri != null) {
            checking = true
            scope.launch {
                val validation = withContext(Dispatchers.IO) {
                    try {
                        runCatching {
                            val manager = SecureBackupManager(context.applicationContext)
                            val input = context.contentResolver.openInputStream(uri)
                                ?: error("Não foi possível abrir o arquivo escolhido.")
                            val backup = manager.inspectBackup(input, credential)
                            val local = manager.summary()
                            require(local.clients.toSet() == backup.clients.toSet() &&
                                local.assessments.toSet() == backup.assessments.toSet() &&
                                local.forms.toSet() == backup.forms.toSet() &&
                                local.charges.toSet() == backup.charges.toSet() &&
                                local.operations == backup.operations &&
                                local.preRegistrations.toSet() ==
                                    backup.preRegistrations.toSet()) {
                                "O backup é válido, mas não contém exatamente o estado atual " +
                                    "dos dados. Faça um novo backup antes de atualizar."
                            }
                            backup
                        }
                    } finally { credential.fill('\u0000') }
                }
                validation.onSuccess {
                    verifiedSnapshot = it
                    backupVerified = true
                    backupReport = "Backup verificado: ${it.recordCount} registro(s), " +
                        "incluindo o estado atual do aparelho."
                }.onFailure {
                    verifiedSnapshot = null
                    backupVerified = false
                    backupReport = it.localizedMessage ?: "Falha ao verificar o backup."
                }
                checking = false
            }
        } else credential.fill('\u0000')
    }

    val unknownSources = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        message = if (service.installAllowed())
            "Instalação permitida. Toque em Instalar atualização."
        else "A instalação de APKs por esta fonte ainda não foi autorizada."
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider()
        Text("Atualizações do aplicativo", style = MaterialTheme.typography.titleLarge)
        Text("O Android pode instalar um APK compatível sobre o aplicativo existente, " +
            "sem apagar clientes ou avaliações. É necessário confirmar a atualização no instalador " +
            "do sistema. Não há instalação silenciosa.",
            style = MaterialTheme.typography.bodySmall)
        Text("Importante: cada APK DEBUG do GitHub Actions pode ter um certificado diferente. " +
            "As atualizações sem reinstalação exigem versões RELEASE assinadas sempre " +
            "com a mesma chave privada. Não desinstale antes de verificar seu backup .efbak.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(service.installedVersionDiagnostics(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary)
        if (context.packageName != "br.com.evolufisico.android") {
            Text("Esta cópia é um aplicativo de testes separado (${context.packageName}). " +
                "Ela não atualiza automaticamente a Beta anterior nem o RELEASE oficial. " +
                "Os APKs DEBUG do GitHub podem ter assinaturas diferentes. " +
                "Faça backup nesta cópia, instale o RELEASE com assinatura permanente e " +
                "restaure os dados nele antes de remover qualquer instalação antiga.",
                style=MaterialTheme.typography.bodyMedium,
                color=MaterialTheme.colorScheme.error)
        }
        OutlinedButton(
            enabled = !checking,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                checking = true
                available = null
                selectedApk = null
                summary = null
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        runCatching { service.releaseFromGitHub() }
                    }
                    result.onSuccess {
                        available = it
                        message = if (it == null)
                            "Ainda não há APK RELEASE oficial publicado. Os APKs DEBUG de " +
                                "outras execuções não conseguirão atualizar esta instalação. " +
                                "Use um APK local somente se a assinatura for idêntica."
                        else "Disponível no repositório: ${it.tag}. O arquivo será conferido antes de instalar."
                    }.onFailure {
                        message = "Falha ao consultar GitHub Releases: ${it.message}"
                    }
                    checking = false
                }
            }
        ) { Text("Procurar atualizações no GitHub") }

        available?.let { release ->
            Button(
                modifier = Modifier.fillMaxWidth(), enabled = !checking,
                onClick = {
                    checking = true
                    selectedApk = null
                    scope.launch {
                        val result = withContext(Dispatchers.IO) {
                            runCatching {
                                val file = service.download(release)
                                file to service.validate(file)
                            }
                        }
                        result.onSuccess {
                            selectedApk = it.first
                            summary = it.second
                            message = "Download concluído; APK e assinatura conferidos."
                        }.onFailure {
                            message = "Atualização recusada: ${it.message}"
                        }
                        checking = false
                    }
                }
            ) { Text("Baixar e verificar ${release.tag}") }
        }

        OutlinedButton(enabled = !checking, modifier = Modifier.fillMaxWidth(),
            onClick = {
                onDocumentFlow(true)
                filePicker.launch(arrayOf("application/vnd.android.package-archive", "application/octet-stream", "*/*"))
            }) { Text("Selecionar um APK já baixado") }

        summary?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        OutlinedTextField(
            value = backupPassword,
            onValueChange = {
                backupPassword = it.take(128)
                backupVerified = false
                verifiedSnapshot = null
                backupReport = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Senha, se o backup .efbak for criptografado") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        OutlinedButton(
            enabled = !checking,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onDocumentFlow(true)
                backupPicker.launch(arrayOf("*/*"))
            }
        ) { Text("Verificar backup JSON ou .efbak") }
        backupReport?.let {
            Text(it, color = if (backupVerified) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }
        Button(modifier = Modifier.fillMaxWidth(),
            enabled = !checking && selectedApk != null && backupVerified &&
                verifiedSnapshot != null,
            onClick = { confirmInstall = true }) {
            Text("Instalar atualização verificada")
        }
        Text("O instalador só será liberado após verificar um backup JSON novo " +
            "ou .efbak criptografado antigo, que corresponda aos registros atuais. " +
            "Backups JSON não exigem senha; arquivos antigos exigem a senha original. " +
            "A assinatura do APK também deve ser compatível. Não desinstale sem migrar os dados.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (checking) Text("Verificando o arquivo, assinatura e versão...")
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall) }
    }

    if (confirmInstall) {
        AlertDialog(
            onDismissRequest = { confirmInstall = false },
            title = { Text("Instalar atualização?") },
            text = {
                Text("O sistema Android pedirá sua autorização. " +
                    "O aplicativo verificará novamente a assinatura e a versão do APK. " +
                    "Uma instalação por cima mantém a base local somente se for aceita " +
                    "como atualização do mesmo aplicativo.")
            },
            confirmButton = {
                Button(onClick = {
                    confirmInstall = false
                    val file = selectedApk ?: return@Button
                    val snapshot = verifiedSnapshot ?: return@Button
                    checking = true
                    scope.launch {
                        val validation = withContext(Dispatchers.IO) {
                            runCatching {
                                check(SecureBackupManager(context.applicationContext).summary() == snapshot) {
                                    "Os dados mudaram após a verificação do backup. " +
                                        "Exporte e confira um novo arquivo antes de atualizar."
                                }
                                service.validate(file)
                            }
                        }
                        checking = false
                        validation.onFailure {
                            backupVerified = false
                            verifiedSnapshot = null
                            backupReport = null
                            message = "Instalação bloqueada: ${it.message}"
                        }.onSuccess {
                            if (!service.installAllowed()) {
                                message = "Autorize a instalação desta fonte no Android e tente novamente."
                                unknownSources.launch(service.unknownSourcesSettings())
                            } else {
                                runCatching { context.startActivity(service.installerIntent(file)) }
                                    .onFailure { message = "Não foi possível abrir o instalador: ${it.message}" }
                            }
                        }
                    }
                }) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmInstall = false }) { Text("Cancelar") }
            }
        )
    }
}
