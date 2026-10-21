package br.com.evolufisico.android.backup

import java.io.InputStream
import java.security.MessageDigest

/**
 * Confere os bytes reais devolvidos pelo provedor Android após salvar um backup.
 * Não registra conteúdo ou dados pessoais e não depende do packageName instalado.
 */
object BackupFileVerification {
    fun matches(saved:InputStream, expected:ByteArray):Boolean {
        val hash=MessageDigest.getInstance("SHA-256")
        var readBytes=0L
        saved.use { source ->
            val block=ByteArray(8192)
            while(true) {
                val count=source.read(block)
                if(count<0) break
                if(count==0) continue
                readBytes+=count
                if(readBytes>expected.size.toLong()) return false
                hash.update(block,0,count)
            }
        }
        val reference=MessageDigest.getInstance("SHA-256").digest(expected)
        return readBytes==expected.size.toLong() &&
            MessageDigest.isEqual(hash.digest(),reference)
    }
}
