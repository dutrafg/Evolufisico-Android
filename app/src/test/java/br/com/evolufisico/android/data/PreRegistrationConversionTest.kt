package br.com.evolufisico.android.data

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class PreRegistrationConversionTest {
    private fun draft()=PreRegistration.create(
        "Pessoa Teste","provisorio@example.org","(11) 99887-7766",
        Instant.parse("2026-10-21T10:00:00Z"))

    @Test fun createsClientUsingStableDraftIdAndKeepsOriginalListUntouched() {
        val record=draft()
        val old=emptyList<Client>()
        val next=PreRegistrationConversion.prepare(old,record)
        assertEquals(0,old.size)
        assertEquals(1,next.size)
        assertEquals(record.id,next.single().id)
        assertEquals(record.name,next.single().name)
        assertEquals(record.email,next.single().email)
        assertEquals(record.phone,next.single().phone)
    }
    @Test fun repeatedCommitDoesNotCreateDuplicate() {
        val record=draft()
        val once=PreRegistrationConversion.prepare(emptyList(),record)
        val again=PreRegistrationConversion.prepare(once,record)
        assertSame(once,again)
        assertEquals(1,again.size)
    }
    @Test fun matchingIdWithDifferentContentCannotOverwriteExistingClient() {
        val record=draft()
        val completed=PreRegistrationConversion.prepare(emptyList(),record)
        assertThrows(IllegalArgumentException::class.java) {
            PreRegistrationConversion.prepare(completed,record.copy(name="Outra Pessoa"))
        }
    }
    @Test fun existingEmailAndInvalidDataDoNotCreateAnotherClient() {
        val record=draft()
        val clients=listOf(Client("another-id","Pessoa Existente",
            "11988887766","PROVISORIO@example.org"))
        assertThrows(IllegalArgumentException::class.java) {
            PreRegistrationConversion.prepare(clients,record)
        }
        assertThrows(IllegalStateException::class.java) {
            PreRegistrationConversion.prepare(emptyList(),record.copy(phone="4"))
        }
        assertEquals(1,clients.size)
    }
}
