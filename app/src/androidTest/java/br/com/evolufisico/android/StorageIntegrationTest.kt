package br.com.evolufisico.android

import android.content.Context
import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.billing.EncryptedChargeStore
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import br.com.evolufisico.android.data.EncryptedClientStore
import br.com.evolufisico.android.forms.EncryptedFormStore
import br.com.evolufisico.android.forms.FormCatalog
import br.com.evolufisico.android.forms.FormResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.time.LocalDate

/** Somente executar em emulador vazio; protege instalações já preenchidas. */
@RunWith(AndroidJUnit4::class)
class StorageIntegrationTest {
    @Test
    fun encryptedStoresAndPortableRestoreOnEmptySandbox() {
        val app = ApplicationProvider.getApplicationContext<Context>()
        val clients = EncryptedClientStore(app)
        val assessments = EncryptedAssessmentStore(app)
        val charges = EncryptedChargeStore(app)
        val forms = EncryptedFormStore(app)
        val manager = SecureBackupManager(app)
        val initial = manager.summary()
        // Nunca apagar dados de uma instalação que já tenha registros.
        assertEquals("Execute apenas em instalação de teste vazia", 0, initial.recordCount)
        val client = Client("test-client-1", "Pessoa de Teste")
        val assessment = Assessment("test-eval-1", client.id, LocalDate.now().toString(),
            "Iniciante", "Saúde", "", 3, 72.5, 170.0, 80.0, 95.0, null, null)
        val charge = Charge("test-charge-1", client.id, "Avaliação", 2500,
            LocalDate.now().toString())
        val template = FormCatalog.templates.first()
        val form = FormResponse("test-form-1", client.id, template.id,
            LocalDate.now().toString(),
            template.questions.associate { it.id to "Relato fictício." })
        val password = "TesteBackupSenhaForte123!".toCharArray()
        try {
            clients.save(listOf(client))
            val firstCiphertext = java.io.File(app.filesDir, "clients.enc").readBytes()
            clients.save(listOf(client))
            val secondCiphertext = java.io.File(app.filesDir, "clients.enc").readBytes()
            // Criptografar duas vezes deve produzir IVs distintos, sem IV fornecido pelo app.
            assertFalse(firstCiphertext.contentEquals(secondCiphertext))
            assertEquals(client.name, clients.load().first().name)
            assessments.save(listOf(assessment))
            charges.save(listOf(charge), setOf(client.id))
            forms.save(listOf(form), setOf(client.id))
            assertEquals(form.answers, forms.load().first().answers)
            assertEquals(1, clients.load().size)
            val data = manager.export(password)
            assertFalse(String(data, Charsets.ISO_8859_1).contains("Pessoa de Teste"))
            forms.save(emptyList(), emptySet())
            charges.save(emptyList(), emptySet())
            assessments.save(emptyList())
            clients.save(emptyList())
            val result = manager.restore(ByteArrayInputStream(data), password)
            assertEquals(4, result.recordCount)
            assertEquals(form.templateId, forms.load().first().templateId)
            assertEquals(client.name, clients.load().first().name)
            assertEquals(charge.amountCents, charges.load().first().amountCents)
            assertEquals(assessment.weightKg, assessments.load().first().weightKg, 0.01)
        } finally {
            forms.save(emptyList(), emptySet())
            charges.save(emptyList(), emptySet())
            assessments.save(emptyList())
            clients.save(emptyList())
            password.fill('\u0000')
        }
    }

    @Test
    fun activityProtectsScreenshotsAndRecentPreviews() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val flags = activity.window.attributes.flags
                @Suppress("DEPRECATION")
                val versionName = activity.packageManager
                    .getPackageInfo(activity.packageName, 0).versionName.orEmpty()
                val blocked = flags and WindowManager.LayoutParams.FLAG_SECURE != 0
                assertEquals(ScreenshotPolicy.shouldBlock(versionName), blocked)
            }
        }
    }
}
