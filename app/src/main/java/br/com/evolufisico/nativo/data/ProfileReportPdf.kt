package br.com.evolufisico.nativo.data

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.text.NumberFormat
import java.util.Locale

internal object ProfileReportPdf {
    private data class Line(val text: String, val heading: Boolean = false)
    private data class DrawLine(val text: String, val size: Int, val color: String)
    private data class Page(val lines: List<DrawLine>)

    fun assessment(student: Student, assessment: Assessment, professionalName: String, registration: String): ByteArray {
        val lines = mutableListOf(
            Line("Dados do aluno", true),
            Line("Nome: " + student.name),
            Line("Telefone: " + student.phone.ifBlank { "Não informado" }),
            Line("E-mail: " + student.email.ifBlank { "Não informado" }),
            Line("Nascimento: " + student.birthDate.ifBlank { "Não informado" }),
            Line(""),
            Line("Medidas e indicadores", true),
            Line("Data da avaliação: " + assessment.date),
            Line("Peso: " + number(assessment.weightKg) + " kg"),
            Line("Altura: " + number(assessment.heightCm) + " cm"),
            Line("IMC: " + (assessment.bmi?.let(::number) ?: "Não calculado"))
        )
        assessment.waistCm?.let { lines += Line("Cintura: " + number(it) + " cm") }
        assessment.hipCm?.let { lines += Line("Quadril: " + number(it) + " cm") }
        assessment.bodyFatPercent?.let { lines += Line("Gordura corporal informada: " + number(it) + "%") }
        listOf("Pescoço" to assessment.neckCm, "Peitoral" to assessment.chestCm, "Braço" to assessment.armCm, "Coxa" to assessment.thighCm)
            .forEach { (label, value) -> value?.let { lines += Line("$label: " + number(it) + " cm") } }
        val folds = listOf("Tríceps" to assessment.tricepsFoldMm, "Subescapular" to assessment.subscapularFoldMm, "Peitoral" to assessment.chestFoldMm, "Axilar média" to assessment.axillaryFoldMm, "Supra-ilíaca" to assessment.suprailiacFoldMm, "Abdominal" to assessment.abdominalFoldMm, "Coxa" to assessment.thighFoldMm)
        if (folds.any { it.second != null }) {
            lines += Line("Dobras cutâneas · Pollock 7", true)
            lines += Line("Sexo: " + assessment.sex + (assessment.ageYears?.let { " · idade: $it anos" } ?: ""))
            folds.forEach { (label, value) -> value?.let { lines += Line("$label: " + number(it) + " mm") } }
            assessment.pollock7?.let {
                lines += Line("Soma: " + number(it.sumMm) + " mm · densidade: " + number(it.density))
                lines += Line("Gordura estimada: " + number(it.bodyFatPercent) + "% · massa de gordura: " + number(it.fatMassKg) + " kg · massa livre: " + number(it.leanMassKg) + " kg")
                if (it.wasClamped) lines += Line("A estimativa foi limitada ao intervalo válido para o método.")
            }
        }
        lines += Line("")
        lines += Line("Observações", true)
        lines += Line(assessment.notes.ifBlank { "Nenhuma observação registrada." })
        lines += Line("")
        lines += Line("Os indicadores são descritivos e não constituem diagnóstico.")
        return render("EvoluFísico · Relatório de Avaliação", "Avaliação física", professionalName, registration, lines)
    }

    fun analysis(student: Student, assessments: List<Assessment>, summary: String, professionalName: String, registration: String): ByteArray {
        require(assessments.size in 3..4) { "Selecione três ou quatro avaliações." }
        val dates = assessments.map { it.date }.distinct().sorted()
        require(AnalysisMath.isEligibleWindow(dates)) { "O período precisa conter três ou quatro datas distintas e pelo menos 21 dias." }
        val lines = mutableListOf(
            Line("Dados do aluno", true),
            Line("Nome: " + student.name),
            Line("Telefone: " + student.phone.ifBlank { "Não informado" }),
            Line("E-mail: " + student.email.ifBlank { "Não informado" }),
            Line(""),
            Line("Período analisado", true),
            Line("Datas: " + dates.joinToString(", ")),
            Line("Avaliações selecionadas: " + assessments.size),
            Line(""),
            Line("Síntese revisada", true),
            Line(summary),
            Line(""),
            Line("Este relatório é descritivo e exploratório; não constitui diagnóstico, previsão ou prescrição.")
        )
        return render("EvoluFísico · Relatório de Análise", "Análises Completas", professionalName, registration, lines)
    }

    fun intakeForm(student: Student, form: IntakeForm, professionalName: String, registration: String): ByteArray {
        val lines = listOf(
            Line("Dados do aluno", true),
            Line("Nome: " + student.name),
            Line("Telefone: " + student.phone.ifBlank { "Não informado" }),
            Line("E-mail: " + student.email.ifBlank { "Não informado" }),
            Line(""),
            Line("Registro", true),
            Line("Formulário: " + form.template),
            Line("Data: " + form.date),
            Line(""),
            Line("Respostas", true),
            Line(form.responses),
            Line(""),
            Line("Registro de apoio ao atendimento. Não constitui diagnóstico.")
        )
        return render("EvoluFísico · Anamnese", form.template, professionalName, registration, lines)
    }

    private fun render(title: String, subtitle: String, professionalName: String, registration: String, lines: List<Line>): ByteArray {
        val contentWidth = 595 - 48 * 2
        val outputLines = mutableListOf<DrawLine>()
        outputLines += DrawLine(title, 20, "0.11 0.12 0.14")
        outputLines += DrawLine(subtitle, 10, "0.37 0.40 0.41")
        val professional = listOf(professionalName.trim(), registration.trim()).filter { it.isNotBlank() }.joinToString(" · ")
        if (professional.isNotBlank()) outputLines += DrawLine(professional, 10, "0.37 0.40 0.41")
        outputLines += DrawLine("", 12, "0 0 0")
        lines.forEach { line ->
            val size = if (line.heading) 13 else 11
            val color = if (line.heading) "0.06 0.45 0.33" else "0.22 0.24 0.25"
            wrap(line.text, contentWidth, size).forEach { outputLines += DrawLine(it, size, color) }
        }

        val pages = mutableListOf<Page>()
        var current = mutableListOf<DrawLine>()
        var cursorY = 715
        for (line in outputLines) {
            if (cursorY < 58) {
                pages += Page(current.toList())
                current = mutableListOf()
                cursorY = 715
            }
            current += line
            cursorY -= if (line.size >= 13) 20 else 16
        }
        if (current.isNotEmpty() || pages.isEmpty()) pages += Page(current.toList())
        return writePdf(pages)
    }

    private fun wrap(text: String, width: Int, fontSize: Int): List<String> {
        if (text.isEmpty()) return listOf("")
        val maxCharacters = (width / (fontSize * 0.53)).toInt().coerceAtLeast(18)
        val result = mutableListOf<String>()
        text.split('\n').forEach { paragraph ->
            var line = ""
            paragraph.split(Regex("\\s+")).filter { it.isNotEmpty() }.forEach { word ->
                if (word.length > maxCharacters) {
                    if (line.isNotEmpty()) { result += line; line = "" }
                    word.chunked(maxCharacters).dropLast(1).forEach { result += it }
                    line = word.takeLast(word.length % maxCharacters).ifEmpty { "" }
                } else if (line.isEmpty()) line = word
                else if (line.length + word.length + 1 <= maxCharacters) line += " $word"
                else { result += line; line = word }
            }
            if (line.isNotEmpty()) result += line
            if (paragraph.isEmpty()) result += ""
        }
        return result.ifEmpty { listOf("") }
    }

    private fun writePdf(pages: List<Page>): ByteArray {
        val objects = mutableListOf<ByteArray>()
        fun obj(value: String) = value.toByteArray(Charsets.US_ASCII)
        objects += obj("<< /Type /Catalog /Pages 2 0 R >>")
        val pageIds = pages.indices.map { 4 + it * 2 }
        objects += obj("<< /Type /Pages /Kids [${pageIds.joinToString(" ") { "$it 0 R" }}] /Count ${pages.size} >>")
        objects += obj("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>")

        pages.forEachIndexed { index, page ->
            val pageId = 4 + index * 2
            val contentId = pageId + 1
            val stream = buildString {
                append("0.06 0.45 0.33 RG\n")
                append("48 754 m 547 754 l S\n")
                var y = 782
                for (line in page.lines) {
                    append("BT /F1 ").append(line.size).append(" Tf ")
                    append(line.color).append(" rg 48 ").append(y).append(" Td ")
                    append('<').append(toHex(line.text)).append("> Tj ET\n")
                    y -= if (line.size >= 13) 20 else 16
                }
                append("0.75 0.77 0.78 RG 48 42 m 547 42 l S\n")
                append("BT /F1 9 Tf 0.37 0.40 0.41 rg 48 27 Td <")
                    .append(toHex("EvoluFísico · página ${index + 1}"))
                    .append("> Tj ET\n")
            }.toByteArray(Charsets.US_ASCII)
            objects += obj("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R >> >> /Contents $contentId 0 R >>")
            objects += obj("<< /Length ${stream.size} >>\nstream\n") + stream + obj("endstream")
        }

        val output = ByteArrayOutputStream()
        output.write(obj("%PDF-1.4\n"))
        val offsets = mutableListOf(0)
        objects.forEachIndexed { index, body ->
            offsets += output.size()
            output.write(obj("${index + 1} 0 obj\n"))
            output.write(body)
            output.write(obj("\nendobj\n"))
        }
        val xrefOffset = output.size()
        output.write(obj("xref\n0 ${objects.size + 1}\n"))
        output.write(obj("0000000000 65535 f \n"))
        offsets.drop(1).forEach { offset -> output.write(obj(String.format(Locale.ROOT, "%010d 00000 n \n", offset))) }
        output.write(obj("trailer\n<< /Size ${objects.size + 1} /Root 1 0 R >>\nstartxref\n$xrefOffset\n%%EOF\n"))
        return output.toByteArray()
    }

    private fun toHex(value: String): String {
        val bytes = value.toByteArray(Charset.forName("windows-1252"))
        return bytes.joinToString("") { "%02X".format(Locale.ROOT, it.toInt() and 0xFF) }
    }

    private fun number(value: Double): String = NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }.format(value)
}
