package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDateTime

/** Regra sem dependências Android; nunca inclui nomes ou dados clínicos. */
object AgendaReminderRules {
    fun key(item: Appointment) = "${item.id}@${item.startIso}"

    fun upcoming(
        appointments: List<Appointment>,
        now: LocalDateTime,
        notified: Set<String>,
        leadMinutes: Long = 60
    ): List<Appointment> {
        require(leadMinutes in 1..120)
        val cutoff=now.plusMinutes(leadMinutes)
        return appointments.filter { item ->
            if(item.status != "Agendado" || key(item) in notified) return@filter false
            val start=runCatching { LocalDateTime.parse(item.startIso) }.getOrNull()
                ?: return@filter false
            !start.isBefore(now) && start.isBefore(cutoff)
        }.distinctBy(::key)
    }
}
