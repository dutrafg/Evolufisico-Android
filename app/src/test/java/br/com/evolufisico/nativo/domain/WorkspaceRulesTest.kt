package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class WorkspaceRulesTest {
    private fun invalid(block:()->Unit) { assertThrows(Exception::class.java,block) }
    @Test fun validTrainingCanBeSaved() = WorkspaceRules.validate(TrainingSession(title="Sessão A"))
    @Test fun trainingRequiresTitle() { invalid{WorkspaceRules.validate(TrainingSession())} }
    @Test fun trainingRejectsNonpositiveDuration() { invalid{WorkspaceRules.validate(TrainingSession(title="A",durationMinutes=0))} }
    @Test fun trainingRejectsDurationAboveLimit() { invalid{WorkspaceRules.validate(TrainingSession(title="A",durationMinutes=721))} }
    @Test fun trainingAllowsDurationLimits() { listOf(1,720).forEach{WorkspaceRules.validate(TrainingSession(title="A",durationMinutes=it))} }
    @Test fun trainingEffortIsOptional() = WorkspaceRules.validate(TrainingSession(title="A",effort=null))
    @Test fun trainingRejectsEffortAboveTen() { invalid{WorkspaceRules.validate(TrainingSession(title="A",effort=11))} }
    @Test fun trainingRejectsNegativeEffort() { invalid{WorkspaceRules.validate(TrainingSession(title="A",effort=-1))} }
    @Test fun trainingRejectsOversizedInstructions() { invalid{WorkspaceRules.validate(TrainingSession(title="A",exercises="x".repeat(12001)))} }
    @Test fun habitSupportsZeroRealizedAmount() = WorkspaceRules.validate(HabitEntry(title="Caminhada",value=0.0,target=30.0,unit="min"))
    @Test fun habitRejectsNaN() { invalid{WorkspaceRules.validate(HabitEntry(title="H",value=Double.NaN))} }
    @Test fun habitRejectsInfiniteTarget() { invalid{WorkspaceRules.validate(HabitEntry(title="H",target=Double.POSITIVE_INFINITY))} }
    @Test fun habitRejectsNegativeAmount() { invalid{WorkspaceRules.validate(HabitEntry(title="H",value=-1.0))} }
    @Test fun habitRejectsZeroGoal() { invalid{WorkspaceRules.validate(HabitEntry(title="H",target=0.0))} }
    @Test fun habitRequiresUnit() { invalid{WorkspaceRules.validate(HabitEntry(title="H",unit=" "))} }
    @Test fun habitAcceptsDecimalValues() = WorkspaceRules.validate(HabitEntry(title="Registro",value=1.25,target=2.5,unit="L"))
    @Test fun habitProgressClampsAtOne() = assertEquals(1f,WorkspaceRules.habitProgress(3.0,2.0)!!,0.00001f)
    @Test fun habitProgressComputesFraction() = assertEquals(0.5f,WorkspaceRules.habitProgress(1.0,2.0)!!,0.00001f)
    @Test fun habitProgressRejectsInvalidValues() { assertNull(WorkspaceRules.habitProgress(Double.NaN,1.0));assertNull(WorkspaceRules.habitProgress(1.0,0.0));assertNull(WorkspaceRules.habitProgress(-1.0,2.0)) }
    @Test fun noteAllowsEmptyBody() = WorkspaceRules.validate(NotebookEntry(title="Lembrete"))
    @Test fun noteLimitsBodySize() { invalid{WorkspaceRules.validate(NotebookEntry(title="N",body="x".repeat(12001)))} }
    @Test fun noteAllowsBodyAtLimit() = WorkspaceRules.validate(NotebookEntry(title="N",body="x".repeat(12000)))
    @Test fun linkedRecordRequiresName() { invalid{WorkspaceRules.validate(NotebookEntry(title="N",studentId="client",student=""))} }
    @Test fun clientNameDoesNotRequireIdentityForUnlinkedRecord() = WorkspaceRules.validate(TrainingSession(title="A",student="Contato"))
    @Test fun notesRequireValidIdentity() { invalid{WorkspaceRules.validate(NotebookEntry(id="",title="N"))} }
    @Test fun dateRejectsImpossibleDay() { invalid{WorkspaceRules.date("2026-02-30")} }
    @Test fun dateRequiresIsoWidth() { invalid{WorkspaceRules.date("2026-2-03")} }
    @Test fun dateAcceptsLeapDay() { assertEquals(LocalDate.of(2024,2,29),WorkspaceRules.date("2024-02-29")) }
    @Test fun taskRejectsBlankTitle() { invalid{WorkspaceRules.validate(TaskItem(title=" "))} }
    @Test fun taskCanHaveLongNotesUpToLimit() = WorkspaceRules.validate(TaskItem(title="Retorno",notes="x".repeat(4000)))
    @Test fun overdueDoesNotIncludeCompletedTasks() { assertFalse(WorkspaceRules.overdue(TaskItem(title="A",dueDate="2020-01-01",done=true),LocalDate.of(2026,1,1))) }
    @Test fun overdueExcludesToday() { assertFalse(WorkspaceRules.overdue(TaskItem(title="A",dueDate="2026-01-01"),LocalDate.of(2026,1,1))) }
    @Test fun overdueIncludesYesterday() { assertTrue(WorkspaceRules.overdue(TaskItem(title="A",dueDate="2025-12-31"),LocalDate.of(2026,1,1))) }
    @Test fun searchIgnoresAccentsAndCase() { assertTrue(WorkspaceRules.search("Avaliação de João","JOAO avaliacao")) }
    @Test fun searchRequiresAllQueryWords() { assertFalse(WorkspaceRules.search("João avaliação","joao treino")) }
    @Test fun blankSearchMatchesEverything() { assertTrue(WorkspaceRules.search("","  ")) }
    @Test fun latestKeepsSeparateClientsWithSameName() {
        val result=WorkspaceRules.latest(listOf(Evaluation(studentId="a",name="Ana"),Evaluation(studentId="b",name="Ana")))
        assertEquals(2,result.size)
    }
    @Test fun latestChoosesMostRecentAssessment() {
        val result=WorkspaceRules.latest(listOf(Evaluation(id="old",studentId="a",date="2025-01-01"),Evaluation(id="new",studentId="a",date="2026-01-01")))
        assertEquals("new",result.single().id)
    }
    @Test fun latestSameDaySelectionIsDeterministic() {
        val a=Evaluation(id="a",studentId="x",date="2026-01-01");val b=a.copy(id="b")
        assertEquals(WorkspaceRules.latest(listOf(a,b)),WorkspaceRules.latest(listOf(b,a)))
    }
    @Test fun invalidTaskDateIsNotReportedAsOverdue() { assertFalse(WorkspaceRules.overdue(TaskItem(title="A",dueDate="broken"))) }
}
