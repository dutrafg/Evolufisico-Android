package br.com.evolufisico.android.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargeRulesTest {
    @Test fun amountParsingUsesExactCents() {
        assertEquals(2500L, ChargeRules.parseReais("25,00"))
        assertEquals(999L, ChargeRules.parseReais("9.99"))
        assertEquals(100L, ChargeRules.parseReais("1"))
        assertNull(ChargeRules.parseReais("0"))
        assertNull(ChargeRules.parseReais("10,999"))
        assertNull(ChargeRules.parseReais("1e4"))
        assertNull(ChargeRules.parseReais("-20"))
    }
    @Test fun chargeReferencesClientAndHasValidDates() {
        val c = Charge("1", "cliente-1", "Avaliação", 2500, "2026-10-20")
        assertNull(ChargeRules.validate(c, setOf("cliente-1")))
        assertEquals("Cliente inválido.", ChargeRules.validate(c, emptySet()))
        assertTrue(ChargeRules.validate(c.copy(dueDateIso = "2026-99-01"),
            setOf("cliente-1"))!!.contains("vencimento"))
        assertFalse(c.isReceived)
        assertTrue(c.copy(receivedDateIso = "2026-10-21").isReceived)
    }
    @Test fun overdueUsesDueDateAndReceivedStatus() {
        val charge = Charge("1", "a", "A", 2500, "2026-01-05")
        assertTrue(ChargeRules.isOverdue(charge, java.time.LocalDate.parse("2026-01-06")))
        assertFalse(ChargeRules.isOverdue(charge, java.time.LocalDate.parse("2026-01-05")))
        assertFalse(ChargeRules.isOverdue(charge.copy(receivedDateIso = "2026-01-04"),
            java.time.LocalDate.parse("2026-01-06")))
    }

    @Test fun monthlyTotalsUseReceiptDateAndDueDateCorrectly() {
        val pending = Charge("a","c","A",2000,"2026-10-31")
        val received = Charge("b","c","B",3000,"2026-09-20","2026-10-02")
        val month = java.time.YearMonth.of(2026,10)
        assertEquals(2000L, ChargeRules.monthlyTotal(listOf(pending,received),false,month))
        assertEquals(3000L, ChargeRules.monthlyTotal(listOf(pending,received),true,month))
        assertEquals(0L, ChargeRules.monthlyTotal(listOf(pending,received),true,
            java.time.YearMonth.of(2026,9)))
    }
    @Test fun monthlyAndEditingReversalAreExact() {
        val month = java.time.YearMonth.of(2026,10)
        val a = Charge("id-a","c","Avaliação",2500,"2026-10-25")
        val b = Charge("id-b","c","Sessão",1900,"2026-09-30","2026-10-01")
        assertEquals(2500L, ChargeRules.monthlyTotal(listOf(a,b),false,month))
        assertEquals(1900L, ChargeRules.monthlyTotal(listOf(a,b),true,month))
        assertEquals("25,00", ChargeRules.formatInput(a.amountCents))
        assertEquals(a.id, a.copy(amountCents = 3000).id)
        assertFalse(b.copy(receivedDateIso = null).isReceived)
    }
    @Test fun totalsAreSeparated() {
        val a = Charge("1","a","A",2000,"2026-10-01")
        val b = Charge("2","a","B",1000,"2026-10-01", "2026-10-02")
        assertEquals(2000L, ChargeRules.total(listOf(a,b), false))
        assertEquals(1000L, ChargeRules.total(listOf(a,b), true))
    }
}
