package br.com.evolufisico.android.backup

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Formato independente do Android Keystore: EFBAK004 | salt16 | iv12 | ciphertext + tag16.
 * PBKDF2-HMAC-SHA256 com 250.000 iterações e AES-256-GCM autenticado.
 * Sem chave ou senha persistidas no dispositivo.
 */
object PortableBackupCrypto {
    private val marker = "EFBAK004".toByteArray(Charsets.US_ASCII)
    private const val KDF_ROUNDS = 250000
    private const val MAX_INPUT = 16 * 1024 * 1024

    fun encrypt(data: ByteArray, password: CharArray): ByteArray {
        require(password.size >= 10) { "Use uma senha de backup com no mínimo 10 caracteres." }
        require(data.size in 1..MAX_INPUT) { "Conteúdo do backup fora do limite permitido." }
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val nonce = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val aes = makeKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, aes, GCMParameterSpec(128, nonce))
        cipher.updateAAD(marker)
        return marker + salt + nonce + cipher.doFinal(data)
    }

    fun decrypt(encoded: ByteArray, password: CharArray): ByteArray {
        require(encoded.size in 53..(MAX_INPUT + 52)) { "Tamanho do backup inválido." }
        require(encoded.copyOfRange(0, marker.size).contentEquals(marker)) {
            "Formato do arquivo de backup não reconhecido."
        }
        val salt = encoded.copyOfRange(8, 24)
        val nonce = encoded.copyOfRange(24, 36)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, makeKey(password, salt), GCMParameterSpec(128, nonce))
        cipher.updateAAD(marker)
        return cipher.doFinal(encoded.copyOfRange(36, encoded.size))
    }

    private fun makeKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, KDF_ROUNDS, 256)
        return try {
            val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
            try { SecretKeySpec(bytes, "AES") } finally { bytes.fill(0) }
        } finally {
            spec.clearPassword()
        }
    }
}
