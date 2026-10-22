package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.Evaluation
import org.junit.Assert.*
import org.junit.Test

class IntegrityAuditorTest {
    @Test fun cleanRecordsHaveNoFindings() {
        val report=IntegrityAuditor.run(listOf(Evaluation(id="e",studentId="s",name="Ana",date="2026-01-02")), emptyList(),emptyList(),emptyList(),emptyList())
        assertEquals(1,report.recordCount);assertTrue(report.findings.isEmpty())
    }
    @Test fun detectsDuplicateIdsWithinOneRecordKind() {
        val report=IntegrityAuditor.run(listOf(
            Evaluation(id="same",studentId="s1",name="Ana"),Evaluation(id="same",studentId="s2",name="Bia")
        ),emptyList(),emptyList(),emptyList(),emptyList())
        assertTrue(report.findings.any { it.problem.contains("duplicado") })
    }
    @Test fun detectsWeightGoalWithoutValidBaseline() {
        val report=IntegrityAuditor.run(listOf(Evaluation(id="e",studentId="s",name="Ana",weightKg=90.0,goalWeightKg=70.0)),emptyList(),emptyList(),emptyList(),emptyList())
        assertTrue(report.findings.any { it.section=="Meta" && it.problem.contains("peso") })
    }
    @Test fun acceptsGoalWithValidRangeAndDistinctBaseline() {
        val report=IntegrityAuditor.run(listOf(Evaluation(id="e",studentId="s",name="Ana",date="2026-01-02",weightKg=85.0,goalWeightKg=70.0,goalWeightBaselineKg=90.0,goalDate="2026-05-01")),emptyList(),emptyList(),emptyList(),emptyList())
        assertFalse(report.findings.any { it.section=="Meta" })
    }
    @Test fun detectsGoalDeadlineBeforeAssessment() {
        val report=IntegrityAuditor.run(listOf(Evaluation(id="e",studentId="s",name="Ana",date="2026-02-01",goalDate="2026-01-01")),emptyList(),emptyList(),emptyList(),emptyList())
        assertTrue(report.findings.any { it.problem.contains("Prazo") })
    }
    @Test fun detectsAppointmentOverlapEvenAcrossMidnight() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="23:30",durationMinutes=60)
        val second=Appointment(id="b",student="Bia",date="2026-01-02",time="00:15",durationMinutes=30)
        val report=IntegrityAuditor.run(emptyList(),emptyList(),emptyList(),listOf(first,second),emptyList())
        assertTrue(report.findings.any { it.section=="Agenda" && it.problem.contains("Conflito") })
    }
    @Test fun ignoresAdjacentAppointmentsAndInactiveRecords() {
        val first=Appointment(id="a",student="Ana",date="2026-01-01",time="09:00",durationMinutes=60)
        val second=Appointment(id="b",student="Bia",date="2026-01-01",time="10:00",durationMinutes=30)
        val cancelled=second.copy(id="c",time="09:30",status="Cancelado")
        val report=IntegrityAuditor.run(emptyList(),emptyList(),emptyList(),listOf(first,second,cancelled),emptyList())
        assertFalse(report.findings.any { it.problem.contains("Conflito") })
    }
    @Test fun descriptionDoesNotExposeFullIdentifiersOrClaimDiagnosis() {
        val report=IntegrityAuditor.run(listOf(Evaluation(id="sensitive-full-id",studentId="s",name="",date="nope")),emptyList(),emptyList(),emptyList(),emptyList())
        val text=report.description()
        assertFalse(text.contains("sensitive-full-id"));assertTrue(text.contains("não determina aptidão clínica"))
    }
}
