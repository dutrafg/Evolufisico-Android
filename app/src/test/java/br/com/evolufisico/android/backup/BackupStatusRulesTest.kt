package br.com.evolufisico.android.backup

import org.junit.Assert.*
import org.junit.Test

class BackupStatusRulesTest {
    private val hour = 3_600_000L
    @Test fun automaticBackupRetriesTransientFailureOnlyTwice() {
        assertTrue(BackupStatusRules.retryAfterFailure(0))
        assertTrue(BackupStatusRules.retryAfterFailure(1))
        assertFalse(BackupStatusRules.retryAfterFailure(2))
        assertFalse(BackupStatusRules.retryAfterFailure(Int.MAX_VALUE))
        assertFalse(BackupStatusRules.retryAfterFailure(-1))
    }
    @Test fun firstBackupNotYetDueIsNotLate() {
        assertFalse(BackupStatusRules.isLate(true, 100*hour, 99*hour, 0L))
    }
    @Test fun missedDailyBackupCanBeDetectedWithoutFalseClaimOfSuccess() {
        assertTrue(BackupStatusRules.isLate(true, 200*hour, 160*hour, 0L))
        assertFalse(BackupStatusRules.isLate(true, 200*hour, 0L, 199*hour))
    }
    @Test fun disabledAndClockChangesDoNotTriggerFalseWarnings() {
        assertFalse(BackupStatusRules.isLate(false, 200*hour, 1L, 0L))
        assertFalse(BackupStatusRules.isLate(true, 99L, 100L, 0L))
    }
}
