package br.com.evolufisico.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import br.com.evolufisico.android.evolution.EvolutionPoint
import br.com.evolufisico.android.evolution.EvolutionSeries
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

/**
 * PDF Android nativo sem capturas da UI e sem arquivos temporários em texto puro.
 * Escrita só acontece após o usuário escolher um destino via SAF.
 */
object EvolutionPdfReport {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val INK = 0xFF223448.toInt()
    private const val BLUE = 0xFF245E98.toInt()
    private const val MUTED = 0xFF5C6E80.toInt()
    private const val LIGHT = 0xFFE6EDF5.toInt()
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    fun write(output: OutputStream, clientName: String, series: EvolutionSeries) {
        require(clientName.isNotBlank())
        require(series.points.isNotEmpty()) { "Sem medições para exportar." }
        require(series.points.size <= 20_000) { "Histórico muito grande para exportação." }
        require(series.points.all { it.value.isFinite() && it.value > 0.0 })
        val pdf = PdfDocument()
        try {
            var index = 0
            var pageNumber = 1
            while (index < series.points.size) {
                val firstPage = pageNumber == 1
                val page = pdf.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                )
                val canvas = page.canvas
                var y = header(canvas, clientName, series, pageNumber, firstPage)
                if (firstPage) {
                    chart(canvas, series.points, 50f, y + 12f, 490f, 165f)
                    y += 195f
                }
                drawText(canvas, "DATA", 52f, y, 10f, BLUE, true)
                drawText(canvas, "MEDIÇÃO", 290f, y, 10f, BLUE, true)
                drawText(canvas, "VAR. DESDE INÍCIO", 408f, y, 10f, BLUE, true)
                y += 13
                canvas.drawLine(50f, y, 545f, y, pen(LIGHT, 1f))
                y += 21
                while (index < series.points.size && y <= 737f) {
                    val point = series.points[index]
                    if (index % 2 == 0) {
                        canvas.drawRect(49f, y - 17f, 545f, y + 6f, pen(0xFFF4F7FA.toInt(), fill = true))
                    }
                    drawText(canvas, point.date.format(dateFormat), 52f, y, 10f, INK)
                    drawText(canvas, "${format(point.value)} ${series.metric.unit}", 290f, y, 10f, INK)
                    drawText(canvas, signed(point.value - series.points.first().value), 408f, y, 10f, INK)
                    y += 25
                    index++
                }
                footer(canvas, pageNumber)
                pdf.finishPage(page)
                pageNumber++
            }
            pdf.writeTo(output)
            output.flush()
        } finally {
            pdf.close()
        }
    }

    private fun header(
        canvas: Canvas, clientName: String, series: EvolutionSeries, page: Int, firstPage: Boolean
    ): Float {
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 14f, pen(BLUE, fill = true))
        drawText(canvas, "EVOLUFÍSICO  /  RELATÓRIO DE EVOLUÇÃO", 49f, 49f, 17f, INK, true)
        canvas.drawLine(49f, 61f, 545f, 61f, pen(LIGHT, 1f))
        val safeName = if (clientName.length > 44) clientName.take(43) + "…" else clientName
        drawText(canvas, "Cliente: $safeName", 50f, 86f, 12f, INK, true)
        drawText(canvas,
            "Indicador: ${series.metric.title} (${series.metric.unit})   •   ${series.period.title}",
            50f, 107f, 10f, MUTED)
        drawText(canvas, "Gerado em ${LocalDate.now().format(dateFormat)}  •  Página $page",
            50f, 126f, 9f, MUTED)
        if (firstPage) {
            val first = series.points.first()
            val last = series.points.last()
            drawText(canvas, "Período: ${first.date.format(dateFormat)} a ${last.date.format(dateFormat)}",
                50f, 147f, 10f, INK)
            drawText(canvas,
                "Início: ${format(first.value)}     Último: ${format(last.value)}     " +
                    "Variação: ${signed(last.value - first.value)}",
                50f, 166f, 10f, BLUE, true)
            return 181f
        }
        return 160f
    }

    private fun chart(canvas: Canvas, points: List<EvolutionPoint>,
        left: Float, top: Float, width: Float, height: Float) {
        val values = points.map { it.value }
        val low = values.minOrNull()!!
        val high = values.maxOrNull()!!
        val spread = (high - low).takeIf { it > 0.000001 } ?: maxOf(1.0, abs(high) * 0.10)
        val floor = low - 0.15 * spread
        val ceiling = high + 0.15 * spread
        val right = left + width
        val bottom = top + height
        canvas.drawLine(left, bottom, right, bottom, pen(LIGHT, 1f))
        canvas.drawLine(left, top, left, bottom, pen(LIGHT, 1f))
        fun x(i: Int): Float = if (values.size == 1) left + width / 2
            else left + (width * i.toFloat() / (values.size - 1))
        fun y(v: Double): Float = bottom - ((v - floor) / (ceiling - floor) * height).toFloat()
        val graph = pen(BLUE, 2f)
        values.forEachIndexed { index, value ->
            if (index > 0) canvas.drawLine(
                x(index - 1), y(values[index - 1]), x(index), y(value), graph
            )
            // Não desenhar milhares de pontos em uma única página.
            if (values.size <= 100) {
                canvas.drawCircle(x(index), y(value), 2.6f, pen(BLUE, fill = true))
            }
        }
    }

    private fun footer(canvas: Canvas, page: Int) {
        canvas.drawLine(49f, 775f, 546f, 775f, pen(LIGHT, 1f))
        drawText(canvas, "Indicadores descritivos, não equivalem a diagnóstico clínico.",
            49f, 792f, 9f, MUTED)
        drawText(canvas, "Documento com dados pessoais. Armazenar em local seguro.   •   $page",
            49f, 807f, 8f, MUTED)
    }

    private fun pen(color: Int, stroke: Float = 1f, fill: Boolean = false) = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        this.color = color
        style = if (fill) Paint.Style.FILL else Paint.Style.STROKE
        strokeWidth = stroke
    }

    private fun drawText(
        canvas: Canvas, text: String, x: Float, y: Float, size: Float, color: Int,
        bold: Boolean = false
    ) {
        val paint = pen(color, fill = true).apply {
            textSize = size
            typeface = Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
        }
        canvas.drawText(text, x, y, paint)
    }

    private fun format(number: Double): String =
        String.format(Locale.forLanguageTag("pt-BR"), "%.2f", number)

    private fun signed(number: Double): String =
        String.format(Locale.forLanguageTag("pt-BR"), "%+.2f", number)
}
