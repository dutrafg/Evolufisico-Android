package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDate

/** Indicadores de agenda calculados dos registros existentes, sem alterar o banco. */
data class AgendaPulseSummary(
    val scheduledToday:Int, val upcomingSevenDays:Int, val overdue:Int,
    val completedMonth:Int, val missedMonth:Int
)

object AgendaPulse {
    fun matches(a:Appointment, filter:String, selectedDay:LocalDate?, today:LocalDate):Boolean {
        val day=runCatching { LocalDate.parse(a.startIso.substringBefore('T')) }.getOrNull()
            ?: return false
        return when(filter) {
            "Hoje" -> day==today
            "Dia" -> selectedDay!=null && day==selectedDay
            "Semana" -> a.status=="Agendado" && !day.isBefore(today) &&
                day.isBefore(today.plusDays(7))
            "Pendentes" -> a.status=="Agendado" && day.isBefore(today)
            "Futuras" -> a.status=="Agendado" && !day.isBefore(today)
            "Concluídas" -> a.status=="Concluído"
            "Canceladas" -> a.status=="Cancelado"
            "Faltas" -> a.status=="Faltou"
            else -> true
        }
    }

    fun summarize(rows:List<Appointment>,today:LocalDate):AgendaPulseSummary {
        val firstOfMonth=today.withDayOfMonth(1)
        val nextMonth=firstOfMonth.plusMonths(1)
        var todayCount=0
        var weekCount=0
        var overdueCount=0
        var completed=0
        var missed=0
        for(a in rows) {
            val day=runCatching { LocalDate.parse(a.startIso.substringBefore('T')) }
                .getOrNull() ?: continue
            when(a.status) {
                "Agendado" -> {
                    if(day==today) todayCount++
                    if(!day.isBefore(today) && day.isBefore(today.plusDays(7))) weekCount++
                    if(day.isBefore(today)) overdueCount++
                }
                "Concluído" -> if(!day.isBefore(firstOfMonth) && day.isBefore(nextMonth))
                    completed++
                "Faltou" -> if(!day.isBefore(firstOfMonth) && day.isBefore(nextMonth))
                    missed++
            }
        }
        return AgendaPulseSummary(todayCount,weekCount,overdueCount,completed,missed)
    }
}
