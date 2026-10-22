package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class ClientBackupScopeTest {
    @Test fun includesEverySupportedLinkedModule() {
        listOf("evaluation","charge","intake","appointment","task","training","habit","note").forEach {
            assertTrue(ClientBackupScope.includes(it,"client-a","client-a"))
        }
    }
    @Test fun excludesOtherClientsAndUnlinkedRecords() {
        assertFalse(ClientBackupScope.includes("note","a","b"))
        assertFalse(ClientBackupScope.includes("note","a",""))
    }
    @Test fun excludesJointBillingWithEitherLegacyNameOrPermanentId() {
        assertFalse(ClientBackupScope.includes("charge","a","a",secondStudentId="b"))
        assertFalse(ClientBackupScope.includes("charge","a","a",secondStudent="Outro cliente"))
    }
    @Test fun excludesGlobalTemplatesAndUnknownKinds() {
        assertFalse(ClientBackupScope.includes("training","a","a",isTemplate=true))
        assertFalse(ClientBackupScope.includes("unknown","a","a"))
    }
    @Test(expected=IllegalArgumentException::class) fun rejectsEmptyTarget() {
        ClientBackupScope.includes("note","","a")
    }
}
