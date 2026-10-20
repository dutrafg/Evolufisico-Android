package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AgendaPulseTest {
    private val today=LocalDate.parse("2026-10-20")
    private fun ap(id:String,date:String,status:String="Agendado")=
        Appointment(id,"c",date+"T09:00",60,"Treino",status)

    @Test fun weekIsSevenDaysStartingToday() {
        assertTrue(AgendaPulse.matches(ap("1","2026-10-20"),"Semana",null,today))
        assertTrue(AgendaPulse.matches(ap("2","2026-10-26"),"Semana",null,today))
        assertFalse(AgendaPulse.matches(ap("3","2026-10-27"),"Semana",null,today))
        assertFalse(AgendaPulse.matches(ap("4","2026-10-21","Cancelado"),
            "Semana",null,today))
    }
    @Test fun pendingIgnoresCancelledAndCompleted() {
        assertTrue(AgendaPulse.matches(ap("1","2026-10-19"),"Pendentes",null,today))
        assertFalse(AgendaPulse.matches(ap("2","2026-10-19","Faltou"),
            "Pendentes",null,today))
        assertFalse(AgendaPulse.matches(ap("3","2026-10-20"),"Pendentes",null,today))
    }
    @Test fun summarySeparatesStatusesAndMonth() {
        val rows=listOf(ap("1","2026-10-20"),ap("2","2026-10-26"),
            ap("3","2026-10-27"),ap("4","2026-10-19"),
            ap("5","2026-10-01","Concluído"),ap("6","2026-10-18","Faltou"),
            ap("7","2026-09-30","Faltou"),ap("8","2026-10-18","Cancelado"))
        assertEquals(AgendaPulseSummary(1,2,1,1,1),AgendaPulse.summarize(rows,today))
    }
    @Test fun dayFilterRequiresSelectedDay() {
        assertFalse(AgendaPulse.matches(ap("1","2026-10-20"),"Dia",null,today))
        assertTrue(AgendaPulse.matches(ap("1","2026-10-20"),"Dia",today,today))
    }
}
