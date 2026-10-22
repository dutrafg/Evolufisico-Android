package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Prepared scenarios, not a claim that QA has run or that development stages were completed. */
@RunWith(Parameterized::class)
class Beta20MatrixTest(private val case:Int) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name="beta20-{0}")
        fun cases():Collection<Array<Any>> = (0 until 10000).map{arrayOf<Any>(it)}
    }
    @Test fun trainingBoundaryScenario(){
        val n=case/5;val today=LocalDate.of(2026,1,1).plusDays(n.toLong())
        val row=WorkoutSet(exercise="Remada",repetitions=n%1000+1,loadKg=(n%2001).toDouble(),done=true)
        val item=TrainingSession(title="T",studentId="client$n",student="C",date=today.toString(),completed=true,sets=listOf(row))
        fun history(rows:List<TrainingSession>)=TrainingInsights.history(rows,item.studentId,today.minusDays(90),today,today)
        when(case%5){
            0->{val point=history(listOf(item)).single();assertEquals(row.loadKg*row.repetitions,point.volume,0.0);assertEquals(row.loadKg,point.maximumKg,0.0)}
            1->{val count=n%12;val existing=n%201;val allowed=count in 1..10 && count<=200-existing
                val result=runCatching{TrainingInsights.batch("E",count,10,25.0,existing)}
                assertEquals(allowed,result.isSuccess);if(allowed)assertEquals(count,result.getOrThrow().map{it.id}.toSet().size)}
            2->{val invalid=listOf(item.copy(isTemplate=true),item.copy(completed=false),item.copy(studentId="other"),item.copy(date=today.plusDays(1).toString()))
                assertTrue(history(invalid).isEmpty())}
            3->{val point=TrainingInsights.Point(today.toString(),listOf("=","+","-","@")[n%4]+"E$n",1,10,20.0,200.0)
                assertTrue(TrainingInsights.csv(listOf(point)).contains("\"'"+point.exercise+"\""))}
            4->{val other=item.copy(sets=listOf(row.copy(exercise=" REMADA ")))
                assertEquals(history(listOf(item,other)),history(listOf(other,item)))
                assertEquals(2,history(listOf(item,other)).single().sets)}
        }
    }
}
