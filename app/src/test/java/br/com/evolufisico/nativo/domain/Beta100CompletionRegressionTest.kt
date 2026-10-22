package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test

class Beta100CompletionRegressionTest {
    @Test fun catalogRejectsDuplicateNormalizedNames() {
        val first=ExerciseDefinition(name="Remada  Curvada")
        assertThrows(IllegalArgumentException::class.java) {
            ExerciseCatalog.checkUnique(ExerciseDefinition(name=" remada\u00A0curvada "),listOf(first))
        }
    }
    @Test fun catalogCanEditItsOwnRecord() {
        val item=ExerciseDefinition(name="Agachamento")
        ExerciseCatalog.checkUnique(item.copy(instructions="Texto atualizado"),listOf(item))
    }
    @Test fun archivedCatalogEntriesStayOutOfPickerByDefault() {
        val rows=listOf(ExerciseDefinition(name="Elevação lateral",archived=true))
        assertTrue(ExerciseCatalog.filter(rows,"").isEmpty())
        assertEquals(1,ExerciseCatalog.filter(rows,"elevacao",includeArchived=true).size)
    }
    @Test fun catalogSearchIncludesEquipmentAndGroup() {
        val item=ExerciseDefinition(name="Remada",muscleGroup="Costas",equipment="Halteres")
        assertEquals(listOf(item),ExerciseCatalog.filter(listOf(item),"costas halteres"))
        assertTrue(ExerciseCatalog.filter(listOf(item),"",group="Pernas").isEmpty())
    }
    @Test fun catalogCsvNeutralizesSpreadsheetFormulas() {
        val csv=ExerciseCatalog.csv(listOf(ExerciseDefinition(name="=1+1")))
        assertTrue(csv.contains("\"'=1+1\""))
    }
    @Test fun catalogRequiresBoundedInstructions() {
        assertThrows(IllegalArgumentException::class.java) {
            ExerciseCatalog.validate(ExerciseDefinition(name="Remada",instructions="x".repeat(4001)))
        }
    }
    @Test fun chargeCannotLinkTheSamePermanentClientTwice() {
        assertThrows(IllegalArgumentException::class.java) {
            RecordRules.validate(Charge(student="Ana",studentId="a",secondStudent="Ana",secondStudentId="a"))
        }
    }
    @Test fun equalNamesWithDifferentIdsRemainValid() {
        RecordRules.validate(Charge(student="Ana",studentId="a",secondStudent="Ana",secondStudentId="b"))
    }
    @Test fun chargeRejectsExcessiveDiscountAndNegativeAmounts() {
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Charge(student="Ana",amountCents=100,discountCents=101)) }
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Charge(student="Ana",amountCents=-1)) }
    }
    @Test fun chargeRejectsPaidCourtesyAndInvalidInstallments() {
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Charge(student="Ana",paid=true,exempt=true)) }
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Charge(student="Ana",installments=25)) }
    }
    @Test fun intakeRejectsMalformedCalendarDates() {
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Intake(student="Ana",date="2026-02-30")) }
    }
    @Test fun intakeRejectsUnboundedAnswers() {
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Intake(student="Ana",answers=mapOf("objective" to "x".repeat(12001)))) }
    }
    @Test fun questionnaireUsesUniqueStableKeysAndSevenScreeningQuestions() {
        val all=IntakeQuestionnaire.run{common+parq+starter+intermediate+advanced}
        assertEquals(all.size,all.map{it.id}.distinct().size)
        assertEquals(7,IntakeQuestionnaire.parq.size)
        assertEquals(all.size,IntakeQuestionnaire.labels.size)
    }
    @Test fun evaluationRejectsNonFiniteMeasurement() {
        assertThrows(IllegalArgumentException::class.java) { RecordRules.validate(Evaluation(name="Ana",weightKg=Double.NaN)) }
    }
    @Test fun dailyCheckinsHaveDeterministicOrderAcrossScreens() {
        val rows=listOf(Evaluation(id="z",name="Ana",studentId="a",date="2026-10-22"),Evaluation(id="a",name="Ana",studentId="a",date="2026-10-22"))
        assertEquals(listOf("a","z"),EvaluationTimeline.ordered(rows).map{it.id})
        assertEquals(EvaluationTimeline.ordered(rows),EvaluationTimeline.ordered(rows.reversed()))
        assertEquals("z",WorkspaceRules.latest(rows).single().id)
    }
    @Test fun pauseKeepsExactMillisecondsInsteadOfRoundedDisplay() {
        val paused=RestCountdown.seconds(60).start(1000).pause(21500)
        assertEquals(39500L,paused.remainingMillis)
        assertFalse(paused.running)
        assertEquals(39500L,paused.remainingAt(999999))
    }
    @Test fun countdownResumesFromPausedRemainder() {
        val resumed=RestCountdown.seconds(60).start(1000).pause(21500).start(30000)
        assertEquals(69500L,resumed.deadlineElapsed)
        assertEquals(0L,resumed.tick(70000).remainingMillis)
        assertFalse(resumed.tick(70000).running)
    }
    @Test fun addingRestUsesCurrentDeadlineInsteadOfStaleUiCounter() {
        val updated=RestCountdown.seconds(60).start(1000).addSeconds(30,21500)
        assertEquals(69500L,updated.remainingMillis)
        assertEquals(91000L,updated.deadlineElapsed)
    }
    @Test fun restIsCappedAndExpiredStateDoesNotRestartAutomatically() {
        assertEquals(RestCountdown.MAX_MILLIS,RestCountdown.seconds(3600).addSeconds(30,0).remainingMillis)
        assertEquals(RestCountdown(0),RestCountdown(0).start(100))
        assertFalse(RestCountdown.seconds(1).start(0).addSeconds(30,2000).running)
    }
    @Test fun manualRestRejectsOutOfRangeDurations() {
        assertThrows(IllegalArgumentException::class.java) { RestCountdown.seconds(0) }
        assertThrows(IllegalArgumentException::class.java) { RestCountdown.seconds(3601) }
    }
    @Test fun adjacencyIsNotAnAgendaConflict() {
        val first=Appointment(id="a",student="Ana",date="2026-10-22",time="09:00",durationMinutes=60)
        assertTrue(Agenda.conflicts(listOf(first,first.copy(id="b",time="10:00"))).isEmpty())
    }
    @Test fun agendaFindsNestedIntervalsWithoutLosingLongerAnchor() {
        val first=Appointment(id="a",student="Ana",date="2026-10-22",time="09:00",durationMinutes=180)
        val rows=listOf(first,first.copy(id="b",time="09:30",durationMinutes=10),first.copy(id="c",time="11:00",durationMinutes=10))
        assertEquals(setOf("b","c"),Agenda.conflicts(rows).map{it.first.id}.toSet())
    }
    @Test fun agendaConflictsSpanMidnightAndIgnoreCancelledVisits() {
        val first=Appointment(id="a",student="Ana",date="2026-10-22",time="23:45",durationMinutes=60)
        val second=first.copy(id="b",date="2026-10-23",time="00:15")
        assertEquals(1,Agenda.conflicts(listOf(second,first)).size)
        assertTrue(Agenda.conflicts(listOf(second.copy(status="Cancelado"),first)).isEmpty())
    }
    @Test fun globalCatalogIsExcludedFromIndividualClientBackup() {
        assertFalse(ClientBackupScope.includes("exercise","a","a"))
    }
}
