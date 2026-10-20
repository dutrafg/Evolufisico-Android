package br.com.evolufisico.android.insights

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class InsightsTest {
    private val date=LocalDate.parse("2026-10-20")
    @Test fun considersDueDatesAndNoDoubleCounting() {
        val snapshot=BackupSnapshot(listOf(Client("c","Pessoa")),emptyList(),listOf(
            Charge("old","c","Mensalidade",2500,"2026-10-19"),
            Charge("soon","c","Diária",3000,"2026-10-23"),
            Charge("received","c","Pago",5000,"2026-10-01","2026-10-01")
        ),operations=OperationsData(
            appointments=listOf(
                Appointment("a","c","2026-10-22T09:00",60,"Treinamento"),
                Appointment("b","c","2026-10-15T09:00",60,"Avaliação")
            ),goals=listOf(
                ClientGoal("g","c","Carga","kg",20.0,40.0,25.0,"2026-10-18")
            )
        ))
        val result=Insights.calculate(snapshot,date)
        assertEquals(2500L,result.overdueCents)
        assertEquals(3000L,result.dueSoonCents)
        assertEquals(1,result.lateGoals)
        assertEquals(1,result.upcomingAppointments)
        assertEquals(5,result.alerts.size)
        assertTrue(result.alerts.any{it.title.contains("passada")})
        assertEquals(2,result.alerts.count{it.kind==InsightKind.BILLING})
    }
    @Test fun emptyDataDoesNotProduceAlerts() {
        val v=Insights.calculate(BackupSnapshot(emptyList(),emptyList(),emptyList()),date)
        assertEquals(0,v.alerts.size)
        assertEquals(0L,v.overdueCents)
    }
}
