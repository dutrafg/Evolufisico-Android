package br.com.evolufisico.android.backup

import org.junit.Assert.*
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class BackupClockTest {
    @Test fun nextTimeTodayOrTomorrow() {
        val time = ZonedDateTime.of(2026,10,20,10,30,0,0,ZoneId.of("America/Sao_Paulo"))
        assertEquals(11, BackupClock.nextTime(time,11,0).hour)
        assertEquals(20, BackupClock.nextTime(time,11,0).dayOfMonth)
        assertEquals(21, BackupClock.nextTime(time,10,0).dayOfMonth)
        assertEquals(30L*60L*1000L, BackupClock.delayMillis(time,11,0))
    }
    @Test fun invalidClockTimeRejected() {
        assertTrue(runCatching {
            BackupClock.nextTime(ZonedDateTime.now(),24,0)
        }.isFailure)
        assertTrue(runCatching {
            BackupClock.nextTime(ZonedDateTime.now(),22,60)
        }.isFailure)
    }
}
