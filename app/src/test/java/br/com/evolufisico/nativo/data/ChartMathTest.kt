package br.com.evolufisico.nativo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartMathTest {
    @Test fun spacedEvaluationsUseCalendarDaysInsteadOfUniformIndex() {
        val coordinates = ChartMath.datePositions(listOf("2026-01-01", "2026-01-11", "2026-01-31"))!!
        assertEquals(0f, coordinates.first(), 0.0001f)
        assertEquals(1f / 3f, coordinates[1], 0.001f)
        assertEquals(1f, coordinates.last(), 0.0001f)
    }

    @Test fun rejectsEqualAndReversedDates() {
        assertNull(ChartMath.datePositions(listOf("2026-01-01", "2026-01-01")))
        assertNull(ChartMath.datePositions(listOf("2026-02-01", "2026-01-01")))
    }

    @Test fun rejectsUnparseableAndSinglePoint() {
        assertNull(ChartMath.datePositions(listOf("2026-01-01")))
        assertNull(ChartMath.datePositions(listOf("data", "2026-01-01")))
    }

    @Test fun leapDayKeepsRealSpacing() {
        val x = ChartMath.datePositions(listOf("2024-02-28", "2024-02-29", "2024-03-01"))!!
        assertEquals(.5f, x[1], 0.0001f)
        assertTrue(x.all { it in 0f..1f })
    }
}
