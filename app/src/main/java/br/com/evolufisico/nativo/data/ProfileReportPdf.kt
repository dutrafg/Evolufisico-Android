package br.com.evolufisico.nativo.data

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Locale

internal object ProfileReportPdf {
    private data class Line(val text: String, val heading: Boolean = false)

    fun assessment(
        student: Student,
        assessment: Assessment,
        professionalName: String,
        registration: String
    ): ByteArray {
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
        lines += Line("")
        lines += Line("Observações", true)
        lines += Line(assessment.notes.ifBlank { "Nenhuma observação registrada." })
        lines += Line("")
        lines += Line("Os indicadores são descritivos e não constituem diagnóstico.")
        return render("EvoluFísico · Relatório de Avaliação", "Avaliação física", professionalName, registration, lines)
    }

    fun intakeForm(
        student: Student,
        form: IntakeForm,
        professionalName: String,
        registration: String
    ): ByteArray {
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
        val document = PdfDocument()
        val output = ByteArrayOutputStream()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48f
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(55, 60, 64)
            textSize = 12f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        val headingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(16, 116, 84)
            textSize = 14f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(28, 31, 35)
            textSize = 21f
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }
        val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(95, 101, 105)
            textSize = 10f
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = 0f

        fun startPage() {
            pageNumber += 1
            page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            val canvas = page!!.canvas
            canvas.drawColor(android.graphics.Color.WHITE)
            canvas.drawText(title, margin, 62f, titlePaint)
            canvas.drawText(subtitle, margin, 84f, mutedPaint)
            val professional = listOf(professionalName.trim(), registration.trim()).filter { it.isNotBlank() }.joinToString(" · ")
            if (professional.isNotBlank()) canvas.drawText(professional.take(90), margin, 104f, mutedPaint)
            y = if (professional.isNotBlank()) 140f else 126f
        }

        fun finishPage() {
            val current = page ?: return
            current.canvas.drawLine(margin, 795f, pageWidth - margin, 795f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.LTGRAY
                strokeWidth = 0.7f
            })
            current.canvas.drawText("EvoluFísico · página $pageNumber", margin, 815f, mutedPaint)
            document.finishPage(current)
            page = null
        }

        fun wrapped(text: String, paint: Paint): List<String> {
            if (text.isEmpty()) return listOf("")
            val words = text.split(Regex("\\s+"))
            val result = mutableListOf<String>()
            var line = ""
            for (word in words) {
                val candidate = if (line.isEmpty()) word else "$line $word"
                if (line.isNotEmpty() && paint.measureText(candidate) > pageWidth - margin * 2) {
                    result += line
                    line = word
                } else line = candidate
            }
            if (line.isNotEmpty()) result += line
            return result
        }

        startPage()
        for (line in lines) {
            val paint = if (line.heading) headingPaint else bodyPaint
            if (line.heading && y > 145f) y += 6f
            for (textLine in wrapped(line.text, paint)) {
                if (y > pageHeight - 72f) {
                    finishPage()
                    startPage()
                }
                if (textLine.isNotEmpty()) page!!.canvas.drawText(textLine, margin, y, paint)
                y += if (line.heading) 20f else 17f
            }
        }
        finishPage()
        document.writeTo(output)
        document.close()
        return output.toByteArray()
    }

    private fun number(value: Double): String = NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }.format(value)
}
