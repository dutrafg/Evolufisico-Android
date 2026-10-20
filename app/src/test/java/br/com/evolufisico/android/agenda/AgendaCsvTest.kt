package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test

class AgendaCsvTest {
    @Test fun exportsAllVisibleStatusesInChronologicalOrder() {
        val rows=listOf(
            Appointment("id-2","b","2026-10-21T16:00",30,"Avaliação","Cancelado"),
            Appointment("id-1","a","2026-10-20T09:30",60,"Treino","Faltou")
        )
        val csv=AgendaCsv.generate(rows,mapOf("a" to "Maria","b" to "João"))
            .toString(Charsets.UTF_8)
        assertTrue(csv.startsWith("\uFEFFID;Cliente;Data;Horário;"))
        assertTrue(csv.indexOf("id-1")<csv.indexOf("id-2"))
        assertTrue(csv.contains("Faltou"))
        assertTrue(csv.contains("Cancelado"))
        assertEquals(3,csv.trim().split("\r\n").size)
    }
    @Test fun escapesFormulaQuotesAndNewLines() {
        val a=Appointment("1","x","2026-10-20T09:00",60,"=SUM(1;2)",
            "Agendado","linha; \"teste\"\nseguinte")
        val csv=AgendaCsv.generate(listOf(a),mapOf("x" to "+PAGAR"))
            .toString(Charsets.UTF_8)
        assertTrue(csv.contains("'+PAGAR"))
        assertTrue(csv.contains("\"'=SUM(1;2)\""))
        assertTrue(csv.contains("\"linha; \"\"teste\"\"\nseguinte\""))
    }
    @Test fun headerStillExistsWhenEmpty() {
        assertTrue(AgendaCsv.generate(emptyList(),emptyMap())
            .toString(Charsets.UTF_8).contains("Duração (min)"))
    }
}
