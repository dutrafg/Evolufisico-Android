package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class ReminderPolicyTest {
    @Test fun supportedLeadTimesAreExactlyTheSettingsChoices() {
        assertEquals(listOf(0,5,10,15,30,60,1440),ReminderPolicy.allowedLeadMinutes)
        ReminderPolicy.allowedLeadMinutes.forEach { assertEquals(it,ReminderPolicy.validateLead(it)) }
        listOf(-1,1,14,16,45,61,120,1441).forEach { value ->
            assertThrows(IllegalArgumentException::class.java){ReminderPolicy.validateLead(value)}
        }
    }

    @Test fun schedulingThresholdHasMillisecondPrecision() {
        for(lead in ReminderPolicy.allowedLeadMinutes){
            val threshold=lead*60_000L
            assertFalse(ReminderPolicy.shouldSchedule(threshold,lead))
            assertTrue(ReminderPolicy.shouldSchedule(threshold+1,lead))
            assertFalse(ReminderPolicy.shouldSchedule(0,lead))
        }
    }

    @Test fun delayedWorkerNeverNotifiesForPastOrLateAppointments() {
        assertTrue(ReminderPolicy.shouldNotify(60,0))
        assertFalse(ReminderPolicy.shouldNotify(61,0))
        assertFalse(ReminderPolicy.shouldNotify(0,0))
        for(lead in ReminderPolicy.allowedLeadMinutes){
            assertFalse(ReminderPolicy.shouldNotify(-1,lead))
            assertFalse(ReminderPolicy.shouldNotify(0,lead))
            assertTrue(ReminderPolicy.shouldNotify(1,lead))
            assertTrue(ReminderPolicy.shouldNotify(maxOf(lead*60L,60L),lead))
            if(lead>0)assertFalse(ReminderPolicy.shouldNotify(lead*60L+1,lead))
        }
    }
}
