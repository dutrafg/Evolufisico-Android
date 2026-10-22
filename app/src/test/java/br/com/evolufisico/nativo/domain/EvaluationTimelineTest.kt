package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import org.junit.Assert.*
import org.junit.Test

class EvaluationTimelineTest {
    @Test fun sameDateChoicesRemainBoundToUniqueIds() {
        val items=listOf(Evaluation(id="one",date="2026-02-01"),Evaluation(id="two",date="2026-02-01"))
        val choices=EvaluationTimeline.choices(items)
        assertEquals(listOf("one","two"),choices.map { it.id })
        assertEquals(listOf("2026-02-01 • 1","2026-02-01 • 2"),choices.map { it.label })
    }
    @Test fun uniqueDatesKeepSimpleLabels() {
        val choices=EvaluationTimeline.choices(listOf(Evaluation(id="a",date="2026-01-01"),Evaluation(id="b",date="2026-02-01")))
        assertEquals(listOf("2026-01-01","2026-02-01"),choices.map { it.label })
    }
    @Test fun pointsScaleElapsedDaysNotIndex() {
        val points=EvaluationTimeline.points(listOf("2026-01-01" to 10.0,"2026-01-11" to 20.0,"2026-01-21" to 30.0))
        assertEquals(listOf(0f,.5f,1f),points.map { it.x })
    }
    @Test fun equalDatesShareXAndConstantSeriesCenter() {
        val points=EvaluationTimeline.points(listOf("2026-01-01" to 4.0,"2026-01-01" to 4.0))
        assertEquals(listOf(.5f,.5f),points.map { it.x })
        assertEquals(listOf(.5f,.5f),points.map { it.y })
    }
    @Test fun reversedInputOrderIsPreserved() {
        val points=EvaluationTimeline.points(listOf("2026-02-01" to 2.0,"2026-01-01" to 1.0))
        assertEquals(listOf(1f,0f),points.map { it.x })
    }
    @Test fun invalidDateRejectsWholeSeries() {
        assertTrue(EvaluationTimeline.points(listOf("2026-01-01" to 1.0,"n/a" to 2.0)).isEmpty())
    }
    @Test fun nonFiniteValuesRejectWholeSeries() {
        assertTrue(EvaluationTimeline.points(listOf("2026-01-01" to 1.0,"2026-02-01" to Double.NaN)).isEmpty())
    }
    @Test fun emptySeriesProducesNoPoints() = assertTrue(EvaluationTimeline.points(emptyList()).isEmpty())
}
