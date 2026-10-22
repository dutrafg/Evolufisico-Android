package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** 10,000 QA scenarios. They are data vectors, not 10,000 product features or development stages. */
@RunWith(Parameterized::class)
class WorkspaceMatrixTest(private val case:Int) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name="workspace-{0}")
        fun cases():Collection<Array<Any>> = (0 until 10000).map{arrayOf<Any>(it)}
    }
    @Test fun boundaryAndLinkScenario() {
        val n=case/8
        when(case%8) {
            0 -> {
                val duration=n-100
                val actual=runCatching{WorkspaceRules.validate(TrainingSession(title="Sessão $case",durationMinutes=duration))}.isSuccess
                assertEquals(duration>=1 && duration<=720,actual)
            }
            1 -> {
                val effort=n%15-2
                val actual=runCatching{WorkspaceRules.validate(TrainingSession(title="Sessão $case",effort=effort))}.isSuccess
                assertEquals(effort>=0 && effort<=10,actual)
            }
            2 -> {
                val value=when(n%7){0->Double.NaN;1->Double.POSITIVE_INFINITY;2->-n.toDouble()-1;else->n.toDouble()/10}
                val target=if(n%9==0)0.0 else n.toDouble()+1
                val actual=runCatching{WorkspaceRules.validate(HabitEntry(title="Hábito $case",value=value,target=target))}.isSuccess
                assertEquals(value.isFinite() && value>=0 && target>0,actual)
            }
            3 -> {
                val value=(n%301).toDouble()
                val target=(n%173+1).toDouble()
                val progress=WorkspaceRules.habitProgress(value,target)!!
                assertTrue(progress in 0f..1f)
                if(value>=target)assertEquals(1f,progress,0f)
                else assertEquals(value/target,progress.toDouble(),0.000001)
            }
            4 -> {
                val year=2020+n/372
                val month=n/31%12+1
                val day=n%35+1
                val value="%04d-%02d-%02d".format(java.util.Locale.ROOT,year,month,day)
                assertEquals(day<=YearMonth.of(year,month).lengthOfMonth(),runCatching{WorkspaceRules.date(value)}.isSuccess)
            }
            5 -> {
                val name=if(n%2==0)"João" else "Márcia"
                val plain=if(n%2==0)"joao" else "marcia"
                assertTrue(WorkspaceRules.search("Avaliação $name registro $n","$plain avaliacao $n"))
                assertFalse(WorkspaceRules.search("Avaliação $name registro $n","$plain consulta"))
            }
            6 -> {
                val offset=n%101-50
                val done=n%3==0
                val today=LocalDate.of(2026,10,22)
                val task=TaskItem(title="Tarefa $case",dueDate=today.plusDays(offset.toLong()).toString(),done=done)
                assertEquals(!done && offset<0,WorkspaceRules.overdue(task,today))
            }
            7 -> {
                val old=Evaluation(id="old-$n",studentId="a-$n",name="Mesmo nome",date="2025-01-01")
                val recent=old.copy(id="recent-$n",date="2026-01-01")
                val other=recent.copy(id="other-$n",studentId="b-$n")
                val result=WorkspaceRules.latest(if(n%2==0)listOf(old,other,recent) else listOf(recent,other,old))
                assertEquals(2,result.size)
                assertEquals(recent,result.first{it.studentId==old.studentId})
                assertEquals(other,result.first{it.studentId==other.studentId})
            }
        }
    }
}
