package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class DashboardInsightsTest {
    private val today=LocalDate.of(2026,10,22)
    @Test fun periodIncludesTodayAndFirstDayButNotFutureOrPreviousDay(){
        assertTrue(DashboardInsights.inPeriod("2026-10-22",today,7))
        assertTrue(DashboardInsights.inPeriod("2026-10-16",today,7))
        assertFalse(DashboardInsights.inPeriod("2026-10-15",today,7))
        assertFalse(DashboardInsights.inPeriod("2026-10-23",today,7))
    }
    @Test fun leapDayAndInvalidDatesAreHandled(){
        val march=LocalDate.of(2024,3,1)
        assertTrue(DashboardInsights.inPeriod("2024-02-29",march,2))
        assertFalse(DashboardInsights.inPeriod("2023-02-29",today,366))
        assertFalse(DashboardInsights.inPeriod("",today,7))
        assertThrows(IllegalArgumentException::class.java){DashboardInsights.inPeriod("2026-10-22",today,0)}
    }
    @Test fun templatesAndOpenWorkoutsAreNotCompletedActivity(){
        val rows=listOf(TrainingSession(date=today.toString(),completed=true),
            TrainingSession(date=today.toString(),completed=true,isTemplate=true),
            TrainingSession(date=today.toString(),completed=false),TrainingSession(date="2026-10-23",completed=true))
        assertEquals(1,DashboardInsights.period(emptyList(),rows,emptyList(),today,7).completedTraining)
    }
    @Test fun habitCountsAreExplicitlyRecordCountsAndRespectTarget(){
        val rows=listOf(HabitEntry(date=today.toString(),value=2.0,target=1.0),
            HabitEntry(date=today.toString(),value=0.5,target=1.0),HabitEntry(date=today.toString(),value=0.0,target=0.0))
        val result=DashboardInsights.period(emptyList(),emptyList(),rows,today,7)
        assertEquals(3,result.habitEntries);assertEquals(1,result.achievedHabits)
    }
    @Test fun newerAssessmentClearsAnOldOverdueReassessment(){
        val old=Evaluation(id="a",studentId="client",date="2026-10-01",nextAssessment="2026-10-10")
        val newer=old.copy(id="b",date="2026-10-20",nextAssessment="2026-11-01")
        assertTrue(DashboardInsights.dueReassessments(listOf(old,newer),today).isEmpty())
    }
    @Test fun homonymsRemainSeparateAndFutureEvaluationDoesNotHideDueVisit(){
        val a=Evaluation(id="a",studentId="a",name="Ana",date="2026-10-01",nextAssessment=today.toString())
        val b=a.copy(id="b",studentId="b")
        val future=a.copy(id="future",date="2026-11-01",nextAssessment="2026-12-01")
        assertEquals(setOf("a","b"),DashboardInsights.dueReassessments(listOf(a,b,future),today).map{it.studentId}.toSet())
    }
    @Test fun largeFontsReduceGridDensityAndUnfilledRowsKeepColumnPolicy(){
        assertEquals(1,LayoutPolicy.cardColumns(360.0,1.0))
        assertEquals(4,LayoutPolicy.cardColumns(1120.0,1.0))
        assertEquals(2,LayoutPolicy.cardColumns(1120.0,2.0))
        assertThrows(IllegalArgumentException::class.java){LayoutPolicy.cardColumns(Double.NaN,1.0)}
    }
}
