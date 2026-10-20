package br.com.evolufisico.android.timeline

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import org.junit.Assert.*
import org.junit.Test

class ClientTimelineTest {
    @Test fun isolatesClientAndSortsNewestFirst() {
        val data=BackupSnapshot(
            clients=listOf(Client("a","Ana"),Client("b","Bia")),
            assessments=emptyList(),
            charges=listOf(Charge("c","a","Diária",2500,"2026-10-10","2026-10-15"),
                Charge("d","b","Outra",5000,"2026-10-19")),
            operations=OperationsData(appointments=listOf(
                Appointment("p","a","2026-10-22T10:00",60,"Treino"),
                Appointment("q","b","2026-10-23T11:00",60,"Avaliação")),
                goals=listOf(ClientGoal("g","a","Peso","kg",90.0,80.0,85.0,"2026-11-30")))
        )
        val events=ClientTimeline.build(data,"a")
        assertEquals(4,events.size)
        assertEquals("2026-11-30",events.first().dateIso)
        assertFalse(events.any{it.title=="Outra"})
        assertTrue(events.any{it.title.startsWith("Recebimento")})
        assertTrue(runCatching{ClientTimeline.build(data,"unknown")}.isFailure)
    }
}
