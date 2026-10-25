package br.com.evolufisico.nativo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AssessmentMathTest {
    @Test fun bmiCalculatesAndRoundsToOneDecimal() {
        assertEquals(22.9, AssessmentMath.bmi(70.0, 175.0)!!, 0.0)
    }

    @Test fun bmiRejectsInvalidAndOutOfRangeMeasurements() {
        assertNull(AssessmentMath.bmi(0.0, 175.0))
        assertNull(AssessmentMath.bmi(70.0, 0.0))
        assertNull(AssessmentMath.bmi(Double.NaN, 175.0))
        assertNull(AssessmentMath.bmi(70.0, 280.0))
    }

    @Test fun localizedNumberParserAcceptsCommaAndRejectsInvalidValues() {
        assertEquals(72.5, AssessmentMath.parseNumber(" 72,5 ")!!, 0.0)
        assertNull(AssessmentMath.parseNumber("0"))
        assertNull(AssessmentMath.parseNumber("abc"))
    }

    @Test fun navyBodyFatHandlesBothSexesAndRequiresFemaleHip() {
        val male = AssessmentMath.navyBodyFat("Masculino", 180.0, 90.0, 40.0, null)
        val female = AssessmentMath.navyBodyFat("Feminino", 165.0, 75.0, 32.0, 100.0)
        assertTrue(male != null && male in 1.0..70.0)
        assertTrue(female != null && female in 1.0..70.0)
        assertNull(AssessmentMath.navyBodyFat("Feminino", 165.0, 75.0, 32.0, null))
        assertNull(AssessmentMath.navyBodyFat("Outro", 165.0, 75.0, 32.0, 100.0))
    }

    @Test fun rfmUsesSexSpecificFormulaAndRejectsInvalidResults() {
        assertEquals(24.0, AssessmentMath.rfmBodyFat("Masculino", 180.0, 90.0)!!, 0.0)
        assertEquals(34.8, AssessmentMath.rfmBodyFat("Feminino", 165.0, 80.0)!!, 0.0)
        assertNull(AssessmentMath.rfmBodyFat("Outro", 165.0, 80.0))
    }

    @Test fun pollock7MatchesReferenceForBothSexesAndDerivesMasses() {
        val folds = listOf(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0)
        val male = AssessmentMath.pollock7("Masculino", 30, 80.0, folds)!!
        assertEquals(280.0, male.sumMm, 0.0)
        assertEquals(1.0247, male.density, 0.0001)
        assertEquals(33.1, male.bodyFatPercent, 0.1)
        assertEquals(26.5, male.fatMassKg, 0.1)
        assertEquals(53.5, male.leanMassKg, 0.1)
        val female = AssessmentMath.pollock7("Feminino", 30, 60.0, folds)!!
        assertTrue(female.bodyFatPercent in 1.0..70.0)
        assertEquals(60.0, female.fatMassKg + female.leanMassKg, 0.1)
        assertTrue(!male.wasClamped && !female.wasClamped)
    }

    @Test fun pollock7RejectsIncompleteAndOutOfRangeInputs() {
        val complete = listOf(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0)
        assertNull(AssessmentMath.pollock7("Outro", 30, 80.0, complete))
        assertNull(AssessmentMath.pollock7("Masculino", 17, 80.0, complete))
        assertNull(AssessmentMath.pollock7("Masculino", 30, 80.0, complete.dropLast(1)))
        assertNull(AssessmentMath.pollock7("Masculino", 30, 80.0, listOf<Double?>(10.0, 20.0, 30.0, null, 50.0, 60.0, 70.0)))
        assertNull(AssessmentMath.pollock7("Masculino", 30, 80.0, complete.toMutableList().also { it[3] = 0.0 }))
    }

    @Test fun analysisWindowRequiresThreeDistinctDatesAndTwentyOneDays() {
        assertTrue(AnalysisMath.isEligibleWindow(listOf("2026-01-01", "2026-01-15", "2026-02-01")))
        assertTrue(!AnalysisMath.isEligibleWindow(listOf("2026-01-01", "2026-02-01")))
        assertTrue(!AnalysisMath.isEligibleWindow(listOf("2026-01-01", "2026-01-01", "2026-02-01")))
        assertTrue(!AnalysisMath.isEligibleWindow(listOf("invalida", "2026-01-15", "2026-02-01")))
    }

    @Test fun analysisTrendReportsObservedChangeWithoutExtrapolation() {
        val result = AnalysisMath.trend(
            listOf("2026-02-01", "2026-01-01", "2026-01-15"), listOf(69.0, 70.0, 71.0)
        )!!
        assertEquals(70.0, result.first, 0.0)
        assertEquals(69.0, result.latest, 0.0)
        assertEquals(-1.0, result.absoluteChange, 0.0)
        assertTrue(result.directionChanged)
        assertEquals(-1.0, result.changePer30Days, 0.1)
    }

    @Test fun analysisTrendRejectsMissingValuesInvalidDatesAndShortSeries() {
        assertNull(AnalysisMath.trend(listOf("2026-01-01", "2026-01-15", "2026-02-01"), listOf(70.0, null, 69.0)))
        assertNull(AnalysisMath.trend(listOf("2026-01-01", "2026-01-15", "2026-02-01"), listOf(70.0, Double.NaN, 69.0)))
        assertNull(AnalysisMath.trend(listOf("2026-01-01", "2026-01-02", "2026-01-03"), listOf(70.0, 69.0, 68.0)))
    }

    @Test fun pairedPollockAndBioimpedanceAreComparedOnlyWhenBothAreRecorded() {
        val row = Assessment(
            id = 1, studentId = 7, date = "2026-01-10", weightKg = 80.0, heightCm = 180.0,
            waistCm = 88.0, hipCm = 100.0, notes = "", sex = "Masculino", ageYears = 30,
            tricepsFoldMm = 10.0, subscapularFoldMm = 20.0, chestFoldMm = 30.0,
            axillaryFoldMm = 40.0, suprailiacFoldMm = 50.0, abdominalFoldMm = 60.0, thighFoldMm = 70.0,
            bioimpedanceBodyFatPercent = 28.8, bioimpedanceDevice = "BIA InBody"
        )
        val evidence = AnalysisMath.pairedEvidence(listOf(row)).single()
        assertEquals("2026-01-10", evidence.date)
        assertEquals(28.8, evidence.bioimpedancePercent, 0.0)
        assertEquals(4.3, evidence.differencePoints, 0.1)
        assertTrue(AnalysisMath.pairedEvidence(listOf(row.copy(bioimpedanceBodyFatPercent = null))).isEmpty())
    }

    @Test fun analysisCompletenessAndReportStateTheirLimits() {
        assertEquals(50, AnalysisMath.completeness(6, 3))
        assertNull(AnalysisMath.completeness(0, 0))
        val report = AnalysisMath.report("Ana", emptyList())
        assertTrue(report.contains("Dados insuficientes"))
        assertTrue(report.contains("não diagnóstico"))
        assertTrue(report.contains("Bioimpedância não é registrada"))
    }

    @Test fun basalMetabolicFormulasAndHeartRateRespectInputBounds() {
        assertEquals(1695.7, AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(1648.8, AssessmentMath.mifflinStJeor("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(187, AssessmentMath.maximumHeartRate(30))
        assertNull(AssessmentMath.maximumHeartRate(17))
        assertNull(AssessmentMath.maximumHeartRate(101))
        assertNull(AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 17))
    }
}
