package br.com.evolufisico.nativo.system

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.work.*
import br.com.evolufisico.nativo.data.CryptoStore
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/** Device-bound encrypted automatic backup to a SAF folder (best-effort Android background job).
 * These files aren't portable: only the same installed application's Keystore key can decrypt.
 */
class NativeBackupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result { return try {
        val prefs=applicationContext.getSharedPreferences("native-backup",Context.MODE_PRIVATE)
        val uriString=prefs.getString("folder",null) ?: return Result.failure()
        val dir=DocumentFile.fromTreeUri(applicationContext,Uri.parse(uriString)) ?: return Result.retry()
        val bytes=CryptoStore(applicationContext).use { it.exportDeviceEncrypted() }
        val name="EvoluFisico-auto-${LocalDateTime.now().toString().replace(':','-')}.efdevice"
        // Keep older copies; remove only a partial file if the new write fails.
        try {
        val file=dir.createFile("application/octet-stream",name) ?: return Result.retry()
        try {
            applicationContext.contentResolver.openOutputStream(file.uri)?.use { it.write(bytes); it.flush() }
                ?: error("Destino de backup indisponível")
            check(file.length() == bytes.size.toLong()) { "Backup automático incompleto" }
            prefs.edit().putString("last-success",LocalDateTime.now().toString()).apply()
            Result.success()
        } catch (_: Exception) {
            file.delete()
            Result.retry()
        }
        } finally { bytes.fill(0) }
    } catch (_: Exception) { Result.retry() }
    }

    companion object {
        fun schedule(context:Context,hour:Int,minute:Int) {
            require(hour in 0..23 && minute in 0..59)
            val now=LocalDateTime.now()
            var next=now.toLocalDate().atTime(LocalTime.of(hour,minute))
            if (!next.isAfter(now)) next=next.plusDays(1)
            val delay=Duration.between(now,next).toMillis()
            val request=PeriodicWorkRequestBuilder<NativeBackupWorker>(24,TimeUnit.HOURS)
                .setInitialDelay(delay,TimeUnit.MILLISECONDS)
                .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("evolufisico-daily-backup",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,request)
            context.getSharedPreferences("native-backup",Context.MODE_PRIVATE).edit()
                .putString("time","%02d:%02d".format(hour,minute)).putBoolean("enabled",true).apply()
        }
        fun disable(context:Context) {
            WorkManager.getInstance(context).cancelUniqueWork("evolufisico-daily-backup")
            context.getSharedPreferences("native-backup",Context.MODE_PRIVATE).edit().putBoolean("enabled",false).apply()
        }
    }
}
