package br.com.evolufisico.android.ops

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class OperationalRulesTest {
    private val clients=setOf("c1")
    @Test fun agendaValidationAndConflict() {
        val a=Appointment("a","c1","2026-10-21T14:00",60,"Avaliação")
        val b=Appointment("b","c1","2026-10-21T14:30",60,"Treinamento")
        assertNull(OperationalRules.appointment(a,clients))
        assertTrue(OperationalRules.conflict(b,listOf(a)))
        assertFalse(OperationalRules.conflict(b.copy(startIso="2026-10-21T15:00"),listOf(a)))
        assertFalse(OperationalRules.conflict(b,listOf(a.copy(status="Cancelado"))))
        assertNotNull(OperationalRules.appointment(a.copy(durationMinutes=2),clients))
        assertNotNull(OperationalRules.appointment(a.copy(clientId="unknown"),clients))
    }
    @Test fun absenceStatusIsValidAndCancelStillFreesSlot() {
        val a=Appointment("a","c1","2026-10-20T13:00",60,"Avaliação","Faltou")
        assertNull(OperationalRules.appointment(a,clients))
        assertTrue(OperationalRules.conflict(a.copy(id="b"),listOf(a)))
        assertFalse(OperationalRules.conflict(a.copy(id="b"),
            listOf(a.copy(status="Cancelado"))))
    }
    @Test fun goalsSupportBothDirectionsAndLimits() {
        val g=ClientGoal("g","c1","Peso","kg",90.0,80.0,85.0,"2026-12-31")
        assertNull(OperationalRules.goal(g,clients))
        assertEquals(50,OperationalRules.progress(g))
        assertEquals(100,OperationalRules.progress(g.copy(current=70.0)))
        assertEquals(0,OperationalRules.progress(g.copy(current=100.0)))
        assertNotNull(OperationalRules.goal(g.copy(target=90.0),clients))
    }
    @Test fun clientNotesValidateCategoriesAndDates() {
        val note=ClientNote("n1","c1","2026-10-20","Treino",
            "Acompanhamento","Registro fictício.",true)
        assertNull(OperationalRules.note(note,clients))
        assertNotNull(OperationalRules.note(note.copy(clientId="inexistente"),clients))
        assertNotNull(OperationalRules.note(note.copy(category="Inexistente"),clients))
        assertNotNull(OperationalRules.note(note.copy(dateIso="20/10/2026"),clients))
        assertNotNull(OperationalRules.note(note.copy(body=""),clients))
    }
    @Test fun servicesUseIntegralCents() {
        assertNull(OperationalRules.service(ServiceItem("s","Avaliação",2500,60)))
        assertNotNull(OperationalRules.service(ServiceItem("s","Avaliação",-1,60)))
        assertNotNull(OperationalRules.service(ServiceItem("s","Avaliação",2500,0)))
    }
}
