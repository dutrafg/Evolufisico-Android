package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Prepared QA cases, not a claim of executed tests or development stages. */
@RunWith(Parameterized::class)
class Beta10MatrixTest(private val case:Int) {
    companion object {
        @JvmStatic @Parameterized.Parameters(name="beta10-{0}")
        fun cases():Collection<Array<Any>> = (0 until 10000).map{arrayOf<Any>(it)}
    }
    @Test fun dateLayoutAndNavigationBoundaries(){
        val n=case/4
        when(case%4){
            0->{val today=LocalDate.of(2024,1,1).plusDays(n.toLong());val days=listOf(7,30,90)[n%3]
                assertTrue(DashboardInsights.inPeriod(today.minusDays(days-1L).toString(),today,days))
                assertFalse(DashboardInsights.inPeriod(today.minusDays(days.toLong()).toString(),today,days))
                assertFalse(DashboardInsights.inPeriod(today.plusDays(1).toString(),today,days))}
            1->{val width=240.0+n;val scale=1.0+(n%15)/10.0
                val normal=LayoutPolicy.cardColumns(width,scale)
                assertTrue(normal in 1..4)
                assertTrue(LayoutPolicy.cardColumns(width,scale+0.5)<=normal)
                if(normal>1)assertTrue(normal*250.0*scale+(normal-1)*12.0<=width)}
            2->{val chosen=ModuleOrder.defaults[n%16]
                val moved=ModuleOrder.move(ModuleOrder.defaults,chosen,if(n%2==0)-1 else 1)
                assertEquals((0..15).toSet(),moved.toSet());assertEquals(16,moved.size)
                assertEquals(moved,ModuleOrder.parse(moved.joinToString(",")))}
            3->{val today=LocalDate.of(2026,1,1).plusDays(n.toLong())
                val old=Evaluation(id="old",studentId="c$n",date=today.minusDays(2).toString(),nextAssessment=today.minusDays(1).toString())
                val fresh=old.copy(id="new",date=today.toString(),nextAssessment=today.plusDays(1).toString())
                assertTrue(DashboardInsights.dueReassessments(listOf(fresh,old),today).isEmpty())}
        }
    }
}
