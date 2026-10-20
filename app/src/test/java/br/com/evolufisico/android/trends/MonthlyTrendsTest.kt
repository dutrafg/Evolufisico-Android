package br.com.evolufisico.android.trends

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationsData
import org.junit.Assert.*
import org.junit.Test
import java.time.YearMonth

class MonthlyTrendsTest {
    private val snapshot=BackupSnapshot(
        clients=listOf(Client("a","Ana"),Client("b","Bia")),
        assessments=emptyList(),
        charges=listOf(
            Charge("1","a","Setembro",10000,"2026-09-30","2026-10-01"),
            Charge("2","a","Outubro",3000,"2026-10-20"),
            Charge("3","b","Outro cliente",4500,"2026-10-20")
        ),operations=OperationsData(
            appointments=listOf(
                Appointment("p1","a","2026-10-19T10:00",60,"Treino","Concluído"),
                Appointment("p2","a","2026-10-21T10:00",60,"Treino","Agendado"),
                Appointment("p3","b","2026-10-22T10:00",60,"Treino","Cancelado")
            ))
    )
    @Test fun separatesBillingMonthFromReceiptMonth() {
        val oct=MonthlyTrends.collect(snapshot,YearMonth.of(2026,10),1).single()
        assertEquals(7500L,oct.billedCents)
        assertEquals(10000L,oct.receivedCents)
        assertEquals(7500L,oct.pendingCents)
        assertEquals(1,oct.completed)
        assertEquals(1,oct.scheduled)
        assertEquals(1,oct.cancelled)
    }
    @Test fun clientFilterAndChronologicalOrder() {
        val rows=MonthlyTrends.collect(snapshot,YearMonth.of(2026,10),2,"a")
        assertEquals(listOf(YearMonth.of(2026,9),YearMonth.of(2026,10)),
            rows.map{it.month})
        assertEquals(10000L,rows[0].billedCents)
        assertEquals(3000L,rows[1].billedCents)
        assertEquals(0,rows[1].cancelled)
        assertTrue(runCatching{MonthlyTrends.collect(snapshot,months=0)}.isFailure)
    }
}
