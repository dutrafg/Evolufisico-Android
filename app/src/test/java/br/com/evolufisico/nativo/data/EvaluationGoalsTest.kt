package br.com.evolufisico.nativo.data

import org.junit.Assert.*
import org.junit.Test

class EvaluationGoalsTest {
    @Test fun newCheckInCarriesGoalWithoutChangingItsBaseline() {
        val previous=Evaluation(studentId="stable",name="Rita",weightKg=82.0,goalWeightKg=70.0,
            goalWeightBaselineKg=90.0,goalBodyFatPercent=22.0,goalBodyFatBaselinePercent=30.0,
            goalDate="2026-12-31",goalNotes="meta")
        val checkIn=previous.newCheckIn()
        assertEquals("stable",checkIn.studentId);assertNull(checkIn.weightKg)
        assertEquals(previous.goalWeightKg,checkIn.goalWeightKg)
        assertEquals(previous.goalWeightBaselineKg,checkIn.goalWeightBaselineKg)
        assertEquals(previous.goalBodyFatPercent,checkIn.goalBodyFatPercent)
        assertEquals(previous.goalBodyFatBaselinePercent,checkIn.goalBodyFatBaselinePercent)
        assertEquals(previous.goalDate,checkIn.goalDate);assertEquals(previous.goalNotes,checkIn.goalNotes)
    }
}
