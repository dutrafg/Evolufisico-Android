package br.com.evolufisico.android.billing

import org.junit.Assert.*
import org.junit.Test

class BillingCsvTest {
    @Test fun exportedAmountsRemainExactAndAreSorted() {
        val charges=listOf(
            Charge("b","1","Serviço",1999,"2026-10-23","2026-10-23"),
            Charge("a","1","Treino",2500,"2026-10-21")
        )
        val text=BillingCsv.generate(charges,mapOf("1" to "Maria"))
            .toString(Charsets.UTF_8)
        assertTrue(text.startsWith("\uFEFFID;Cliente;Descrição;Valor em centavos"))
        assertTrue(text.indexOf("a;Maria")<text.indexOf("b;Maria"))
        assertTrue(text.contains(";2500;2026-10-21;Pendente;"))
        assertTrue(text.contains(";1999;2026-10-23;Recebida;2026-10-23"))
    }
    @Test fun escapesSpreadsheetExpressionsAndSpecialCharacters() {
        val charge=Charge("1","a","=SUM(1;2)\n\"nota\"",2500,"2026-10-21")
        val txt=BillingCsv.generate(listOf(charge),mapOf("a" to "+CLIENTE"))
            .toString(Charsets.UTF_8)
        assertTrue(txt.contains("'+CLIENTE"))
        assertTrue(txt.contains("\"'=SUM(1;2)\n\"\"nota\"\"\""))
    }
    @Test fun emptyCsvStillContainsHeader() {
        assertTrue(BillingCsv.generate(emptyList(),emptyMap())
            .toString(Charsets.UTF_8).contains("Vencimento"))
    }
}
