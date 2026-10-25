package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.Evaluation
import org.junit.Assert.*
import org.junit.Test

class AgendaTimelineTest {
    private val visit=Appointment(id="visit",student="Teste",date="2026-12-31",time="23:30")

    @Test fun overnightConflictAndAdjacentVisit() {
        val next=visit.copy(id="next",date="2027-01-01",time="00:00")
        assertTrue(Agenda.overlaps(visit,next))
        assertFalse(Agenda.overlaps(visit,next.copy(time="00:30")))
        assertFalse(Agenda.overlaps(visit,next.copy(status="Cancelado")))
    }

    @Test fun conflictInFinalWeekRejectsEntireBatch() {
        val series=Agenda.recurrence(visit,12)
        assertEquals(12,series.map { it.id }.distinct().size)
        assertThrows(IllegalArgumentException::class.java) {
            Agenda.checkBatch(series,listOf(series.last().copy(id="occupied")))
        }
        assertEquals("2026-12-31",visit.date)
    }

    @Test fun editDoesNotConflictWithItsOwnRecord() {
        Agenda.checkBatch(listOf(visit.copy(time="23:45")),listOf(visit))
    }

    @Test fun appointmentRejectsImpossibleCalendarAndClockValues() {
        assertThrows(IllegalArgumentException::class.java){Agenda.validate(visit.copy(date="2026-02-31"))}
        assertThrows(IllegalArgumentException::class.java){Agenda.validate(visit.copy(time="12:99"))}
    }

    @Test fun sameDayEvaluationsRemainIndividuallySelectable() {
        val choices=EvaluationTimeline.choices(listOf(Evaluation(id="one",date="2026-10-21"),
            Evaluation(id="two",date="2026-10-21")))
        assertEquals(listOf("one","two"),choices.map { it.id })
        assertEquals(2,choices.map { it.label }.distinct().size)
    }

    @Test fun chartUsesElapsedDays() {
        val points=EvaluationTimeline.points(listOf("2026-10-01" to 70.0,"2026-10-02" to 71.0,
            "2026-10-11" to 72.0))
        assertEquals(.1f,points[1].x,.0001f)
        assertEquals(0f,points[2].y,.0001f)
    }
}
