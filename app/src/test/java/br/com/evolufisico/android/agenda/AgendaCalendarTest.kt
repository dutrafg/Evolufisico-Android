package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class AgendaCalendarTest {
    private val list = listOf(
        Appointment("a","c","2026-10-20T09:00",60,"Avaliação"),
        Appointment("b","c","2026-10-20T10:30",60,"Treino","Concluído"),
        Appointment("c","c","2026-10-20T14:00",60,"Treino","Cancelado"),
        Appointment("d","c","2026-11-01T14:00",60,"Treino")
    )
    @Test fun mondayFirstAndRightMonth() {
        val cells=AgendaCalendar.weeks(YearMonth.of(2026,10),list)
        assertEquals(7,cells.first().size)
        assertNull(cells.first()[0])
        assertEquals(LocalDate.of(2026,10,1),cells.first()[3]!!.date)
        assertEquals(31,cells.flatten().count { it != null })
        val day=cells.flatten().filterNotNull().single { it.date.dayOfMonth == 20 }
        assertEquals(3,day.total)
        assertEquals(1,day.scheduled)
        assertEquals(1,day.completed)
        assertEquals(1,day.cancelled)
    }
    @Test fun dayAndMonthFiltersDoNotIncludeOutsideDates() {
        assertEquals(3,AgendaCalendar.filter(list,LocalDate.of(2026,10,20)).size)
        assertEquals(1,AgendaCalendar.active(YearMonth.of(2026,10),list))
        assertEquals(1,AgendaCalendar.active(YearMonth.of(2026,11),list))
    }
    @Test fun leapYearFebruaryHas29Days() {
        val days=AgendaCalendar.weeks(YearMonth.of(2028,2),emptyList()).flatten()
        assertEquals(29,days.count { it != null })
        assertTrue(days.size in listOf(28,35,42))
    }
}
