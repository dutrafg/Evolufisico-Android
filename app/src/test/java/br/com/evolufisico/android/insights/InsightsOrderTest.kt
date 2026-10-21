package br.com.evolufisico.android.insights

import org.junit.Assert.*
import org.junit.Test

class InsightsOrderTest {
    @Test fun legacyAndUnknownValuesNeverLoseCategories() {
        assertEquals(InsightsOrder.standard,InsightsOrder.parse(null))
        assertEquals(InsightsOrder.standard,InsightsOrder.parse(""))
        assertEquals(
            listOf(InsightKind.GOAL,InsightKind.BILLING,InsightKind.APPOINTMENT),
            InsightsOrder.parse("GOAL,GOAL,desconhecido,BILLING")
        )
    }

    @Test fun movedOrderRoundTripsAndCanBeRestored() {
        val moved=InsightsOrder.move(InsightsOrder.standard,InsightKind.GOAL,-1)
        val first=InsightsOrder.move(moved,InsightKind.GOAL,-1)
        assertEquals(InsightKind.GOAL,first.first())
        assertEquals(first,InsightsOrder.parse(InsightsOrder.serialize(first)))
        assertEquals(InsightsOrder.standard,InsightsOrder.parse(null))
        assertEquals(first,InsightsOrder.move(first,InsightKind.GOAL,-1))
        assertEquals(first,InsightsOrder.move(first,InsightKind.BILLING,0))
    }

    @Test fun sortingChangesOnlyViewPriorityAndKeepsOriginalData() {
        val records=listOf(
            Insight(InsightKind.BILLING,"client1","Conta","2026-10-21",1200),
            Insight(InsightKind.GOAL,"client2","Meta","2026-10-19"),
            Insight(InsightKind.APPOINTMENT,"client3","Atendimento","2026-10-20T12:00"),
            Insight(InsightKind.BILLING,"client4","Outra conta","2026-10-18",850)
        )
        val original=records.toList()
        val order=listOf(InsightKind.GOAL,InsightKind.APPOINTMENT,InsightKind.BILLING)
        val sorted=InsightsOrder.sort(records,order)
        assertEquals(listOf(InsightKind.GOAL,InsightKind.APPOINTMENT,
            InsightKind.BILLING,InsightKind.BILLING),sorted.map { it.kind })
        assertEquals("Outra conta",sorted[2].title)
        assertEquals(original,records)
        assertEquals(2050L,sorted.sumOf { it.amountCents })
    }
}
