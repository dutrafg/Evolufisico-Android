package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** 10,000 new reminder timing QA scenarios; these are test cases, not development-stage claims. */
@RunWith(Parameterized::class)
class Beta3MatrixTest(private val case:Int) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name="beta3-reminder-{0}")
        fun cases():Collection<Array<Any>> = (0 until 10_000).map{arrayOf<Any>(it)}
    }

    @Test fun reminderLeadBoundaryScenario(){
        val lead=ReminderPolicy.allowedLeadMinutes[(case/10)%4]
        val n=case/40
        when(case%10){
            0->{val delay=(n%120_000_000).toLong()-60_000L
                assertEquals(delay>lead*60_000L,ReminderPolicy.shouldSchedule(delay,lead))}
            1->{val seconds=(n%200_000).toLong()-100_000L
                assertEquals(seconds in 1..(lead*60L),ReminderPolicy.shouldNotify(seconds,lead))}
            2->{val threshold=lead*60_000L+(n%3-1)
                assertEquals(threshold>lead*60_000L,ReminderPolicy.shouldSchedule(threshold.toLong(),lead))}
            3->{val seconds=lead*60L+(n%3-1)
                assertEquals(seconds in 1..(lead*60L),ReminderPolicy.shouldNotify(seconds.toLong(),lead))}
            4->{assertEquals(lead,ReminderPolicy.validateLead(lead))}
            5->{val invalid=listOf(-1,0,1,14,16,29,31,59,61,1439,1441)[n%11]
                assertFalse(runCatching{ReminderPolicy.validateLead(invalid)}.isSuccess)}
            6->{val before=lead*60_000L-1
                assertFalse(ReminderPolicy.shouldSchedule(before,lead))}
            7->{val after=lead*60_000L+1
                assertTrue(ReminderPolicy.shouldSchedule(after,lead))}
            8->{val remaining=(n%lead.coerceAtLeast(1)*60L+1)
                assertTrue(ReminderPolicy.shouldNotify(remaining,lead))}
            9->{val overdue=-(n%86_400L+1)
                assertFalse(ReminderPolicy.shouldNotify(overdue,lead))}
        }
    }
}
