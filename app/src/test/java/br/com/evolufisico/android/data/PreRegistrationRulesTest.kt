package br.com.evolufisico.android.data

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class PreRegistrationRulesTest {
    private val start=Instant.parse("2026-10-21T10:00:00Z")
    private fun record()=PreRegistration.create("Pessoa Teste",
        "teste@example.org","(11) 99999-1234",start)

    @Test fun validDraftDoesNotCreateACompleteClientOrAssessment() {
        val r=record()
        assertNull(PreRegistrationRules.validate(r))
        assertEquals("Pessoa Teste",r.name)
        assertEquals(start.toString(),r.createdAtIso)
    }
    @Test fun rejectsInvalidContactsAndTimestamps() {
        val r=record()
        assertNotNull(PreRegistrationRules.validate(r.copy(email="invalid")))
        assertNotNull(PreRegistrationRules.validate(r.copy(phone="123")))
        assertNotNull(PreRegistrationRules.validate(r.copy(name="")))
        assertNotNull(PreRegistrationRules.validate(r.copy(updatedAtIso="bad")))
        assertNotNull(PreRegistrationRules.validate(r.copy(
            updatedAtIso=start.minusSeconds(10).toString())))
    }
    @Test fun rejectsDuplicateIdsAndMoreThanThreeHundredRecords() {
        val r=record()
        assertThrows(IllegalArgumentException::class.java) {
            PreRegistrationRules.validateAll(listOf(r,r))
        }
        val copies=(0..300).map { r.copy(id="id-$it") }
        assertThrows(IllegalArgumentException::class.java) {
            PreRegistrationRules.validateAll(copies)
        }
        PreRegistrationRules.validateAll(copies.take(300))
    }
}
