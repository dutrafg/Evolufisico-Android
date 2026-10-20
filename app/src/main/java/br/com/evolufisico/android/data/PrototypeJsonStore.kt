package br.com.evolufisico.android.data

import android.content.Context
import android.util.AtomicFile
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Persistência simples (alpha): arquivos novos JSON em armazenamento interno,
 * com leitura de AES-GCM legado para migração sem apagar os originais.
 */
class PrototypeJsonStore(context: Context, stem: String, private val legacyAlias: String) {
    private val plain = AtomicFile(File(context.filesDir, "${stem}.json"))
    private val legacy = AtomicFile(File(context.filesDir, "${stem}.enc"))

    @Synchronized
    fun load(): JSONObject? {
        if (exists(plain)) return decode(plain.openRead().use { it.readBytes() })
        if (!exists(legacy)) return null
        val old = legacy.openRead().use { it.readBytes() }
        require(old.size >= 29 && old[0] == 1.toByte()) { "Formato legado inválido." }
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val key = keystore.getKey(legacyAlias, null) as? SecretKey
            ?: error("Chave anterior indisponível. Preserve o backup original.")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, old.copyOfRange(1, 13)))
        val bytes = cipher.doFinal(old.copyOfRange(13, old.size))
        val parsed = try { decode(bytes) } finally { bytes.fill(0) }
        save(parsed) // cópia JSON atômica; o arquivo cifrado antigo permanece intacto
        return parsed
    }

    @Synchronized
    fun save(json: JSONObject) {
        val data = json.toString().toByteArray(Charsets.UTF_8)
        require(data.size <= 16 * 1024 * 1024) { "Banco local maior que 16 MB." }
        val output = plain.startWrite()
        try {
            output.write(data)
            plain.finishWrite(output)
        } catch (e: Exception) {
            plain.failWrite(output)
            throw e
        }
    }

    private fun exists(file: AtomicFile): Boolean {
        val base = file.baseFile
        return base.exists() || File(base.path + ".bak").exists() ||
            File(base.path + ".new").exists()
    }

    private fun decode(bytes: ByteArray): JSONObject {
        require(bytes.isNotEmpty() && bytes[0] == '{'.code.toByte()) { "JSON inválido." }
        return JSONObject(String(bytes, Charsets.UTF_8))
    }
}
