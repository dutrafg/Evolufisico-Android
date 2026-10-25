package br.com.evolufisico.nativo.domain
import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
class HtmlHomeTest {
 @Test fun statWidthsMatchHtmlEditor(){assertEquals(listOf(3,6,12),listOf("small","medium","large").map{HtmlHome.cardSpan("homeStats",it)})}
 @Test fun panelWidthsAndInvalidSavedSizesHaveSafeDefaults(){assertEquals(listOf(4,6,8,12),listOf("small","medium","large","full").map{HtmlHome.cardSpan("homeCards",it)});assertEquals(3,HtmlHome.cardSpan("homeStats","corrupt"));assertEquals(6,HtmlHome.cardSpan("homeCards","corrupt"))}
 @Test(expected=IllegalArgumentException::class) fun unknownLayoutGroupIsRejected(){HtmlHome.cardSpan("unknown","small")}
 private val today=LocalDate.of(2026,10,22)
 private fun summary(a:List<Appointment> = emptyList(),t:List<TaskItem> = emptyList(),n:List<NotebookEntry> = emptyList(),g:List<OrganizationGoal> = emptyList())=HtmlHome.summarize(a,t,n,g,today)
 @Test fun emptyDashboardHasSevenZeroDaysAndNoInventedAlerts(){val s=summary();assertEquals(0,s.todayEvents);assertEquals(7,s.week.size);assertTrue(s.week.all{it.second==0});assertEquals("Sem alertas prioritários no momento",s.priorities.single().title);assertEquals(2,s.insights.size)}
 @Test fun allFourRecordTypesShareTodayAndFuture(){val s=summary(listOf(Appointment(student="Ana",date="2026-10-22")),listOf(TaskItem(title="T",dueDate="2026-10-22")),listOf(NotebookEntry(title="N",reminder="2026-10-22")),listOf(OrganizationGoal(title="G",due="2026-10-22")));assertEquals(4,s.todayEvents);assertEquals(4,s.week.first().second);assertEquals(4,s.upcoming.size)}
 @Test fun completedAndArchivedPlanningRecordsDoNotCountAsEvents(){val s=summary(listOf(Appointment(student="A",date="2026-10-22",status="Cancelado")),listOf(TaskItem(title="T",done=true)),listOf(NotebookEntry(title="N",reminder="2026-10-22",archived=true)),listOf(OrganizationGoal(title="G",due="2026-10-22",status="Concluída")));assertEquals(0,s.todayEvents);assertEquals(0,s.activeGoals);assertEquals(0,s.pendingTasks)}
 @Test fun pausedGoalsStillCountAsActiveLikeHtml(){assertEquals(1,summary(g=listOf(OrganizationGoal(title="G",status="Pausada"))).activeGoals)}
 @Test fun overdueIncludesTasksAndGoalsButNotCompleted(){val s=summary(t=listOf(TaskItem(title="T",dueDate="2026-10-21"),TaskItem(title="F",dueDate="2026-10-20",done=true)),g=listOf(OrganizationGoal(title="G",due="2026-10-21")));assertEquals(2,s.overdue);assertEquals(2,s.priorities.size);assertTrue(s.upcoming.isEmpty())}
 @Test fun urgentOverdueTaskIsNotListedTwice(){val s=summary(t=listOf(TaskItem(title="T",dueDate="2026-10-21",priority=3)));assertEquals(1,s.priorities.size);assertEquals("tasks",s.priorities.single().section)}
 @Test fun invalidDatesDoNotEnterCalendar(){val s=summary(t=listOf(TaskItem(title="T",dueDate="2026-02-30")),g=listOf(OrganizationGoal(title="G",due="invalid")));assertEquals(0,s.overdue);assertTrue(s.upcoming.isEmpty());assertEquals(1,s.pendingTasks)}
 @Test fun upcomingSortsByDateTimeAndLimitsToFive(){val s=summary(a=(0..7).reversed().map{Appointment(student="A$it",date="2026-10-22",time="${10+it}:00")});assertEquals(5,s.upcoming.size);assertEquals("A0",s.upcoming.first().title);assertEquals("A4",s.upcoming.last().title)}
 @Test fun weekIsExactlyTodayThroughSixDaysLater(){val s=summary();assertEquals("2026-10-22",s.week.first().first);assertEquals("2026-10-28",s.week.last().first)}
}
