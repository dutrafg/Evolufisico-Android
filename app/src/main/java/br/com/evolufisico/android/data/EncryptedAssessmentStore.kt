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
 * Banco local cifrado e atômico. A chave não é exportável; dados não sobrevivem
 * à perda do Android Keystore ou desinstalação. Nenhuma recuperação silenciosa.
 */
class EncryptedAssessmentStore(context: Context) {
    private val file = AtomicFile(File(context.filesDir, "assessments.enc"))

    fun load(): List<Assessment> {
        val base = file.baseFile
        if (!base.exists() && !File(base.path + ".bak").exists() &&
            !File(base.path + ".new").exists()) return emptyList()
        val blob = file.openRead().use { it.readBytes() }
        require(blob.size >= 29 && blob[0] == 1.toByte()) { "Arquivo de avaliações inválido." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, blob.copyOfRange(1, 13)))
        val root = JSONObject(String(cipher.doFinal(blob.copyOfRange(13, blob.size)), Charsets.UTF_8))
        require(root.getInt("schema") == 1) { "Esquema de avaliações não suportado." }
        val records = root.getJSONArray("assessments")
        val items = (0 until records.length()).map { fromJson(records.getJSONObject(it)) }
        require(items.distinctBy { it.id }.size == items.size) { "IDs de avaliações duplicados." }
        return items
    }

    fun save(items: List<Assessment>) {
        require(items.distinctBy { it.id }.size == items.size) { "IDs de avaliações duplicados." }
        require(items.size <= 20_000) { "Limite local de avaliações excedido." }
        val array = JSONArray()
        items.forEach { record ->
            require(record.clientId.isNotBlank() && record.id.isNotBlank())
            array.put(toJson(record))
        }
        val plain = JSONObject().put("schema", 1).put("assessments", array)
            .toString().toByteArray(Charsets.UTF_8)
        val nonce = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(), GCMParameterSpec(128, nonce))
        val blob = byteArrayOf(1) + nonce + cipher.doFinal(plain)
        val stream = file.startWrite()
        try {
            stream.write(blob)
            file.finishWrite(stream)
        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
    }

    private fun toJson(a: Assessment): JSONObject = JSONObject()
        .put("id", a.id).put("clientId", a.clientId).put("dateIso", a.dateIso)
        .put("trainingLevel", a.trainingLevel).put("goal", a.goal)
        .put("limitations", a.limitations).put("weeklyFrequency", a.weeklyFrequency)
        .put("weightKg", a.weightKg).put("heightCm", a.heightCm)
        .put("waistCm", a.waistCm ?: JSONObject.NULL).put("hipCm", a.hipCm ?: JSONObject.NULL)
        .put("armCm", a.armCm ?: JSONObject.NULL).put("thighCm", a.thighCm ?: JSONObject.NULL)
        .put("healthHistory", a.healthHistory).put("dailyHabits", a.dailyHabits)
        .put("trainingExperience", a.trainingExperience)

    private fun fromJson(j: JSONObject): Assessment = Assessment(
        id = j.getString("id"), clientId = j.getString("clientId"),
        dateIso = j.getString("dateIso"), trainingLevel = j.getString("trainingLevel"),
        goal = j.getString("goal"), limitations = j.getString("limitations"),
        weeklyFrequency = j.getInt("weeklyFrequency"),
        weightKg = j.getDouble("weightKg"), heightCm = j.getDouble("heightCm"),
        waistCm = optional(j, "waistCm"), hipCm = optional(j, "hipCm"),
        armCm = optional(j, "armCm"), thighCm = optional(j, "thighCm"),
        healthHistory = j.optString("healthHistory", ""),
        dailyHabits = j.optString("dailyHabits", ""),
        trainingExperience = j.optString("trainingExperience", "")
    )

    private fun optional(j: JSONObject, field: String): Double? =
        if (j.isNull(field)) null else j.getDouble(field)

    private fun key(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val KEY_ALIAS = "evolufisico.assessments.v1"
    }
}
