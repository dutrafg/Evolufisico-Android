package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test

class AgendaActionsTest {
    private val ap=Appointment("original","c","2026-10-20T10:30",60,"Treino","Concluído","teste")

    @Test fun duplicatesIntoNextWeekWithOwnIdAndScheduledStatus() {
        val result=AgendaActions.duplicateNextWeek(ap,listOf(ap),setOf("c"),"new-id")
        assertEquals("new-id",result.id)
        assertEquals("2026-10-27T10:30",result.startIso)
        assertEquals("Agendado",result.status)
        assertEquals(ap.notes,result.notes)
        assertEquals("Concluído",ap.status)
    }
    @Test fun rejectsConflictsInsteadOfCreatingPartialDuplicate() {
        val conflict=ap.copy(id="busy",startIso="2026-10-27T11:00",status="Agendado")
        assertThrows(IllegalArgumentException::class.java) {
            AgendaActions.duplicateNextWeek(ap,listOf(ap,conflict),setOf("c"),"new-id")
        }
    }
    @Test fun doesNotAllowReopenIntoBusySlot() {
        val occupied=ap.copy(id="another",status="Agendado")
        assertThrows(IllegalArgumentException::class.java) {
            AgendaActions.changeStatus(ap,"Agendado",listOf(ap,occupied))
        }
        assertEquals("Faltou",AgendaActions.changeStatus(ap,"Faltou",listOf(ap)).status)
    }
    @Test fun rejectsMissingClientAndDuplicateIds() {
        assertThrows(IllegalArgumentException::class.java) {
            AgendaActions.duplicateNextWeek(ap,listOf(ap),emptySet(),"new-id")
        }
        assertThrows(IllegalArgumentException::class.java) {
            AgendaActions.duplicateNextWeek(ap,listOf(ap),setOf("c"),"original")
        }
    }
}
