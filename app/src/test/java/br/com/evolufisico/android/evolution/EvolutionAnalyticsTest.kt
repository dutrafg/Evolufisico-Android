package br.com.evolufisico.android.evolution

import br.com.evolufisico.android.data.Assessment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EvolutionAnalyticsTest {
    private fun a(id: String, client: String, date: String, weight: Double,
                  waist: Double? = null, hip: Double? = null) =
        Assessment(id, client, date, "Iniciante", "", "", 3, weight, 180.0,
            waist, hip, null, null)

    @Test fun onlySelectedClientIsIncludedAndSorted() {
        val entries = listOf(
            a("2", "ana", "2026-02-11", 82.0),
            a("3", "outra", "2026-03-01", 130.0),
            a("1", "ana", "2026-01-12", 80.0)
        )
        val s = EvolutionAnalytics.build(entries, "ana", EvolutionMetric.WEIGHT, EvolutionPeriod.ALL)
        assertEquals(listOf(80.0, 82.0), s.points.map { it.value })
        assertEquals(2.0, s.absoluteChange!!, 0.0001)
        assertEquals(2.5, s.percentChange!!, 0.0001)
    }

    @Test fun monthlyChoosesLastMeasurementThatHasMetric() {
        val entries = listOf(
            a("a", "one", "2026-01-02", 70.0, 70.0),
            a("b", "one", "2026-01-20", 71.0, null),
            a("c", "one", "2026-02-20", 72.0, 67.0)
        )
        val s = EvolutionAnalytics.build(entries, "one", EvolutionMetric.WAIST, EvolutionPeriod.MONTHLY)
        assertEquals(listOf(70.0, 67.0), s.points.map { it.value })
        assertEquals(1, s.ignoredInvalid)
    }

    @Test fun quarterlyChoosesLastRecordedDay() {
        val entries = listOf(
            a("a", "one", "2025-11-15", 70.0),
            a("b", "one", "2026-01-21", 71.0),
            a("c", "one", "2026-03-31", 72.0),
            a("d", "one", "2026-04-10", 73.0)
        )
        val s = EvolutionAnalytics.build(entries, "one", EvolutionMetric.WEIGHT, EvolutionPeriod.QUARTERLY)
        assertEquals(listOf(70.0, 72.0, 73.0), s.points.map { it.value })
    }

    @Test fun missingOrInvalidDataDoesNotBreakCharts() {
        val entries = listOf(
            a("1", "one", "bad", 80.0, 75.0),
            a("2", "one", "2026-01-01", Double.NaN, null),
            a("3", "one", "2026-02-01", 81.0, 76.0)
        )
        val s = EvolutionAnalytics.build(entries, "one", EvolutionMetric.WAIST, EvolutionPeriod.ALL)
        assertEquals(1, s.points.size)
        assertEquals(2, s.ignoredInvalid)
        assertNull(s.absoluteChange)
        assertNull(s.percentChange)
    }

    @Test fun ratioAndSameDateTieAreDeterministic() {
        val entries = listOf(
            a("b", "one", "2026-01-01", 82.0, 85.0, 100.0),
            a("a", "one", "2026-01-01", 80.0, 90.0, 100.0)
        )
        val s = EvolutionAnalytics.build(entries, "one", EvolutionMetric.WHR, EvolutionPeriod.MONTHLY)
        assertEquals(1, s.points.size)
        assertEquals("b", s.points.first().assessmentId)
        assertEquals(0.85, s.points.first().value, 0.00001)
    }

    @Test fun emptyHistoryGivesNoFakeTrend() {
        val s = EvolutionAnalytics.build(emptyList(), "one",
            EvolutionMetric.BMI, EvolutionPeriod.MONTHLY)
        assertTrue(s.points.isEmpty())
        assertNull(s.absoluteChange)
    }
}
