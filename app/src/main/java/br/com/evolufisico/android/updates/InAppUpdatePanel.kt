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
import androidx.compose.material3.Checkbox
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
    var confirmBackup by remember { mutableStateOf(false) }
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
        Text("IMPORTANTE: as versões precisam usar a MESMA assinatura digital. " +
            "APKs de depuração gerados em máquinas diferentes podem ser incompatíveis.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                            "Nenhum APK publicado em GitHub Releases. Use o arquivo APK local."
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

        androidx.compose.foundation.layout.Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Checkbox(checked = confirmBackup, onCheckedChange = { confirmBackup = it })
            Text("Já salvei meu backup criptografado (.efbak) fora do aplicativo.",
                style = MaterialTheme.typography.bodySmall)
        }

        Button(modifier = Modifier.fillMaxWidth(),
            enabled = !checking && selectedApk != null && confirmBackup,
            onClick = { confirmInstall = true }) {
            Text("Instalar atualização verificada")
        }
        Text("Se o Android rejeitar a assinatura, NÃO desinstale antes de " +
            "exportar e conferir seu backup em uma pasta externa. A desinstalação " +
            "normal apaga os dados locais e as chaves do Android Keystore.",
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
                    val validation = runCatching { service.validate(file) }
                    validation.onFailure {
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
                }) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmInstall = false }) { Text("Cancelar") }
            }
        )
    }
}
