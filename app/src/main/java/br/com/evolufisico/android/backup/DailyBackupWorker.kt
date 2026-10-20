package br.com.evolufisico.android.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/** WorkManager executa no horário aproximado: Doze/bateria podem adiar a execução. */
class DailyBackupScheduler(private val context: Context) {
    private val prefs = context.getSharedPreferences("daily_backup", Context.MODE_PRIVATE)
    private val workName = "evolufisico-daily-backup-v1"
    private val keys = SealedBackupCredential(context)

    fun state(): DailyBackupState = DailyBackupState(
        prefs.getBoolean("active",false),
        prefs.getInt("hour",22),prefs.getInt("minute",0),
        prefs.getString("folder",null),
        prefs.getLong("last_success",0),
        prefs.getString("last_error",null),
        prefs.getLong("last_attempt",0),
        prefs.getLong("enabled_at",0),
        prefs.getInt("last_records",0),
        prefs.getString("last_file",null)
    )

    fun activate(folder: String, hour: Int, minute: Int, password: CharArray) {
        require(hour in 0..23 && minute in 0..59)
        require(password.size >= 10)
        val uri = Uri.parse(folder)
        require(uri.scheme == "content" &&
            context.contentResolver.persistedUriPermissions.any {
                it.uri == uri && it.isWritePermission && it.isReadPermission
            }) { "Selecione uma pasta com permissão permanente de leitura e escrita." }
        // Verifica o acesso e o banco antes de habilitar agendamento.
        SecureBackupManager(context).summary()
        keys.save(password)
        val saved = prefs.edit().putBoolean("active",true)
            .putInt("hour",hour).putInt("minute",minute).putString("folder",folder)
            .putLong("enabled_at",System.currentTimeMillis())
            .putLong("last_success",0L).putLong("last_attempt",0L)
            .remove("last_file").remove("last_error").putInt("last_records",0).commit()
        if (!saved) {
            keys.clear()
            error("Falha ao salvar programação.")
        }
        try { enqueue(hour,minute) }
        catch (e: Exception) {
            disable()
            throw e
        }
    }

    fun disable() {
        prefs.edit().putBoolean("active",false).remove("folder").commit()
        WorkManager.getInstance(context).cancelUniqueWork(workName)
        keys.clear()
    }

    private fun enqueue(hour: Int, minute: Int) {
        val delay = BackupClock.delayMillis(ZonedDateTime.now(),hour,minute)
        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val request = PeriodicWorkRequestBuilder<DailyBackupWorker>(24,TimeUnit.HOURS)
            .setInitialDelay(delay,TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workName,ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,request
        )
    }

    fun markAttempt() {
        prefs.edit().putLong("last_attempt",System.currentTimeMillis())
            .remove("last_error").commit()
    }
    fun markSuccess(report: BackupRunReport) {
        prefs.edit().putLong("last_success",System.currentTimeMillis())
            .putString("last_file",report.fileName)
            .putInt("last_records",report.recordCount)
            .remove("last_error").commit()
    }
    fun markFailure(error: String) {
        prefs.edit().putString("last_error",error.take(250)).commit()
    }
}

class DailyBackupWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext,workerParameters) {
    override suspend fun doWork(): Result {
        if (!DailyBackupScheduler(applicationContext).state().active) return Result.success()
        // Falhas são registradas no próprio executor e não desabilitam o agendamento.
        runCatching { VerifiedBackupExecutor.execute(applicationContext) }
        return Result.success()
    }
}
