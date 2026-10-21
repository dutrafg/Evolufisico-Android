package br.com.evolufisico.android

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.*
import br.com.evolufisico.android.agenda.WeeklyAppointments
import br.com.evolufisico.android.agenda.AgendaIcs
import br.com.evolufisico.android.agenda.AgendaCsv
import br.com.evolufisico.android.agenda.AgendaPulse
import br.com.evolufisico.android.agenda.AgendaActions
import br.com.evolufisico.android.agenda.AgendaAvailability
import br.com.evolufisico.android.agenda.AgendaWindow
import br.com.evolufisico.android.agenda.AgendaFilters
import br.com.evolufisico.android.agenda.AppointmentShareText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

enum class OperationsTab { AGENDA, GOALS, SERVICES, NOTES }

@Composable
fun OperationsScreen(
    clients: List<Client>, store: OperationalStore, tab: OperationsTab,
    onDocumentFlow: (Boolean) -> Unit = {},
    quickCreateId:Int=0,onQuickConsumed:()->Unit={}
) {
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
        OperationsTab.AGENDA -> AppointmentsModule(clients,data.appointments,data.services,
            busy,message,onDocumentFlow,quickCreateId,onQuickConsumed) {
            persist(data.copy(appointments=it))
        }
        OperationsTab.GOALS -> GoalsModule(clients,data.goals,busy,message,
            quickCreateId,onQuickConsumed) {
            persist(data.copy(goals=it))
        }
        OperationsTab.SERVICES -> ServicesModule(data.services,busy,message,
            quickCreateId,onQuickConsumed) {
            persist(data.copy(services=it))
        }
        OperationsTab.NOTES -> ClientNotesModule(clients,data.notes,busy,message,
            quickCreateId,onQuickConsumed) {
            persist(data.copy(notes=it))
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
    busy:Boolean,message:String?,onDocumentFlow:(Boolean)->Unit,
    quickCreateId:Int=0,onQuickConsumed:()->Unit={},
    save:(List<Appointment>)->Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf<String?>(null) }
    var clientSelector by remember { mutableStateOf(false) }
    var period by remember { mutableStateOf("Todas") }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var editing by remember { mutableStateOf<Appointment?>(null) }
    var editingOpen by remember { mutableStateOf(false) }
    var remove by remember { mutableStateOf<Appointment?>(null) }
    var duplicate by remember { mutableStateOf<Appointment?>(null) }
    var transition by remember { mutableStateOf<Pair<Appointment,String>?>(null) }
    LaunchedEffect(quickCreateId,busy,clients.size) {
        if(quickCreateId>0 && !busy) {
            if(clients.isNotEmpty()) { editing=null;editingOpen=true }
            onQuickConsumed()
        }
    }
    val name=clients.associate{it.id to it.name}
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var exportResult by remember { mutableStateOf<String?>(null) }
    var pendingCsv by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var pendingIcs by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var pendingIcsNames by remember { mutableStateOf<Map<String,String>>(emptyMap()) }
    var pendingCsvNames by remember { mutableStateOf<Map<String,String>>(emptyMap()) }
    val todayDate=LocalDate.now()
    val today=todayDate.toString()
    val pulse=AgendaPulse.summarize(all,todayDate)
    val visible=AgendaFilters.filter(all,name,period,selectedDay,todayDate,search,selectedClientId)
    val icsLauncher=rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/calendar")
    ) { uri:Uri? ->
        onDocumentFlow(false)
        if(uri!=null) {
            scope.launch {
                val result=withContext(Dispatchers.IO) {
                    runCatching {
                        val bytes=AgendaIcs.generate(pendingIcs,pendingIcsNames)
                        context.contentResolver.openOutputStream(uri,"w")?.use {
                            it.write(bytes);it.flush()
                        } ?: error("Não foi possível escrever arquivo ICS.")
                        bytes.size
                    }
                }
                exportResult=result.fold(
                    onSuccess={ "Agenda exportada em ICS ("+it+" bytes)." },
                    onFailure={ "Falha ao exportar ICS: "+(it.localizedMessage?:"Erro.") }
                )
            }
        }
    }
    val csvLauncher=rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri:Uri? ->
        onDocumentFlow(false)
        if(uri!=null) {
            val snapshot=pendingCsv
            scope.launch {
                val result=withContext(Dispatchers.IO) {
                    runCatching {
                        val bytes=AgendaCsv.generate(snapshot,pendingCsvNames)
                        context.contentResolver.openOutputStream(uri,"w")?.use {
                            it.write(bytes);it.flush()
                        } ?: error("Não foi possível escrever o CSV.")
                        snapshot.size
                    }
                }
                exportResult=result.fold(
                    onSuccess={ "CSV salvo com $it atendimento(s)." },
                    onFailure={ "Falha ao exportar CSV: "+(it.localizedMessage?:"Erro.") }
                )
            }
        }
    }
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    LazyColumn(contentPadding=PaddingValues(horizontal=VisualLayout.edgePaddingDp(
        androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp).dp,vertical=20.dp),
        verticalArrangement=Arrangement.spacedBy(18.dp),
        modifier=Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp)) {
        item {
            Text("Agenda de atendimentos",style=MaterialTheme.typography.titleLarge)
            Text("Organize o seu dia e acompanhe os próximos atendimentos.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Card(shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
                colors=CardDefaults.cardColors(
                    containerColor=VisualTokens.panel(VisualTokens.dark(MaterialTheme.colorScheme)))) {
                Column(Modifier.fillMaxWidth().padding(15.dp),
                    verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text("Hoje: "+pulse.scheduledToday+
                        "  ·  Próximos 7 dias: "+pulse.upcomingSevenDays,
                        style=MaterialTheme.typography.titleSmall)
                    Text("Pendentes: "+pulse.overdue+
                        "  ·  Concluídos no mês: "+pulse.completedMonth+
                        "  ·  Faltas: "+pulse.missedMonth,
                        style=MaterialTheme.typography.bodySmall,
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            AgendaCalendarPanel(all,selectedDay,onSelect={ day ->
                selectedDay=day
                period=if(day==null) "Todas" else "Dia"
            })
        }
        item {
            Button(modifier=Modifier.fillMaxWidth(),enabled=!busy&&clients.isNotEmpty(),
                onClick={ editing=null;editingOpen=true }) { Text("Novo atendimento") }
            if(clients.isEmpty()) Text("Cadastre um cliente antes de agendar.")
            OutlinedTextField(search,{search=it.take(80)},modifier=Modifier.fillMaxWidth(),
                label={Text("Buscar cliente, serviço, observação ou data")},singleLine=true)
            Box {
                OutlinedButton(onClick={clientSelector=true}) {
                    Text("Cliente: "+(selectedClientId?.let{name[it]}?:"Todos"))
                }
                DropdownMenu(expanded=clientSelector,onDismissRequest={clientSelector=false}) {
                    DropdownMenuItem(text={Text("Todos os clientes")},onClick={
                        selectedClientId=null;clientSelector=false
                    })
                    clients.sortedBy{it.name}.forEach{c->
                        DropdownMenuItem(text={Text(c.name)},onClick={
                            selectedClientId=c.id;clientSelector=false
                        })
                    }
                }
            }
            Text("Resultados: "+visible.size+" atendimento(s)",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                listOf("Todas","Hoje","Dia","Semana","Pendentes","Futuras",
                    "Concluídas","Canceladas","Faltas").forEach { c ->
                    FilterChip(selected=period==c,onClick={period=c},
                        enabled=c!="Dia" || selectedDay!=null,label={Text(c)})
                }
            }
            Text("Semana: de hoje até os próximos seis dias. Pendentes: atendimentos "+
                "ainda agendados com data anterior a hoje.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(
                enabled=!busy&&visible.any{it.status=="Agendado"},
                modifier=Modifier.fillMaxWidth(),
                onClick={
                    pendingIcs=visible.filter { it.status=="Agendado" }.toList()
                    pendingIcsNames=name.toMap()
                    onDocumentFlow(true)
                    icsLauncher.launch("EvoluFisico_Agenda_"+today+".ics")
                }
            ) { Text("Exportar atendimentos filtrados (.ics)") }
            OutlinedButton(
                enabled=!busy&&visible.isNotEmpty(),
                modifier=Modifier.fillMaxWidth(),
                onClick={
                    pendingCsv=visible.toList()
                    pendingCsvNames=name.toMap()
                    onDocumentFlow(true)
                    csvLauncher.launch("EvoluFisico_Atendimentos_"+today+".csv")
                }
            ) { Text("Exportar lista filtrada (.csv)") }
            Text("O CSV inclui todos os estados visíveis, inclusive faltas e cancelamentos.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Exporta somente atendimentos agendados para calendários externos.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            exportResult?.let { Text(it,color=MaterialTheme.colorScheme.primary) }
            message?.let { Text(it,color=MaterialTheme.colorScheme.primary) }
        }
        if(visible.isEmpty()) item { Text("Não há agendamentos nesta seleção.") }
        items(visible,key={it.id}) { a ->
            Card(shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
                modifier=Modifier.fillMaxWidth(),
                elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
                Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    Text(name[a.clientId]?:"Cliente indisponível",style=MaterialTheme.typography.titleMedium)
                    Text(a.startIso.replace('T',' ') + " · "+a.durationMinutes+" min")
                    Text(a.service)
                    Text("Situação: "+a.status,color=MaterialTheme.colorScheme.primary)
                    if(a.notes.isNotBlank()) Text(a.notes)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                        TextButton(enabled=!busy,onClick={editing=a;editingOpen=true}) {Text("Editar")}
                        TextButton(enabled=!busy,onClick={duplicate=a}) {Text("Duplicar +7d")}
                        TextButton(onClick={
                            val text=AppointmentShareText.format(
                                a,name[a.clientId]?:"Cliente")
                            val share=Intent(Intent.ACTION_SEND).apply {
                                type="text/plain"
                                putExtra(Intent.EXTRA_TEXT,text)
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(
                                    share,"Compartilhar atendimento"))
                            }.onFailure { exportResult="Não foi possível abrir o compartilhamento." }
                        }) { Text("Compartilhar") }
                        if(a.status=="Agendado") {
                            TextButton(enabled=!busy,onClick={
                                transition=a to "Concluído"
                            }) {Text("Concluir")}
                            TextButton(enabled=!busy,onClick={
                                transition=a to "Cancelado"
                            }) {Text("Cancelar")}
                            TextButton(enabled=!busy,onClick={
                                transition=a to "Faltou"
                            }) {Text("Registrar falta")}
                        } else {
                            TextButton(enabled=!busy,onClick={
                                transition=a to "Agendado"
                            }) {Text("Reabrir")}
                        }
                        TextButton(enabled=!busy,onClick={remove=a}) {Text("Excluir")}
                    }
                }
            }
        }
    }
    }
    duplicate?.let { a ->
        AlertDialog(onDismissRequest={duplicate=null},
            title={Text("Duplicar atendimento?")},
            text={Text("Criar um novo atendimento sete dias depois, com os mesmos "+
                "cliente, serviço e duração? O original será preservado.")},
            confirmButton={Button(enabled=!busy,onClick={
                val result=runCatching {
                    AgendaActions.duplicateNextWeek(a,all,clients.map{it.id}.toSet())
                }
                result.onSuccess { save(all+it) }
                    .onFailure { exportResult=it.localizedMessage?:"Não foi possível duplicar." }
                duplicate=null
            }) {Text("Duplicar")}},
            dismissButton={TextButton(onClick={duplicate=null}) {Text("Voltar")}})
    }
    transition?.let { (a,nextStatus) ->
        AlertDialog(onDismissRequest={transition=null},
            title={Text("Confirmar alteração?")},
            text={Text("Alterar o atendimento de "+
                (name[a.clientId]?:"Cliente")+" para "+nextStatus+"?")},
            confirmButton={Button(enabled=!busy,onClick={
                val result=runCatching {
                    AgendaActions.changeStatus(a,nextStatus,all)
                }
                result.onSuccess { changed ->
                    save(all.map { if(it.id==a.id) changed else it })
                }.onFailure {
                    exportResult=it.localizedMessage?:"Não foi possível alterar a situação."
                }
                transition=null
            }) {Text("Confirmar")}},
            dismissButton={TextButton(onClick={transition=null}) {Text("Voltar")}})
    }
    if(editingOpen && clients.isNotEmpty()) AppointmentEditor(clients,services,editing,all,selectedDay,
        onDismiss={editingOpen=false},onSave={ generated ->
            save(if(editing==null) all+generated else
                all.map { old -> if(old.id==generated.first().id)generated.first() else old })
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
    preferredDay:LocalDate?,
    onDismiss:()->Unit,onSave:(List<Appointment>)->Unit
) {
    var clientId by remember(current?.id){mutableStateOf(current?.clientId?:clients.first().id)}
    var date by remember(current?.id){mutableStateOf(current?.startIso?.substringBefore('T')?:
        preferredDay?.toString()?:LocalDate.now().toString())}
    var time by remember(current?.id){mutableStateOf(current?.startIso?.substringAfter('T')?:"14:00")}
    var mins by remember(current?.id){mutableStateOf((current?.durationMinutes?:60).toString())}
    var service by remember(current?.id){mutableStateOf(current?.service?:"Avaliação física")}
    var note by remember(current?.id){mutableStateOf(current?.notes?:"")}
    var repeats by remember(current?.id) {mutableStateOf("1")}
    var clientMenu by remember {mutableStateOf(false)}
    var issue by remember {mutableStateOf<String?>(null)}
    var showSuggestions by remember(current?.id) { mutableStateOf(false) }
    var suggestionWindow by remember(current?.id) { mutableStateOf(AgendaWindow()) }
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
                val previewStart=runCatching {
                    LocalDateTime.parse("${date}T${time}")
                }.getOrNull()
                val previewDuration=mins.toIntOrNull()
                val occupied=previewStart!=null && previewDuration!=null &&
                    previewDuration in 15..480 && current?.status!="Cancelado" &&
                    OperationalRules.conflict(
                        Appointment(current?.id?:"agenda-preview",clientId,
                            previewStart.toString(),previewDuration,service),
                        existing)
                if(occupied) {
                    Text("O horário selecionado está ocupado.",
                        color=MaterialTheme.colorScheme.error,
                        style=MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick={showSuggestions=!showSuggestions}) {
                    Text(if(showSuggestions) "Ocultar horários livres" else "Buscar horários livres")
                }
                if(showSuggestions || occupied) {
                    Text("Horários sugeridos (sem modificar os atendimentos):",
                        style=MaterialTheme.typography.labelMedium)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                        listOf(AgendaWindow(7,22),AgendaWindow(8,18),
                            AgendaWindow(9,21)).forEach { option ->
                            FilterChip(selected=suggestionWindow.startHour==option.startHour,
                                onClick={suggestionWindow=option.copy(
                                    weekdaysOnly=suggestionWindow.weekdaysOnly)},
                                label={Text("${option.startHour}h–${option.endHour}h")})
                        }
                    }
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                        Checkbox(checked=suggestionWindow.weekdaysOnly,
                            onCheckedChange={
                                suggestionWindow=suggestionWindow.copy(weekdaysOnly=it)
                            })
                        Text("Somente segunda a sexta",
                            modifier=Modifier.weight(1f),
                            style=MaterialTheme.typography.bodySmall)
                    }
                    if(previewStart!=null && previewDuration!=null &&
                        previewDuration in 15..480) {
                        val suggestions=AgendaAvailability.suggest(
                            previewStart,previewDuration,existing,current?.id,
                            window=suggestionWindow)
                        if(suggestions.isEmpty()) {
                            Text("Sem horários livres nos próximos sete dias nesta janela.",
                                style=MaterialTheme.typography.bodySmall)
                        } else {
                            Row(Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                                suggestions.forEach { slot ->
                                    AssistChip(onClick={
                                        date=slot.toLocalDate().toString()
                                        time=slot.toLocalTime().toString()
                                        issue=null
                                        showSuggestions=false
                                    },label={Text(slot.toLocalDate().dayOfMonth.toString()+
                                        "/"+slot.toLocalDate().monthValue+
                                        " "+slot.toLocalTime().toString())})
                                }
                            }
                        }
                    } else {
                        Text("Informe uma data, hora e duração válidas para pesquisar.",
                            style=MaterialTheme.typography.bodySmall)
                    }
                }
                if(current==null) {
                    OutlinedTextField(repeats,{repeats=it.take(2)},
                        label={Text("Repetir semanalmente (1 a 12 atendimentos)")},
                        singleLine=true)
                    Text("A série inteira será recusada se algum horário estiver ocupado.",
                        style=MaterialTheme.typography.bodySmall)
                }
                issue?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            }
        },
        confirmButton={Button(onClick={
            val new=Appointment(current?.id?:java.util.UUID.randomUUID().toString(),
                clientId,"${date}T${time}",mins.toIntOrNull()?:0,service,
                current?.status?:"Agendado",note)
            val error=OperationalRules.appointment(new,clients.map{it.id}.toSet())
            issue=error
            if(issue==null) {
                val quantity=if(current==null)repeats.toIntOrNull() else 1
                if(quantity==null) issue="Informe de 1 a 12 atendimentos."
                else {
                    val result=runCatching {
                        if(current!=null) {
                            require(!OperationalRules.conflict(new,existing)) {
                                "Há outro atendimento nesse intervalo."
                            }
                            listOf(new)
                        } else WeeklyAppointments.plan(new,existing,quantity)
                    }
                    result.onSuccess { onSave(it) }
                        .onFailure { issue=it.localizedMessage ?: "Série inválida." }
                }
            }
        }){Text("Salvar")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
    )
}
