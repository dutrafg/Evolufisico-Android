package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test

class Beta33WorkspaceTest {
    private val profile=Evaluation(id="e1",studentId="a",name="Ana",date="2026-10-01",weightKg=70.0)
    private fun snapshot(evals:List<Evaluation> = listOf(profile),charges:List<Charge> = emptyList(),training:List<TrainingSession> = emptyList(),notes:List<NotebookEntry> = emptyList())=
        ClientWorkspace.snapshot("a",evals,charges,emptyList(),emptyList(),training,emptyList(),notes)
    @Test fun homonymsNeverJoinClientRecords(){
        val other=profile.copy(id="e2",studentId="b")
        val data=snapshot(listOf(profile,other),listOf(Charge(studentId="b",student="Ana"),Charge(studentId="a",student="Ana")))
        assertEquals(1,data.evaluations.size);assertEquals(1,data.charges.size)
    }
    @Test fun latestProfileUsesDateThenStableId(){
        val newest=profile.copy(id="z",phone="123")
        assertEquals(newest,snapshot(listOf(profile,newest)).profile)
    }
    @Test fun jointChargesAreIncludedOnceByEitherClientSlot(){
        val joint=Charge(studentId="b",secondStudentId="a",student="B",secondStudent="A")
        assertEquals(listOf(joint),snapshot(charges=listOf(joint)).charges)
    }
    @Test fun templatesAndArchivedNotesDoNotPolluteDossier(){
        val training=listOf(TrainingSession(studentId="a"),TrainingSession(studentId="a",isTemplate=true))
        val notes=listOf(NotebookEntry(studentId="a"),NotebookEntry(studentId="a",archived=true))
        val data=snapshot(training=training,notes=notes)
        assertEquals(1,data.training.size);assertEquals(1,data.notes.size)
    }
    @Test fun contactEditPreservesMeasurementsAndIdentity(){
        val changed=ClientWorkspace.editContact(profile," Ana Maria "," 123 "," a@b.com ")
        assertEquals(profile.id,changed.id);assertEquals(profile.studentId,changed.studentId)
        assertEquals(profile.weightKg,changed.weightKg);assertEquals("Ana Maria",changed.name)
        assertEquals("Ana",profile.name)
    }
    @Test fun invalidContactCannotInjectLineBreaksOrBlankName(){
        assertThrows(IllegalArgumentException::class.java){ClientWorkspace.editContact(profile," ","","")}
        assertThrows(IllegalArgumentException::class.java){ClientWorkspace.editContact(profile,"Ana\nOutra","","")}
        assertThrows(IllegalArgumentException::class.java){ClientWorkspace.editContact(profile,"Ana","x".repeat(41),"")}
    }
    @Test fun agendaCsvNamesAreOptIn(){
        val rows=listOf(Appointment(student="CLIENT_SECRET",date="2026-10-01"))
        fun csv(names:Boolean)=ExportTables.build("Agenda",emptyList(),emptyList(),emptyList(),emptyList(),emptyList(),names,appointments=rows)
        assertFalse(csv(false).contains("CLIENT_SECRET"));assertTrue(csv(true).contains("CLIENT_SECRET"))
    }
    @Test fun noteBodyIsOptInAndFormulaSafe(){
        val notes=listOf(NotebookEntry(title="N",body="=PRIVATE_BODY"))
        fun csv(body:Boolean)=ExportTables.build("Notas",emptyList(),emptyList(),emptyList(),emptyList(),emptyList(),notes=notes,includeNoteBody=body)
        assertFalse(csv(false).contains("PRIVATE_BODY"));assertTrue(csv(true).contains("\"'=PRIVATE_BODY\""))
    }
    @Test fun exerciseLibraryDeduplicatesNamesAndResetsExecution(){
        val rows=listOf(TrainingSession(date="2026-10-01",sets=listOf(WorkoutSet(exercise="Remada",done=true))),
            TrainingSession(date="2026-10-02",sets=listOf(WorkoutSet(exercise=" REMADA ",loadKg=30.0,done=true))))
        val result=TrainingInsights.library(rows)
        assertEquals(1,result.size);assertFalse(result.single().done);assertEquals(30.0,result.single().loadKg,0.0)
    }
    @Test fun missingProfileAndInvalidLibraryValuesAreRejectedOrSkipped(){
        assertThrows(IllegalStateException::class.java){snapshot(emptyList())}
        assertTrue(TrainingInsights.library(listOf(TrainingSession(sets=listOf(WorkoutSet(exercise="E",loadKg=Double.NaN))))).isEmpty())
    }
}
