package br.com.evolufisico.android

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.evolufisico.android.forms.FormCatalog
import br.com.evolufisico.android.forms.FormResponse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate

/** PDF com dados totalmente fictícios. Verifica paginação e leitura no Android real. */
@RunWith(AndroidJUnit4::class)
class PdfExportInstrumentationTest {
    @Test fun longUnbrokenAnswersProduceReadableMultipagePdf() {
        val app=ApplicationProvider.getApplicationContext<android.content.Context>()
        val template=FormCatalog.find("anamnese_iniciante")!!
        val answers=template.questions.associate {
            it.id to ("Resposta longa • " + "PalavraSemEspacos".repeat(43)).take(700)
        }
        val response=FormResponse("qa-form","qa-client",template.id,
            LocalDate.now().toString(),answers)
        val out=ByteArrayOutputStream()
        FormPdfReport.write(out,"Pessoa Fictícia de QA",response)
        val bytes=out.toByteArray()
        assertTrue("PDF inválido ou vazio",bytes.size>1800)
        assertTrue("Cabeçalho do PDF não encontrado",
            bytes.take(5).toByteArray().contentEquals("%PDF-".toByteArray()))
        val saved=File(app.cacheDir,"qa-report-multipage.pdf")
        try {
            saved.writeBytes(bytes)
            ParcelFileDescriptor.open(saved,ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { pdf ->
                    assertTrue("Documento deve ter mais de uma página",pdf.pageCount>=2)
                    for(index in 0 until pdf.pageCount) {
                        pdf.openPage(index).use { page ->
                            assertEquals(595,page.width)
                            assertEquals(842,page.height)
                            val bitmap=Bitmap.createBitmap(page.width,page.height,Bitmap.Config.ARGB_8888)
                            try {
                                bitmap.eraseColor(Color.WHITE)
                                page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                assertTrue("Página renderizada vazia",bitmap.getPixel(20,5)!=Color.WHITE)
                            } finally { bitmap.recycle() }
                        }
                    }
                }
            }
        } finally { saved.delete() }
    }
}
