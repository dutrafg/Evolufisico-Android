package br.com.evolufisico.nativo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.Assessment
import br.com.evolufisico.nativo.data.ChargeRecord
import br.com.evolufisico.nativo.data.IntakeForm
import br.com.evolufisico.nativo.data.Student
import br.com.evolufisico.nativo.data.TaskRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun AnalyticsScreen(students: List<Student>, assessments: List<Assessment>, onOpenStudent: (Long) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Análises", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Resumo descritivo dos dados registrados, sem inferir diagnóstico.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Critério de série", fontWeight = FontWeight.SemiBold)
                    Text("O resumo longitudinal requer pelo menos 3 avaliações em dias diferentes, distribuídas por 21 dias ou mais.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        if (students.isEmpty()) item { WorkflowEmpty("Sem alunos", "Cadastre um aluno e registre avaliações para iniciar o acompanhamento.") }
        items(students, key = { it.id }) { student ->
            val history = assessments.filter { it.studentId == student.id }.distinctBy { it.date }.sortedByDescending { it.date }
            val first = history.getOrNull(1)
            val latest = history.firstOrNull()
            val days = if (history.size >= 2) {
                val a = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(history.last().date)?.time }.getOrNull()
                val b = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(history.first().date)?.time }.getOrNull()
                if (a != null && b != null) ((b - a) / 86_400_000L).toInt() else 0
            } else 0
            val qualified = history.size >= 3 && days >= 21
            Card(onClick = { onOpenStudent(student.id) }, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(student.name, fontWeight = FontWeight.SemiBold)
                    if (latest == null) Text("Sem avaliações registradas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else {
                        Text("Última avaliação: " + displayNativeDate(latest.date) + " · " + formatNativeNumber(latest.weightKg) + " kg")
                        if (first != null) {
                            val delta = latest.weightKg - first.weightKg
                            Text("Variação de peso: " + (if (delta > 0) "+" else "") + formatNativeNumber(delta) + " kg entre as duas últimas avaliações.")
                        }
                        Text(if (qualified) "Série longitudinal disponível · " + history.size + " datas · " + days + " dias"
                            else "Histórico inicial · " + history.size + " data(s) · " + days + " dias", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

private fun formatNativeNumber(value: Double): String = String.format(Locale("pt","BR"), "%.1f", value)

@Composable
internal fun TaskScreen(
    tasks: List<TaskRecord>,
    onAdd: () -> Unit,
    onEdit: (TaskRecord) -> Unit,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Tarefas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Pendências e próximos passos do atendimento.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Button(onClick = onAdd) { Text("Nova tarefa") }
        }
        if (tasks.isEmpty()) item { WorkflowEmpty("Nenhuma tarefa", "Adicione lembretes para acompanhar retornos e pendências.") }
        items(tasks, key = { it.id }) { task ->
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = task.completed, onCheckedChange = { onToggle(task.id, it) })
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.SemiBold)
                        Text(displayNativeDate(task.dueDate), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!task.completed && task.dueDate < todayNativeIso()) {
                            Text("Atrasada", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Column {
                        TextButton(onClick = { onEdit(task) }) { Text("Editar") }
                        TextButton(onClick = { onDelete(task.id) }) { Text("Excluir") }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ChargeScreen(
    charges: List<ChargeRecord>,
    students: List<Student>,
    maskValues: Boolean,
    onAdd: () -> Unit,
    onReceive: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Cobranças", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Acompanhe valores recebidos e pendentes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Button(onClick = onAdd) { Text("Nova cobrança") }
        }
        if (charges.isEmpty()) item { WorkflowEmpty("Nenhuma cobrança", "Os valores ficam associados aos registros locais deste aparelho.") }
        items(charges, key = { it.id }) { charge ->
            val studentName = students.firstOrNull { it.id == charge.studentId }?.name ?: "Sem aluno vinculado"
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(charge.title, fontWeight = FontWeight.SemiBold)
                    Text(studentName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Vencimento: " + displayNativeDate(charge.dueDate), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (charge.status == "Pendente" && charge.dueDate < todayNativeIso()) {
                        Text("Vencida", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(if (maskValues) "Valor oculto" else "R$ " + String.format(Locale("pt","BR"), "%.2f", charge.amount), fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (charge.status == "Pendente") Button(onClick = { onReceive(charge.id) }) { Text("Receber") }
                        else Text("Recebido", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 10.dp))
                        TextButton(onClick = { onDelete(charge.id) }) { Text("Excluir") }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FormsScreen(
    forms: List<IntakeForm>,
    students: List<Student>,
    onAdd: () -> Unit,
    onEdit: (IntakeForm) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Formulários", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Anamneses locais vinculadas ao Perfil Unificado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Button(onClick = onAdd) { Text("Nova anamnese") }
        }
        if (forms.isEmpty()) item { WorkflowEmpty("Nenhum formulário salvo", "Escolha um modelo, registre as respostas e vincule ao aluno.") }
        items(forms, key = { it.id }) { form ->
            val name = students.firstOrNull { it.id == form.studentId }?.name ?: "Sem aluno vinculado"
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(form.template, fontWeight = FontWeight.SemiBold)
                    Text(name + " · " + displayNativeDate(form.date), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Respostas", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(form.responses, maxLines = 8)
                    Row {
                        TextButton(onClick = { onEdit(form) }) { Text("Editar") }
                        TextButton(onClick = { onDelete(form.id) }) { Text("Excluir") }
                    }
                }
            }
        }
    }
}

@Composable
internal fun TaskFormDialog(initial: TaskRecord?, onDismiss: () -> Unit, onSave: (Long?, String, String) -> Unit) {
    var title by rememberSaveable(initial?.id) { mutableStateOf(initial?.title.orEmpty()) }
    var date by rememberSaveable(initial?.id) { mutableStateOf(initial?.dueDate ?: todayNativeIso()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova tarefa" else "Editar tarefa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it; error = null }, label = { Text("Tarefa *") }, singleLine = true)
                OutlinedTextField(date, { date = it; error = null }, label = { Text("Data (AAAA-MM-DD)") }, singleLine = true)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = { TextButton(onClick = {
            if (title.isBlank()) error = "Informe a tarefa."
            else if (!validIsoDate(date)) error = "Informe uma data válida no formato AAAA-MM-DD."
            else onSave(initial?.id, title, date)
        }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
internal fun ChargeFormDialog(students: List<Student>, initialStudentId: Long? = null, onDismiss: () -> Unit, onSave: (Long?, String, Double, String, String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf(todayNativeIso()) }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedStudentId by rememberSaveable(initialStudentId) { mutableStateOf(initialStudentId) }
    val student = students.firstOrNull { it.id == selectedStudentId }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova cobrança") },
        text = {
            Column(Modifier.heightIn(max = 500.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StudentPicker(students, student, { selectedStudentId = it?.id })
                OutlinedTextField(title, { title = it; error = null }, label = { Text("Descrição *") }, singleLine = true)
                OutlinedTextField(amount, { amount = it; error = null }, label = { Text("Valor (R$) *") }, singleLine = true)
                OutlinedTextField(dueDate, { dueDate = it; error = null }, label = { Text("Vencimento (AAAA-MM-DD)") }, singleLine = true)
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, minLines = 2)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = { TextButton(onClick = {
            val parsed = amount.trim().replace(",", ".").toDoubleOrNull()
            if (title.isBlank() || parsed == null || parsed <= 0.0) error = "Informe descrição e valor maior que zero."
            else if (!validIsoDate(dueDate)) error = "Informe uma data válida."
            else onSave(student?.id, title, parsed, dueDate, notes)
        }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
internal fun IntakeFormDialog(
    students: List<Student>,
    initial: IntakeForm? = null,
    initialStudentId: Long? = null,
    onDismiss: () -> Unit,
    onSave: (Long?, Long?, String, String) -> Unit
) {
    val initialTemplate = if (initial == null || initial.template in formTemplates) initial?.template ?: formTemplates.first() else "Personalizado"
    var template by rememberSaveable(initial?.id) { mutableStateOf(initialTemplate) }
    var customTitle by rememberSaveable(initial?.id) { mutableStateOf(if (initialTemplate == "Personalizado") initial?.template.orEmpty() else "") }
    var answers by rememberSaveable(initial?.id) { mutableStateOf(initial?.responses.orEmpty()) }
    var selectedStudentId by rememberSaveable(initial?.id, initialStudentId) { mutableStateOf(initial?.studentId ?: initialStudentId) }
    val student = students.firstOrNull { it.id == selectedStudentId }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova anamnese" else "Editar anamnese") },
        text = {
            Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TemplatePicker(template, { template = it; error = null })
                if (template == "Personalizado") OutlinedTextField(customTitle, { customTitle = it }, label = { Text("Nome do formulário") }, singleLine = true)
                StudentPicker(students, student, { selectedStudentId = it?.id })
                Text(formQuestions[template] ?: "Registre as informações relevantes ao atendimento.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(answers, { answers = it; error = null }, label = { Text("Respostas *") }, minLines = 5)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = { TextButton(onClick = {
            val finalTemplate = if (template == "Personalizado" && customTitle.isNotBlank()) customTitle.trim() else template
            if (template == "Personalizado" && customTitle.isBlank()) error = "Informe o nome do formulário."
            else if (answers.isBlank()) error = "Preencha as respostas antes de salvar."
            else onSave(initial?.id, student?.id, finalTemplate, answers)
        }) { Text(if (initial == null) "Salvar" else "Salvar alterações") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun StudentPicker(students: List<Student>, selected: Student?, onSelect: (Student?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selected?.name ?: "Sem vínculo com aluno",
            onValueChange = {},
            readOnly = true,
            label = { Text("Aluno (opcional)") },
            trailingIcon = { TextButton(onClick = { expanded = true }) { Text("Escolher") } },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Sem vínculo com aluno") }, onClick = { onSelect(null); expanded = false })
            students.forEach { item ->
                DropdownMenuItem(text = { Text(item.name) }, onClick = { onSelect(item); expanded = false })
            }
        }
    }
}

@Composable
private fun TemplatePicker(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { Text("Modelo") }, trailingIcon = { TextButton(onClick = { expanded = true }) { Text("Alterar") } }, modifier = Modifier.fillMaxWidth())
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            formTemplates.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { onSelect(item); expanded = false }) }
        }
    }
}

@Composable
private fun WorkflowEmpty(title: String, body: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private val formTemplates = listOf(
    "Iniciante · Anamnese geral",
    "Iniciante · Histórico de saúde",
    "Iniciante · Hábitos e objetivos",
    "Intermediário · Rotina e treinamento",
    "Intermediário · Alimentação e sono",
    "Intermediário · Mobilidade e lesões",
    "Avançado · Anamnese completa",
    "Avançado · Acompanhamento físico",
    "Avançado · Reavaliação",
    "Personalizado"
)
private val formQuestions = mapOf(
    "Iniciante · Anamnese geral" to "Objetivo principal · Rotina diária · Atividade física atual · Preferências · Observações",
    "Iniciante · Histórico de saúde" to "Condições relatadas · Medicamentos · Alergias · Cirurgias · Restrições para exercício",
    "Iniciante · Hábitos e objetivos" to "Sono · Hidratação · Alimentação relatada · Hábitos de movimento · Objetivos",
    "Intermediário · Rotina e treinamento" to "Experiência · Frequência semanal · Duração · Equipamentos disponíveis · Preferências",
    "Intermediário · Alimentação e sono" to "Rotina alimentar relatada · Horários · Qualidade do sono · Hidratação · Dificuldades",
    "Intermediário · Mobilidade e lesões" to "Lesões anteriores · Dores relatadas · Limitações de movimento · Mobilidade · Cuidados",
    "Avançado · Anamnese completa" to "Histórico de saúde · Atividade física · Rotina · Objetivos · Restrições · Observações",
    "Avançado · Acompanhamento físico" to "Adesão · Resposta percebida · Barreiras · Mudanças de rotina · Próximos passos",
    "Avançado · Reavaliação" to "Objetivo inicial · Mudanças percebidas · Dificuldades · Ajustes necessários · Próximas metas",
    "Personalizado" to "Use este roteiro livre para registrar informações adicionais do atendimento."
)

private fun todayNativeIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())
private fun displayNativeDate(value: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(value)
    parsed?.let { SimpleDateFormat("dd/MM/yyyy", Locale("pt","BR")).format(it) } ?: value
}.getOrDefault(value)
private fun validIsoDate(value: String): Boolean = runCatching {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
    format.parse(value)?.let { format.format(it) == value } == true
}.getOrDefault(false)
