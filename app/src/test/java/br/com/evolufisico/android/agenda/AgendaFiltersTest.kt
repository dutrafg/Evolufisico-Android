package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class AgendaFiltersTest {
    private val today=LocalDate.parse("2026-10-20")
    private val a=Appointment("a","1","2026-10-20T10:00",60,"Avaliação Física",
        "Agendado","Primeira sessão")
    private val b=Appointment("b","2","2026-10-19T10:00",60,"Treino",
        "Agendado","Remarcação necessária")
    private val c=Appointment("c","1","2026-10-20T08:00",60,"Treino",
        "Concluído","")
    private val names=mapOf("1" to "João","2" to "Mário")

    @Test fun searchIgnoresAccentsAndCase() {
        val out=AgendaFilters.filter(listOf(a,b,c),names,"Todas",null,today,"AVALIACAO",null)
        assertEquals(listOf("a"),out.map{it.id})
        val byClient=AgendaFilters.filter(listOf(a,b,c),names,"Todas",null,today,"joao",null)
        assertEquals(listOf("c","a"),byClient.map{it.id})
    }
    @Test fun filterByClientAndStatusBothApply() {
        val rows=AgendaFilters.filter(listOf(a,b,c),names,"Hoje",null,today,"","1")
        assertEquals(listOf("c","a"),rows.map{it.id})
        val pending=AgendaFilters.filter(listOf(a,b,c),names,"Pendentes",null,today,"",null)
        assertEquals(listOf("b"),pending.map{it.id})
    }
    @Test fun searchLooksIntoNotesAndEmptySearchKeepsAll() {
        assertEquals("b",AgendaFilters.filter(listOf(a,b,c),names,
            "Todas",null,today,"remarcacao",null).single().id)
        assertEquals(3,AgendaFilters.filter(listOf(a,b,c),names,
            "Todas",null,today,"  ",null).size)
    }
}
