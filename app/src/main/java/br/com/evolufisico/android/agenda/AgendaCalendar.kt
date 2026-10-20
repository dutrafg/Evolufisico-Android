package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDate
import java.time.YearMonth

data class AgendaDay(
    val date: LocalDate,
    val scheduled: Int,
    val completed: Int,
    val cancelled: Int,
    val missed: Int
) {
    val total: Int get() = scheduled + completed + cancelled + missed
}

object AgendaCalendar {
    /** Calendário inicia na segunda-feira e possui entre quatro e seis semanas. */
    fun weeks(month: YearMonth, records: List<Appointment>): List<List<AgendaDay?>> {
        val first = month.atDay(1)
        val prefix = first.dayOfWeek.value - 1
        val total = ((prefix + month.lengthOfMonth() + 6) / 7) * 7
        val byDate = records.groupBy { it.startIso.take(10) }
        return (0 until total).map { position ->
            val number = position - prefix + 1
            if (number !in 1..month.lengthOfMonth()) null
            else {
                val date = month.atDay(number)
                val daily = byDate[date.toString()].orEmpty()
                AgendaDay(date, daily.count { it.status == "Agendado" },
                    daily.count { it.status == "Concluído" },
                    daily.count { it.status == "Cancelado" },
                    daily.count { it.status == "Faltou" })
            }
        }.chunked(7)
    }

    fun filter(records: List<Appointment>, day: LocalDate): List<Appointment> =
        records.filter { it.startIso.take(10) == day.toString() }
            .sortedBy { it.startIso }

    /** Resumo de agenda ativa para prevenir confusão entre cancelado e agendado. */
    fun active(month: YearMonth, records: List<Appointment>): Int =
        records.count { it.startIso.startsWith(month.toString()) &&
            it.status == "Agendado" }
}
