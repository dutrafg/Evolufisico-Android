package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class PolishQaTest {
    @Test fun homonymsWithCommonImportedIdPrefixRemainUnambiguous(){
        val rows=listOf(Evaluation(studentId="prefixA",name="Ana"),Evaluation(studentId="prefixB",name="Ana"))
        val choices=ClientLabels.choices(rows)
        assertEquals(2,choices.map{it.second}.toSet().size)
        assertEquals(listOf("prefixA","prefixB"),choices.map{it.first})
    }
    @Test fun shortIdsThatPrefixAnotherIdStillHaveDifferentLabels(){
        val rows=listOf(Evaluation(studentId="abc",name="Ana"),Evaluation(studentId="abcdefg",name="Ana"))
        assertEquals(2,ClientLabels.choices(rows).map{it.second}.toSet().size)
    }
    @Test fun duplicateEvaluationDoesNotDuplicateClientChoice(){
        val a=Evaluation(studentId="same",name="Ana")
        assertEquals(1,ClientLabels.choices(listOf(a,a.copy(id="other"))).size)
    }
    @Test fun deviceLocaleDoesNotChangeHomonymMatching(){
        val previous=Locale.getDefault()
        try{
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            val rows=listOf(Evaluation(studentId="one",name="IRIS"),Evaluation(studentId="two",name="iris"))
            assertTrue(ClientLabels.choices(rows).all{it.second.contains(" · ")})
        }finally{Locale.setDefault(previous)}
    }
    @Test fun validFinancialExtremesCannotOverflow(){
        assertEquals(Long.MAX_VALUE,Financial.net(Long.MAX_VALUE,0))
        assertEquals(0L,Financial.net(0,Long.MAX_VALUE))
        assertEquals(0L,Financial.net(Long.MAX_VALUE,Long.MAX_VALUE))
    }
    @Test fun negativeValuesCannotWrapAroundIntoValidPrices(){
        assertThrows(IllegalArgumentException::class.java){Financial.net(Long.MAX_VALUE,-1)}
        assertThrows(IllegalArgumentException::class.java){Financial.net(Long.MIN_VALUE,1)}
    }
    @Test fun pastedPdfSpacesDoNotSplitTheSameExercise(){
        assertEquals(TrainingInsights.exerciseKey("Remada baixa"),TrainingInsights.exerciseKey("  REMADA\u00a0 baixa "))
        assertNotEquals(TrainingInsights.exerciseKey("Remada baixa"),TrainingInsights.exerciseKey("Remada alta"))
    }
    @Test fun pastedSpacesAreNormalizedInDailyHistoryAsWell(){
        val today=LocalDate.of(2026,10,22)
        val session=TrainingSession(title="T",studentId="c",student="C",date=today.toString(),completed=true,sets=listOf(
            WorkoutSet(exercise="Remada baixa",done=true),WorkoutSet(exercise="REMADA\u00a0baixa",done=true)))
        val result=TrainingInsights.history(listOf(session),"c",today,today,today)
        assertEquals(1,result.size);assertEquals(2,result.single().sets)
    }
}
