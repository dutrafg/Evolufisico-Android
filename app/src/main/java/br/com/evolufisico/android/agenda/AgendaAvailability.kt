package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationalRules
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

/** Regras de disponibilidade locais. Sugestões não modificam a agenda. */
data class AgendaWindow(
    val startHour:Int=7,
    val endHour:Int=22,
    val intervalMinutes:Int=15,
    val weekdaysOnly:Boolean=false
) {
    fun valid():Boolean =
        startHour in 0..22 && endHour in 1..23 &&
        startHour<endHour && intervalMinutes in listOf(15,30,60)
}

object AgendaAvailability {
    fun suggest(
        requested:LocalDateTime,
        durationMinutes:Int,
        existing:List<Appointment>,
        excludingId:String?=null,
        limit:Int=3,
        horizonDays:Int=7,
        window:AgendaWindow=AgendaWindow()
    ):List<LocalDateTime> {
        if(durationMinutes !in 15..480 || limit !in 1..10 ||
            horizonDays !in 1..30 || !window.valid()) return emptyList()
        val endDate=requested.toLocalDate().plusDays(horizonDays.toLong())
        val blocking=if(excludingId==null) existing else existing.filterNot {
            it.id==excludingId
        }
        val output=mutableListOf<LocalDateTime>()
        val step=window.intervalMinutes.toLong()
        var cursor=requested.plusMinutes(1).withSecond(0).withNano(0)
        val remainder=cursor.minute%window.intervalMinutes
        if(remainder>0) cursor=cursor.plusMinutes((window.intervalMinutes-remainder).toLong())
        while(cursor.toLocalDate().isBefore(endDate) && output.size<limit) {
            val start=cursor.toLocalTime()
            val end=cursor.plusMinutes(durationMinutes.toLong())
            val sameDay=end.toLocalDate()==cursor.toLocalDate()
            val withinHours=!start.isBefore(LocalTime.of(window.startHour,0)) &&
                sameDay && !end.toLocalTime().isAfter(LocalTime.of(window.endHour,0))
            val weekday=cursor.dayOfWeek!=DayOfWeek.SATURDAY &&
                cursor.dayOfWeek!=DayOfWeek.SUNDAY
            if(withinHours && (!window.weekdaysOnly || weekday)) {
                val candidate=Appointment("__agenda_suggestion__","",cursor.toString(),
                    durationMinutes,"Disponibilidade")
                if(!OperationalRules.conflict(candidate,blocking)) output+=cursor
            }
            cursor=cursor.plusMinutes(step)
        }
        return output
    }
}
