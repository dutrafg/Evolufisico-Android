package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.AssessmentRules
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Histórico de avaliações por cliente, persistido somente depois de AES-GCM + AtomicFile.
 * Não grava dados de anamnese em logs ou preferências não cifradas.
 */
@Composable
fun AssessmentsScreen(clients: List<Client>,store:EncryptedAssessmentStore,
    quickCreateId:Int=0,onQuickConsumed:()->Unit={}
) {
    val assessments = remember { mutableStateListOf<Assessment>() }
    var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id.orEmpty()) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var warning by remember { mutableStateOf<String?>(null) }
    var editorOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Assessment?>(null) }
    var deleting by remember { mutableStateOf<Assessment?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(store) {
        val result = withContext(Dispatchers.IO) { runCatching { store.load() } }
        result.onSuccess { assessments.addAll(it) }
            .onFailure { error = "Falha ao abrir avaliações cifradas. Os arquivos não foram apagados." }
        loading = false
    }
    LaunchedEffect(clients) {
        if (clients.isNotEmpty() && clients.none { it.id == selectedClientId })
            selectedClientId = clients.first().id
    }
    fun persist(next: List<Assessment>) {
        if (saving || loading || error != null) return
        saving = true
        scope.launch {
            val result = withContext(Dispatchers.IO) { runCatching { store.save(next) } }
            result.onSuccess {
                assessments.clear()
                assessments.addAll(next)
                warning = "Avaliações salvas no armazenamento criptografado."
            }.onFailure {
                error = "Erro ao salvar avaliações. Nada foi alterado na lista em memória."
            }
            saving = false
        }
    }

    LaunchedEffect(quickCreateId,loading,error,clients.size) {
        if(quickCreateId>0 && !loading) {
            if(error==null && clients.isNotEmpty()) {
                editing=null
                editorOpen=true
            }
            onQuickConsumed()
        }
    }
    if (loading) {
        AssessmentNotice("Carregando avaliações...")
        return
    }
    error?.let {
        AssessmentNotice(it + " Não cadastre novas avaliações antes de resolver este erro.")
        return
    }
    if (clients.isEmpty()) {
        AssessmentNotice("Cadastre um cliente antes de realizar uma avaliação.")
        return
    }
    val visible = assessments.filter { it.clientId == selectedClientId }
        .sortedWith(compareByDescending<Assessment> { it.dateIso }.thenByDescending { it.id })

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Avaliação física e anamnese", style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold)
        ClientChooser(clients, selectedClientId, onSelect = { selectedClientId = it })
        Button(
            enabled = !saving,
            onClick = { editing = null; editorOpen = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Nova avaliação") }
        warning?.let {
            Text(it, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary)
        }
        Text("Histórico • ${visible.size} avaliação(ões)",
            style = MaterialTheme.typography.titleMedium)
        if (visible.isEmpty()) {
            Text("Nenhuma avaliação para este cliente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visible, key = { it.id }) { record ->
                    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val displayDate = runCatching {
                                LocalDate.parse(record.dateIso)
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            }.getOrElse { record.dateIso }
                            Text(displayDate, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold)
                            Text("Objetivo: " + record.goal.ifEmpty { "Não informado" })
                            Text("Nível: ${record.trainingLevel} • Frequência: ${record.weeklyFrequency}x/semana")
                            Text("Peso: ${number(record.weightKg)} kg • Altura: ${number(record.heightCm)} cm")
                            Text("IMC: ${number(AssessmentRules.bmi(record.weightKg, record.heightCm))}",
                                color = MaterialTheme.colorScheme.primary)
                            AssessmentRules.waistHipRatio(record.waistCm, record.hipCm)?.let {
                                Text("Relação cintura/quadril: ${number(it, 2)}")
                            }
                            if (record.limitations.isNotBlank()) {
                                Text("Anamnese: ${record.limitations}")
                            }
                            listOf("Cintura" to record.waistCm, "Quadril" to record.hipCm,
                                "Braço" to record.armCm, "Coxa" to record.thighCm)
                                .filter { it.second != null }
                                .forEach { (label, value) ->
                                    Text("$label: ${number(value!!)} cm",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                TextButton(enabled = !saving, onClick = {
                                    editing = record
                                    editorOpen = true
                                }) { Text("Editar") }
                                TextButton(enabled = !saving, onClick = { deleting = record }) {
                                    Text("Excluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        AssessmentEditor(
            clientId = selectedClientId,
            existing = editing,
            knownClientIds = clients.map { it.id }.toSet(),
            onDismiss = { editorOpen = false },
            onSave = { item ->
                val next = if (editing == null) assessments.toList() + item
                    else assessments.map { if (it.id == item.id) item else it }
                persist(next)
                editorOpen = false
            }
        )
    }
    deleting?.let { record ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Excluir avaliação?") },
            text = { Text("A avaliação selecionada será removida permanentemente deste dispositivo.") },
            confirmButton = { Button(onClick = {
                persist(assessments.filterNot { it.id == record.id })
                deleting = null
            }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ClientChooser(clients: List<Client>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Cliente: " + (clients.firstOrNull { it.id == selected }?.name ?: "Selecione"))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            clients.forEach { client ->
                DropdownMenuItem(
                    text = { Text(client.name) },
                    onClick = { onSelect(client.id); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun AssessmentNotice(message: String) {
    Column(Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AssessmentEditor(
    clientId: String,
    existing: Assessment?,
    knownClientIds: Set<String>,
    onDismiss: () -> Unit,
    onSave: (Assessment) -> Unit
) {
    var date by remember(existing?.id) { mutableStateOf(existing?.dateIso ?: LocalDate.now().toString()) }
    var level by remember(existing?.id) { mutableStateOf(existing?.trainingLevel ?: "Iniciante") }
    var goal by remember(existing?.id) { mutableStateOf(existing?.goal ?: "") }
    var limitations by remember(existing?.id) { mutableStateOf(existing?.limitations ?: "") }
    var frequency by remember(existing?.id) { mutableStateOf((existing?.weeklyFrequency ?: 0).toString()) }
    var weight by remember(existing?.id) { mutableStateOf(existing?.weightKg?.toString() ?: "") }
    var height by remember(existing?.id) { mutableStateOf(existing?.heightCm?.toString() ?: "") }
    var waist by remember(existing?.id) { mutableStateOf(existing?.waistCm?.toString() ?: "") }
    var hip by remember(existing?.id) { mutableStateOf(existing?.hipCm?.toString() ?: "") }
    var arm by remember(existing?.id) { mutableStateOf(existing?.armCm?.toString() ?: "") }
    var thigh by remember(existing?.id) { mutableStateOf(existing?.thighCm?.toString() ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    fun build(): Assessment {
        val client = existing?.clientId ?: clientId
        return Assessment(
            id = existing?.id ?: java.util.UUID.randomUUID().toString(),
            clientId = client,
            dateIso = date.trim(),
            trainingLevel = level,
            goal = goal.trim(),
            limitations = limitations.trim(),
            weeklyFrequency = frequency.trim().toIntOrNull() ?: -1,
            weightKg = AssessmentRules.parseNumber(weight, true) ?: Double.NaN,
            heightCm = AssessmentRules.parseNumber(height, true) ?: Double.NaN,
            waistCm = AssessmentRules.parseNumber(waist, false),
            hipCm = AssessmentRules.parseNumber(hip, false),
            armCm = AssessmentRules.parseNumber(arm, false),
            thighCm = AssessmentRules.parseNumber(thigh, false)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (existing == null) "Nova avaliação" else "Editar avaliação",
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Column(Modifier.heightIn(max = 540.dp).verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Anamnese", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                    OutlinedTextField(
                        value = date, onValueChange = { date = it }, singleLine = true,
                        label = { Text("Data (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth()
                    )
                    var menu by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { menu = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Nível de atividade: $level")
                        }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            AssessmentRules.levels.forEach {
                                DropdownMenuItem(text = { Text(it) },
                                    onClick = { level = it; menu = false })
                            }
                        }
                    }
                    OutlinedTextField(value = goal, onValueChange = { goal = it.take(200) },
                        label = { Text("Objetivo principal") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = frequency, onValueChange = { frequency = it },
                        singleLine = true, label = { Text("Treinos por semana (0 a 7)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = limitations,
                        onValueChange = { limitations = it.take(600) },
                        label = { Text("Anamnese: limitações, cuidados e observações") },
                        minLines = 2, maxLines = 5, modifier = Modifier.fillMaxWidth())
                    Text("Antropometria", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                    NumberField("Peso (kg) *", weight, { weight = it })
                    NumberField("Altura (cm) *", height, { height = it })
                    NumberField("Cintura (cm)", waist, { waist = it })
                    NumberField("Quadril (cm)", hip, { hip = it })
                    NumberField("Braço (cm)", arm, { arm = it })
                    NumberField("Coxa (cm)", thigh, { thigh = it })
                    val p = AssessmentRules.parseNumber(weight, true)
                    val h = AssessmentRules.parseNumber(height, true)
                    if (p != null && h != null && p.isFinite() && h.isFinite() && p > 0 && h > 0) {
                        Text("Prévia do IMC: ${number(AssessmentRules.bmi(p, h))}",
                            color = MaterialTheme.colorScheme.primary)
                    }
                    val rcq = AssessmentRules.waistHipRatio(
                        AssessmentRules.parseNumber(waist, false),
                        AssessmentRules.parseNumber(hip, false))
                    rcq?.let { Text("Prévia cintura/quadril: ${number(it, 2)}") }
                    Text("IMC e RCQ são indicadores descritivos, não diagnósticos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    validationError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(onClick = {
                        val record = build()
                        val issue = AssessmentRules.validate(record, knownClientIds)
                        if (issue == null) onSave(record) else validationError = issue
                    }) { Text("Salvar") }
                }
            }
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, update: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = update,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

private fun number(value: Double, decimals: Int = 1): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%.${decimals}f", value)
