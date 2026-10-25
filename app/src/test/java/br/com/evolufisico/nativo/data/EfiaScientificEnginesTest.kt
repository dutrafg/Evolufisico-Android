package br.com.evolufisico.nativo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EfiaScientificEnginesTest {
    private fun measure(
        date: String, id: Long, weight: Double,
        device: String = "BIA teste", studentId: Long = 10L,
        fat: Double? = 25.0, lean: Double? = 60.0
    ) = Assessment(
        id = id, studentId = studentId, date = date,
        weightKg = weight, heightCm = 180.0, waistCm = 85.0,
        hipCm = 100.0, notes = "", bioimpedanceBodyFatPercent = fat,
        bioimpedanceLeanMassKg = lean, bioimpedanceDevice = device
    )

    private fun series() = listOf(
        measure("2026-01-01", 1L, 80.0),
        measure("2026-01-31", 2L, 79.0, lean = 59.0),
        measure("2026-03-02", 3L, 78.0, lean = 58.0)
    )

    @Test fun fiveEnginesProvideActualSafeResults() {
        val result = EfiaScientificEngines.analyze(series())
        assertTrue(result.eligible)
        assertTrue(result.ef78QualityIssues.any { it.contains("Padronização") })
        assertEquals(-2.0, result.ef79Changes.getValue("peso").change!!, 0.0)
        assertNull(result.ef79Changes.getValue("peso").mdc95)
        assertNull(result.ef79Changes.getValue("peso").detectable)
        assertEquals(3, result.ef80Balances.size)
        assertTrue(result.ef81RecordedFacts.isEmpty())
        assertEquals("preliminar", result.ef82Trends.getValue("peso").status)
        assertEquals(-1.0, result.ef82Trends.getValue("peso").robust30!!, 0.0)
        assertTrue(result.ef82Trends.getValue("peso").outlierDates.isEmpty())
        assertTrue(result.asText().contains("EF82"))
    }

    @Test fun identityAndDateConditionsBlockAllComparativeEngines() {
        for (rows in listOf(
            series().take(2),
            series().mapIndexed { i, m -> if (i == 2) m.copy(studentId = 999L) else m },
            series().mapIndexed { i, m -> if (i == 2) m.copy(date = "2026-01-31") else m },
            series().mapIndexed { i, m -> if (i == 2) m.copy(id = 2L) else m },
            listOf(measure("2026-01-01", 1L, 80.0), measure("2026-01-30", 2L, 79.0), measure("2026-02-03", 3L, 78.0))
        )) {
            val result = EfiaScientificEngines.analyze(rows)
            assertFalse(result.eligible)
            assertNull(result.ef79Changes.getValue("peso").change)
            assertEquals("insuficiente", result.ef82Trends.getValue("peso").status)
        }
    }

    @Test fun changingBioimpedanceDeviceBlocksFatButNotWeight() {
        val different = series().mapIndexed { index, row ->
            if (index == 2) row.copy(bioimpedanceDevice = "BIA diferente") else row
        }
        val result = EfiaScientificEngines.analyze(different)
        assertTrue(result.eligible)
        assertNull(result.ef79Changes.getValue("gordura").change)
        assertFalse(result.ef79Changes.getValue("gordura").comparable)
        assertEquals(-2.0, result.ef79Changes.getValue("peso").change!!, 0.0)
        assertEquals("insuficiente", result.ef82Trends.getValue("massaMagra").status)
    }

    @Test fun missingDevicePreventsComparingCompositionWithoutHidingWeight() {
        val badDevice = series().map { it.copy(bioimpedanceDevice = "") }
        val result = EfiaScientificEngines.analyze(badDevice)
        assertTrue(result.eligible)
        assertNull(result.ef79Changes.getValue("gordura").change)
        assertEquals(-2.0, result.ef79Changes.getValue("peso").change!!, 0.0)
    }

    @Test fun largerThanTotalLeanMassIsFlaggedAndSuppressesComposition() {
        val inconsistent = series().mapIndexed { index, row ->
            if (index == 1) row.copy(bioimpedanceLeanMassKg = 95.0) else row
        }
        val result = EfiaScientificEngines.analyze(inconsistent)
        assertTrue(result.ef80Balances.any { it.status.startsWith("incompatível") })
        assertNull(result.ef79Changes.getValue("gordura").change)
        assertNull(result.ef79Changes.getValue("massaMagra").change)
        assertEquals("insuficiente", result.ef82Trends.getValue("gordura").status)
    }

    @Test fun mdcRequiresPositiveSemAndDeclaredSource() {
        val noSource = EfiaScientificEngines.analyze(series(), sem = mapOf("peso" to 0.2))
        assertNull(noSource.ef79Changes.getValue("peso").mdc95)
        val supplied = EfiaScientificEngines.analyze(series(),
            sem = mapOf("peso" to 0.2), semSources = mapOf("peso" to "estudo do instrumento"))
        assertEquals(0.55, supplied.ef79Changes.getValue("peso").mdc95!!, 0.01)
        assertEquals(true, supplied.ef79Changes.getValue("peso").detectable)
    }

    @Test fun notesNeverBecomeInventedAdherenceEvents() {
        val records = series().map { it.copy(notes = "A pessoa diz que iniciou treino e mudou a dieta.") }
        val result = EfiaScientificEngines.analyze(records)
        assertTrue(result.ef81RecordedFacts.isEmpty())
        assertTrue(result.asText().contains("não há inferência causal"))
    }

    @Test fun analysisRejectsIneligibleInvalidWeightsWithoutGuessing() {
        val records = series().map { it.copy(weightKg = 19.0, waistCm = null, bioimpedanceBodyFatPercent = null, bioimpedanceLeanMassKg = null) }
        val result = EfiaScientificEngines.analyze(records)
        assertFalse(result.eligible)
        assertNull(result.ef79Changes.getValue("peso").change)
    }
    @Test fun explicitlyRegisteredEventsAreVisibleButNotCausal() {
        val rows = series().mapIndexed { index, row ->
            if (index == 1) row.copy(contextEvent = "Início de treino") else row
        }
        val result = EfiaScientificEngines.analyze(rows)
        assertEquals(1, result.ef81RecordedFacts.size)
        assertTrue(result.ef81RecordedFacts.single().contains("2026-01-31: Início de treino"))
        assertTrue(result.ef81RecordedFacts.single().contains("sem causalidade"))
    }

}
