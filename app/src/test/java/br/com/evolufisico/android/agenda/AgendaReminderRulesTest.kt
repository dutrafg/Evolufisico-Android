package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class AgendaReminderRulesTest {
    private val now=LocalDateTime.parse("2026-10-21T10:00:00")
    private fun a(id:String,time:String,status:String="Agendado")=
        Appointment(id,"client-test",time,60,"Avaliação",status)

    @Test fun onlyUpcomingScheduledAppointmentsTrigger() {
        val records=listOf(
            a("now","2026-10-21T10:00:00"),
            a("soon","2026-10-21T10:59:59"),
            a("past","2026-10-21T09:59:59"),
            a("later","2026-10-21T11:00:00"),
            a("done","2026-10-21T10:30:00","Concluído"),
            a("cancel","2026-10-21T10:30:00","Cancelado"),
            a("bad","2026-10-21T10:bad"))
        assertEquals(setOf("now","soon"),
            AgendaReminderRules.upcoming(records,now,emptySet()).map { it.id }.toSet())
    }

    @Test fun sameAppointmentAndTimeNotRepeatedAcrossWorkerExecutions() {
        val item=a("id1","2026-10-21T10:30:00")
        val seen=setOf(AgendaReminderRules.key(item))
        assertTrue(AgendaReminderRules.upcoming(listOf(item),now,seen).isEmpty())
        val changed=item.copy(startIso="2026-10-21T10:45:00")
        assertEquals(listOf(changed),
            AgendaReminderRules.upcoming(listOf(changed),now,seen))
    }

    @Test fun duplicatedAppointmentIdsOnlyNotifiedOnce() {
        val x=a("id1","2026-10-21T10:30:00")
        assertEquals(1,AgendaReminderRules.upcoming(listOf(x,x),now,emptySet()).size)
    }

    @Test fun invalidLeadWindowIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            AgendaReminderRules.upcoming(emptyList(),now,emptySet(),0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            AgendaReminderRules.upcoming(emptyList(),now,emptySet(),121)
        }
    }
}
