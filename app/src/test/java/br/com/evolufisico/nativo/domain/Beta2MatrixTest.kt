package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.LocalDate

/** 10,000 new Beta 2 QA scenarios. Not a count of development stages or product features. */
@RunWith(Parameterized::class)
class Beta2MatrixTest(private val case:Int) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name="beta2-{0}")
        fun cases():Collection<Array<Any>> = (0 until 10000).map{arrayOf<Any>(it)}
    }
    @Test fun boundaryScenario(){
        val n=case/10
        when(case%10){
            0->{val reps=n-100;val item=TrainingSession(title="T",sets=listOf(WorkoutSet(exercise="E",repetitions=reps)))
                assertEquals(reps in 1..1000,runCatching{WorkspaceRules.validate(item)}.isSuccess)}
            1->{val kg=n*2.5-100;val item=TrainingSession(title="T",sets=listOf(WorkoutSet(exercise="E",loadKg=kg)))
                assertEquals(kg in 0.0..2000.0,runCatching{WorkspaceRules.validate(item)}.isSuccess)}
            2->{val sets=(0 until n%20).map{WorkoutSet(exercise="E$it",done=it%2==0,repetitions=it+1,loadKg=n/10.0)}
                val original=TrainingSession(title="T$n",completed=true,effort=7,sets=sets)
                val copy=ProfessionalTools.newSession(original,LocalDate.of(2026,1,1).plusDays(n.toLong()))
                assertEquals(sets.map{it.exercise},copy.sets.map{it.exercise});assertTrue(copy.sets.none{it.done});assertFalse(copy.completed)
                assertEquals(sets.size,copy.sets.map{it.id}.toSet().size);assertTrue(copy.sets.none{row->sets.any{it.id==row.id}})}
            3->{val trigger=listOf("=","+","-","@","\t="," \uFEFF=","\u0000=","\u200B=")[n%8]
                val encoded=ProfessionalTools.csvCell(trigger+"text$n;\"\nnext")
                assertTrue(encoded.startsWith("\"'"));assertTrue(encoded.endsWith("\""));assertTrue(encoded.contains("\"\""))}
            4->{val today=LocalDate.of(2026,10,22);val length=n%14
                val rows=(0 until length).map{HabitEntry(title="H",studentId="c$n",student="C",date=today.minusDays(it.toLong()).toString(),value=1.0,target=1.0)}
                val ref=HabitEntry(title="H",studentId="c$n",student="C")
                val result=ProfessionalTools.habitSummary(ref,rows+rows,today)
                assertEquals(length,result.streak);assertEquals(minOf(length,7),result.achievedDays)}
            5->{val task=TaskItem(title="T$n",priority=n%6-1)
                assertEquals(task.priority in 1..3,runCatching{WorkspaceRules.validate(task)}.isSuccess)
                val normal=task.copy(priority=1,done=false);val urgent=task.copy(id="urgent",priority=3,done=false)
                assertEquals(urgent,ProfessionalTools.taskOrder(listOf(normal,urgent)).first())}
            6->{val length=n%500;val note=NotebookEntry(title="N",body="x".repeat(length))
                val preview=ProfessionalTools.notePreview(note,false)
                assertEquals(if(length>240)241 else length,preview.length);assertEquals(note.body,ProfessionalTools.notePreview(note,true))}
            7->{val width=240.0+n;val scale=1.0+(n%11)/10.0
                val columns=LayoutPolicy.columns(width,scale)
                assertTrue(columns in 1..3);if(columns>1)assertTrue(width/columns>=160.0*scale)
                assertTrue(LayoutPolicy.columns(width,scale)>=LayoutPolicy.columns(width,scale+0.5))}
            8->{val size=n%75+1;val total=n;val all=(0 until ((total+size-1)/size)).flatMap{LayoutPolicy.pageBounds(total,it,size).toList()}
                assertEquals((0 until total).toList(),all)
                assertTrue(LayoutPolicy.pageBounds(total,Int.MAX_VALUE,size).all{it in 0 until total})}
            9->{val baseline=100.0+n/10.0;val target=baseline-10.0;val current=target-n/100.0
                val result=GoalTracking.progress(baseline,current,target)!!
                assertEquals(100.0,result.percent,0.00001);assertEquals(0.0,result.remaining,0.0)}
        }
    }
}
