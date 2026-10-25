package br.com.evolufisico.nativo.data

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyBackupSchedulerTest {
    private val zone = ZoneId.of("America/Sao_Paulo")

    @Test fun schedulesForLaterTodayWhenChosenTimeHasNotPassed() {
        val now = ZonedDateTime.of(2026, 10, 25, 21, 30, 0, 0, zone)
        assertEquals(30 * 60 * 1000L, DailyBackupScheduler.nextDelayMillis(now, LocalTime.of(22, 0)))
    }

    @Test fun schedulesForTomorrowWhenChosenTimeHasPassed() {
        val now = ZonedDateTime.of(2026, 10, 25, 22, 30, 0, 0, zone)
        assertEquals(23 * 60 * 60 * 1000L + 30 * 60 * 1000L, DailyBackupScheduler.nextDelayMillis(now, LocalTime.of(22, 0)))
    }

    @Test fun equalCurrentTimeSchedulesNextOccurrenceTomorrow() {
        val now = ZonedDateTime.of(2026, 10, 25, 22, 0, 0, 0, zone)
        assertEquals(24 * 60 * 60 * 1000L, DailyBackupScheduler.nextDelayMillis(now, LocalTime.of(22, 0)))
    }
    @Test fun backupFileNamesAreUniqueAndChronologicallySortable() {
        val early = DailyBackupScheduler.fileName(java.time.LocalDateTime.parse("2026-10-25T22:00:00.100"))
        val late = DailyBackupScheduler.fileName(java.time.LocalDateTime.parse("2026-10-25T22:00:00.200"))
        assertTrue(early.startsWith("evolufisico-auto-2026-10-25-220000-100"))
        assertTrue(late > early)
        assertTrue(early.endsWith(".json"))
        assertEquals(early, DailyBackupScheduler.fileName(java.time.LocalDateTime.parse("2026-10-25T22:00:00.100")))
    }

}
