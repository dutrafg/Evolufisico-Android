package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

enum class OperationsTab { AGENDA, GOALS, SERVICES }

@Composable
fun OperationsScreen(clients: List<Client>, store: OperationalStore, tab: OperationsTab) {
    var data by remember(store) { mutableStateOf(OperationsData()) }
    var loading by remember(store) { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var fatal by remember(store) { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope= rememberCoroutineScope()

    LaunchedEffect(store) {
        val result=withContext(Dispatchers.IO) { runCatching { store.load() } }
        result.onSuccess { data=it }.onFailure {
            fatal="Não foi possível carregar registros locais: "+(it.localizedMessage?:"Erro desconhecido")
        }
        loading=false
    }
    fun persist(next: OperationsData) {
        if (loading||busy||fatal!=null) return
        busy=true
        scope.launch {
            val result=withContext(Dispatchers.IO) {
                runCatching { store.save(next,clients.map{it.id}.toSet()) }
            }
            result.onSuccess { data=next;message="Alterações salvas no dispositivo." }
                .onFailure { message="Falha ao salvar: "+(it.localizedMessage?:"Dados anteriores mantidos.") }
            busy=false
        }
    }
    if (loading) { OperationNotice("Carregando dados locais...");return }
    if (fatal!=null) { OperationNotice(fatal!!);return }
    when(tab) {
        OperationsTab.AGENDA -> AppointmentsModule(clients,data.appointments,data.services,busy,message) {
            persist(data.copy(appointments=it))
        }
        OperationsTab.GOALS -> GoalsModule(clients,data.goals,busy,message) {
            persist(data.copy(goals=it))
        }
        OperationsTab.SERVICES -> ServicesModule(data.services,busy,message) {
            persist(data.copy(services=it))
        }
    }
}

@Composable
fun OperationNotice(msg:String) {
    Column(Modifier.fillMaxSize().padding(22.dp),
        verticalArrangement=Arrangement.Center) { Text(msg) }
}

@Composable
private fun AppointmentsModule(
    clients:List<Client>, all:List<Appointment>, services:List<ServiceItem>,
    busy:Boolean,message:String?,
    save:(List<Appointment>)->Unit
) {
    var search by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("Todas") }
    var editing by remember { mutableStateOf<Appointment?>(null) }
    var editingOpen by remember { mutableStateOf(false) }
    var remove by remember { mutableStateOf<Appointment?>(null) }
    val name=clients.associate{it.id to it.name}
    val today=LocalDate.now().toString()
    val visible=all.filter { a ->
        val matchPeriod=when(period) {
            "Hoje" -> a.startIso.startsWith(today)
            "Futuras" -> a.startIso.substringBefore('T')>=today && a.status=="Agendado"
            "Concluídas" -> a.status=="Concluído"
            "Canceladas" -> a.status=="Cancelado"
            else -> true
        }
        matchPeriod && (search.isBlank() || a.service.contains(search,true) ||
            (name[a.clientId]?:"").contains(search,true) || a.startIso.contains(search))
    }.sortedBy { it.startIso }
    LazyColumn(contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp),modifier=Modifier.fillMaxSize()) {
        item {
            Text("Agenda de atendimentos",style=MaterialTheme.typography.titleLarge)
            Text("Agendamentos locais vinculados ao cliente.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Hoje: "+all.count{it.startIso.startsWith(today)&&it.status=="Agendado"}+
                " · Concluídos: "+all.count{it.status=="Concluído"})
        }
        item {
            Button(modifier=Modifier.fillMaxWidth(),enabled=!busy&&clients.isNotEmpty(),
                onClick={ editing=null;editingOpen=true }) { Text("Novo atendimento") }
            if(clients.isEmpty()) Text("Cadastre um cliente antes de agendar.")
            OutlinedTextField(search,{search=it.take(80)},modifier=Modifier.fillMaxWidth(),
                label={Text("Buscar cliente, serviço ou data")},singleLine=true)
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf("Todas","Hoje","Futuras","Concluídas","Canceladas").forEach { c ->
                    FilterChip(selected=period==c,onClick={period=c},label={Text(c)})
                }
            }
            message?.let { Text(it,color=MaterialTheme.colorScheme.primary) }
        }
        if(visible.isEmpty()) item { Text("Não há agendamentos nesta seleção.") }
        items(visible,key={it.id}) { a ->
            Card(shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Text(name[a.clientId]?:"Cliente indisponível",style=MaterialTheme.typography.titleMedium)
                    Text(a.startIso.replace('T',' ') + " · "+a.durationMinutes+" min")
                    Text(a.service)
                    Text("Situação: "+a.status,color=MaterialTheme.colorScheme.primary)
                    if(a.notes.isNotBlank()) Text(a.notes)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                        TextButton(enabled=!busy,onClick={editing=a;editingOpen=true}) {Text("Editar")}
                        if(a.status=="Agendado") {
                            TextButton(enabled=!busy,onClick={
                                save(all.map{if(it.id==a.id)it.copy(status="Concluído") else it})
                            }) {Text("Concluir")}
                            TextButton(enabled=!busy,onClick={
                                save(all.map{if(it.id==a.id)it.copy(status="Cancelado") else it})
                            }) {Text("Cancelar")}
                        }
                        TextButton(enabled=!busy,onClick={remove=a}) {Text("Excluir")}
                    }
                }
            }
        }
    }
    if(editingOpen) AppointmentEditor(clients,services,editing,all,
        onDismiss={editingOpen=false},onSave={
            save(if(editing==null) all+it else all.map{old->if(old.id==it.id)it else old})
            editingOpen=false
        })
    remove?.let { a ->
        AlertDialog(onDismissRequest={remove=null},title={Text("Excluir atendimento?")},
            text={Text("O atendimento será removido do histórico.")},
            confirmButton={Button(onClick={
                save(all.filterNot{it.id==a.id});remove=null
            }){Text("Excluir")}},
            dismissButton={TextButton(onClick={remove=null}){Text("Voltar")}})
    }
}

@Composable
private fun AppointmentEditor(
    clients:List<Client>,services:List<ServiceItem>,current:Appointment?,existing:List<Appointment>,
    onDismiss:()->Unit,onSave:(Appointment)->Unit
) {
    var clientId by remember(current?.id){mutableStateOf(current?.clientId?:clients.first().id)}
    var date by remember(current?.id){mutableStateOf(current?.startIso?.substringBefore('T')?:LocalDate.now().toString())}
    var time by remember(current?.id){mutableStateOf(current?.startIso?.substringAfter('T')?:"14:00")}
    var mins by remember(current?.id){mutableStateOf((current?.durationMinutes?:60).toString())}
    var service by remember(current?.id){mutableStateOf(current?.service?:"Avaliação física")}
    var note by remember(current?.id){mutableStateOf(current?.notes?:"")}
    var clientMenu by remember {mutableStateOf(false)}
    var issue by remember {mutableStateOf<String?>(null)}
    AlertDialog(onDismissRequest=onDismiss,
        title={Text(if(current==null)"Novo atendimento" else "Editar atendimento")},
        text={
            Column(Modifier.heightIn(max=460.dp).verticalScroll(rememberScrollState()),
                verticalArrangement=Arrangement.spacedBy(7.dp)) {
                Box { TextButton(onClick={clientMenu=true}){
                    Text("Cliente: "+(clients.firstOrNull{it.id==clientId}?.name?:"Selecione"))
                }
                    DropdownMenu(expanded=clientMenu,onDismissRequest={clientMenu=false}) {
                        clients.forEach{c-> DropdownMenuItem(text={Text(c.name)},onClick={
                            clientId=c.id;clientMenu=false
                        })}
                    }
                }
                OutlinedTextField(date,{date=it.take(10)},label={Text("Data AAAA-MM-DD")},singleLine=true)
                OutlinedTextField(time,{time=it.take(5)},label={Text("Hora HH:MM")},singleLine=true)
                OutlinedTextField(mins,{mins=it.take(3)},label={Text("Duração em minutos")},singleLine=true)
                Text("Escolha serviço do catálogo ou digite livremente:",
                    style=MaterialTheme.typography.labelSmall)
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                    services.filter{it.enabled}.take(10).forEach{item->
                        AssistChip(onClick={service=item.name;mins=item.durationMinutes.toString()},
                            label={Text(item.name)})
                    }
                }
                OutlinedTextField(service,{service=it.take(120)},label={Text("Serviço")},singleLine=true)
                OutlinedTextField(note,{note=it.take(500)},label={Text("Observações")},minLines=2)
                issue?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            }
        },
        confirmButton={Button(onClick={
            val new=Appointment(current?.id?:java.util.UUID.randomUUID().toString(),
                clientId,"${date}T${time}",mins.toIntOrNull()?:0,service,
                current?.status?:"Agendado",note)
            val error=OperationalRules.appointment(new,clients.map{it.id}.toSet())
            issue=error?:if(OperationalRules.conflict(new,existing))
                "Há outro atendimento nesse intervalo." else null
            if(issue==null)onSave(new)
        }){Text("Salvar")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
    )
}
