package br.com.evolufisico.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import java.time.LocalDate
import java.util.Locale

@Composable
fun GoalsModule(clients:List<Client>,all:List<ClientGoal>,busy:Boolean,message:String?,
    quickCreateId:Int=0,onQuickConsumed:()->Unit={},
    save:(List<ClientGoal>)->Unit) {
    var selected by remember { mutableStateOf(clients.firstOrNull()?.id.orEmpty()) }
    var choose by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Todas") }
    var editing by remember { mutableStateOf<ClientGoal?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var removing by remember { mutableStateOf<ClientGoal?>(null) }
    LaunchedEffect(quickCreateId,busy,clients.size) {
        if(quickCreateId>0 && !busy) {
            if(clients.isNotEmpty()) { editing=null;showEditor=true }
            onQuickConsumed()
        }
    }
    LaunchedEffect(clients) {
        if(clients.isNotEmpty() && clients.none{it.id==selected}) selected=clients.first().id
    }
    val visible=all.filter {
        it.clientId==selected && (status=="Todas" ||
            if(status=="Concluídas") it.completed else !it.completed)
    }.sortedBy{it.deadlineIso}
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Metas por cliente",style=MaterialTheme.typography.titleLarge)
            Text("Progresso das metas definidas no atendimento.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Box {
                OutlinedButton(onClick={choose=true},modifier=Modifier.fillMaxWidth()){
                    Text("Cliente: "+(clients.firstOrNull{it.id==selected}?.name?:"Selecione"))
                }
                DropdownMenu(expanded=choose,onDismissRequest={choose=false}) {
                    clients.forEach{c->DropdownMenuItem(text={Text(c.name)},onClick={
                        selected=c.id;choose=false
                    })}
                }
            }
            Button(enabled=!busy&&clients.isNotEmpty(),modifier=Modifier.fillMaxWidth(),
                onClick={editing=null;showEditor=true}){Text("Nova meta")}
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                listOf("Todas","Ativas","Concluídas").forEach { option ->
                    FilterChip(selected=status==option,onClick={status=option},label={Text(option)})
                }
            }
            message?.let{Text(it,color=MaterialTheme.colorScheme.primary)}
        }
        if(visible.isEmpty())item{Text("Nenhuma meta nesta seleção.")}
        items(visible,key={it.id}){g->
            Card(shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text(g.title,style=MaterialTheme.typography.titleMedium)
                    Text("Prazo: "+g.deadlineIso + if(g.completed)" · Concluída" else "")
                    Text("Inicial: "+g.starting.toString()+" "+g.unit+" · Atual: "+
                        g.current.toString()+" "+g.unit+" · Alvo: "+g.target.toString()+" "+g.unit)
                    LinearProgressIndicator(
                        progress={OperationalRules.progress(g)/100f},
                        modifier=Modifier.fillMaxWidth())
                    Text(OperationalRules.progress(g).toString()+"% do progresso",
                        color=MaterialTheme.colorScheme.primary)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                        TextButton(enabled=!busy,onClick={editing=g;showEditor=true}){Text("Atualizar")}
                        TextButton(enabled=!busy,onClick={
                            save(all.map{if(it.id==g.id)it.copy(completed=!it.completed) else it})
                        }){Text(if(g.completed)"Reabrir" else "Concluir")}
                        TextButton(enabled=!busy,onClick={removing=g}){Text("Excluir")}
                    }
                }
            }
        }
    }
    if(showEditor && clients.isNotEmpty()) GoalEditor(selected,clients.map{it.id}.toSet(),editing,
        onDismiss={showEditor=false},onSave={g->
            save(if(editing==null)all+g else all.map{if(it.id==g.id)g else it})
            showEditor=false
        })
    removing?.let{g->AlertDialog(onDismissRequest={removing=null},
        title={Text("Excluir meta?")},text={Text("O histórico desta meta será removido.")},
        confirmButton={Button(onClick={save(all.filterNot{it.id==g.id});removing=null}){Text("Excluir")}},
        dismissButton={TextButton(onClick={removing=null}){Text("Cancelar")}})}
}

@Composable
private fun GoalEditor(clientId:String,ids:Set<String>,current:ClientGoal?,
    onDismiss:()->Unit,onSave:(ClientGoal)->Unit) {
    var title by remember(current?.id){mutableStateOf(current?.title?:"Meta de condicionamento")}
    var unit by remember(current?.id){mutableStateOf(current?.unit?:"kg")}
    var start by remember(current?.id){mutableStateOf(current?.starting?.toString()?:"90")}
    var target by remember(current?.id){mutableStateOf(current?.target?.toString()?:"80")}
    var actual by remember(current?.id){mutableStateOf(current?.current?.toString()?:"90")}
    var deadline by remember(current?.id){mutableStateOf(current?.deadlineIso?:LocalDate.now().plusMonths(3).toString())}
    var error by remember {mutableStateOf<String?>(null)}
    fun number(v:String):Double?=v.replace(',','.').toDoubleOrNull()
    AlertDialog(onDismissRequest=onDismiss,title={Text(if(current==null)"Nova meta" else "Atualizar meta")},
        text={Column(Modifier.heightIn(max=450.dp).verticalScroll(rememberScrollState()),
            verticalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(title,{title=it.take(100)},label={Text("Objetivo")},singleLine=true)
            OutlinedTextField(unit,{unit=it.take(16)},label={Text("Unidade (kg, cm, rep)")},singleLine=true)
            OutlinedTextField(start,{start=it.take(18)},label={Text("Valor inicial")},singleLine=true)
            OutlinedTextField(target,{target=it.take(18)},label={Text("Alvo")},singleLine=true)
            OutlinedTextField(actual,{actual=it.take(18)},label={Text("Valor atual")},singleLine=true)
            OutlinedTextField(deadline,{deadline=it.take(10)},label={Text("Prazo AAAA-MM-DD")},singleLine=true)
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }},
        confirmButton={Button(onClick={
            val g=ClientGoal(current?.id?:java.util.UUID.randomUUID().toString(),
                current?.clientId?:clientId,title.trim(),unit.trim(),number(start)?:Double.NaN,
                number(target)?:Double.NaN,number(actual)?:Double.NaN,deadline,
                current?.completed?:false)
            error=OperationalRules.goal(g,ids)
            if(error==null)onSave(g)
        }){Text("Salvar")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
    )
}
