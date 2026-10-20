package br.com.evolufisico.android.backup

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** A senha portátil só é mantida cifrada com chave NÃO exportável do Android Keystore. */
class SealedBackupCredential(context: Context) {
    private val prefs = context.getSharedPreferences("daily_backup", Context.MODE_PRIVATE)
    private val alias = "evolufisico.dailybackup.credential.v1"

    fun save(password: CharArray) {
        require(password.size in 10..128) { "Senha deve ter entre 10 e 128 caracteres." }
        val plain = String(password).toByteArray(Charsets.UTF_8)
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key())
            val iv = cipher.iv
            require(iv.size == 12)
            val blob = byteArrayOf(1) + iv + cipher.doFinal(plain)
            check(prefs.edit().putString("sealed_password",
                Base64.encodeToString(blob, Base64.NO_WRAP)).commit()) {
                "Não foi possível guardar a senha criptografada."
            }
        } finally { plain.fill(0) }
    }

    fun read(): CharArray {
        val encoded = prefs.getString("sealed_password", null)
            ?: error("Senha protegida indisponível. Reconfigure o backup automático.")
        val blob = Base64.decode(encoded, Base64.DEFAULT)
        require(blob.size >= 29 && blob[0] == 1.toByte()) { "Credencial inválida." }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, blob.copyOfRange(1,13)))
        val plain = cipher.doFinal(blob.copyOfRange(13,blob.size))
        return try { String(plain, Charsets.UTF_8).toCharArray() }
        finally { plain.fill(0) }
    }

    fun clear() { prefs.edit().remove("sealed_password").commit() }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias,null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        return generator.generateKey()
    }
}
