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
        prefs.getString("last_error",null)
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
        check(prefs.edit().putBoolean("active",true)
            .putInt("hour",hour).putInt("minute",minute).putString("folder",folder)
            .remove("last_error").commit()) { "Falha ao salvar programação." }
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

    fun markSuccess() {
        prefs.edit().putLong("last_success",System.currentTimeMillis())
            .remove("last_error").commit()
    }
    fun markFailure(error: String) {
        prefs.edit().putString("last_error",error.take(250)).commit()
    }
}

class DailyBackupWorker(appContext: Context, workerParameters: WorkerParameters):
    CoroutineWorker(appContext,workerParameters) {
    override suspend fun doWork(): Result {
        val scheduler = DailyBackupScheduler(applicationContext)
        val state = scheduler.state()
        if (!state.active) return Result.success()
        val secret = try { SealedBackupCredential(applicationContext).read() }
            catch (e: Exception) {
                scheduler.markFailure("Não foi possível abrir a senha protegida: " + e.javaClass.simpleName)
                return Result.success()
            }
        try {
            val location = Uri.parse(state.folderUri ?: error("Selecione novamente a pasta de backups."))
            require(applicationContext.contentResolver.persistedUriPermissions.any {
                it.uri == location && it.isReadPermission && it.isWritePermission
            }) { "Permissão da pasta expirou; selecione novamente." }
            val manager = SecureBackupManager(applicationContext)
            val data = manager.export(secret)
            val parent = DocumentsContract.buildDocumentUriUsingTree(
                location,DocumentsContract.getTreeDocumentId(location))
            val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"))
            val destination = DocumentsContract.createDocument(
                applicationContext.contentResolver,parent,"application/octet-stream",
                "EvoluFisico_AUTO_${stamp}.efbak"
            ) ?: error("A pasta não aceita novos arquivos.")
            try {
                applicationContext.contentResolver.openOutputStream(destination,"w")?.use {
                    it.write(data); it.flush()
                } ?: error("Falha ao criar backup automático.")
                // Verificação criptográfica real por releitura, não apenas tamanho.
                val input = applicationContext.contentResolver.openInputStream(destination)
                    ?: error("Não foi possível reler o arquivo de backup.")
                val checked = manager.inspectBackup(input,secret)
                check(checked.recordCount == manager.summary().recordCount) {
                    "Contagem de registros inconsistente; confira o backup."
                }
                scheduler.markSuccess()
            } catch (e: Exception) {
                runCatching {
                    DocumentsContract.deleteDocument(applicationContext.contentResolver,destination)
                }
                throw e
            } finally { data.fill(0) }
        } catch (e: Exception) {
            scheduler.markFailure(e.localizedMessage ?: e.javaClass.simpleName)
        } finally { secret.fill('\u0000') }
        // Periódico permanece agendado; erro visível em Configurações.
        return Result.success()
    }
}
