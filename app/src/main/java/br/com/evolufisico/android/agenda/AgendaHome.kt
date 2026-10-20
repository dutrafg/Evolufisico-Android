package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDateTime

/** Prévia do início: leitura pura, sem modificar ou persistir atendimentos. */
data class UpcomingAppointment(
    val id:String, val clientName:String, val startIso:String, val service:String
)

object AgendaHome {
    fun next(
        appointments:List<Appointment>, clientNames:Map<String,String>,
        now:LocalDateTime, limit:Int=5
    ):List<UpcomingAppointment> {
        require(limit in 1..20)
        return appointments.asSequence().filter { it.status=="Agendado" }
            .mapNotNull { a ->
                val date=runCatching { LocalDateTime.parse(a.startIso) }.getOrNull()
                if(date==null || date.isBefore(now)) null else date to a
            }.sortedWith(compareBy<Pair<LocalDateTime,Appointment>> { it.first }
                .thenBy { it.second.id })
            .take(limit)
            .map { (_,a) ->
                UpcomingAppointment(a.id,clientNames[a.clientId]?:"Cliente indisponível",
                    a.startIso,a.service)
            }.toList()
    }
    fun pendingCount(appointments:List<Appointment>,now:LocalDateTime):Int =
        appointments.count { a ->
            a.status=="Agendado" &&
                (runCatching { LocalDateTime.parse(a.startIso) }.getOrNull()
                    ?.isBefore(now)==true)
        }
}
