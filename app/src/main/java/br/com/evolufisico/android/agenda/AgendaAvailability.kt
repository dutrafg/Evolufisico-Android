package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationalRules
import java.time.LocalDateTime
import java.time.LocalTime

/** Sugestões locais para encontrar horários livres, sem gravar dados ou substituir regras de conflito. */
object AgendaAvailability {
    private val firstTime=LocalTime.of(7,0)
    private val lastEnd=LocalTime.of(22,0)
    private const val STEP_MINUTES=15L

    fun suggest(
        requested:LocalDateTime,
        durationMinutes:Int,
        existing:List<Appointment>,
        excludingId:String?=null,
        limit:Int=3,
        horizonDays:Int=7
    ):List<LocalDateTime> {
        if(durationMinutes !in 15..480 || limit !in 1..10 || horizonDays !in 1..30) {
            return emptyList()
        }
        val endDate=requested.toLocalDate().plusDays(horizonDays.toLong())
        val blocking=if(excludingId==null) existing
            else existing.filterNot { it.id==excludingId }
        val output=mutableListOf<LocalDateTime>()
        var cursor=requested.plusMinutes(1).withSecond(0).withNano(0)
        val remainder=cursor.minute%15
        if(remainder>0) cursor=cursor.plusMinutes((15-remainder).toLong())
        while(cursor.toLocalDate().isBefore(endDate) && output.size<limit) {
            val start=cursor.toLocalTime()
            val finish=start.plusMinutes(durationMinutes.toLong())
            val insideHours=!start.isBefore(firstTime) &&
                !start.plusMinutes(durationMinutes.toLong()).isBefore(start) &&
                !finish.isAfter(lastEnd)
            if(insideHours) {
                val candidate=Appointment("__agenda_suggestion__", "", cursor.toString(),
                    durationMinutes,"Disponibilidade")
                if(!OperationalRules.conflict(candidate,blocking)) output+=cursor
            }
            cursor=cursor.plusMinutes(STEP_MINUTES)
        }
        return output
    }
}
