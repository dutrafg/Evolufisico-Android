package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test

class Beta100ExportLimitsTest {
    @Test fun oversizedAgendaIsRejected(){
        val item=Appointment(student="C")
        assertThrows(IllegalArgumentException::class.java){ExportTables.build("Agenda",emptyList(),emptyList(),emptyList(),emptyList(),emptyList(),appointments=List(20_001){item})}
    }
    @Test fun oversizedNotesAreRejectedEvenWhenContentIsOff(){
        val item=NotebookEntry(title="N",body="texto")
        assertThrows(IllegalArgumentException::class.java){ExportTables.build("Notas",emptyList(),emptyList(),emptyList(),emptyList(),emptyList(),notes=List(20_001){item})}
    }
    @Test fun seriesLimitAppliesToExpandedRowsBeforeCsvBuilding(){
        val item=TrainingSession(title="T",sets=List(200){WorkoutSet(exercise="E")})
        assertThrows(IllegalArgumentException::class.java){ExportTables.build("Séries",emptyList(),emptyList(),List(101){item},emptyList(),emptyList())}
    }
    @Test fun exactNoteRowLimitRemainsExportable(){
        val item=NotebookEntry(title="N")
        val csv=ExportTables.build("Notas",emptyList(),emptyList(),emptyList(),emptyList(),emptyList(),notes=List(20_000){item})
        assertEquals(20_001,csv.count{it=='\n'})
    }
}
