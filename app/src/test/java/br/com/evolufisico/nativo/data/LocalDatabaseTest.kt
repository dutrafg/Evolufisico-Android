package br.com.evolufisico.nativo.data

import android.content.Context
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LocalDatabaseTest {
    private lateinit var context: Context
    private lateinit var database: LocalDatabase

    @Before fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase("evolufisico-native.db")
        database = LocalDatabase(context)
    }

    @After fun tearDown() {
        database.close()
        context.deleteDatabase("evolufisico-native.db")
    }

    @Test fun studentAssessmentAndProfileDataSupportCreateUpdateDelete() {
        val studentId = database.saveStudent(" Ana ", "1199", "ana@example.com", "1990-01-01")
        assertEquals("Ana", database.student(studentId)?.name)

        database.updateStudent(studentId, "Ana Silva", "1188", "ana.silva@example.com", "1990-01-01")
        assertEquals("Ana Silva", database.student(studentId)?.name)

        val assessmentId = database.saveAssessment(studentId, "2026-01-10", 70.0, 175.0, 82.0, 95.0, "Inicial", 24.0, 38.0, 98.0, 31.0, 55.0, sex = "Feminino", ageYears = 34, tricepsFoldMm = 10.0, subscapularFoldMm = 20.0, chestFoldMm = 30.0, axillaryFoldMm = 40.0, suprailiacFoldMm = 50.0, abdominalFoldMm = 60.0, thighFoldMm = 70.0)
        assertEquals(assessmentId, database.assessments(studentId).first().id)
        assertEquals(22.9, database.assessments(studentId).first().bmi!!, 0.0)
        assertEquals(34, database.assessments(studentId).first().ageYears)
        assertTrue(database.assessments(studentId).first().pollock7 != null)

        database.saveAssessment(studentId, "2026-02-10", 68.5, 175.0, 80.0, 94.0, "Revisada", id = assessmentId)
        assertEquals(1, database.assessments(studentId).size)
        assertEquals("Revisada", database.assessments(studentId).first().notes)
        assertThrows(IllegalArgumentException::class.java) {
            database.saveAssessment(studentId, "10/02/2026", 68.5, 175.0, null, null, "")
        }

        database.deleteStudent(studentId)
        assertNull(database.student(studentId))
        assertTrue(database.assessments(studentId).isEmpty())
    }

    @Test fun appointmentsAndTasksSupportLifecycleAndValidation() {
        val appointmentId = database.saveAppointment(null, "Avaliação inicial", "2026-03-12", "09:30", "Sala 1", "Observação", "Avaliação")
        assertEquals("Avaliação", database.appointments().single().kind)
        database.saveAppointment(appointmentId, "Retorno", "2026-03-19", "10:00", "", "", "Retorno")
        assertEquals("Retorno", database.appointments().single().title)
        assertEquals(1, database.appointments("2026-03-19").size)
        assertThrows(IllegalArgumentException::class.java) {
            database.saveAppointment(null, "Inválido", "2026-03-19", "29:80", "", "")
        }
        database.deleteAppointment(appointmentId)
        assertTrue(database.appointments().isEmpty())

        val taskId = database.saveTask(null, "Enviar relatório", "2026-03-20")
        database.setTaskCompleted(taskId, true)
        assertTrue(database.tasks().single().completed)
        database.saveTask(taskId, "Enviar relatório atualizado", "2026-03-21", false)
        assertEquals("Enviar relatório atualizado", database.tasks().single().title)
        assertFalse(database.tasks().single().completed)
        assertThrows(IllegalArgumentException::class.java) { database.saveTask(null, "", "2026-03-21") }
        database.deleteTask(taskId)
        assertTrue(database.tasks().isEmpty())
    }

    @Test fun formsAndChargesCanBeEditedReceivedDeletedAndUnlinked() {
        val studentId = database.saveStudent("Bia", "", "", "")
        val formId = database.saveIntakeForm(studentId, "Anamnese inicial", "2026-03-01", "Sono: 8h")
        database.saveIntakeForm(studentId, "Anamnese atualizada", "2026-03-02", "Sono: 7h", formId)
        assertEquals("Anamnese atualizada", database.intakeForms(studentId).single().template)

        val chargeId = database.saveCharge(studentId, "Mensalidade", 150.0, "2026-03-10", "")
        assertEquals("Pendente", database.charges().single().status)
        database.markChargeReceived(chargeId)
        database.saveCharge(studentId, "Mensalidade atualizada", 160.0, "2026-03-11", "ajuste", chargeId)
        assertEquals("Recebido", database.charges().single().status)
        assertEquals(0, database.pendingChargeCount())

        database.deleteStudent(studentId)
        assertNull(database.intakeForms().single().studentId)
        assertNull(database.charges().single().studentId)

        database.deleteIntakeForm(formId)
        database.deleteCharge(chargeId)
        assertTrue(database.intakeForms().isEmpty())
        assertTrue(database.charges().isEmpty())
    }

    @Test fun backupRestoreIsCompleteAndInvalidBackupDoesNotEraseCurrentData() {
        val studentId = database.saveStudent("Caio", "1198", "caio@example.com", "1988-04-03")
        database.saveAssessment(studentId, "2026-01-10", 80.0, 180.0, 88.0, 100.0, "ok", 20.0, sex = "Masculino", ageYears = 30, tricepsFoldMm = 10.0, subscapularFoldMm = 20.0, chestFoldMm = 30.0, axillaryFoldMm = 40.0, suprailiacFoldMm = 50.0, abdominalFoldMm = 60.0, thighFoldMm = 70.0)
        database.saveAppointment(null, "Consulta", "2026-03-12", "09:30", "", "", "Atendimento")
        database.saveTask(null, "Ligar", "2026-03-13")
        database.saveIntakeForm(studentId, "Anamnese", "2026-03-10", "Respostas")
        database.saveCharge(studentId, "Plano", 120.0, "2026-03-15", "nota")
        val backup = database.exportBackupJson()

        database.saveStudent("Outro", "", "", "")
        database.restoreBackupJson(backup)
        assertEquals(1, database.students().size)
        assertEquals(1, database.allAssessments().size)
        assertEquals(30, database.allAssessments().single().ageYears)
        assertEquals(70.0, database.allAssessments().single().thighFoldMm!!, 0.0)
        assertTrue(database.allAssessments().single().pollock7 != null)
        assertEquals(1, database.appointments().size)
        assertEquals(1, database.tasks().size)
        assertEquals(1, database.intakeForms().size)
        assertEquals(1, database.charges().size)

        assertThrows(IllegalArgumentException::class.java) {
            database.restoreBackupJson("{\"format\":\"incompatível\",\"schema\":2}")
        }
        assertEquals("Caio", database.students().single().name)
    }
}
