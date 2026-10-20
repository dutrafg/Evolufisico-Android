package br.com.evolufisico.android.operations

import org.junit.Assert.*
import org.junit.Test

class ClientOperationsCsvTest {
    @Test fun spreadsheetEscapingAndEmptyRate() {
        val csv=ClientOperationsCsv.generate(listOf(ClientOperations(
            "c","=SOMA(A1);\"Teste\"",0,0,0,0,0,0,2500,0,1000)))
            .toString(Charsets.UTF_8)
        assertTrue(csv.startsWith("\uFEFFCliente;Agendados"))
        assertTrue(csv.contains("\"'=SOMA(A1);\"\"Teste\"\"\""))
        assertTrue(csv.contains("2500;0;1000"))
        assertEquals(2,csv.trim().split("\r\n").size)
    }
    @Test fun emptyFileStillHasHeader() {
        assertTrue(ClientOperationsCsv.generate(emptyList()).toString(Charsets.UTF_8)
            .contains("Comparecimento %"))
    }
}
