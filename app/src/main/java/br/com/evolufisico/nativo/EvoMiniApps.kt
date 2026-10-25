package br.com.evolufisico.nativo

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.Assessment
import br.com.evolufisico.nativo.data.AssessmentMath
import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.ChargeRecord
import br.com.evolufisico.nativo.data.EfiaScientificEngines
import br.com.evolufisico.nativo.data.IntakeForm
import br.com.evolufisico.nativo.data.Student
import br.com.evolufisico.nativo.data.TaskRecord
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

internal data class MiniApp(val id: String, val title: String, val category: String, val description: String)
internal object EvoMiniCatalog {
    val entries = listOf(
        MiniApp("list", "Minha Lista", "Rotina", "Concluir e editar tarefas sem abrir outro módulo."),
        MiniApp("agenda", "Minha Agenda", "Rotina", "Consultar e editar próximos atendimentos."),
        MiniApp("charges", "Recebimentos", "Financeiro", "Marcar cobranças pendentes como recebidas."),
        MiniApp("students", "Meus Alunos", "Alunos", "Acessar os perfis dos alunos cadastrados."),
        MiniApp("rechecks", "Reavaliações", "Alunos", "Acompanhar alunos com avaliações em atraso."),
        MiniApp("assessments", "Últimas Avaliações", "Avaliações", "Abrir o histórico das avaliações recentes."),
        MiniApp("bmi", "IMC Rápido", "Ferramentas", "Calcular índice de massa corporal sem sair do Início."),
        MiniApp("forms", "Anamneses", "Avaliações", "Editar formulários registrados."),
        MiniApp("evolution", "Evolução Rápida", "Avaliações", "Comparar mudanças documentadas no peso."),
        MiniApp("efia", "Auditoria EFI", "Inteligência", "Consultar disponibilidade da análise EF78–EF82.")
    )
    val categories = listOf("Todas") + entries.map { it.category }.distinct()
    fun validIds(ids: Set<String>): Set<String> = ids.intersect(entries.map { it.id }.toSet())
}

@Composable
internal fun EvoStoreScreen(installed: Set<String>, onToggle: (String) -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("Todas") }
    val visible = EvoMiniCatalog.entries.filter {
        (category == "Todas" || it.category == category) &&
            (search.isBlank() || it.title.contains(search, true) || it.category.contains(search, true) ||
                it.description.contains(search, true))
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("EvoStore", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Miniaplicativos funcionais para a dashboard. Gerencie as instalações aqui, sem botões de exclusão nos cards.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(installed.size.toString() + " miniaplicativo(s) instalado(s)")
            OutlinedTextField(search, { search = it }, label = { Text("Pesquisar na loja") },
                singleLine = true, modifier = Modifier.fillMaxWidth())
        }
        item {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EvoMiniCatalog.categories.forEach { option ->
                    if (category == option) Button(onClick = { category = option }) { Text(option) }
                    else OutlinedButton(onClick = { category = option }) { Text(option) }
                }
            }
        }
        if (visible.isEmpty()) item { Text("Nenhum miniaplicativo encontrado.") }
        items(visible, key = { it.id }) { app ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(app.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(app.category, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    Text(app.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (app.id in installed) OutlinedButton(onClick = { onToggle(app.id) }) { Text("Desinstalar") }
                    else Button(onClick = { onToggle(app.id) }) { Text("Instalar no Início") }
                }
            }
        }
    }
}

@Composable
internal fun EvoMiniWidgets(
    installed: Set<String>,
    students: List<Student>, assessments: List<Assessment>,
    appointments: List<Appointment>, tasks: List<TaskRecord>,
    charges: List<ChargeRecord>, forms: List<IntakeForm>,
    maskFinancial: Boolean, reevaluationDays: Int,
    onAddTask: () -> Unit, onEditTask: (TaskRecord) -> Unit, onToggleTask: (Long, Boolean) -> Unit,
    onAddAppointment: () -> Unit, onEditAppointment: (Appointment) -> Unit,
    onReceiveCharge: (Long) -> Unit, onOpenStudent: (Long) -> Unit,
    onEditForm: (IntakeForm) -> Unit
) {
    if (installed.isEmpty()) return
    val today = LocalDate.now()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Miniaplicativos instalados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        EvoMiniCatalog.entries.filter { it.id in installed }.forEach { app ->
            Card(Modifier.fillMaxWidth().height(270.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(app.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(app.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    when (app.id) {
                        "list" -> {
                            val pending = tasks.filterNot { it.completed }.sortedBy { it.dueDate }.take(3)
                            if (pending.isEmpty()) Text("Nenhuma tarefa pendente.")
                            pending.forEach { task ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = false, onCheckedChange = { onToggleTask(task.id, it) })
                                    Text(task.title, Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onEditTask(task) }) { Text("Editar") }
                                }
                            }
                            Button(onClick = onAddTask) { Text("Nova tarefa") }
                        }
                        "agenda" -> {
                            val next = appointments.filter { it.date >= today.toString() }
                                .sortedWith(compareBy<Appointment> { it.date }.thenBy { it.time }).take(3)
                            if (next.isEmpty()) Text("Nenhum compromisso futuro.")
                            next.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.date + " · " + item.time + " · " + item.title,
                                        Modifier.weight(1f), maxLines = 2)
                                    TextButton(onClick = { onEditAppointment(item) }) { Text("Editar") }
                                }
                            }
                            Button(onClick = onAddAppointment) { Text("Novo atendimento") }
                        }
                        "charges" -> {
                            val pending = charges.filter { it.status == "Pendente" }.sortedBy { it.dueDate }.take(3)
                            if (pending.isEmpty()) Text("Nenhum recebimento pendente.")
                            pending.forEach { charge ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val money = if (maskFinancial) "Valor oculto"
                                        else NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(charge.amount)
                                    Text(charge.title + " · " + money, Modifier.weight(1f), maxLines = 2)
                                    TextButton(onClick = { onReceiveCharge(charge.id) }) { Text("Receber") }
                                }
                            }
                        }
                        "students" -> {
                            if (students.isEmpty()) Text("Nenhum aluno cadastrado.")
                            students.sortedBy { it.name }.take(4).forEach { student ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(student.name, Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onOpenStudent(student.id) }) { Text("Abrir") }
                                }
                            }
                        }
                        "rechecks" -> {
                            val latest = assessments.groupBy { it.studentId }
                                .mapValues { (_, list) -> list.maxByOrNull { it.date } }
                            val due = students.mapNotNull { student ->
                                val date = latest[student.id]?.date ?: return@mapNotNull null
                                val days = runCatching {
                                    ChronoUnit.DAYS.between(LocalDate.parse(date), today)
                                }.getOrNull() ?: return@mapNotNull null
                                if (days >= reevaluationDays) student to days else null
                            }.sortedByDescending { it.second }.take(3)
                            if (due.isEmpty()) Text("Nenhuma reavaliação vencida.")
                            due.forEach { (student, days) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(student.name + " · " + days + " dias",
                                        Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onOpenStudent(student.id) }) { Text("Abrir") }
                                }
                            }
                        }
                        "assessments" -> {
                            val recent = assessments.sortedWith(
                                compareByDescending<Assessment> { it.date }.thenByDescending { it.id }).take(3)
                            if (recent.isEmpty()) Text("Nenhuma avaliação cadastrada.")
                            recent.forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val name = students.firstOrNull { it.id == item.studentId }?.name ?: "Aluno"
                                    Text(name + " · " + item.date, Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onOpenStudent(item.studentId) }) { Text("Abrir") }
                                }
                            }
                        }
                        "bmi" -> {
                            var weight by rememberSaveable { mutableStateOf("") }
                            var height by rememberSaveable { mutableStateOf("") }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(weight, { weight = it }, Modifier.weight(1f),
                                    label = { Text("Peso kg") }, singleLine = true)
                                OutlinedTextField(height, { height = it }, Modifier.weight(1f),
                                    label = { Text("Altura cm") }, singleLine = true)
                            }
                            val result = AssessmentMath.bmi(
                                AssessmentMath.parseNumber(weight) ?: 0.0,
                                AssessmentMath.parseNumber(height) ?: 0.0)
                            Text(if (result == null) "Informe peso e altura válidos."
                                else "IMC: " + String.format(Locale("pt", "BR"), "%.1f", result))
                            Text("Indicador de triagem; não constitui diagnóstico.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                        "forms" -> {
                            if (forms.isEmpty()) Text("Nenhuma anamnese registrada.")
                            forms.sortedByDescending { it.date }.take(3).forEach { form ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(form.template + " · " + form.date,
                                        Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onEditForm(form) }) { Text("Editar") }
                                }
                            }
                        }
                        "evolution" -> {
                            val changes = assessments.groupBy { it.studentId }.mapNotNull { (id, rows) ->
                                val ordered = rows.sortedBy { it.date }
                                if (ordered.size < 2) null else Triple(id, ordered.first(), ordered.last())
                            }.take(3)
                            if (changes.isEmpty()) Text("São necessárias duas avaliações do mesmo aluno.")
                            changes.forEach { (id, first, last) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val name = students.firstOrNull { it.id == id }?.name ?: "Aluno"
                                    Text(name + " · " + String.format(Locale("pt", "BR"),
                                        "%+.1f kg", last.weightKg - first.weightKg),
                                        Modifier.weight(1f), maxLines = 1)
                                    TextButton(onClick = { onOpenStudent(id) }) { Text("Abrir") }
                                }
                            }
                        }
                        "efia" -> {
                            val candidate = assessments.groupBy { it.studentId }
                                .mapNotNull { (id, rows) ->
                                    val dates = rows.distinctBy { it.date }.sortedBy { it.date }.takeLast(4)
                                    if (dates.size < 3) null else id to EfiaScientificEngines.analyze(dates)
                                }.take(3)
                            if (candidate.isEmpty()) Text("Registre três ou quatro avaliações para auditar.")
                            candidate.forEach { (id, audit) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text((students.firstOrNull { it.id == id }?.name ?: "Aluno") +
                                        (if (audit.eligible) " · auditoria disponível"
                                        else " · intervalo insuficiente"),
                                        Modifier.weight(1f), maxLines = 2)
                                    TextButton(onClick = { onOpenStudent(id) }) { Text("Abrir") }
                                }
                            }
                            Text("Análise exploratória, sem diagnóstico.",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
