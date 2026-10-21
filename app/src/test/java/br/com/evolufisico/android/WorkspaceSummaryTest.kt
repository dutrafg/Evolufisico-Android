package br.com.evolufisico.android

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationsData
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class WorkspaceSummaryTest {
    @Test fun countsOnlyScheduledTodayAndReportsNextWithoutModifyingData() {
        val today=LocalDateTime.parse("2026-10-20T09:00")
        val ops=OperationsData(appointments=listOf(
            Appointment("a","1","2026-10-20T10:00",60,"Avaliação"),
            Appointment("b","1","2026-10-19T10:00",60,"Treino"),
            Appointment("c","1","2026-10-20T12:00",60,"Treino","Concluído")
        ))
        val snap=BackupSnapshot(clients=listOf(Client("1","Maria")),assessments=emptyList(),
            charges=emptyList(),operations=ops)
        val metrics=WorkspaceSummary.calculate(snap,today)
        assertEquals(1,metrics.clients)
        assertEquals(1,metrics.appointmentsToday)
        assertEquals(1,metrics.overdueAppointments)
        assertEquals("a",metrics.next.single().id)
        assertEquals(0,metrics.pendingCharges)
    }
    @Test fun agendaSummaryIncludesWeekAndMonthlyAttendanceWithoutCancellations() {
        val now=LocalDateTime.parse("2026-10-21T09:00")
        val items=listOf(
            Appointment("now","1","2026-10-21T10:00",60,"Avaliação"),
            Appointment("week","1","2026-10-27T10:00",60,"Retorno"),
            Appointment("outside","1","2026-10-28T10:00",60,"Retorno"),
            Appointment("overdue","1","2026-10-20T10:00",60,"Retorno"),
            Appointment("complete1","1","2026-10-19T10:00",60,"Treino","Concluído"),
            Appointment("complete2","1","2026-10-18T10:00",60,"Treino","Concluído"),
            Appointment("missed","1","2026-10-17T10:00",60,"Treino","Faltou"),
            Appointment("canceled","1","2026-10-17T11:00",60,"Treino","Cancelado"),
            Appointment("othermonth","1","2026-09-21T10:00",60,"Treino","Faltou"))
        val snapshot=BackupSnapshot(
            clients=listOf(Client("1","Maria")),assessments=emptyList(),
            charges=emptyList(),operations=OperationsData(appointments=items))
        val metrics=WorkspaceSummary.calculate(snapshot,now)
        assertEquals(1,metrics.appointmentsToday)
        assertEquals(2,metrics.appointmentsSevenDays)
        assertEquals(1,metrics.overdueAppointments)
        assertEquals(66,metrics.attendancePercent)
    }
    @Test fun attendanceIsUndefinedWithoutCompletionsOrAbsences() {
        val empty=WorkspaceSummary.calculate(
            BackupSnapshot(emptyList(),emptyList(),emptyList()),
            LocalDateTime.parse("2026-10-21T09:00"))
        assertNull(empty.attendancePercent)
        assertEquals(0,empty.appointmentsSevenDays)
    }
    @Test fun actionableAlertsShowOnlyNeededShortcuts() {
        val m=WorkspaceMetrics(4,2,1,3,emptyList())
        val actions=WorkspaceAttention.actions(m)
        assertEquals(listOf("AGENDA","AGENDA","BILLING"),actions.map{it.route})
        assertTrue(WorkspaceAttention.actions(WorkspaceMetrics(0,0,0,0,emptyList())).isEmpty())
    }
    @Test fun emptyDatabaseProducesZeroes() {
        val m=WorkspaceSummary.calculate(BackupSnapshot(emptyList(),emptyList(),emptyList()),
            LocalDateTime.parse("2026-10-20T09:00"))
        assertEquals(0,m.clients)
        assertTrue(m.next.isEmpty())
    }
}
