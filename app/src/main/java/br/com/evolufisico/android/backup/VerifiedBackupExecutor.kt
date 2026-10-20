package br.com.evolufisico.android.backup

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class BackupRunReport(val fileName: String, val recordCount: Int)

/**
 * Um único executor serve ao trabalho periódico e ao backup manual imediato.
 * Mantém bloqueio intra-processo, grava novo arquivo e verifica por releitura
 * antes de marcá-lo como concluído.
 */
object VerifiedBackupExecutor {
    @Synchronized
    fun execute(context: Context): BackupRunReport {
        val app = context.applicationContext
        val scheduler = DailyBackupScheduler(app)
        scheduler.markAttempt()
        var password: CharArray? = null
        var data: ByteArray? = null
        var destination: Uri? = null
        try {
            val state = scheduler.state()
            check(state.active) { "Ative o backup diário nas Configurações." }
            val folder = Uri.parse(state.folderUri
                ?: error("Selecione uma pasta de backups antes de executar."))
            require(app.contentResolver.persistedUriPermissions.any {
                it.uri == folder && it.isReadPermission && it.isWritePermission
            }) { "Permissão da pasta não está mais disponível. Escolha a pasta novamente." }

            val credential = CharArray(0) // Protótipo: backup sem senha ou criptografia
            password = credential
            val manager = SecureBackupManager(app)
            val archive = manager.export(credential)
            data = archive
            val parent = DocumentsContract.buildDocumentUriUsingTree(
                folder, DocumentsContract.getTreeDocumentId(folder)
            )
            val stamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"))
            val fileName = "EvoluFisico_AUTO_${stamp}_${UUID.randomUUID().toString().take(8)}.json"
            val created = DocumentsContract.createDocument(
                app.contentResolver, parent, "application/octet-stream", fileName
            ) ?: error("A pasta escolhida não aceita a criação de arquivos.")
            destination = created
            app.contentResolver.openOutputStream(created, "w")?.use { out ->
                out.write(archive)
                out.flush()
            } ?: error("Não foi possível gravar o novo arquivo de backup.")

            // Reabrir o JSON detecta falhas de escrita e conteúdo inválido.
            val input = app.contentResolver.openInputStream(created)
                ?: error("A pasta permite gravar, mas não permite reler o backup.")
            val check = manager.inspectBackup(input, credential)
            check(check == manager.summary()) {
                "Os dados locais mudaram durante a criação do backup. Tente novamente."
            }
            val report = BackupRunReport(fileName, check.recordCount)
            destination = null // arquivo verificado: não apagar em finally
            scheduler.markSuccess(report)
            return report
        } catch (e: Exception) {
            scheduler.markFailure(
                "Backup não confirmado: " + (e.localizedMessage ?: e.javaClass.simpleName)
            )
            throw e
        } finally {
            if (destination != null) {
                runCatching {
                    DocumentsContract.deleteDocument(app.contentResolver, destination)
                }
            }
            password?.fill('\u0000')
            data?.fill(0)
        }
    }
}
