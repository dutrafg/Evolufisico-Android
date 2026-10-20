package br.com.evolufisico.android.billing

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class BillingFiltersTest {
    private val today=LocalDate.parse("2026-10-20")
    private fun c(id:String,due:String,received:String?=null,client:String="1")=
        Charge(id,client,"Avaliação física",2500,due,received)
    private val names=mapOf("1" to "João","2" to "Maria")
    private val charges=listOf(
        c("late","2026-10-19"),
        c("today","2026-10-20"),
        c("last","2026-10-26"),
        c("out","2026-10-27"),
        c("paid","2026-10-21","2026-10-20"),
        c("other","2026-10-21",client="2")
    )

    @Test fun sevenDayBoundaryIsInclusiveOfTodayAndExcludesDayEight() {
        assertEquals(listOf("today","other","last"),
            BillingFilters.filter(charges,names,"Próximos 7 dias","",today)
                .map{it.id})
    }
    @Test fun overdueDoesNotIncludeReceivedOrTodays() {
        assertEquals(listOf("late"),
            BillingFilters.filter(charges,names,"Atrasadas","",today).map{it.id})
        assertEquals(listOf("paid"),
            BillingFilters.filter(charges,names,"Recebidas","",today).map{it.id})
    }
    @Test fun searchIgnoresAccentsAndCanSelectClientId() {
        assertEquals(5,BillingFilters.filter(charges,names,"Todas","AVALIACAO",today,"1").size)
        assertEquals(listOf("other"),
            BillingFilters.filter(charges,names,"Todas","maria",today,"2").map{it.id})
    }
    @Test fun clientIdPreventsMixingDuplicateNames() {
        val names=mapOf("1" to "Maria","2" to "Maria")
        val found=BillingFilters.filter(charges,names,"Todas","Maria",today,"2")
        assertEquals(listOf("other"),found.map{it.id})
    }
    @Test fun malformedDateIsIgnoredInsteadOfCrashing() {
        assertTrue(BillingFilters.filter(listOf(c("invalid","2026-80-22")),
            names,"Todas","",today).isEmpty())
    }
}
