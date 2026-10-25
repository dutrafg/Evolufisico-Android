package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.evolufisico.nativo.data.Charge
import br.com.evolufisico.nativo.domain.MonthlyLedger
import br.com.evolufisico.nativo.domain.Financial
import java.time.YearMonth

@Composable internal fun MonthlyLedgerPanel(rows:List<Charge>,onPdf:(String)->Unit) {
    var selected by rememberSaveable{mutableStateOf(YearMonth.now().toString())}
    val month=runCatching{YearMonth.parse(selected)}.getOrNull()?.takeIf{it.year in 1..9999}
    Text("Caixa mensal",style=MaterialTheme.typography.titleLarge)
    Text("Recebimentos pela data do pagamento; pendências pelo vencimento. Meses anteriores permanecem no histórico.")
    Field("Período (AAAA-MM)",selected,{selected=it.take(7)})
    if(month==null)Text("Informe um mês válido, como 2026-10.",color=MaterialTheme.colorScheme.error)
    else {
        ActionButtons {
            TextButton(enabled=month.year>1,onClick={selected=month.minusMonths(1).toString()}){Text("Mês anterior")}
            TextButton(onClick={selected=YearMonth.now().toString()}){Text("Mês atual")}
            TextButton(enabled=month.year<9999,onClick={selected=month.plusMonths(1).toString()}){Text("Próximo mês")}
        }
        val report=MonthlyLedger.summary(rows,month)
        DataCard("Recebido no mês",money(report.receivedCents)+" · ${report.paid.size} recebimentos")
        DataCard("A receber no mês",money(report.pendingCents)+" · ${report.pending.size} pendências")
        ActionButtons {OutlinedButton(onClick={onPdf(selected)}){Text("PDF do mês")};OutlinedButton(onClick={onPdf("")}){Text("PDF de todos os meses")}}
        Text("Recebimentos",style=MaterialTheme.typography.titleMedium)
        if(report.paid.isEmpty())Text("Nenhum recebimento neste período.")
        PagedRecords(report.paid,"paid-$selected",{it.id}){c->DataCard(c.student,"${MonthlyLedger.date(c)} · ${c.service} · ${c.paymentMethod} · ${money(Financial.billable(c.amountCents,c.discountCents,c.exempt))}")}
        Text("Pendências",style=MaterialTheme.typography.titleMedium)
        if(report.pending.isEmpty())Text("Nenhuma pendência neste período.")
        PagedRecords(report.pending,"pending-$selected",{it.id}){c->DataCard(c.student,"${c.dueDate} · ${c.service} · ${money(Financial.billable(c.amountCents,c.discountCents,c.exempt))}")}
    }
    Text("Histórico por mês",style=MaterialTheme.typography.titleMedium)
    PagedRecords(MonthlyLedger.periods(rows),"months",{it.toString()}){m->val s=MonthlyLedger.summary(rows,m);DataCard(m.toString(),"Recebido ${money(s.receivedCents)} · a receber ${money(s.pendingCents)}"){TextButton(onClick={selected=m.toString()}){Text("Consultar mês")}}}
    if(rows.any{it.paid&&it.paidDate.isBlank()})Text("Registros antigos sem data de pagamento usam o vencimento como referência. Edite-os para informar a data efetiva.",style=MaterialTheme.typography.bodySmall)
}
