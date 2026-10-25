package br.com.evolufisico.nativo.domain
import org.junit.Test
import org.junit.Assert.*
import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate
class DecisionCenterTest {
 private val today=LocalDate.of(2026,10,22)
 @Test fun latestRecordControlsReturnDate(){val rows=listOf(Evaluation(studentId="a",date="2026-09-01",nextAssessment="2026-10-01"),Evaluation(studentId="a",date="2026-10-21",nextAssessment="2026-11-01"));val r=DecisionCenter.summarize(rows,today);assertEquals(1,r.clients);assertTrue(r.overdue.isEmpty());assertEquals(1,r.upcoming.size)}
 @Test fun homonymsRemainSeparate(){val r=DecisionCenter.summarize(listOf(Evaluation(studentId="a",name="Ana"),Evaluation(studentId="b",name="Ana")),today);assertEquals(2,r.clients)}
 @Test fun todayAndFourteenDaysIncludedButFifteenExcluded(){val r=DecisionCenter.summarize(listOf(0L,14L,15L).map{Evaluation(nextAssessment=today.plusDays(it).toString())},today);assertEquals(2,r.upcoming.size)}
 @Test fun impossibleDatesDoNotBecomeReturns(){val r=DecisionCenter.summarize(listOf(Evaluation(nextAssessment="2026-02-30")),today);assertEquals(1,r.missing);assertTrue(r.overdue.isEmpty())}
 @Test fun sameDateDoesNotCreateEvolution(){val r=DecisionCenter.summarize(listOf(Evaluation(studentId="a",date="2026-10-01",weightKg=80.0),Evaluation(studentId="a",date="2026-10-01",weightKg=85.0)),today);assertEquals(0,r.withHistory);assertTrue(r.changes.isEmpty())}
 @Test fun signedDifferenceUsesDistinctDates(){val r=DecisionCenter.summarize(listOf(Evaluation(studentId="a",date="2026-10-01",weightKg=80.0),Evaluation(studentId="a",date="2026-10-20",weightKg=78.5)),today);assertEquals(1,r.withHistory);assertEquals(-1.5,r.changes.single().delta,0.0)}
 @Test fun absentWeightDoesNotCreateZeroDifference(){val r=DecisionCenter.summarize(listOf(Evaluation(studentId="a",date="2026-10-01",weightKg=80.0),Evaluation(studentId="a",date="2026-10-20")),today);assertEquals(1,r.withHistory);assertTrue(r.changes.isEmpty())}
}
