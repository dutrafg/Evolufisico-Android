package br.com.evolufisico.android

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.forms.AnswerKind
import br.com.evolufisico.android.forms.EncryptedFormStore
import br.com.evolufisico.android.forms.FormCatalog
import br.com.evolufisico.android.forms.FormResponse
import br.com.evolufisico.android.forms.FormRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FormsScreen(
    clients: List<Client>, store: EncryptedFormStore, onDocumentFlow: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedId by remember { mutableStateOf(clients.firstOrNull()?.id.orEmpty()) }
    var records by remember { mutableStateOf<List<FormResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var fatal by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<FormResponse?>(null) }
    var editingOpen by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<FormResponse?>(null) }
    var pendingExport by remember { mutableStateOf<FormResponse?>(null) }
    var choosingClient by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todos") }

    LaunchedEffect(store) {
        val result = withContext(Dispatchers.IO) { runCatching { store.load() } }
        result.onSuccess { records = it }.onFailure {
            fatal = "Não foi possível abrir os formulários criptografados. Dados preservados."
        }
        loading = false
    }
    LaunchedEffect(clients) {
        if (clients.isNotEmpty() && clients.none { it.id == selectedId }) selectedId = clients.first().id
    }

    fun persist(next: List<FormResponse>) {
        if (saving || loading || fatal != null) return
        saving = true
        scope.launch {
            val saved = withContext(Dispatchers.IO) {
                runCatching { store.save(next, clients.map { it.id }.toSet()) }
            }
            saved.onSuccess {
                records = next
                status = "Formulários salvos no banco criptografado."
            }.onFailure {
                fatal = "Erro ao salvar formulários. Dados anteriores preservados: " +
                    (it.localizedMessage ?: "Erro de escrita")
            }
            saving = false
        }
    }

    val exportPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val record = pendingExport
        pendingExport = null
        if (uri != null && record != null && !saving) {
            saving = true
            scope.launch {
                val client = clients.firstOrNull { it.id == record.clientId }
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri,"w")?.use {
                            FormPdfReport.write(it, client?.name ?: "Cliente", record)
                        } ?: error("Não foi possível gravar PDF.")
                    }
                }
                status = result.fold(
                    onSuccess = { "PDF exportado no local escolhido." },
                    onFailure = { "Falha ao exportar PDF: " + (it.localizedMessage ?: "Erro de escrita.") }
                )
                saving = false
            }
        }
    }

    if (loading) { FormNotice("Carregando formulários..."); return }
    if (fatal != null) { FormNotice(fatal!!); return }
    if (clients.isEmpty()) { FormNotice("Cadastre um cliente para preencher formulários."); return }

    val visible = records.filter { record ->
        val model = FormCatalog.find(record.templateId)
        record.clientId == selectedId &&
            (filter == "Todos" || (filter == "Anamneses" && record.templateId.startsWith("anamnese")) ||
                (filter == "Outros" && !record.templateId.startsWith("anamnese"))) &&
            (query.isBlank() || (model?.title ?: "").contains(query.trim(),ignoreCase = true) ||
                record.dateIso.contains(query.trim()))
    }.sortedWith(compareByDescending<FormResponse> { it.dateIso }.thenByDescending { it.id })

    LazyColumn(modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Formulários", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Modelos e respostas individuais vinculados ao perfil do cliente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Box {
                OutlinedButton(onClick = { choosingClient = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cliente: " + (clients.firstOrNull { it.id == selectedId }?.name ?: "Selecione"))
                }
                DropdownMenu(expanded = choosingClient, onDismissRequest = { choosingClient = false }) {
                    clients.forEach { c ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = {
                            selectedId = c.id; choosingClient = false
                        })
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it.take(80) },
                label = { Text("Buscar modelo ou data (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Todos", "Anamneses", "Outros").forEach { choice ->
                    FilterChip(selected = choice == filter, onClick = { filter = choice },
                        label = { Text(choice) })
                }
            }
        }
        item {
            Button(modifier = Modifier.fillMaxWidth(), enabled = !saving, onClick = {
                editing = null
                editingOpen = true
            }) { Text("Novo formulário") }
            status?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            Text("Histórico filtrado · " + visible.size + " registros",
                style = MaterialTheme.typography.titleMedium)
        }
        if (visible.isEmpty()) {
            item { Text("Nenhum formulário preenchido para este cliente.") }
        }
        items(visible, key = { it.id }) { record ->
            val template = FormCatalog.find(record.templateId)
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(template?.title ?: "Modelo indisponível",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(datePt(record.dateIso), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(record.answers.values.count { it.isNotBlank() }.toString() + " resposta(s)",
                        style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        TextButton(enabled = !saving, onClick = { editing = record; editingOpen = true }) {
                            Text("Editar")
                        }
                        TextButton(enabled = !saving, onClick = {
                            pendingExport = record
                            onDocumentFlow(true)
                            exportPicker.launch("EvoluFisico_Formulario_" + record.dateIso + ".pdf")
                        }) { Text("PDF") }
                        TextButton(enabled = !saving, onClick = { deleting = record }) {
                            Text("Excluir")
                        }
                    }
                }
            }
        }
        item {
            Text("Documentos exportados em PDF não possuem criptografia. " +
                "Armazene com segurança. As perguntas são informativas e não substituem diagnóstico.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (editingOpen) FormEditor(
        clientId = selectedId, existing = editing, knownClients = clients.map { it.id }.toSet(),
        onDismiss = { editingOpen = false },
        onSave = { response ->
            val next = if (editing == null) records + response
                else records.map { if (it.id == response.id) response else it }
            persist(next)
            editingOpen = false
        }
    )
    deleting?.let { target ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Excluir formulário?") },
            text = { Text("A exclusão removerá este formulário apenas deste dispositivo. Confirme.") },
            confirmButton = {
                Button(onClick = {
                    persist(records.filterNot { it.id == target.id })
                    deleting = null
                }) { Text("Excluir definitivamente") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FormEditor(
    clientId: String, existing: FormResponse?, knownClients: Set<String>,
    onDismiss: () -> Unit, onSave: (FormResponse) -> Unit
) {
    var templateId by remember(existing?.id) {
        mutableStateOf(existing?.templateId ?: FormCatalog.templates.first().id)
    }
    var date by remember(existing?.id) { mutableStateOf(existing?.dateIso ?: LocalDate.now().toString()) }
    val answers = remember(existing?.id) {
        mutableStateMapOf<String,String>().apply { existing?.answers?.let { putAll(it) } }
    }
    var options by remember { mutableStateOf(false) }
    var problem by remember { mutableStateOf<String?>(null) }
    val template = FormCatalog.find(templateId) ?: FormCatalog.templates.first()

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (existing == null) "Novo formulário" else "Editar formulário",
                    style = MaterialTheme.typography.titleLarge)
                Column(Modifier.heightIn(max = 540.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (existing == null) {
                        Box {
                            OutlinedButton(onClick = { options = true },
                                modifier = Modifier.fillMaxWidth()) { Text(template.title) }
                            DropdownMenu(expanded = options, onDismissRequest = { options = false }) {
                                FormCatalog.templates.forEach { t ->
                                    DropdownMenuItem(text = { Text(t.title) }, onClick = {
                                        templateId = t.id; answers.clear(); problem = null; options = false
                                    })
                                }
                            }
                        }
                    } else {
                        Text(template.title, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(template.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = date, onValueChange = { date = it.take(10) },
                        label = { Text("Data (AAAA-MM-DD)") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth())
                    template.questions.forEach { q ->
                        Text(q.title + if (q.required) " *" else "",
                            style = MaterialTheme.typography.titleSmall)
                        if (q.kind == AnswerKind.YES_NO) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Sim", "Não").forEach { option ->
                                    androidx.compose.material3.FilterChip(
                                        selected = answers[q.id] == option,
                                        onClick = { answers[q.id] = option },
                                        label = { Text(option) }
                                    )
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = answers[q.id].orEmpty(),
                                onValueChange = { answers[q.id] = it.take(700) },
                                label = { Text(q.example) },
                                minLines = 2, maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    problem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Text("* Obrigatório. Exemplos não são respostas reais.",
                        style = MaterialTheme.typography.bodySmall)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = {
                        val record = FormResponse(
                            id = existing?.id ?: java.util.UUID.randomUUID().toString(),
                            clientId = existing?.clientId ?: clientId, templateId = template.id,
                            dateIso = date.trim(),
                            answers = answers.toMap().mapValues { it.value.trim() }
                        )
                        val error = FormRules.validate(record,knownClients)
                        if (error != null) problem = error else onSave(record)
                    }) { Text("Salvar") }
                }
            }
        }
    }
}
@Composable
private fun FormNotice(message: String) {
    Column(Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center) { Text(message) }
}
private fun datePt(input: String) = runCatching {
    LocalDate.parse(input).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")))
}.getOrElse { input }
