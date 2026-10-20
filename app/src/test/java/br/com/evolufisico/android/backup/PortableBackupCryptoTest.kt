package br.com.evolufisico.android.backup

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PortableBackupCryptoTest {
    @Test fun roundTripAcrossIndependentCalls() {
        val payload = """{"clients":[{"name":"Ana"}]}""".toByteArray()
        val password = "MinhaSenhaDeTeste2026!".toCharArray()
        val encrypted = PortableBackupCrypto.encrypt(payload, password)
        assertFalse(encrypted.contentEquals(payload))
        assertArrayEquals(payload, PortableBackupCrypto.decrypt(encrypted, password))
    }

    @Test fun encryptionIsNonDeterministic() {
        val data = "Dados sensíveis".toByteArray()
        val password = "SenhaDeBackup123".toCharArray()
        assertFalse(PortableBackupCrypto.encrypt(data,password)
            .contentEquals(PortableBackupCrypto.encrypt(data,password)))
    }

    @Test fun corruptedCiphertextRejected() {
        val data = PortableBackupCrypto.encrypt("ok".toByteArray(), "SenhaDeBackup123".toCharArray())
        data[data.lastIndex] = (data.last().toInt() xor 1).toByte()
        val result = runCatching {
            PortableBackupCrypto.decrypt(data, "SenhaDeBackup123".toCharArray())
        }
        assertTrue(result.isFailure)
    }

    @Test fun wrongPasswordRejected() {
        val enc = PortableBackupCrypto.encrypt("teste".toByteArray(), "SenhaVerdadeira123".toCharArray())
        assertTrue(runCatching {
            PortableBackupCrypto.decrypt(enc, "SenhaErrada1234".toCharArray())
        }.isFailure)
    }

    @Test fun shortPasswordRejected() {
        assertTrue(runCatching {
            PortableBackupCrypto.encrypt("t".toByteArray(), "123".toCharArray())
        }.isFailure)
    }
}
