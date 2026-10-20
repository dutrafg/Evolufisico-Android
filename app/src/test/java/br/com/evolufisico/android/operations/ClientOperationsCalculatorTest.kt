package br.com.evolufisico.android.operations

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.ops.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ClientOperationsCalculatorTest {
    @Test fun correctPerClientAndPaymentStatus() {
        val records=BackupSnapshot(
            clients=listOf(Client("c1","Ana"),Client("c2","Bruno")),
            assessments=emptyList(),
            charges=listOf(
                Charge("a","c1","Mensalidade",5000,"2026-10-01"),
                Charge("b","c1","Recebida",3000,"2026-10-05","2026-10-06"),
                Charge("c","c2","Futura",2500,"2026-10-30")
            ),
            operations=OperationsData(
                appointments=listOf(
                    Appointment("1","c1","2026-10-01T10:00",60,"Treino","Concluído"),
                    Appointment("2","c1","2026-10-02T10:00",60,"Treino","Faltou"),
                    Appointment("3","c1","2026-10-03T10:00",60,"Treino","Cancelado"),
                    Appointment("4","c2","2026-10-20T10:00",60,"Treino","Agendado")
                ),
                goals=listOf(
                    ClientGoal("g","c1","Peso","kg",80.0,75.0,77.0,"2026-12-01")
                )
            )
        )
        val rows=ClientOperationsCalculator.calculate(records,LocalDate.of(2026,10,20))
        val ana=rows.first()
        assertEquals("c1",ana.clientId)
        assertEquals(5000L,ana.overdueCents)
        assertEquals(5000L,ana.pendingCents)
        assertEquals(3000L,ana.receivedCents)
        assertEquals(50,ana.attendancePercent)
        assertEquals(1,ana.cancelled)
        assertEquals(1,ana.activeGoals)
        val bruno=rows[1]
        assertEquals(2500L,bruno.pendingCents)
        assertEquals(0L,bruno.overdueCents)
        assertNull(bruno.attendancePercent)
    }
    @Test fun emptySnapshotIsSafe() {
        assertTrue(ClientOperationsCalculator.calculate(
            BackupSnapshot(emptyList(),emptyList(),emptyList())
        ).isEmpty())
    }
}
