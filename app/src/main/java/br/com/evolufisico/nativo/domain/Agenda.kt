package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Appointment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

/** One professional's local agenda. Intervals are half-open: adjacent visits do not overlap. */
object Agenda {
    private val inactive = setOf("Cancelado", "Concluído", "Faltou")
    fun start(item: Appointment): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(item.date), LocalTime.parse(item.time))

    fun validate(item: Appointment) {
        require(item.student.isNotBlank()) { "Informe aluno ou contato." }
        require(item.durationMinutes in 1..720) { "Duração deve estar entre 1 e 720 minutos." }
        require(item.status in setOf("Agendado", "Confirmado") + inactive) { "Situação inválida." }
        require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(item.date)) { "Data inválida (AAAA-MM-DD)." }
        require(Regex("\\d{2}:\\d{2}").matches(item.time)) { "Hora inválida (HH:MM)." }
        require(runCatching { start(item) }.isSuccess) { "Data ou hora inválida." }
    }

    fun recurrence(item: Appointment, weeks: Int): List<Appointment> {
        validate(item)
        require(weeks in 1..12) { "Repetição deve estar entre 1 e 12 semanas." }
        return (0 until weeks).map { index ->
            if (index == 0) item else item.copy(id = UUID.randomUUID().toString(),
                date = LocalDate.parse(item.date).plusWeeks(index.toLong()).toString())
        }
    }

    fun overlaps(a: Appointment, b: Appointment): Boolean {
        if (a.id == b.id || a.status in inactive || b.status in inactive) return false
        return runCatching {
            val aStart = start(a); val bStart = start(b)
            aStart < bStart.plusMinutes(b.durationMinutes.toLong()) &&
                bStart < aStart.plusMinutes(a.durationMinutes.toLong())
        }.getOrDefault(false)
    }

    /** Validate the entire batch before writing any of its occurrences. */
    fun checkBatch(items: List<Appointment>, existing: List<Appointment>) {
        require(items.isNotEmpty() && items.size <= 12) { "Lote de agendamentos inválido." }
        require(items.map { it.id }.distinct().size == items.size) { "Identificador de agenda duplicado." }
        items.forEach(::validate)
        val replaced = items.map { it.id }.toSet()
        val checked = existing.filterNot { it.id in replaced }.toMutableList()
        items.forEach { item ->
            val conflict = checked.firstOrNull { overlaps(item, it) }
            require(conflict == null) {
                "Conflito em ${item.date} às ${item.time}: atendimento existente às ${conflict?.time}. Nenhum agendamento foi salvo."
            }
            checked.add(item)
        }
    }
}

/** Policy shared by reminder scheduling and its worker; WorkManager remains approximate. */
object ReminderPolicy {
    val allowedLeadMinutes = listOf(15, 30, 60, 1440)

    fun validateLead(minutes: Int): Int {
        require(minutes in allowedLeadMinutes) { "Escolha 15, 30, 60 ou 1.440 minutos." }
        return minutes
    }

    fun shouldSchedule(millisecondsUntilVisit: Long, leadMinutes: Int): Boolean =
        millisecondsUntilVisit > validateLead(leadMinutes) * 60_000L

    /** A delayed worker may notify only while the appointment is still upcoming and within its lead window. */
    fun shouldNotify(secondsUntilVisit: Long, leadMinutes: Int): Boolean =
        secondsUntilVisit in 1..(validateLead(leadMinutes) * 60L)
}
