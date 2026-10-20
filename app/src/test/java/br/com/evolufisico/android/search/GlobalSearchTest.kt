package br.com.evolufisico.android.search

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import org.junit.Assert.*
import org.junit.Test

class GlobalSearchTest {
    private val sample=BackupSnapshot(
        clients=listOf(Client("c","José da Silva")),
        assessments=emptyList(),
        charges=listOf(Charge("ch","c","Mensalidade",2500,"2026-10-20")),
        forms=emptyList(),
        operations=OperationsData(
            appointments=listOf(Appointment("a","c","2026-10-20T12:00",60,"Treinamento")),
            goals=listOf(ClientGoal("g","c","Redução de peso","kg",100.0,90.0,
                95.0,"2026-12-31")),
            services=listOf(ServiceItem("s","Avaliação física",2500,60)),
            notes=listOf(ClientNote("n","c","2026-10-20","Geral",
                "Alongamento","Evolução fictícia",true))
        )
    )
    @Test fun accentCaseAndClientNames() {
        assertTrue(GlobalSearch.search(sample,"jose").any { it.kind==SearchKind.CLIENTS })
        assertTrue(GlobalSearch.search(sample,"TREINAMENTO").any {
            it.kind==SearchKind.APPOINTMENTS
        })
        assertTrue(GlobalSearch.search(sample,"reduCAO").any { it.kind==SearchKind.GOALS })
        assertTrue(GlobalSearch.search(sample,"").isEmpty())
        assertTrue(GlobalSearch.search(sample,"ALONGAMENTO").any {
            it.kind==SearchKind.NOTES
        })
    }
    @Test fun filteringAndLimit() {
        val only=GlobalSearch.search(sample,"jose",SearchKind.BILLING)
        assertEquals(1,only.size)
        assertEquals(SearchKind.BILLING,only[0].kind)
        assertEquals(1,GlobalSearch.search(sample,"jose",limit=1).size)
        assertTrue(runCatching { GlobalSearch.search(sample,"jose",limit=0) }.isFailure)
    }
}
