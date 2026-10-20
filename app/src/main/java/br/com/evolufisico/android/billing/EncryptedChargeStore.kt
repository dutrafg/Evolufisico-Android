package br.com.evolufisico.android.billing

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Arquivo financeiro cifrado, isolado das avaliações. */
class EncryptedChargeStore(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "charges.enc"))

    fun load(): List<Charge> {
        val base = file.baseFile
        if (!base.exists() && !File(base.path + ".bak").exists()
            && !File(base.path + ".new").exists()) return emptyList()
        val input = file.openRead().use { it.readBytes() }
        require(input.size >= 29 && input[0] == 1.toByte()) { "Arquivo financeiro inválido." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, input.copyOfRange(1, 13)))
        val payload = JSONObject(String(cipher.doFinal(input.copyOfRange(13, input.size)), Charsets.UTF_8))
        require(payload.getInt("schema") == 1) { "Formato de cobranças não suportado." }
        return ChargeJson.fromArray(payload.getJSONArray("charges")).also {
            require(it.distinctBy { item -> item.id }.size == it.size) { "Cobranças duplicadas." }
        }
    }

    fun save(charges: List<Charge>, clientIds: Set<String>) {
        require(charges.size <= 20000 && charges.distinctBy { it.id }.size == charges.size)
        charges.forEach { require(ChargeRules.validate(it, clientIds) == null) }
        val bytes = JSONObject().put("schema", 1).put("charges", ChargeJson.toArray(charges))
            .toString().toByteArray(Charsets.UTF_8)
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        // Android Keystore gera o IV único; IV fornecido pelo app é recusado por padrão.
        val nonce = cipher.iv
        require(nonce.size == 12) { "IV AES-GCM inválido." }
        val encoded = byteArrayOf(1) + nonce + cipher.doFinal(bytes)
        val stream = file.startWrite()
        try { stream.write(encoded); file.finishWrite(stream) }
        catch (error: Exception) { file.failWrite(stream); throw error }
    }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey("evolufisico.charges.v1", null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder("evolufisico.charges.v1",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        return generator.generateKey()
    }
}

object ChargeJson {
    fun toArray(charges: List<Charge>): JSONArray = JSONArray().apply {
        charges.forEach { charge ->
            put(JSONObject().put("id", charge.id).put("clientId", charge.clientId)
                .put("description", charge.description).put("amountCents", charge.amountCents)
                .put("dueDateIso", charge.dueDateIso)
                .put("receivedDateIso", charge.receivedDateIso ?: JSONObject.NULL))
        }
    }

    fun fromArray(array: JSONArray): List<Charge> {
        require(array.length() <= 20000)
        return (0 until array.length()).map { i ->
            val entry = array.getJSONObject(i)
            Charge(entry.getString("id"), entry.getString("clientId"),
                entry.getString("description"), entry.getLong("amountCents"),
                entry.getString("dueDateIso"),
                if (entry.isNull("receivedDateIso")) null else entry.getString("receivedDateIso"))
        }
    }
}
