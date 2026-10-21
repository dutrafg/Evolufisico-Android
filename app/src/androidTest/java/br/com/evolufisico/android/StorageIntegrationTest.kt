package br.com.evolufisico.android

import android.content.Context
import android.view.WindowManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.backup.SealedBackupCredential
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.billing.EncryptedChargeStore
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.PreRegistration
import br.com.evolufisico.android.data.PreRegistrationStore
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import br.com.evolufisico.android.data.EncryptedClientStore
import br.com.evolufisico.android.forms.EncryptedFormStore
import br.com.evolufisico.android.forms.FormCatalog
import br.com.evolufisico.android.forms.FormResponse
import br.com.evolufisico.android.agenda.AgendaCalendar
import br.com.evolufisico.android.agenda.AgendaIcs
import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.ClientGoal
import br.com.evolufisico.android.ops.ServiceItem
import br.com.evolufisico.android.ops.ClientNote
import br.com.evolufisico.android.ops.OperationsData
import br.com.evolufisico.android.ops.OperationalStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.YearMonth

/** Somente executar em emulador vazio; protege instalações já preenchidas. */
@RunWith(AndroidJUnit4::class)
class StorageIntegrationTest {
    @Test
    fun jsonStoresAndPortableRestoreOnEmptySandbox() {
        val app = ApplicationProvider.getApplicationContext<Context>()
        val clients = EncryptedClientStore(app)
        val assessments = EncryptedAssessmentStore(app)
        val charges = EncryptedChargeStore(app)
        val forms = EncryptedFormStore(app)
        val operations = OperationalStore(app)
        val drafts=PreRegistrationStore(app)
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
            val firstJson = java.io.File(app.filesDir, "clients.json").readBytes()
            clients.save(listOf(client))
            val secondJson = java.io.File(app.filesDir, "clients.json").readBytes()
            assertTrue(firstJson.contentEquals(secondJson))
            assertTrue(String(secondJson).contains("Pessoa de Teste"))
            assertEquals(client.name, clients.load().first().name)
            assessments.save(listOf(assessment))
            charges.save(listOf(charge), setOf(client.id))
            forms.save(listOf(form), setOf(client.id))
            val op = OperationsData(
                appointments=listOf(Appointment("a1",client.id,
                    LocalDate.now().atTime(14,0).toString(),60,"Avaliação")),
                goals=listOf(ClientGoal("g1",client.id,"Peso","kg",80.0,75.0,
                    78.0,LocalDate.now().plusMonths(3).toString())),
                services=listOf(ServiceItem("s1","Avaliação",2500,60)),
                notes=listOf(ClientNote("n1",client.id,LocalDate.now().toString(),
                    "Geral","Teste de registro","Observação fictícia.",true))
            )
            operations.save(op,setOf(client.id))
            val draft=PreRegistration.create("Pessoa Provisória",
                "prospect@example.org","(11) 98765-4321")
            drafts.save(listOf(draft))
            assertEquals(1,drafts.load().size)
            assertEquals(1,operations.load().appointments.size)
            assertEquals(form.answers, forms.load().first().answers)
            assertEquals(1, clients.load().size)
            val data = manager.export(password)
            val inspected = manager.inspectBackup(ByteArrayInputStream(data), password)
            assertEquals(9, inspected.recordCount)
            // A copia verificada nao autoriza atualizar se o banco mudou depois.
            clients.save(listOf(client.copy(name="Pessoa Alterada")))
            assertFalse("Backup ficou desatualizado após modificar cliente",
                inspected == manager.summary())
            clients.save(listOf(client))
            assertEquals(inspected,manager.summary())
            // Backups JSON da Beta 11 nao precisam de senha nem do packageName original.
            val noPassword=charArrayOf()
            assertEquals(9,
                manager.inspectBackup(ByteArrayInputStream(data), noPassword).recordCount)
            val damaged = data.clone()
            damaged[damaged.lastIndex] = (damaged.last().toInt() xor 1).toByte()
            assertTrue("Backup corrompido deve falhar validação AES-GCM",
                runCatching { manager.inspectBackup(ByteArrayInputStream(damaged), password) }
                    .isFailure)
            val incorrect = "SenhaPortatilIncorreta123".toCharArray()
            try {
                // JSON do protótipo não precisa de senha.
                assertEquals(8,
                    manager.inspectBackup(ByteArrayInputStream(data), incorrect).recordCount)
            } finally { incorrect.fill('\u0000') }
            assertEquals("Inspeção nunca deve modificar dados", 1, clients.load().size)
            assertEquals(1, clients.load().size) // Verificação não pode alterar o banco
            assertTrue(String(data, Charsets.UTF_8).contains("Pessoa de Teste"))
            val oldEncrypted = br.com.evolufisico.android.backup.PortableBackupCrypto.encrypt(
                data, password)
            val restoredOld = manager.inspectBackup(ByteArrayInputStream(oldEncrypted), password)
            assertEquals(9, restoredOld.recordCount)
            val wrongPassword = "SenhaPortatilIncorreta123".toCharArray()
            try {
                assertTrue("Backup .efbak antigo exige senha correta",
                    runCatching {
                        manager.inspectBackup(ByteArrayInputStream(oldEncrypted), wrongPassword)
                    }.isFailure)
            } finally { wrongPassword.fill('\u0000') }
            drafts.save(emptyList())
            operations.save(OperationsData(), emptySet())
            forms.save(emptyList(), emptySet())
            charges.save(emptyList(), emptySet())
            assessments.save(emptyList())
            clients.save(emptyList())
            val result = manager.restore(ByteArrayInputStream(data), charArrayOf())
            assertEquals(9, result.recordCount)
            assertEquals(draft,drafts.load().single())
            assertEquals("Avaliação",operations.load().services.first().name)
            val restoredAgenda=operations.load().appointments
            assertEquals(1,AgendaCalendar.active(YearMonth.now(),restoredAgenda))
            val calendar=AgendaIcs.generate(restoredAgenda,mapOf(client.id to client.name))
                .toString(Charsets.UTF_8)
            assertTrue(calendar.contains("BEGIN:VEVENT"))
            assertTrue(calendar.contains("SUMMARY:Avaliação - Pessoa de Teste"))
            assertEquals(1,operations.load().goals.size)
            assertEquals("Teste de registro",operations.load().notes.first().title)
            assertEquals(form.templateId, forms.load().first().templateId)
            assertEquals(client.name, clients.load().first().name)
            assertEquals(charge.amountCents, charges.load().first().amountCents)
            assertEquals(assessment.weightKg, assessments.load().first().weightKg, 0.01)
            // Simula queda APÓS o commit e ANTES de remover o diário.
            val interrupted=java.io.File(app.filesDir,"restore_transaction.pending")
            interrupted.writeText("EF_RESTORE_V1_COMMITTED",Charsets.US_ASCII)
            manager.recoverInterruptedRestore()
            assertFalse("Marcador confirmado deve ser finalizado",interrupted.exists())
            assertEquals("Restauração confirmada não pode apagar clientes",
                client.name,clients.load().first().name)
            assertEquals(1,charges.load().size)
            assertEquals(1,operations.load().appointments.size)
            assertEquals(1,drafts.load().size)
        } finally {
            operations.save(OperationsData(), emptySet())
            forms.save(emptyList(), emptySet())
            charges.save(emptyList(), emptySet())
            assessments.save(emptyList())
            clients.save(emptyList())
            password.fill('\u0000')
        }
    }

    @Test
    fun dailyCredentialIsEncryptedAndCanBeReopened() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = SealedBackupCredential(context)
        val secret = "SenhaForteDeTeste123!".toCharArray()
        try {
            store.save(secret)
            val prefs = context.getSharedPreferences("daily_backup",Context.MODE_PRIVATE)
            val ciphertext = prefs.getString("sealed_password","")!!
            assertFalse(ciphertext.contains("SenhaForteDeTeste123!"))
            val restored = store.read()
            assertEquals(String(secret),String(restored))
            restored.fill('\u0000')
            // Qualquer alteração na credencial autenticada deve provocar falha segura.
            prefs.edit().putString("sealed_password","AAAA").commit()
            assertTrue(runCatching { store.read() }.isFailure)
        } finally {
            secret.fill('\u0000')
            store.clear()
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
