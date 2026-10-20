package br.com.evolufisico.android.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AssessmentRulesTest {
    private fun item(
        weight: Double = 80.0, height: Double = 180.0,
        clientId: String = "cliente-1",
        date: String = LocalDate.now().toString(),
        waist: Double? = 90.0, hip: Double? = 100.0
    ) = Assessment(
        "avaliacao-1", clientId, date, "Iniciante", "Bem-estar", "",
        3, weight, height, waist, hip, null, null
    )
    private val clients = setOf("cliente-1")

    @Test fun validAssessment() = assertNull(AssessmentRules.validate(item(), clients))
    @Test fun missingClientRefused() {
        assertEquals("Selecione um cliente válido.",
            AssessmentRules.validate(item(clientId = "inexistente"), clients))
    }
    @Test fun futureDateRefused() {
        assertTrue(AssessmentRules.validate(item(date = LocalDate.now().plusDays(1).toString()), clients)
            ?.contains("data") == true)
    }
    @Test fun invalidNumbersRefused() {
        assertTrue(AssessmentRules.validate(item(weight = Double.NaN), clients)
            ?.contains("peso") == true)
        assertTrue(AssessmentRules.validate(item(height = 20.0), clients)
            ?.contains("altura") == true)
        assertTrue(AssessmentRules.validate(item(waist = Double.POSITIVE_INFINITY), clients)
            ?.contains("cintura") == true)
    }
    @Test fun measuresComputedCorrectly() {
        assertEquals(24.7, AssessmentRules.bmi(80.0, 180.0), 0.001)
        assertEquals(0.9, AssessmentRules.waistHipRatio(90.0, 100.0)!!, 0.001)
        assertNull(AssessmentRules.waistHipRatio(null, 100.0))
        assertNull(AssessmentRules.waistHipRatio(90.0, 0.0))
    }
    @Test fun parseBrazilianDecimal() {
        assertEquals(71.5, AssessmentRules.parseNumber("71,5", true)!!, 0.001)
        assertTrue(AssessmentRules.parseNumber("1,2,3", true)!!.isNaN())
        assertNull(AssessmentRules.parseNumber("", false))
    }
}
