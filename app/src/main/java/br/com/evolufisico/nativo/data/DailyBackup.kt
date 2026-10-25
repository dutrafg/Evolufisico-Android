package br.com.evolufisico.nativo.data

import android.content.Context
import android.net.Uri
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

internal object DailyBackupScheduler {
    const val WORK_NAME = "evolufisico_daily_backup"

    fun nextDelayMillis(now: ZonedDateTime, backupTime: LocalTime): Long {
        var next = now.toLocalDate().atTime(backupTime).atZone(now.zone)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis()
    }

    fun configure(context: Context, enabled: Boolean, time: String, folderUri: String?) {
        val manager = WorkManager.getInstance(context.applicationContext)
        if (!enabled || folderUri.isNullOrBlank()) {
            manager.cancelUniqueWork(WORK_NAME)
            return
        }
        val parsedTime = runCatching { LocalTime.parse(time) }.getOrNull() ?: LocalTime.of(22, 0)
        val delay = nextDelayMillis(ZonedDateTime.now(), parsedTime)
        val request = PeriodicWorkRequestBuilder<DailyBackupWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        manager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }
}

internal class DailyBackupWorker(
    context: Context,
    parameters: androidx.work.WorkerParameters
) : androidx.work.CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val preferences = applicationContext.getSharedPreferences("appearance", Context.MODE_PRIVATE)
            val folderText = preferences.getString("auto_backup_folder_uri", null)
                ?: return@withContext Result.failure()
            val folderUri = Uri.parse(folderText)
            val directory = androidx.documentfile.provider.DocumentFile.fromTreeUri(applicationContext, folderUri)
                ?: return@withContext Result.failure()
            if (!directory.canWrite()) return@withContext Result.failure()

            val date = java.time.LocalDate.now().toString()
            val fileName = "evolufisico-auto-$date.json"
            directory.findFile(fileName)?.delete()
            val file = directory.createFile("application/json", fileName)
                ?: return@withContext Result.retry()
            val json = LocalDatabase(applicationContext).exportBackupJson()
            applicationContext.contentResolver.openOutputStream(file.uri, "wt")?.use {
                it.write(json.toByteArray(Charsets.UTF_8))
            } ?: return@withContext Result.retry()

            directory.listFiles()
                .filter { it.name?.startsWith("evolufisico-auto-") == true && it.name?.endsWith(".json") == true }
                .sortedByDescending { it.name }
                .drop(30)
                .forEach { it.delete() }
            Result.success()
        } catch (_: SecurityException) {
            Result.failure()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
