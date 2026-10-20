package br.com.evolufisico.android.backup

import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/** Cálculos puros, inclusive transição de horário de verão. */
object BackupClock {
    fun nextTime(now: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime {
        require(hour in 0..23 && minute in 0..59)
        var next = now.toLocalDate().atTime(hour, minute).atZone(now.zone)
        if (!next.isAfter(now)) next = now.toLocalDate().plusDays(1)
            .atTime(hour, minute).atZone(now.zone)
        return next
    }
    fun delayMillis(now: ZonedDateTime, hour: Int, minute: Int): Long =
        java.time.Duration.between(now,nextTime(now,hour,minute)).toMillis().coerceAtLeast(0L)
}

data class DailyBackupState(
    val active: Boolean, val hour: Int, val minute: Int,
    val folderUri: String?, val lastSuccessMillis: Long, val lastError: String?
)
