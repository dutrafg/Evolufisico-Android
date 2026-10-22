package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Charge
import br.com.evolufisico.nativo.domain.Financial
import br.com.evolufisico.nativo.domain.WorkspaceRules
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.Locale

@Composable
fun BillingScreen(repo: AppRepository, onPdf: () -> Unit) {
    var editing by remember { mutableStateOf<Charge?>(null) }
    var deleting by remember { mutableStateOf<Charge?>(null) }
    var search by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todos") }
    var view by remember { mutableStateOf("Extrato") }
    var actionError by remember{mutableStateOf("")}
    ActionError(actionError){actionError=""}
    deleting?.let{item->ConfirmDelete("cobrança",{deleting=null},{repo.delete(item)})}
    if (editing!=null) BillingEditor(editing!!, repo, onCancel={editing=null}, onSave={repo.save(it);editing=null})
    else Screen {
        Heading("Cobrança","Serviços, preços, descontos, parcelas, recebimentos e histórico por aluno.")
        val total=repo.charges.sumOf { Financial.billable(it.amountCents,it.discountCents,it.exempt) }
        val received=repo.charges.filter { it.paid && !it.exempt }.sumOf { Financial.billable(it.amountCents,it.discountCents,it.exempt) }
        val pending=total-received
        DataCard("Valor recebido",money(received))
        DataCard("A receber", money(pending))
        DataCard("Faturamento total",money(total))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Button(onClick={editing=Charge()}) {Text("+ Nova cobrança")}
            OutlinedButton(onClick=onPdf) {Text("PDF financeiro")}
        }
        Field("Buscar aluno ou serviço",search,{search=it})
        PillChoices(listOf("Todos","Pago","Pendente","Vencida","Cortesia"),filter,{filter=it})
        PillChoices(listOf("Extrato","Por aluno"),view,{view=it})
        val found=repo.charges.filter {
            WorkspaceRules.search(it.student+" "+it.secondStudent+" "+it.service,search) &&
                (filter=="Todos" || when(filter){
                    "Pago" -> it.paid && !it.exempt
                    "Pendente" -> !it.paid && !it.exempt
                    "Vencida" -> !it.paid && !it.exempt && it.dueDate<LocalDate.now().toString()
                    else -> it.exempt
                })
        }
        if (found.isEmpty()) Text("Nenhum lançamento encontrado.")
        if(view=="Por aluno") {
            found.groupBy { it.studentId.ifBlank{"name:"+it.student.trim().lowercase(Locale.ROOT)} }.forEach { (_, entries) ->
                val name=entries.first().student+entries.first().studentId.takeIf{it.isNotBlank()}?.let{" · "+it.take(6)}.orEmpty()
                DataCard(name,"${entries.size} lançamentos · ${money(entries.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})} · em aberto ${money(entries.filterNot {it.paid}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})}")
            }
        } else PagedRecords(found,search+filter+view,{it.id}) { item ->
            val net=Financial.billable(item.amountCents,item.discountCents,item.exempt)
            DataCard(item.student.ifEmpty { "Sem aluno" },
                "${item.service} • ${money(net)} • ${item.dueDate} • ${if(item.exempt)"Cortesia" else if(item.paid)"Pago" else "Pendente"}"+
                    if(item.secondStudent.isNotBlank()) " • Dupla: ${item.secondStudent}" else "") {
                if(!item.exempt) TextButton(onClick={runCatching{repo.save(item.copy(paid=!item.paid,
                    paidDate=if(!item.paid)LocalDate.now().toString() else ""))}.onFailure{actionError=it.message ?: "Falha ao atualizar pagamento."}}){
                    Text(if(item.paid)"Reabrir" else "Marcar pago")
                }
                TextButton(onClick={editing=item}){Text("Editar")}
                TextButton(onClick={deleting=item}){Text("Excluir")}
            }
            if (item.installments>1) {
                Text("${item.installments} parcelas: ${Financial.installments(net,item.installments).joinToString(" + "){money(it)}}",
                    style=MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BillingEditor(original:Charge,repo:AppRepository,onCancel:()->Unit,onSave:(Charge)->Unit) {
    TrackEditing()
    var discardRequested by remember(original.id) { mutableStateOf(false) }
    BackHandler { discardRequested = true }
    if (discardRequested) AlertDialog(
        onDismissRequest = { discardRequested = false },
        title = { Text("Descartar alterações?") },
        text = { Text("Os dados desta edição que não foram salvos serão perdidos.") },
        confirmButton = { TextButton(onClick = onCancel) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { discardRequested = false }) { Text("Continuar editando") } }
    )
    var student by remember(original.id) { mutableStateOf(original.student) }
    var second by remember(original.id) { mutableStateOf(original.secondStudent) }
    var studentId by remember(original.id) { mutableStateOf(original.studentId) }
    var secondId by remember(original.id) { mutableStateOf(original.secondStudentId) }
    var service by remember(original.id) { mutableStateOf(original.service) }
    var amount by remember(original.id) { mutableStateOf("%.2f".format(Locale.US,original.amountCents/100.0)) }
    var discount by remember(original.id) { mutableStateOf("%.2f".format(Locale.US,original.discountCents/100.0)) }
    var installments by remember(original.id) { mutableStateOf(original.installments.toString()) }
    var due by remember(original.id) { mutableStateOf(original.dueDate) }
    var paidDate by remember(original.id) { mutableStateOf(original.paidDate) }
    var paid by remember(original.id) { mutableStateOf(original.paid) }
    var exempt by remember(original.id) { mutableStateOf(original.exempt) }
    var method by remember(original.id) { mutableStateOf(original.paymentMethod) }
    var notes by remember(original.id) { mutableStateOf(original.notes) }
    var error by remember(original.id) { mutableStateOf("") }
    fun cents(text:String):Long? = runCatching { BigDecimal(text.trim().replace(',','.'))
        .multiply(BigDecimal(100)).setScale(0,RoundingMode.HALF_UP).longValueExact() }.getOrNull()
    Screen {
        Heading("Lançamento financeiro","O desconto é subtraído do preço, mantendo o valor original no histórico.")
        if(repo.evaluations.isNotEmpty()) {
            Text("Aluno cadastrado (opcional)")
            val pupils=repo.evaluations.distinctBy{it.studentId}
            val choices=studentChoices(pupils)
            PillChoices(choices.map{it.second},choices.firstOrNull{it.first==studentId}?.second.orEmpty(),{label ->
                val id=choices.firstOrNull{it.second==label}?.first.orEmpty()
                student=pupils.firstOrNull{it.studentId==id}?.name.orEmpty();studentId=id
            })
        }
        Field("Aluno *",student,{student=it;studentId=repo.evaluations.firstOrNull{e->e.name==it}?.studentId.orEmpty()})
        Field("Segundo aluno (se dupla)",second,{second=it;secondId=repo.evaluations.firstOrNull{e->e.name==it}?.studentId.orEmpty()})
        Text("Serviço",style=MaterialTheme.typography.titleSmall)
        PillChoices(Financial.services.keys.toList(),service,{
            service=it;amount="%.2f".format(Locale.US,(Financial.services[it]?:0)/100.0)
        })
        Field("Descrição do serviço",service,{service=it})
        Field("Valor inicial (R$)",amount,{amount=it})
        Field("Desconto (R$)",discount,{discount=it})
        val baseCents=cents(amount);val discountCents=cents(discount)
        val invalidPrice=baseCents==null||discountCents==null||baseCents<0||discountCents<0||discountCents>baseCents
        val total=Financial.billable(baseCents?:0L,discountCents?:0L,exempt)
        DataCard("Total a cobrar",if(invalidPrice) "Confira valor e desconto" else money(total))
        Field("Parcelas (1–24)",installments,{installments=it})
        if(!invalidPrice && installments.toIntOrNull() in 1..24) {
            Text("Divisão das parcelas: ${Financial.installments(total,installments.toInt()).joinToString(" + "){money(it)}}")
        }
        PillChoices(listOf("Pix","Crédito","Débito","Dinheiro","Transferência","Outro"),method,{method=it})
        DateField("Vencimento",due,{due=it})
        Text("Situação financeira")
        PillChoices(listOf("Pendente","Pago","Cortesia"),if(exempt)"Cortesia"else if(paid)"Pago"else"Pendente") { choice ->
            exempt = choice=="Cortesia"; paid=choice=="Pago"
        }
        if(paid) DateField("Data do pagamento",paidDate,{paidDate=it})
        if(exempt)Text("Cortesia: não será contabilizada como receita nem valor a receber.",style=MaterialTheme.typography.bodySmall)
        Field("Observações / comprovante",notes,{notes=it},lines=3)
        if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={discardRequested=true}){Text("Cancelar")}
            Button(onClick={
                val a=cents(amount);val d=cents(discount);val parts=installments.toIntOrNull()
                error=when {
                    student.isBlank()->"Informe o aluno."
                    service.isBlank()->"Informe o serviço."
                    a==null||a<0||d==null||d<0||d>a->"Valor ou desconto inválido."
                    parts==null||parts !in 1..24->"Quantidade de parcelas inválida."
                    runCatching{LocalDate.parse(due)}.isFailure->"Vencimento inválido."
                    paid && paidDate.isNotBlank() && runCatching{LocalDate.parse(paidDate)}.isFailure->"Data de pagamento inválida."
                    else->""
                }
                if(error.isEmpty())runCatching { onSave(original.copy(student=student.trim(),secondStudent=second.trim(),
                    service=service.trim(),amountCents=a!!,discountCents=d!!,installments=parts!!,
                    paymentMethod=method,dueDate=due,paid=paid && !exempt,exempt=exempt,
                    paidDate=if(paid)paidDate.ifBlank{LocalDate.now().toString()}else "",notes=notes,
                    studentId=studentId,secondStudentId=secondId)) }.onFailure { error=it.message ?: "Não foi possível salvar. Tente novamente." }
            }){Text("Salvar cobrança")}
        }
    }
}
