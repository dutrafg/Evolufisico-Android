package br.com.evolufisico.android.backup

import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.Client
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class IntegrityReportTest {
    private fun a(client: String) = Assessment(
        "a", client, LocalDate.now().toString(), "Iniciante", "", "", 0,
        65.0, 170.0, null, null, null, null
    )
    @Test fun checksCrossModuleOrphans() {
        val client = Client("c","Pessoa")
        val charge = Charge("charge", "c", "Consulta", 2000, "2026-10-20")
        assertNull(IntegrityReport.inspect(listOf(client),listOf(a("c")),listOf(charge)))
        assertNotNull(IntegrityReport.inspect(listOf(client),listOf(a("missing")), emptyList()))
        assertNotNull(IntegrityReport.inspect(listOf(client), emptyList(),
            listOf(charge.copy(clientId = "missing"))))
        assertNotNull(IntegrityReport.inspect(listOf(client,client),emptyList(),emptyList()))
    }
}
