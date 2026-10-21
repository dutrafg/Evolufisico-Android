package br.com.evolufisico.android.backup

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class BackupFileVerificationTest {
    private val snapshot="""{"app":"EvoluFisico-Android","schema":1,"clients":[]}"""
        .toByteArray(Charsets.UTF_8)

    @Test fun acceptsCompleteBackupReadFromDocumentProvider() {
        assertTrue(BackupFileVerification.matches(ByteArrayInputStream(snapshot),snapshot))
    }

    @Test fun refusesTruncatedOrExtendedBackups() {
        assertFalse(BackupFileVerification.matches(
            ByteArrayInputStream(snapshot.copyOf(snapshot.size-1)),snapshot))
        assertFalse(BackupFileVerification.matches(
            ByteArrayInputStream(snapshot+"extra".toByteArray(Charsets.UTF_8)),snapshot))
    }

    @Test fun refusesEqualSizeCorruptionOrEmptyBackups() {
        val corrupted=snapshot.clone()
        corrupted[corrupted.lastIndex]=(corrupted.last().toInt() xor 1).toByte()
        assertFalse(BackupFileVerification.matches(ByteArrayInputStream(corrupted),snapshot))
        assertFalse(BackupFileVerification.matches(ByteArrayInputStream(byteArrayOf()),snapshot))
        assertTrue(BackupFileVerification.matches(ByteArrayInputStream(byteArrayOf()),byteArrayOf()))
    }
}
