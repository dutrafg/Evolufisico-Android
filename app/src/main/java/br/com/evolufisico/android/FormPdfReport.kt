package br.com.evolufisico.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import br.com.evolufisico.android.forms.FormCatalog
import br.com.evolufisico.android.forms.FormResponse
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Layout próprio: nunca imprime o painel do app. */
object FormPdfReport {
    private const val WIDTH = 595
    private const val HEIGHT = 842
    private val navy = Color.rgb(30,72,105)
    private val muted = Color.rgb(90,102,115)
    fun write(out: OutputStream, clientName: String, response: FormResponse) {
        val template = FormCatalog.find(response.templateId) ?: error("Modelo inválido.")
        val doc = PdfDocument()
        val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = navy; textSize = 11f }
        val header = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = navy; textSize = 17f; typeface = Typeface.create("sans-serif",Typeface.BOLD)
        }
        val small = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = muted; textSize = 10f }
        val date = LocalDate.parse(response.dateIso)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy",Locale.forLanguageTag("pt-BR")))
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = 0f
        fun start() {
            if (page != null) doc.finishPage(page!!)
            pageNumber++
            page = doc.startPage(PdfDocument.PageInfo.Builder(WIDTH,HEIGHT,pageNumber).create())
            val c = page!!.canvas
            c.drawRect(0f,0f,WIDTH.toFloat(),12f,Paint().apply { color = navy })
            c.drawText("EVOLUFÍSICO  /  FORMULÁRIOS",40f,49f,header)
            c.drawText("Cliente: " + clientName.take(65),40f,72f,ink)
            c.drawText("Modelo: " + template.title.take(55),40f,90f,ink)
            c.drawText("Data: " + date + "     Página " + pageNumber,40f,108f,small)
            y = 146f
        }
        fun writeText(value: String, paint: Paint) {
            val c = page!!.canvas
            // Respeita largura da página, inclusive respostas longas.
            val words = value.split(Regex("\\s+"))
            var line = ""
            for (word in words) {
                val candidate = if (line.isEmpty()) word else line + " " + word
                if (paint.measureText(candidate) > 506f && line.isNotEmpty()) {
                    if (y > 750f) start()
                    page!!.canvas.drawText(line,44f,y,paint)
                    y += 17f
                    line = word
                } else line = candidate
            }
            if (line.isNotEmpty()) {
                if (y > 750f) start()
                page!!.canvas.drawText(line,44f,y,paint)
                y += 17f
            }
        }
        try {
            start()
            template.questions.forEachIndexed { i, question ->
                if (y > 698f) start()
                y += 8f
                writeText((i+1).toString() + ". " + question.title, ink)
                val reply = response.answers[question.id].orEmpty().ifBlank { "(Não informado)" }
                val safe = reply.replace("\n"," ").replace("\r"," ")
                val replyPaint = Paint(small).apply { textSize = 10.5f }
                writeText(safe,replyPaint)
                y += 11f
            }
            if (y > 715f) start()
            y += 13f
            writeText("Relato informado pelo cliente. Não equivale a laudo ou diagnóstico.",small)
            doc.finishPage(page!!)
            page = null
            doc.writeTo(out)
            out.flush()
        } finally { doc.close() }
    }
}
