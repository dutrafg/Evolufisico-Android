package br.com.evolufisico.android.forms

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

/** Repositório offline, isolado de clientes e avaliações, atômico e cifrado. */
class EncryptedFormStore(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "forms.enc"))
    @Synchronized
    fun load(): List<FormResponse> {
        val base = file.baseFile
        if (!base.exists() && !File(base.path + ".bak").exists() &&
            !File(base.path + ".new").exists()) return emptyList()
        val blob = file.openRead().use { it.readBytes() }
        require(blob.size >= 29 && blob[0] == 1.toByte()) { "Arquivo de formulários inválido." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, blob.copyOfRange(1,13)))
        val root = JSONObject(String(cipher.doFinal(blob.copyOfRange(13,blob.size)), Charsets.UTF_8))
        require(root.getInt("schema") == 1) { "Versão de formulários não reconhecida." }
        return FormsJson.fromArray(root.getJSONArray("forms")).also {
            require(it.distinctBy { entry -> entry.id }.size == it.size)
        }
    }
    @Synchronized
    fun save(items: List<FormResponse>, clientIds: Set<String>) {
        require(items.size <= 20000 && items.distinctBy { it.id }.size == items.size)
        items.forEach { item -> require(FormRules.validate(item, clientIds) == null) {
            FormRules.validate(item, clientIds) ?: "Formulário inválido."
        } }
        val plain = JSONObject().put("schema", 1).put("forms", FormsJson.toArray(items))
            .toString().toByteArray(Charsets.UTF_8)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val iv = cipher.iv
        require(iv.size == 12) { "IV de criptografia inválido." }
        val blob = byteArrayOf(1) + iv + cipher.doFinal(plain)
        val stream = file.startWrite()
        try { stream.write(blob); file.finishWrite(stream) }
        catch (e: Exception) { file.failWrite(stream); throw e }
    }
    private fun key(): SecretKey {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keystore.getKey("evolufisico.forms.v1", null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder("evolufisico.forms.v1",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        return generator.generateKey()
    }
}
object FormsJson {
    fun toArray(items: List<FormResponse>) = JSONArray().apply {
        items.forEach { item ->
            val answers = JSONObject()
            item.answers.forEach { (key, value) -> answers.put(key, value) }
            put(JSONObject().put("id",item.id).put("clientId",item.clientId)
                .put("templateId",item.templateId).put("dateIso",item.dateIso)
                .put("answers",answers))
        }
    }
    fun fromArray(array: JSONArray): List<FormResponse> {
        require(array.length() <= 20000)
        return (0 until array.length()).map { i ->
            val json = array.getJSONObject(i)
            val answers = json.getJSONObject("answers")
            require(answers.length() <= 30)
            FormResponse(json.getString("id"),json.getString("clientId"),
                json.getString("templateId"),json.getString("dateIso"),
                answers.keys().asSequence().associateWith { answers.getString(it) })
        }
    }
}
