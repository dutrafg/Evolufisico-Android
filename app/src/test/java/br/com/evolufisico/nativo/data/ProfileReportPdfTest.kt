package br.com.evolufisico.nativo.data

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets

class ProfileReportPdfTest {
    private val student = Student(7, "Ana Silva", "11999990000", "ana@example.com", "1990-02-03")
    private val assessment = Assessment(
        id = 2, studentId = 7, date = "2026-10-25", weightKg = 70.0, heightCm = 170.0,
        waistCm = 78.0, hipCm = 98.0, notes = "Coleta em condições habituais.",
        bodyFatPercent = 24.0, neckCm = 34.0, chestCm = 92.0, armCm = 29.0, thighCm = 52.0
    )

    @Test fun assessmentReportProducesReadablePdfBytes() {
        val bytes = ProfileReportPdf.assessment(student, assessment, "Profissional", "CREF 0000")
        val prefix = String(bytes.take(8).toByteArray(), StandardCharsets.US_ASCII)
        assertTrue(prefix.startsWith("%PDF-"))
        assertTrue(bytes.size > 800)
    }

    @Test fun intakeReportPaginatesLongResponsesAndProducesPdf() {
        val form = IntakeForm(1, 7, "Anamnese inicial", "2026-10-25", ("Resposta sobre rotina e saúde. ").repeat(1200))
        val bytes = ProfileReportPdf.intakeForm(student, form, "Profissional", "CREF 0000")
        val prefix = String(bytes.take(8).toByteArray(), StandardCharsets.US_ASCII)
        assertTrue(prefix.startsWith("%PDF-"))
        assertTrue(bytes.size > 1500)
    }
}
