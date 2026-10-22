package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.Charge
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.data.Intake
import br.com.evolufisico.nativo.data.TaskItem
import java.time.LocalDate
import java.time.LocalTime

/** Local, deterministic QA for stored input. No clinical diagnosis and no server transmission. */
data class AuditFinding(val section: String, val recordId: String, val problem: String)
data class AuditReport(val recordCount: Int, val findings: List<AuditFinding>) {
    fun description(): String = buildString {
        append("Registros verificados: ").append(recordCount).append(". ")
        if (findings.isEmpty()) append("Nenhuma inconsistência identificada nas regras automáticas disponíveis.")
        else {
            append(findings.size).append(" ocorrência(s) para conferência:")
            findings.take(25).forEach { finding ->
                append("\n• ").append(finding.section).append(" (").append(finding.recordId.take(14))
                    .append("): ").append(finding.problem)
            }
            if (findings.size > 25) append("\n… e mais ").append(findings.size - 25).append(" ocorrência(s).")
        }
        append("\nEste diagnóstico não modifica dados e não determina aptidão clínica.")
    }
}
object IntegrityAuditor {
    fun run(evaluations: List<Evaluation>, charges: List<Charge>, intakes: List<Intake>,
            appointments: List<Appointment>, tasks: List<TaskItem>): AuditReport {
        val problems = mutableListOf<AuditFinding>()
        fun add(type: String, id: String, message: String) { problems.add(AuditFinding(type, id, message)) }
        fun isDate(text: String) = runCatching { LocalDate.parse(text) }.isSuccess
        fun <T> checkIds(type: String, list: List<T>, getId: (T) -> String) {
            val seen = mutableSetOf<String>()
            list.forEach { record ->
                val id = getId(record)
                if (id.isBlank()) add(type, "sem ID", "Identificador vazio")
                else if (!seen.add(id)) add(type, id, "Identificador duplicado na mesma categoria")
            }
        }
        checkIds("Avaliação", evaluations, Evaluation::id)
        checkIds("Cobrança", charges, Charge::id)
        checkIds("Formulário", intakes, Intake::id)
        checkIds("Agenda", appointments, Appointment::id)
        checkIds("Tarefa", tasks, TaskItem::id)
        val students = evaluations.map { it.studentId }.filter(String::isNotBlank).toSet()
        evaluations.forEach { e ->
            runCatching{RecordRules.validate(e)}.onFailure{add("Avaliação",e.id,it.message ?: "Registro inválido")}
            if (e.studentId.isBlank()) add("Avaliação", e.id, "Sem identificador de aluno")
            if (e.name.isBlank()) add("Avaliação", e.id, "Nome não preenchido")
            if (!isDate(e.date)) add("Avaliação", e.id, "Data inválida")
            if (e.nextAssessment.isNotBlank() && !isDate(e.nextAssessment))
                add("Avaliação", e.id, "Data da próxima avaliação inválida")
            if (e.age != null && e.age !in 1..120) add("Avaliação", e.id, "Idade fora dos limites de entrada")
            if (e.goalWeightKg != null && (e.goalWeightBaselineKg == null ||
                    !e.goalWeightKg.isFinite() || e.goalWeightKg !in 20.0..400.0 ||
                    e.goalWeightBaselineKg !in 20.0..400.0 ||
                    kotlin.math.abs(e.goalWeightKg - e.goalWeightBaselineKg) < 0.01))
                add("Meta", e.id, "Meta de peso ou linha de base inválida")
            if (e.goalBodyFatPercent != null && (e.goalBodyFatBaselinePercent == null ||
                    !e.goalBodyFatPercent.isFinite() || e.goalBodyFatPercent !in 0.0..100.0 ||
                    e.goalBodyFatBaselinePercent !in 0.0..100.0 ||
                    kotlin.math.abs(e.goalBodyFatPercent - e.goalBodyFatBaselinePercent) < 0.01))
                add("Meta", e.id, "Meta de gordura ou linha de base inválida")
            if (e.goalNotes.length > 500) add("Meta", e.id, "Observação da meta excede 500 caracteres")
            if (e.goalDate.isNotBlank() && (!isDate(e.goalDate) ||
                    (isDate(e.date) && runCatching { LocalDate.parse(e.goalDate).isBefore(LocalDate.parse(e.date)) }.getOrDefault(false))))
                add("Meta", e.id, "Prazo da meta inválido ou anterior à avaliação")
            if (e.trainingAdherence != null && e.trainingAdherence !in 0..100 ||
                e.dietAdherence != null && e.dietAdherence !in 0..100)
                add("Avaliação", e.id, "Aderência fora da faixa de 0 a 100")
            AssessmentChecks.review(e).forEach { add("Avaliação", e.id, it.explanation) }
        }
        evaluations.groupBy { it.studentId }.filterKeys(String::isNotBlank).forEach { (_, history) ->
            val chronological = history.sortedWith(compareBy<Evaluation> { it.date }.thenBy { it.id })
            chronological.zipWithNext().forEach { (previous, current) ->
                if (previous.date == current.date)
                    add("Histórico", current.id, "Duas avaliações do mesmo aluno têm a mesma data; confira a sequência")
                AssessmentChecks.review(current, previous)
                    .filter { it.key in setOf("variação de peso", "datas") }
                    .forEach { add("Histórico", current.id, it.explanation) }
            }
        }
        charges.forEach { c ->
            runCatching{RecordRules.validate(c)}.onFailure{add("Cobrança",c.id,it.message ?: "Registro inválido")}
            if(c.secondStudentId.isNotBlank() && c.secondStudentId !in students)add("Cobrança",c.id,"Segundo cliente vinculado sem avaliação cadastrada")
            if (!isDate(c.dueDate)) add("Cobrança", c.id, "Data de vencimento inválida")
            if (c.paidDate.isNotBlank() && !isDate(c.paidDate)) add("Cobrança", c.id, "Data de pagamento inválida")
            if (c.amountCents < 0 || c.discountCents < 0 || c.discountCents > c.amountCents)
                add("Cobrança", c.id, "Valor ou desconto inválido")
            if (c.installments !in 1..24) add("Cobrança", c.id, "Número de parcelas inválido")
            if (c.paid && c.exempt) add("Cobrança", c.id, "Cortesia marcada simultaneamente como paga")
            if (c.student.isBlank()) add("Cobrança", c.id, "Aluno/contato não informado")
            if (c.studentId.isNotBlank() && c.studentId !in students)
                add("Cobrança", c.id, "Aluno vinculado não encontrado nas avaliações (pode ser pré-cadastro)")
        }
        intakes.forEach { i ->
            runCatching{RecordRules.validate(i)}.onFailure{add("Formulário",i.id,it.message ?: "Registro inválido")}
            if (!isDate(i.date)) add("Formulário", i.id, "Data inválida")
            if (i.student.isBlank()) add("Formulário", i.id, "Aluno não informado")
            if (i.studentId.isNotBlank() && i.studentId !in students)
                add("Formulário", i.id, "Aluno vinculado não encontrado nas avaliações (pode ser pré-cadastro)")
        }
        appointments.forEach { a ->
            runCatching{Agenda.validate(a)}.onFailure{add("Agenda",a.id,it.message ?: "Registro inválido")}
            if(a.studentId.isNotBlank() && a.studentId !in students)add("Agenda",a.id,"Cliente vinculado sem avaliação cadastrada")
            if (!isDate(a.date)) add("Agenda", a.id, "Data inválida")
            if (runCatching { LocalTime.parse(a.time) }.isFailure) add("Agenda", a.id, "Horário inválido")
            if (a.durationMinutes !in 1..720) add("Agenda", a.id, "Duração inválida")
            if (a.student.isBlank()) add("Agenda", a.id, "Contato não informado")
        }
        Agenda.conflicts(appointments).forEach { (appointment,other) ->
            add("Agenda", appointment.id, "Conflito de horário com ${other.date} às ${other.time}")
        }
        tasks.forEach { task ->
            runCatching{WorkspaceRules.validate(task)}.onFailure{add("Tarefa",task.id,it.message ?: "Registro inválido")}
            if(task.studentId.isNotBlank() && task.studentId !in students)add("Tarefa",task.id,"Cliente vinculado sem avaliação cadastrada")
            if (!isDate(task.dueDate)) add("Tarefa", task.id, "Data inválida")
            if (task.title.isBlank()) add("Tarefa", task.id, "Título vazio")
        }
        return AuditReport(evaluations.size + charges.size + intakes.size + appointments.size + tasks.size, problems)
    }
}
