package br.com.evolufisico.nativo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BackupInspectorTest {
    private val base = """{"format":"evolufisico-native-backup","schema":6,
        "students":[{"id":1,"name":"Teste"}],
        "assessments":[{"id":2,"student_id":1}],"appointments":[],
        "tasks":[],"intake_forms":[],"charges":[]}"""

    @Test fun validBackupProducesNonDestructiveSummary() {
        val summary = BackupInspector.inspect(base)
        assertEquals(6, summary.schema)
        assertEquals(1, summary.students)
        assertEquals(1, summary.assessments)
        assertEquals(2, summary.totalRecords)
        assertTrue(summary.asText().contains("avaliações: 1"))
    }

    @Test fun rejectsCorruptJson() {
        assertThrows(IllegalArgumentException::class.java) { BackupInspector.inspect("{") }
    }

    @Test fun rejectsUnsupportedAndFutureBackupSchema() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupInspector.inspect(base.replace("\"schema\":6", "\"schema\":7"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BackupInspector.inspect(base.replace("evolufisico-native-backup", "formato-falso"))
        }
    }

    @Test fun rejectsAbsentStudentsArray() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupInspector.inspect(base.replace("\"students\":[{\"id\":1,\"name\":\"Teste\"}],", ""))
        }
    }

    @Test fun rejectsDuplicateStudentIds() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupInspector.inspect(base.replace(
                "\"students\":[{\"id\":1,\"name\":\"Teste\"}]",
                "\"students\":[{\"id\":1},{\"id\":1}]"
            ))
        }
    }

    @Test fun rejectsAssessmentsWithMissingStudent() {
        assertThrows(IllegalArgumentException::class.java) {
            BackupInspector.inspect(base.replace("\"student_id\":1", "\"student_id\":99"))
        }
    }

    @Test fun acceptsOptionalUnlinkedIntakeFormAndCharge() {
        val valid = base.replace("\"intake_forms\":[]", "\"intake_forms\":[{\"id\":3,\"student_id\":null}]")
            .replace("\"charges\":[]", "\"charges\":[{\"id\":4,\"student_id\":null}]")
        assertEquals(4, BackupInspector.inspect(valid).totalRecords)
    }
}
