package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class AgendaHomeTest {
    private val now=LocalDateTime.parse("2026-10-20T12:00")
    private fun event(id:String,date:String,status:String="Agendado") =
        Appointment(id,"c",date,60,"Treino",status)

    @Test fun nextIsChronologicalAndIgnoresInactiveOrPast() {
        val rows=listOf(
            event("later","2026-10-22T12:00"),
            event("past","2026-10-20T11:59"),
            event("tomorrow","2026-10-21T09:00"),
            event("cancel","2026-10-20T12:30","Cancelado"),
            event("now","2026-10-20T12:00")
        )
        val next=AgendaHome.next(rows,mapOf("c" to "Maria"),now,2)
        assertEquals(listOf("now","tomorrow"),next.map{it.id})
        assertEquals("Maria",next.first().clientName)
        assertEquals(1,AgendaHome.pendingCount(rows,now))
    }
    @Test fun invalidDatesDoNotCrashDashboard() {
        val bad=event("bad","not-a-date")
        assertTrue(AgendaHome.next(listOf(bad),emptyMap(),now).isEmpty())
        assertEquals(0,AgendaHome.pendingCount(listOf(bad),now))
    }
    @Test fun missingClientHasNonSensitiveLabel() {
        val item=AgendaHome.next(listOf(event("a","2026-10-20T16:00")),
            emptyMap(),now).single()
        assertEquals("Cliente indisponível",item.clientName)
    }
}
