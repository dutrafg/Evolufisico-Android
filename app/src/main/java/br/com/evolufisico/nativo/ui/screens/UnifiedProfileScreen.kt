package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.domain.Financial
import br.com.evolufisico.nativo.domain.Metrics
import java.time.LocalDate

/** Perfil 3.0 reúne o histórico disponível e abre cada fluxo no seu módulo nativo. */
@Composable
fun UnifiedProfileScreen(
    repository: AppRepository,
    initialStudentId: String = "",
    onOpenModule: (Int, String) -> Unit,
) = Screen {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable(initialStudentId) { mutableStateOf(initialStudentId) }
    var section by rememberSaveable { mutableStateOf("Visão geral") }
    val all = repository.evaluations.toList()
    val profiles = br.com.evolufisico.nativo.domain.WorkspaceRules.latest(all)
        .filter { br.com.evolufisico.nativo.domain.WorkspaceRules.search(it.name, query) }
        .sortedWith(compareBy<Evaluation> { br.com.evolufisico.nativo.domain.HtmlParity.sortName(it.name) }.thenBy { it.studentId })
    val student = profiles.firstOrNull { it.studentId == selectedId }

    Heading("Perfil Unificado", "Cadastro, avaliações, evolução e financeiro do aluno em uma ficha.")
    ActionButtons {
        OutlinedButton(onClick = { onOpenModule(1, selectedId) }) { Text("Nova avaliação") }
        OutlinedButton(onClick = { onOpenModule(4, selectedId) }) { Text("Formulários") }
    }
    Field("Pesquisar aluno", query, { query = it })
    if (selectedId.isBlank() || student == null) {
        Text("Alunos com avaliação registrada · ${profiles.size}", style = MaterialTheme.typography.titleMedium)
        if (profiles.isEmpty()) DataCard("Nenhum aluno encontrado", "Cadastre uma avaliação para iniciar o perfil do aluno.") {
            TextButton(onClick = { onOpenModule(1, "") }) { Text("Cadastrar pelo módulo Avaliações") }
        }
        profiles.forEach { profile ->
            val count = all.count { it.studentId == profile.studentId }
            DataCard(profile.name, "$count avaliações · último registro em ${profile.date}") {
                TextButton(onClick = { selectedId = profile.studentId; section = "Visão geral" }) { Text("Abrir perfil") }
                TextButton(onClick = { onOpenModule(1, profile.studentId) }) { Text("Avaliações") }
            }
        }
    } else {
        TextButton(onClick = { selectedId = "" }) { Text("← Todos os alunos") }
        val history = all.filter { it.studentId == selectedId }.sortedByDescending { it.date }
        val charges = repository.charges.filter { it.studentId == selectedId || (it.studentId.isBlank() && it.student.equals(student.name, true)) }
        val intakes = repository.intakes.filter { it.studentId == selectedId || (it.studentId.isBlank() && it.student.equals(student.name, true)) }
        DataCard(student.name, listOf(student.email, student.phone, "${history.size} avaliações", if (intakes.isNotEmpty()) "${intakes.size} formulários" else "Sem formulários").filter(String::isNotBlank).joinToString(" · ")) {
            TextButton(onClick = { onOpenModule(1, selectedId) }) { Text("Abrir avaliação") }
        }
        PillChoices(listOf("Visão geral", "Avaliações", "Evolução", "Financeiro"), section) { section = it }
        when (section) {
            "Avaliações" -> {
                TextButton(onClick = { onOpenModule(1, selectedId) }) { Text("Nova avaliação") }
                history.forEach { e -> DataCard(e.date, "Peso ${Metrics.oneDecimal(e.weightKg)} kg · altura ${Metrics.oneDecimal(e.heightCm)} cm") {
                    TextButton(onClick = { onOpenModule(1, selectedId) }) { Text("Abrir histórico") }
                } }
            }
            "Evolução" -> {
                Text("Comparação entre datas do mesmo aluno. Os indicadores são descritivos e não representam diagnóstico.", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { onOpenModule(2, selectedId) }) { Text("Abrir evolução completa") }
                history.take(5).forEach { e -> DataCard(e.date, "Peso ${Metrics.oneDecimal(e.weightKg)} kg · gordura ${Metrics.oneDecimal(e.bodyFatPercent)}%") }
            }
            "Financeiro" -> {
                val open = charges.filter { !it.paid && !it.exempt }.sumOf { Financial.billable(it.amountCents, it.discountCents, it.exempt) }
                DataCard("Em aberto", money(open))
                charges.sortedByDescending { it.dueDate }.take(8).forEach { c -> DataCard(c.service, "${c.dueDate} · ${money(Financial.billable(c.amountCents, c.discountCents, c.exempt))} · ${if (c.paid) "Recebido" else "Pendente"}") }
                TextButton(onClick = { onOpenModule(6, selectedId) }) { Text("Abrir cobranças") }
            }
            else -> {
                DataCard("Última avaliação", student.date, actions = {})
                DataCard("Próximo retorno", student.nextAssessment.ifBlank { "Ainda não informado" })
                DataCard("Formulários", if (intakes.isEmpty()) "Nenhum formulário vinculado." else "${intakes.size} registro(s) vinculados.") {
                    TextButton(onClick = { onOpenModule(4, selectedId) }) { Text("Abrir formulários") }
                }
                TextButton(onClick = { onOpenModule(2, selectedId) }) { Text("Ver evolução") }
                TextButton(onClick = { section = "Financeiro" }) { Text("Ver financeiro") }
            }
        }
    }
}
