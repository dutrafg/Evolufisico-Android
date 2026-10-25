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

    @Test fun basalMetabolicFormulasAndHeartRateRespectInputBounds() {
        assertEquals(1695.7, AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(1648.8, AssessmentMath.mifflinStJeor("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(187, AssessmentMath.maximumHeartRate(30))
        assertNull(AssessmentMath.maximumHeartRate(17))
        assertNull(AssessmentMath.maximumHeartRate(101))
        assertNull(AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 17))
    }
}
