package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class MetricsTest {
    @Test fun bmiCalculatesWithCm() = assertEquals(25.0, Metrics.bmi(100.0, 200.0)!!, 1e-8)
    @Test fun preventsZeroDivision() { assertNull(Metrics.bmi(80.0, 0.0)); assertNull(Metrics.waistHipRatio(80.0, 0.0)) }
    @Test fun doesNotPenalizeFirstTimeUsers() { assertNull(Metrics.adherence(false, null, false, null)); assertEquals(90.0, Metrics.adherence(false, 0, true, 90)!!, 1e-8) }
    @Test fun averagesOnlyApplicableScores() = assertEquals(60.0, Metrics.adherence(true, 40, true, 80)!!, 1e-8)
    @Test fun fatMassRequiresMeasuredPercentage() { assertNull(Metrics.fatMassKg(90.0, null)); assertEquals(18.0, Metrics.fatMassKg(90.0, 20.0)!!, 1e-8) }
}
