package br.com.evolufisico.android.reporting

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CsvReportsTest {
    private val snapshot=BackupSnapshot(
        clients=listOf(Client("c1","João; \"Silva\""),Client("c2","Maria")),
        assessments=emptyList(),
        charges=listOf(Charge("p1","c1","Mensalidade",2550,"2026-10-25")),
        forms=emptyList(),
        operations=OperationsData(
            appointments=listOf(Appointment("a1","c1","2026-10-20T10:00",60,"Treino"),
                Appointment("a2","c2","2026-10-20T11:00",60,"Avaliação")),
            goals=listOf(ClientGoal("g1","c1","Peso","kg",90.0,80.0,85.0,"2026-12-01")),
            services=listOf(ServiceItem("s1","Treino",2500,60)),
            notes=listOf(ClientNote("n1","c1","2026-10-20","Treino",
                "Anotação teste","Corpo fictício.",true))
        )
    )
    @Test fun csvEscapingProtectsStructureAndFormulas() {
        assertEquals("\"João; \"\"Silva\"\"\"",CsvReports.escape("João; \"Silva\""))
        assertEquals("'=SUM(A1:A9)",CsvReports.escape("=SUM(A1:A9)"))
        assertEquals("\"a\nb\"",CsvReports.escape("a\nb"))
        assertTrue(CsvReports.generate(snapshot,ReportKind.CLIENTS).toString(Charsets.UTF_8)
            .startsWith("\uFEFFID;Cliente\r\n"))
    }
    @Test fun clientAndPeriodFilter() {
        val csv=CsvReports.generate(snapshot,ReportKind.APPOINTMENTS,
            ReportFilter("c1",LocalDate.parse("2026-10-20"),LocalDate.parse("2026-10-20")))
            .toString(Charsets.UTF_8)
        assertTrue(csv.contains("Treino"))
        assertFalse(csv.contains("Avaliação"))
        assertTrue(runCatching {
            ReportFilter("c1",LocalDate.parse("2027-01-01"),LocalDate.parse("2026-01-01"))
        }.isFailure)
    }
    @Test fun exportedNotesCanBeFilteredByClient() {
        val result=CsvReports.generate(snapshot,ReportKind.NOTES,
            ReportFilter("c1")).toString(Charsets.UTF_8)
        assertTrue(result.contains("Anotação teste"))
        assertTrue(result.contains("Corpo fictício."))
    }
    @Test fun summaryUsesIntegerCents() {
        val csv=CsvReports.generate(snapshot,ReportKind.OVERVIEW).toString(Charsets.UTF_8)
        assertTrue(csv.contains("Pendente (centavos);2550"))
        assertTrue(csv.contains("Atendimentos;2"))
        assertTrue(runCatching{
            CsvReports.generate(snapshot,ReportKind.CLIENTS,ReportFilter("absent"))
        }.isFailure)
    }
}
