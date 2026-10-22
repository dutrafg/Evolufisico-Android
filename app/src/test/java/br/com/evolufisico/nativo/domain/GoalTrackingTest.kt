package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class GoalTrackingTest {
    @Test fun downwardTargetMeasuresProgressInCorrectDirection() {
        val result=GoalTracking.progress(90.0,80.0,70.0)!!
        assertEquals(50.0,result.percent,.0001)
        assertEquals(10.0,result.remaining,.0001)
    }
    @Test fun upwardTargetMeasuresProgressInCorrectDirection() {
        val result=GoalTracking.progress(60.0,70.0,80.0)!!
        assertEquals(50.0,result.percent,.0001)
    }
    @Test fun wrongDirectionDoesNotShowNegativeProgress() {
        assertEquals(0.0,GoalTracking.progress(90.0,95.0,70.0)!!.percent,.0001)
    }
    @Test fun completedTargetCapsAtOneHundred() {
        assertEquals(100.0,GoalTracking.progress(90.0,65.0,70.0)!!.percent,.0001)
    }
    @Test fun noChangeShowsZeroAndFullDistance() {
        val result=GoalTracking.progress(90.0,90.0,70.0)!!
        assertEquals(0.0,result.percent,.0001); assertEquals(20.0,result.remaining,.0001)
    }
    @Test fun fatPercentageUsesPercentagePointDistance() {
        val result=GoalTracking.progress(30.0,25.0,20.0)!!
        assertEquals(50.0,result.percent,.0001); assertEquals(5.0,result.remaining,.0001)
    }
    @Test fun missingValuesAreNotInvented() {
        assertNull(GoalTracking.progress(null,80.0,70.0))
        assertNull(GoalTracking.progress(90.0,null,70.0))
        assertNull(GoalTracking.progress(90.0,80.0,null))
    }
    @Test fun nonFiniteValuesAreRejected() {
        assertNull(GoalTracking.progress(Double.NaN,80.0,70.0))
        assertNull(GoalTracking.progress(90.0,Double.POSITIVE_INFINITY,70.0))
        assertNull(GoalTracking.progress(90.0,80.0,Double.NEGATIVE_INFINITY))
    }
    @Test fun identicalBaselineAndGoalCannotProduceFakePercentage() {
        assertNull(GoalTracking.progress(80.0,80.0,80.0))
        assertNull(GoalTracking.progress(80.0,80.0,80.005))
    }
    @Test fun targetMovementCanBeUpOrDown() {
        assertTrue(GoalTracking.validateTarget(90.0,70.0,20.0..400.0))
        assertTrue(GoalTracking.validateTarget(60.0,80.0,20.0..400.0))
    }
    @Test fun targetBoundsAreEnforced() {
        assertFalse(GoalTracking.validateTarget(90.0,19.9,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(90.0,400.1,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(30.0,100.1,0.0..100.0))
    }
    @Test fun baselineAndTargetMustBeUsable() {
        assertFalse(GoalTracking.validateTarget(null,70.0,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(Double.NaN,70.0,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(90.0,Double.NaN,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(500.0,70.0,20.0..400.0))
        assertFalse(GoalTracking.validateTarget(90.0,90.0,20.0..400.0))
    }
    @Test fun missingTargetDoesNotRequireBaseline() {
        assertTrue(GoalTracking.validateTarget(null,null,20.0..400.0))
    }
    @Test fun deadlineCountsCalendarDays() {
        val due=LocalDate.now().plusDays(10)
        assertEquals(10L,GoalTracking.progress(90.0,80.0,70.0,due)!!.targetDateDays)
        assertEquals(0L,GoalTracking.progress(90.0,80.0,70.0,LocalDate.now())!!.targetDateDays)
        assertEquals(-4L,GoalTracking.progress(90.0,80.0,70.0,LocalDate.now().minusDays(4))!!.targetDateDays)
    }
    @Test fun summaryMakesNoClinicalClaims() {
        val result=GoalTracking.summary("Peso",90.0,80.0,70.0,"kg")
        assertTrue(result.contains("50%")); assertTrue(result.contains("10.0 kg"))
        assertFalse(result.contains("apto",ignoreCase=true))
    }
    @Test fun missingGoalGetsPlainExplanation() {
        assertTrue(GoalTracking.summary("Peso",null,80.0,70.0,"kg").contains("dados insuficientes"))
    }
}
