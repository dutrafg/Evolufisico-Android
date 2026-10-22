package br.com.evolufisico.nativo.domain
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.ui.workspacePalettes
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ProfessionalToolsTest {
    private val today=LocalDate.of(2026,10,22)
    private fun row(date:LocalDate=today,value:Double=2.0,target:Double=1.0)=HabitEntry(title="Água",studentId="a",student="Ana",date=date.toString(),value=value,target=target,unit="L")
    @Test fun duplicateResetsEveryExecutionField(){
        val source=TrainingSession(title="A",completed=true,effort=8,sets=listOf(WorkoutSet(exercise="Remada",done=true)))
        val copy=ProfessionalTools.newSession(source,today)
        assertNotEquals(source.id,copy.id);assertNotEquals(source.sets[0].id,copy.sets[0].id)
        assertFalse(copy.completed);assertFalse(copy.sets[0].done);assertNull(copy.effort);assertEquals(today.toString(),copy.date)
    }
    @Test fun templateDropsClientAndExecution(){
        val source=TrainingSession(title="A",studentId="a",student="Ana",completed=true,effort=9,sets=listOf(WorkoutSet(exercise="Remada",done=true)))
        val template=ProfessionalTools.template(source)
        WorkspaceRules.validate(template);assertTrue(template.isTemplate);assertEquals("",template.studentId);assertEquals("",template.student)
    }
    @Test fun templateCannotBeCompleted(){assertThrows(Exception::class.java){WorkspaceRules.validate(TrainingSession(title="T",isTemplate=true,completed=true))}}
    @Test fun duplicateSetsAreRejected(){val set=WorkoutSet(exercise="A");assertThrows(Exception::class.java){WorkspaceRules.validate(TrainingSession(title="T",sets=listOf(set,set)))}}
    @Test fun blankExerciseIsRejected(){assertThrows(Exception::class.java){WorkspaceRules.validate(TrainingSession(title="T",sets=listOf(WorkoutSet())))}}
    @Test fun nonFiniteLoadIsRejected(){assertThrows(Exception::class.java){WorkspaceRules.validate(TrainingSession(title="T",sets=listOf(WorkoutSet(exercise="A",loadKg=Double.NaN))))}}
    @Test fun tooManySetsAreRejected(){assertThrows(Exception::class.java){WorkspaceRules.validate(TrainingSession(title="T",sets=(0..200).map{WorkoutSet(exercise="A")}))}}
    @Test fun volumeOnlyIncludesMarkedSets(){val session=TrainingSession(title="A",sets=listOf(WorkoutSet(exercise="A",repetitions=10,loadKg=5.0,done=true),WorkoutSet(exercise="B",loadKg=20.0)));assertEquals(50.0,ProfessionalTools.completedVolume(session),0.0)}
    @Test fun templatesHaveNoExecutedVolume(){assertEquals(0.0,ProfessionalTools.completedVolume(TrainingSession(isTemplate=true,sets=listOf(WorkoutSet(exercise="A",done=true,loadKg=3.0)))),0.0)}
    @Test fun streakDoesNotDoubleCountSameDay(){val ref=row();val summary=ProfessionalTools.habitSummary(ref,listOf(ref,ref.copy(id="two"),row(today.minusDays(1))),today);assertEquals(2,summary.streak);assertEquals(2,summary.achievedDays)}
    @Test fun yesterdayMayKeepStreakWhenTodayNotLogged(){assertEquals(1,ProfessionalTools.habitSummary(row(),listOf(row(today.minusDays(1))),today).streak)}
    @Test fun futureDatesCannotExtendStreak(){assertEquals(0,ProfessionalTools.habitSummary(row(),listOf(row(today.plusDays(1))),today).streak)}
    @Test fun separateClientsAndUnitsNeverJoin(){assertEquals(1,ProfessionalTools.habitSummary(row(),listOf(row(),row(today.minusDays(1)).copy(studentId="b"),row(today.minusDays(2)).copy(unit="h")),today).streak)}
    @Test fun missedDayBreaksStreak(){assertEquals(1,ProfessionalTools.habitSummary(row(),listOf(row(),row(today.minusDays(1),0.0),row(today.minusDays(2))),today).streak)}
    @Test fun taskOrderPlacesUrgencyBeforeDueDateAndCompletedLast(){val old=TaskItem(id="old",title="Old",priority=1,dueDate="2025-01-01");val urgent=old.copy(id="urgent",priority=3,dueDate="2026-01-01");val done=urgent.copy(id="done",done=true);assertEquals(listOf(urgent,old,done),ProfessionalTools.taskOrder(listOf(done,old,urgent)))}
    @Test fun taskPriorityIsValidated(){assertThrows(Exception::class.java){WorkspaceRules.validate(TaskItem(title="T",priority=4))}}
    @Test fun csvEscapesQuotesSemicolonsAndLines(){assertEquals("\"a;\"\"b\n c\"",ProfessionalTools.csvCell("a;\"b\n c"))}
    @Test fun csvNeutralizesAllFormulaPrefixes(){listOf("=SUM(1)"," +1","-1","@x","\t=x","\u0000=x","\uFEFF=x").forEach{assertTrue(ProfessionalTools.csvCell(it).startsWith("\"'"))}}
    @Test fun csvKeepsAccentAndUtf8Bom(){assertTrue(ProfessionalTools.csv(listOf("Nome"),listOf(listOf("João"))).startsWith("\uFEFF\"Nome\"\r\n\"João\""))}
    @Test fun inconsistentCsvColumnsAreRejected(){assertThrows(Exception::class.java){ProfessionalTools.csv(listOf("A"),listOf(listOf("1","2")))}}
    @Test fun csvNamesAreOptIn(){val e=Evaluation(name="Private Name");val anonymous=ExportTables.build("Avaliações",listOf(e),emptyList(),emptyList(),emptyList(),emptyList());assertFalse(anonymous.contains("Private Name"));val named=ExportTables.build("Avaliações",listOf(e),emptyList(),emptyList(),emptyList(),emptyList(),true);assertTrue(named.contains("Private Name"))}
    @Test fun csvExcludesTrainingTemplates(){val csv=ExportTables.build("Treinos",emptyList(),emptyList(),listOf(TrainingSession(title="SecretTemplate",isTemplate=true)),emptyList(),emptyList());assertFalse(csv.contains("SecretTemplate"))}
    @Test fun notePreviewHasBoundedLength(){val note=NotebookEntry(body="x".repeat(1000));assertEquals(241,ProfessionalTools.notePreview(note,false).length);assertEquals(note.body,ProfessionalTools.notePreview(note,true))}
    @Test fun achievedGoalHasNoRemainingDistance(){assertEquals(0.0,GoalTracking.progress(90.0,65.0,70.0)!!.remaining,0.0);assertEquals(0.0,GoalTracking.progress(60.0,85.0,80.0)!!.remaining,0.0)}
    @Test fun allPalettePrimaryPairsMeetNormalTextContrast(){workspacePalettes.forEach{p->assertTrue(p.label,ColorMath.contrast(p.light.toInt(),0xFFFFFFFF.toInt())>=4.5);assertTrue(p.label,ColorMath.contrast(p.dark.toInt(),0xFF10212C.toInt())>=4.5)}}
    @Test fun blackWhiteContrastMatchesReference(){assertEquals(21.0,ColorMath.contrast(0xFF000000.toInt(),0xFFFFFFFF.toInt()),0.0001)}
}
