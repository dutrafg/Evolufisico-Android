package br.com.evolufisico.android

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.forms.FormCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/** Consulta somente leitura: não duplica dados nem armazena cópias desprotegidas. */
@Composable
fun ClientOverviewDialog(
    client: Client,
    manager: SecureBackupManager,
    onDocumentFlow: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var snapshot by remember(client.id) { mutableStateOf<BackupSnapshot?>(null) }
    var loading by remember(client.id) { mutableStateOf(true) }
    var working by remember { mutableStateOf(false) }
    var error by remember(client.id) { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(client.id, manager) {
        loading = true
        val r = withContext(Dispatchers.IO) { runCatching { manager.summary() } }
        r.onSuccess { snapshot = it }
            .onFailure { error = "Falha ao verificar dados protegidos. Nenhum arquivo foi modificado." }
        loading = false
    }

    val pdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val data = snapshot
        if (uri != null && data != null && !working) {
            working = true
            scope.launch {
                val r = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri, "w")?.use { stream ->
                            ClientDossierPdf.write(stream, client, data)
                        } ?: error("O destino do arquivo não permite gravação.")
                    }
                }
                resultMessage = r.fold(
                    onSuccess = { "Ficha PDF exportada." },
                    onFailure = { "Não foi possível gerar ficha PDF: " + (it.message ?: "Erro ao gravar.") }
                )
                working = false
            }
        }
    }

    Dialog(onDismissRequest = { if (!working) onDismiss() }) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ficha do cliente", style = MaterialTheme.typography.titleLarge)
                Text(client.name, color = MaterialTheme.colorScheme.primary)
                if(client.phone.isNotBlank()) Text("Telefone: "+client.phone,
                    style=MaterialTheme.typography.bodySmall)
                if(client.email.isNotBlank()) Text("E-mail: "+client.email,
                    style=MaterialTheme.typography.bodySmall)
                if(client.birthDateIso.isNotBlank()) Text("Nascimento: "+client.birthDateIso,
                    style=MaterialTheme.typography.bodySmall)
                Column(Modifier.heightIn(max = 475.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (loading) Text("Carregando histórico local…")
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    snapshot?.let { data ->
                        val assessments = data.assessments.filter { it.clientId == client.id }
                        val forms = data.forms.filter { it.clientId == client.id }
                        val charges = data.charges.filter { it.clientId == client.id }
                        Text("Avaliações: " + assessments.size, style = MaterialTheme.typography.titleMedium)
                        assessments.sortedByDescending { it.dateIso }.take(4).forEach {
                            Text(it.dateIso + " · " + it.trainingLevel + " · " +
                                String.format(java.util.Locale.forLanguageTag("pt-BR"),"%.1f kg",it.weightKg))
                        }
                        Text("Formulários: " + forms.size, style = MaterialTheme.typography.titleMedium)
                        forms.sortedByDescending { it.dateIso }.take(4).forEach {
                            Text(it.dateIso + " · " +
                                (FormCatalog.find(it.templateId)?.title ?: "Modelo indisponível"))
                        }
                        val appointments = data.operations.appointments.filter { it.clientId == client.id }
                        val goals = data.operations.goals.filter { it.clientId == client.id }
                        Text("Agenda: " + appointments.size + " agendamentos",
                            style = MaterialTheme.typography.titleMedium)
                        appointments.sortedByDescending { it.startIso }.take(3).forEach {
                            Text(it.startIso.replace('T',' ')+" · "+it.service+" · "+it.status)
                        }
                        Text("Metas: " + goals.size + " cadastradas",
                            style = MaterialTheme.typography.titleMedium)
                        goals.filterNot { it.completed }.take(3).forEach {
                            Text(it.title+" · "+br.com.evolufisico.android.ops.OperationalRules.progress(it)+"%")
                        }
                        val notes = data.operations.notes.filter { it.clientId == client.id }
                        Text("Anotações: " + notes.size, style = MaterialTheme.typography.titleMedium)
                        notes.sortedWith(compareByDescending<br.com.evolufisico.android.ops.ClientNote> {
                            it.pinned
                        }.thenByDescending { it.dateIso }).take(3).forEach {
                            Text(it.dateIso+" · "+it.category+" · "+it.title)
                        }
                        Text("Cobranças: " + charges.size, style = MaterialTheme.typography.titleMedium)
                        Text("Pendentes: " + charges.count { !it.isReceived } +
                            " · Recebidas: " + charges.count { it.isReceived })
                        Text("Valor a receber: " + java.text.NumberFormat
                            .getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR"))
                            .format(charges.filterNot { it.isReceived }.sumOf { it.amountCents } / 100.0))
                        Text("Linha do tempo recente", style = MaterialTheme.typography.titleMedium)
                        val timeline = br.com.evolufisico.android.timeline.ClientTimeline
                            .build(data, client.id)
                        if (timeline.isEmpty()) Text("Sem atividades registradas.")
                        timeline.take(8).forEach { entry ->
                            Text(entry.dateIso + " · " + entry.kind.label + " · " + entry.title,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Resumo descritivo, sem inferências clínicas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedButton(enabled = !working, modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onDocumentFlow(true)
                                pdfLauncher.launch("EvoluFisico_Ficha_"+
                                    LocalDate.now().toString() + ".pdf")
                            }) { Text("Exportar ficha em PDF") }
                        Text("O PDF exportado não é criptografado. Guarde em local seguro.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    resultMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, enabled = !working) { Text("Fechar") }
                }
            }
        }
    }
}
