package br.com.evolufisico.android.data

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
