package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class TrainingInsightsTest {
    private val today=LocalDate.of(2026,10,22)
    private fun session(date:String="2026-10-22",client:String="a",load:Double=20.0)=TrainingSession(
        title="Treino",studentId=client,student="Ana",date=date,completed=true,
        sets=listOf(WorkoutSet(exercise="Remada",repetitions=10,loadKg=load,done=true)))
    private fun history(rows:List<TrainingSession>,client:String="a")=TrainingInsights.history(rows,client,today.minusDays(30),today,today)
    @Test fun permanentIdSeparatesHomonyms(){assertEquals(20.0,history(listOf(session(),session(client="b",load=90.0))).single().maximumKg,0.0)}
    @Test fun noClientSelectionNeverCombinesAllClients(){assertTrue(history(listOf(session()),"").isEmpty())}
    @Test fun excludesTemplatesUnfinishedUnmarkedAndFuture(){
        val original=session()
        val rows=listOf(original,original.copy(isTemplate=true),original.copy(completed=false),
            original.copy(date="2026-10-23"),original.copy(sets=original.sets.map{it.copy(done=false)}))
        assertEquals(1,history(rows).single().sets)
    }
    @Test fun sameDaySessionsAggregateAndWhitespaceCaseDoNotSplitExercise(){
        val a=session();val b=a.copy(sets=a.sets.map{it.copy(exercise="  REMADA  ",loadKg=25.0)})
        val result=history(listOf(a,b)).single()
        assertEquals(2,result.sets);assertEquals(20L,result.repetitions)
        assertEquals(25.0,result.maximumKg,0.0);assertEquals(450.0,result.volume,0.0)
        assertEquals(history(listOf(a,b)),history(listOf(b,a)))
    }
    @Test fun trendsCompareDistinctDaysAndPreserveNegativeChange(){
        val result=TrainingInsights.trends(history(listOf(session("2026-10-21",load=25.0),session(load=20.0)))).single()
        assertEquals(-5.0,result.differenceKg!!,0.0)
        assertEquals("2026-10-21",result.previous!!.date)
    }
    @Test fun zeroLoadAndMissingPreviousAreNotEstimated(){
        val result=TrainingInsights.trends(history(listOf(session(load=0.0)))).single()
        assertEquals(0.0,result.current.volume,0.0);assertNull(result.previous);assertNull(result.differenceKg)
    }
    @Test fun batchIsValidatedBeforeAnyMutationAndAllocatesDistinctIds(){
        val batch=TrainingInsights.batch(" Remada ",3,12,25.0,197)
        assertEquals(3,batch.map{it.id}.toSet().size)
        assertTrue(batch.all{!it.done && it.exercise=="Remada"})
        assertThrows(IllegalArgumentException::class.java){TrainingInsights.batch("Remada",4,12,25.0,197)}
        assertThrows(IllegalArgumentException::class.java){TrainingInsights.batch("Remada",1,12,Double.NaN,0)}
        assertThrows(IllegalArgumentException::class.java){TrainingInsights.batch("",1,12,25.0,0)}
    }
    @Test fun csvOmitsClientAndEscapesExerciseFormulas(){
        val row=TrainingInsights.Point("2026-10-22","=danger",1,10,20.0,200.0)
        val text=TrainingInsights.csv(listOf(row))
        assertTrue(text.contains("\"'=danger\""));assertFalse(text.contains("studentId"))
        assertThrows(IllegalArgumentException::class.java){TrainingInsights.csv(List(20_001){row})}
    }
    @Test fun invertedDatesAreRejected(){assertThrows(IllegalArgumentException::class.java){TrainingInsights.history(listOf(session()),"a",today,today.minusDays(1),today)}}
}
