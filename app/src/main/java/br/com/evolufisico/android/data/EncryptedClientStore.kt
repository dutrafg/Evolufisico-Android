package br.com.evolufisico.android.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Protótipo de persistência local criptografada, sem dados de saúde.
 * Não recria arquivos em caso de corrupção/falha de chave; a UI exibe o erro.
 */
class EncryptedClientStore(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "clients.enc"))

    fun load(): List<Client> {
        val base = file.baseFile
        if (!base.exists() && !File(base.path + ".bak").exists() &&
            !File(base.path + ".new").exists()) return emptyList()
        val bytes = file.openRead().use { it.readBytes() }
        require(bytes.size >= HEADER_SIZE + TAG_SIZE) { "Arquivo criptografado inválido." }
        require(bytes[0] == VERSION) { "Formato de armazenamento não suportado." }
        val iv = bytes.copyOfRange(1, HEADER_SIZE)
        val ciphertext = bytes.copyOfRange(HEADER_SIZE, bytes.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
        val parsed = JSONObject(String(cipher.doFinal(ciphertext), Charsets.UTF_8))
        require(parsed.getInt("schema") == 1) { "Versão dos dados não suportada." }
        val arr = parsed.getJSONArray("clients")
        return (0 until arr.length()).map { i ->
            val item = arr.getJSONObject(i)
            Client(id = item.getString("id"), name = item.getString("name"))
        }
    }

    fun save(clients: List<Client>) {
        require(clients.all { ClientRules.validateName(it.name) == null })
        require(clients.map { it.id }.distinct().size == clients.size)
        val arr = JSONArray()
        clients.forEach { arr.put(JSONObject().put("id", it.id).put("name", it.name)) }
        val plain = JSONObject().put("schema", 1).put("clients", arr)
            .toString().toByteArray(Charsets.UTF_8)
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(), GCMParameterSpec(128, iv))
        val blob = byteArrayOf(VERSION) + iv + cipher.doFinal(plain)
        val stream = file.startWrite()
        try {
            stream.write(blob)
            file.finishWrite(stream)
        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
    }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val KEY_ALIAS = "evolufisico.clients.v1"
        const val HEADER_SIZE = 13
        const val TAG_SIZE = 16
        const val VERSION: Byte = 1
    }
}
