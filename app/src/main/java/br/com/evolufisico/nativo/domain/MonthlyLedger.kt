package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Charge
import java.time.LocalDate
import java.time.YearMonth

/** Cash follows the actual payment date; unpaid entries follow their due date. */
object MonthlyLedger {
    data class Period(val month:YearMonth,val paid:List<Charge>,val pending:List<Charge>) {
        val receivedCents=paid.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
        val pendingCents=pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
    }
    fun date(row:Charge)=runCatching{LocalDate.parse(if(row.paid)row.paidDate.ifBlank{row.dueDate} else row.dueDate)}.getOrNull()
    fun summary(rows:List<Charge>,month:YearMonth):Period {
        val items=rows.filter{!it.exempt && date(it)?.let{d->YearMonth.from(d)==month}==true}.sortedByDescending{date(it)}
        return Period(month,items.filter{it.paid},items.filterNot{it.paid})
    }
    fun periods(rows:List<Charge>,current:YearMonth=YearMonth.now())=(rows.filterNot{it.exempt}.mapNotNull{date(it)?.let(YearMonth::from)}+current).distinct().sortedDescending()
}
