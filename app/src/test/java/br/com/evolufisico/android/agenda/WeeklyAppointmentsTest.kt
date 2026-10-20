package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test

class WeeklyAppointmentsTest {
    private val original=Appointment("a","client","2026-10-20T14:00",60,"Treino")
    @Test fun fourWeeksStaySevenDaysApart() {
        var next=0
        val result=WeeklyAppointments.plan(original,emptyList(),4){"id"+(++next)}
        assertEquals(listOf("2026-10-20T14:00","2026-10-27T14:00",
            "2026-11-03T14:00","2026-11-10T14:00"),result.map { it.startIso })
        assertEquals(4,result.map { it.id }.distinct().size)
    }
    @Test fun conflictPreventsEveryAppointment() {
        val conflict=original.copy(id="existing",startIso="2026-11-03T14:30")
        var next=0
        val result=runCatching {
            WeeklyAppointments.plan(original,listOf(conflict),4){"id-"+(++next)}
        }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("2026-11-03")==true)
    }
    @Test fun cancelledDoesNotBlockButInvalidQuantityDoes() {
        val cancelled=original.copy(id="old",status="Cancelado")
        assertEquals(2,WeeklyAppointments.plan(original,listOf(cancelled),2){"id-2"}.size)
        assertTrue(runCatching { WeeklyAppointments.plan(original,emptyList(),13) }.isFailure)
        assertTrue(runCatching { WeeklyAppointments.plan(original,emptyList(),0) }.isFailure)
    }
}
