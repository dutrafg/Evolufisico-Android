package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class AgendaIcsTest {
    @Test fun exportsOnlyActiveAppointmentsWithCorrectTimes() {
        val events=listOf(
            Appointment("id-1","c","2026-10-20T14:30",60,"Treino, A","Agendado","Notas; teste\nLinha"),
            Appointment("id-2","c","2026-10-21T14:30",60,"Falta","Faltou"),
            Appointment("id-3","c","2026-10-22T14:30",60,"Cancelado","Cancelado")
        )
        val text=AgendaIcs.generate(events,mapOf("c" to "João"),
            Instant.parse("2026-10-20T10:00:00Z")).toString(Charsets.UTF_8)
        assertTrue(text.contains("DTSTART:20261020T143000\r\n"))
        assertTrue(text.contains("DTEND:20261020T153000\r\n"))
        assertTrue(text.contains("SUMMARY:Treino\\, A - João"))
        assertTrue(text.contains("DESCRIPTION:Notas\\; teste\\nLinha"))
        assertEquals(1,text.split("BEGIN:VEVENT").size-1)
        assertTrue(text.endsWith("END:VCALENDAR\r\n"))
    }
    @Test fun longUtf8LinesAreFoldedToMax75Octets() {
        val line="SUMMARY:"+"á".repeat(80)
        val folded=AgendaIcs.fold(line)
        assertTrue(folded.contains("\r\n "))
        assertTrue(folded.split("\r\n").all {
            it.toByteArray(Charsets.UTF_8).size<=75
        })
    }
}
