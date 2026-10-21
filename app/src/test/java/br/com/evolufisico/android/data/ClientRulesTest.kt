package br.com.evolufisico.android.data

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClientRulesTest {
    @Test fun rejectsEmptyName() {
        assertEquals("Informe o nome do cliente.", ClientRules.validateName("   "))
    }
    @Test fun acceptsNormalName() {
        assertNull(ClientRules.validateName("Maria Oliveira"))
    }
    @Test fun rejectsLongName() {
        assertEquals("Use no máximo 80 caracteres.", ClientRules.validateName("a".repeat(81)))
    }
}

class ClientContactRulesTest {
    @Test fun acceptsOptionalContactFields() {
        val entry=Client("id","Maria Silva","(43) 99999-8888","maria@teste.com","1995-07-22")
        assertNull(ClientRules.validateContact(entry,java.time.LocalDate.of(2026,10,21)))
    }
    @Test fun rejectsInvalidContactOrFutureBirthDate() {
        val base=Client("id","Maria")
        assertEquals("Informe um telefone válido (8 a 15 dígitos).",
            ClientRules.validateContact(base.copy(phone="123")))
        assertEquals("Informe um e-mail válido.",
            ClientRules.validateContact(base.copy(email="sem-arroba")))
        assertEquals("Data de nascimento fora do período permitido.",
            ClientRules.validateContact(base.copy(birthDateIso="2099-01-01"),
                java.time.LocalDate.of(2026,10,21)))
    }
    @Test fun existingClientsKeepPermanentIdAfterEditing() {
        val old=Client("permanent","Antigo")
        val updated=old.copy(name="Novo",email="contato@teste.com")
        assertEquals("permanent",updated.id)
        assertNull(ClientRules.validateContact(updated))
        assertEquals("",old.email)
    }
    @Test fun queryMatchesNameEmailAndPhone() {
        val client=Client("id","Ana Oliveira","(43) 98888-7777","ana@example.org")
        assertTrue(ClientSearch.matches(client,"Oliveira"))
        assertTrue(ClientSearch.matches(client,"8888"))
        assertTrue(ClientSearch.matches(client,"EXAMPLE.ORG"))
        assertFalse(ClientSearch.matches(client,"inexistente"))
    }
}
