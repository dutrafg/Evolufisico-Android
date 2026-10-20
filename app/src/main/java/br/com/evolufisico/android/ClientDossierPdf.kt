package br.com.evolufisico.android

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.forms.FormCatalog
import java.io.OutputStream
import java.time.LocalDate
import java.util.Locale

/** Exportação descritiva de um único cliente, sem dados de outros IDs. */
object ClientDossierPdf {
    private const val W = 595
    private const val H = 842
    fun write(out: OutputStream, client: Client, data: BackupSnapshot) {
        val doc = PdfDocument()
        val color = Color.rgb(31, 76, 110)
        val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; textSize = 11f }
        val heading = Paint(ink).apply { textSize = 16f; typeface = Typeface.DEFAULT_BOLD }
        val small = Paint(ink).apply { textSize = 9f }
        var pageNumber = 0
        var page: PdfDocument.Page? = null
        var y = 0f
        fun startPage() {
            page?.let { doc.finishPage(it) }
            pageNumber++
            page = doc.startPage(PdfDocument.PageInfo.Builder(W, H, pageNumber).create())
            val canvas = page!!.canvas
            canvas.drawRect(0f,0f,W.toFloat(),12f,Paint().apply { this.color = color })
            canvas.drawText("EVOLUFÍSICO / FICHA DO CLIENTE",40f,50f,heading)
            canvas.drawText("Cliente: " + client.name.take(65),40f,77f,ink)
            canvas.drawText("Data de emissão: " + LocalDate.now() + "   Página " + pageNumber,40f,98f,small)
            y = 140f
        }
        fun line(text: String, title: Boolean = false) {
            val paint = if (title) ink else small
            val paragraphs = text.replace('\n',' ').replace('\r',' ')
            var current = ""
            fun flush() {
                if (current.isBlank()) return
                if (y > 760f) startPage()
                page!!.canvas.drawText(current,44f,y,paint)
                y += if (title) 21f else 17f
                current = ""
            }
            for (word in paragraphs.split(' ')) {
                if (word.isEmpty()) continue
                val candidate = if (current.isEmpty()) word else current + " " + word
                if (paint.measureText(candidate) > 510f && current.isNotEmpty()) flush()
                if (paint.measureText(word) > 510f) {
                    val chunks = word.chunked(65)
                    chunks.forEach { part ->
                        if (current.isNotEmpty()) flush()
                        current = part
                        flush()
                    }
                } else current = if (current.isEmpty()) word else current + " " + word
            }
            flush()
        }
        try {
            startPage()
            val evaluations = data.assessments.filter { it.clientId == client.id }
                .sortedByDescending { it.dateIso }
            val forms = data.forms.filter { it.clientId == client.id }
                .sortedByDescending { it.dateIso }
            val charges = data.charges.filter { it.clientId == client.id }
                .sortedByDescending { it.dueDateIso }
            line("Avaliações (" + evaluations.size + ")", true)
            if (evaluations.isEmpty()) line("Sem avaliações cadastradas.")
            evaluations.forEach { e ->
                line(e.dateIso + " | " + e.trainingLevel + " | Peso " +
                    String.format(Locale.forLanguageTag("pt-BR"),"%.2f kg",e.weightKg))
            }
            y += 15f
            line("Formulários (" + forms.size + ")", true)
            if (forms.isEmpty()) line("Sem formulários preenchidos.")
            forms.forEach { f ->
                line(f.dateIso + " | " + (FormCatalog.find(f.templateId)?.title ?: "Modelo não encontrado"))
            }
            y += 15f
            line("Cobranças (" + charges.size + ")", true)
            if (charges.isEmpty()) line("Sem cobranças cadastradas.")
            charges.forEach { c ->
                val value = java.text.NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("pt-BR")).format(c.amountCents / 100.0)
                line(c.dueDateIso + " | " + c.description + " | " + value +
                    if (c.isReceived) " | Recebida" else " | Pendente")
            }
            y += 15f
            val appointments=data.operations.appointments.filter { it.clientId==client.id }
                .sortedByDescending { it.startIso }
            line("Atendimentos (" + appointments.size + ")", true)
            appointments.forEach { a ->
                line(a.startIso.replace('T',' ')+" | "+a.service+" | "+a.status)
            }
            y += 15f
            val goals=data.operations.goals.filter { it.clientId==client.id }
            line("Metas (" + goals.size + ")", true)
            goals.forEach { g ->
                line(g.title+" | Atual "+g.current+" "+g.unit+" | Alvo "+g.target+" "+g.unit+
                    if(g.completed) " | Concluída" else " | Em andamento")
            }
            y += 17f
            y += 12f
            val notes=data.operations.notes.filter { it.clientId == client.id }
            line("Anotações (" + notes.size + ")", true)
            notes.sortedByDescending { it.dateIso }.forEach {
                line(it.dateIso+" | "+it.category+" | "+it.title)
            }
            line("Documento informativo. Não substitui laudo ou diagnóstico.")
            doc.finishPage(page!!)
            page = null
            doc.writeTo(out)
            out.flush()
        } finally { doc.close() }
    }
}
