package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class AgendaAvailabilityTest {
    private fun at(iso:String,duration:Int=60,status:String="Agendado")=
        Appointment(iso,"client",iso,duration,"Atendimento",status)

    @Test fun suggestsThreeQuarterHourSlotsAfterCollision() {
        val wanted=LocalDateTime.parse("2026-10-20T14:30")
        val slots=AgendaAvailability.suggest(
            wanted,60,listOf(at("2026-10-20T14:00")),limit=3)
        assertEquals(listOf("2026-10-20T15:00","2026-10-20T15:15",
            "2026-10-20T15:30"),slots.map { it.toString() })
    }
    @Test fun cancelledAppointmentsDoNotBlockSuggestions() {
        val wanted=LocalDateTime.parse("2026-10-20T14:00")
        val slots=AgendaAvailability.suggest(
            wanted,60,listOf(at("2026-10-20T14:15",status="Cancelado")),limit=1)
        assertEquals("2026-10-20T14:15",slots.single().toString())
    }
    @Test fun editingIgnoresOnlyOwnAppointment() {
        val wanted=LocalDateTime.parse("2026-10-20T14:30")
        val own=at("2026-10-20T14:00")
        val other=at("2026-10-20T15:00")
        val slots=AgendaAvailability.suggest(wanted,30,listOf(own,other),
            excludingId=own.id,limit=1)
        assertEquals("2026-10-20T16:00",slots.single().toString())
    }
    @Test fun goesToNextMorningInsteadOfOvernight() {
        val slots=AgendaAvailability.suggest(LocalDateTime.parse("2026-10-20T21:30"),
            90,emptyList(),limit=1)
        assertEquals("2026-10-21T07:00",slots.single().toString())
    }

    @Test fun respectsCustomStartAndEndOfWorkingDay() {
        val target=LocalDateTime.parse("2026-10-20T17:30")
        val found=AgendaAvailability.suggest(target,60,emptyList(),
            limit=2,window=AgendaWindow(8,18))
        assertEquals(listOf("2026-10-21T08:00","2026-10-21T08:15"),
            found.map { it.toString() })
    }
    @Test fun skipsWeekendWhenRequested() {
        val target=LocalDateTime.parse("2026-10-23T20:30")
        val found=AgendaAvailability.suggest(target,60,emptyList(),
            limit=1,window=AgendaWindow(8,18,15,true))
        assertEquals("2026-10-26T08:00",found.single().toString())
    }
    @Test fun intervalOptionsAndInvalidWindows() {
        val target=LocalDateTime.parse("2026-10-20T09:00")
        val found=AgendaAvailability.suggest(target,30,emptyList(),
            limit=2,window=AgendaWindow(8,18,30))
        assertEquals(listOf("2026-10-20T09:30","2026-10-20T10:00"),
            found.map { it.toString() })
        assertTrue(AgendaAvailability.suggest(target,60,emptyList(),
            window=AgendaWindow(20,8)).isEmpty())
    }
    @Test fun availabilityNeverCrossesMidnight() {
        val target=LocalDateTime.parse("2026-10-20T22:30")
        val found=AgendaAvailability.suggest(target,90,emptyList(),
            limit=1,window=AgendaWindow(7,23))
        assertEquals("2026-10-21T07:00",found.single().toString())
    }
    @Test fun rejectsInvalidDurationOrRequestedBounds() {
        val target=LocalDateTime.parse("2026-10-20T12:00")
        assertTrue(AgendaAvailability.suggest(target,0,emptyList()).isEmpty())
        assertTrue(AgendaAvailability.suggest(target,60,emptyList(),limit=0).isEmpty())
        assertTrue(AgendaAvailability.suggest(target,60,emptyList(),horizonDays=0).isEmpty())
    }
}
