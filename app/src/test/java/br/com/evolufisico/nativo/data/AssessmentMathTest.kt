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

    @Test fun basalMetabolicFormulasAndHeartRateRespectInputBounds() {
        assertEquals(1696.7, AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(1648.8, AssessmentMath.mifflinStJeor("Masculino", 70.0, 175.0, 30)!!, 0.1)
        assertEquals(187, AssessmentMath.maximumHeartRate(30))
        assertNull(AssessmentMath.maximumHeartRate(17))
        assertNull(AssessmentMath.maximumHeartRate(101))
        assertNull(AssessmentMath.harrisBenedict("Masculino", 70.0, 175.0, 17))
    }
}
