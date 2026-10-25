package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@Composable fun OrganizationScreen(repo:AppRepository,onBackup:(String)->Unit={},onRestore:(String)->Unit={},onImportJson:()->Unit={},initialView:String="",initialSearch:String="",initialDate:String="",onEvaluations:(String)->Unit={}) {
    var view by rememberSaveable(initialView){mutableStateOf(when(initialView){"notes"->"Notas";"tasks"->"Tarefas";"goals"->"Metas";else->"Agenda e calendário"})}
    var calendarDate by rememberSaveable(initialDate){mutableStateOf(initialDate)}
    var query by rememberSaveable(initialSearch){mutableStateOf(initialSearch)}
    var filter by rememberSaveable(view){mutableStateOf("Todas")}
    var editing by remember{mutableStateOf<Pair<String,Any>?>(null)}
    var deleting by remember{mutableStateOf<Pair<String,Any>?>(null)}
    var error by remember{mutableStateOf("")}
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var exportingJson by remember{mutableStateOf(false)}
    var notice by remember{mutableStateOf("")}
    val jsonPicker=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->
        if(uri!=null){
            val notes=repo.notes.toList();val goals=repo.organizationGoals.toList();val tasks=repo.tasks.toList();val appointments=repo.appointments.toList()
            exportingJson=true
            scope.launch{try{withContext(Dispatchers.IO){val bytes=OrganizationJsonImport.export(notes,goals,tasks,appointments);try{requireNotNull(context.contentResolver.openOutputStream(uri,"wt")){"Não foi possível abrir o arquivo."}.use{it.write(bytes)}}finally{bytes.fill(0)}};notice="JSON exportado. O formato web não possui criptografia e identifica vínculos pelo nome; preserve o backup nativo para restauração completa."}catch(e:Exception){error=e.message ?: "Falha ao exportar JSON."}finally{exportingJson=false}}
        }
    }
    if(notice.isNotBlank())AlertDialog(onDismissRequest={notice=""},title={Text("Exportação JSON")},text={Text(notice)},confirmButton={TextButton(onClick={notice=""}){Text("Entendi")}})
    ActionError(error){error=""}
    deleting?.let{(kind,item)->ConfirmDelete("registro",{deleting=null},{when(item){is TaskItem->repo.delete(item);is NotebookEntry->repo.delete(item);is OrganizationGoal->repo.delete(item)}})}
    editing?.let{(kind,item)->key(kind,item){OrganizationEditor(repo,kind,item,{editing=null})}}
    var transfer by remember{mutableStateOf("")}
    var password by remember{mutableStateOf("")}
    if(transfer.isNotBlank())AlertDialog(onDismissRequest={transfer="";password=""},title={Text(if(transfer=="export")"Exportar organização" else "Importar organização")},
        text={Column {Text("Backup nativo criptografado. Importação aditiva: conflitos cancelam a operação sem sobrescrever. Guarde a senha.");OutlinedTextField(password,{password=it},label={Text("Senha (mínimo 8 caracteres)")},visualTransformation=androidx.compose.ui.text.input.PasswordVisualTransformation())}},
        confirmButton={TextButton(enabled=password.length>=8,onClick={val value=password;val exporting=transfer=="export";password="";transfer="";if(exporting)onBackup(value) else onRestore(value)}){Text("Continuar")}},dismissButton={TextButton(onClick={transfer="";password=""}){Text("Cancelar")}})
    var workspaceOpen by rememberSaveable{mutableStateOf(false)}
    var workspaceEditing by remember{mutableStateOf(false)}
    val parentEditor=LocalEditorState.current
    if(workspaceOpen)androidx.compose.ui.window.Dialog(onDismissRequest={if(!workspaceEditing)workspaceOpen=false},properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
        Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)){Column {
            TextButton(enabled=!workspaceEditing,onClick={workspaceOpen=false}){Text("Voltar à Organização")}
            Box(Modifier.weight(1f)){CompositionLocalProvider(LocalEditorState provides {active->workspaceEditing=active;parentEditor(active)}) {
                OrganizationWorkspaceScreen(repo,onClient={workspaceOpen=false;onEvaluations(it)})
            }}
        }}
    }
    val today=LocalDate.now().toString()
    Column(Modifier.fillMaxSize().padding(horizontal=12.dp)) {
        Heading("Organização","Agenda, notas, tarefas e metas no mesmo espaço.")
        ActionButtons {TextButton(onClick={transfer="export"}){Text("Exportar organização")};TextButton(onClick={transfer="import"}){Text("Importar backup nativo")};TextButton(onClick=onImportJson){Text("Importar JSON do HTML")};TextButton(enabled=!exportingJson,onClick={jsonPicker.launch("evolufisico2-organizacao.json")}){Text(if(exportingJson)"Exportando…" else "Exportar JSON do HTML")}}
        ReferenceSectionTabs(listOf("Agenda e calendário","Notas","Tarefas","Metas"),view,{view=it;query=""})
        if(view=="Agenda e calendário")TextButton(onClick={workspaceOpen=true}){Text("Planejar agenda e retornos")}
        if(view=="Agenda e calendário")Box(Modifier.weight(1f)){CalendarScreen(repo,initialDate=calendarDate,onOpenRecord={kind,id->editing=when(kind){"note"->repo.notes.firstOrNull{it.id==id}?.let{"note" to it};"task"->repo.tasks.firstOrNull{it.id==id}?.let{"task" to it};else->repo.organizationGoals.firstOrNull{it.id==id}?.let{"goal" to it}}})}
        else Box(Modifier.weight(1f)){Screen {
            Button(onClick={editing=when(view){"Notas"->"note" to NotebookEntry();"Tarefas"->"task" to TaskItem();else->"goal" to OrganizationGoal()}}){Text(when(view){"Notas"->"Nova nota";"Tarefas"->"Nova tarefa";else->"Nova meta"})}
            Field("Buscar título, conteúdo ou aluno",query,{query=it})
            when(view) {
                "Notas" -> {
                    PillChoices(listOf("Todas","Ativas","Fixadas","Vinculadas","Arquivadas"),filter,{filter=it})
                    val items=repo.notes.filter{WorkspaceRules.search(it.title+" "+it.body+" "+it.student,query) && when(filter){"Ativas"->!it.archived;"Fixadas"->it.pinned&&!it.archived;"Vinculadas"->it.studentId.isNotBlank()&&!it.archived;"Arquivadas"->it.archived;else->true}}
                    OrganizationCards(items,{it.id},"notes"){n->DataCard(n.title,"${n.student}\n${n.body}\n${if(n.archived)"Arquivada" else if(n.pinned)"Fixada" else "Ativa"}${n.reminder.takeIf{it.isNotBlank()}?.let{" · Lembrete: $it"}.orEmpty()}") {
                        TextButton(onClick={editing="note" to n}){Text("Editar")}
                        TextButton(onClick={runCatching{repo.save(n.copy(pinned=!n.pinned))}.onFailure{error=it.message.orEmpty()}}){Text(if(n.pinned)"Desafixar" else "Fixar")}
                        TextButton(onClick={runCatching{repo.save(n.copy(archived=!n.archived))}.onFailure{error=it.message.orEmpty()}}){Text(if(n.archived)"Restaurar" else "Arquivar")}
                        if(n.reminder.isNotBlank())TextButton(onClick={calendarDate=n.reminder;view="Agenda e calendário"}){Text("Calendário")}
                        TextButton(onClick={deleting="note" to n}){Text("Excluir")}
                    }}
                }
                "Tarefas" -> {
                    Text("${repo.tasks.count{!it.done}} pendentes · ${repo.tasks.count{!it.done&&it.dueDate<today}} atrasadas · ${repo.tasks.count{it.done}} concluídas")
                    PillChoices(listOf("Todas","Pendentes","Hoje","Atrasadas","Urgentes","Vinculadas","Concluídas"),filter,{filter=it})
                    val items=ProfessionalTools.taskOrder(repo.tasks.filter{WorkspaceRules.search(it.title+" "+it.notes+" "+it.student,query) && when(filter){"Pendentes"->!it.done;"Hoje"->it.dueDate==today;"Atrasadas"->!it.done&&it.dueDate<today;"Urgentes"->!it.done&&it.priority==3;"Vinculadas"->it.studentId.isNotBlank();"Concluídas"->it.done;else->true}})
                    OrganizationCards(items,{it.id},"tasks"){t->DataCard(t.title,"${t.dueDate} ${t.time} · ${listOf("Normal","Alta","Urgente")[t.priority-1]} · ${if(t.done)"Concluída" else "Pendente"}\n${t.student}\n${t.notes}") {
                        TextButton(onClick={editing="task" to t}){Text("Editar")}
                        TextButton(onClick={runCatching{repo.save(t.copy(done=!t.done))}.onFailure{error=it.message.orEmpty()}}){Text(if(t.done)"Reabrir" else "Concluir")}
                        TextButton(onClick={calendarDate=t.dueDate;view="Agenda e calendário"}){Text("Calendário")}
                        TextButton(onClick={deleting="task" to t}){Text("Excluir")}
                    }}
                }
                "Metas" -> {
                    PillChoices(listOf("Todas","Em andamento","Pausadas","Vencidas","Concluídas"),filter,{filter=it})
                    val items=repo.organizationGoals.filter{WorkspaceRules.search(it.title+" "+it.progress+" "+it.student,query) && when(filter){"Em andamento"->it.status=="Em andamento";"Pausadas"->it.status=="Pausada";"Concluídas"->it.status=="Concluída";"Vencidas"->it.status!="Concluída"&&it.due.isNotBlank()&&it.due<today;else->true}}.sortedBy{it.due.ifBlank{"9999"}}
                    OrganizationCards(items,{it.id},"goals"){g->DataCard(g.title,"${g.status} · ${g.due}\n${g.student}\n${g.progress}") {
                        TextButton(onClick={editing="goal" to g}){Text("Editar")}
                        TextButton(onClick={runCatching{repo.save(g.copy(status=if(g.status=="Concluída")"Em andamento" else "Concluída"))}.onFailure{error=it.message.orEmpty()}}){Text(if(g.status=="Concluída")"Reabrir" else "Concluir")}
                        if(g.due.isNotBlank())TextButton(onClick={calendarDate=g.due;view="Agenda e calendário"}){Text("Calendário")}
                        TextButton(onClick={deleting="goal" to g}){Text("Excluir")}
                    }}
                }
            }
        }}
    }
}

/** Cards never truncate content to satisfy a chosen size. Order/size persist independently of records. */
@Composable fun <T> OrganizationCards(items:List<T>,id:(T)->String,layoutKey:String="organization",body:@Composable (T)->Unit) {
    val context=LocalContext.current
    val prefs=remember{context.getSharedPreferences("html-layout-$layoutKey",android.content.Context.MODE_PRIVATE)}
    var editing by rememberSaveable{mutableStateOf(false)}
    var savedOrder by remember{mutableStateOf(prefs.getString("order","").orEmpty())}
    TextButton(onClick={editing=!editing}){Text(if(editing)"Concluir organização" else "Organizar cards")}
    if(items.isEmpty())Text("Nenhum registro encontrado neste filtro.")
    val ids=items.map(id);val order=HtmlParity.recordOrder(ids,savedOrder)
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val available=maxWidth
        FlowRow(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            order.forEach{key->key(key){
                val item=items.first{ id(it)==key }
                var size by remember{mutableStateOf(prefs.getString("size-$key","Médio") ?: "Médio")}
                val width=if(available<720.dp || size=="Grande")available else if(size=="Pequeno")(available-24.dp)/3 else (available-12.dp)/2
                Column(Modifier.width(width)) {
                    if(editing) {
                        PillChoices(listOf("Pequeno","Médio","Grande"),size,{size=it;prefs.edit().putString("size-$key",it).apply()})
                        ActionButtons {TextButton(enabled=order.indexOf(key)>0,onClick={savedOrder=HtmlParity.moveRecords(ids,savedOrder,key,-1).joinToString(",");prefs.edit().putString("order",savedOrder).apply()}){Text("Subir")}
                            TextButton(enabled=order.indexOf(key)<order.lastIndex,onClick={savedOrder=HtmlParity.moveRecords(ids,savedOrder,key,1).joinToString(",");prefs.edit().putString("order",savedOrder).apply()}){Text("Descer")}}
                    }
                    body(item)
                }
            }}
        }
    }
}

@Composable private fun OrganizationEditor(repo:AppRepository,kind:String,item:Any,onClose:()->Unit) {
    var title by remember{mutableStateOf(when(item){is NotebookEntry->item.title;is TaskItem->item.title;is OrganizationGoal->item.title;else->""})}
    var text by remember{mutableStateOf(when(item){is NotebookEntry->item.body;is TaskItem->item.notes;is OrganizationGoal->item.progress;else->""})}
    var date by remember{mutableStateOf(when(item){is NotebookEntry->item.reminder;is TaskItem->item.dueDate;is OrganizationGoal->item.due;else->""})}
    var studentId by remember{mutableStateOf(when(item){is NotebookEntry->item.studentId;is TaskItem->item.studentId;is OrganizationGoal->item.studentId;else->""})}
    var student by remember{mutableStateOf(when(item){is NotebookEntry->item.student;is TaskItem->item.student;is OrganizationGoal->item.student;else->""})}
    var time by remember{mutableStateOf((item as? TaskItem)?.time.orEmpty())}
    var priority by remember{mutableIntStateOf((item as? TaskItem)?.priority ?: 1)}
    var status by remember{mutableStateOf((item as? OrganizationGoal)?.status ?: "Em andamento")}
    DialogScreen { WorkspaceEditor(when(kind){"note"->"Nota";"task"->"Tarefa";else->"Meta"},onClose,{
        when(item){is NotebookEntry->repo.save(item.copy(title=title,body=text,studentId=studentId,student=student,reminder=date));is TaskItem->repo.save(item.copy(title=title,notes=text,dueDate=date,studentId=studentId,student=student,time=time,priority=priority));is OrganizationGoal->repo.save(item.copy(title=title,progress=text,due=date,studentId=studentId,student=student,status=status))};onClose()
    }) {
        Field("Título *",title,{title=it});Field(if(kind=="goal")"Acompanhamento descritivo" else "Detalhes",text,{text=it},lines=4)
        ClientPicker(repo,studentId,student){i,n->studentId=i;student=n}
        DateField(if(kind=="note")"Lembrete (opcional)" else "Prazo",date,{date=it})
        if(kind!="task")TextButton(onClick={date=""}){Text("Sem data")}
        if(kind=="task"){Field("Horário (opcional)",time,{time=it});PillChoices(listOf("Normal","Alta","Urgente"),listOf("Normal","Alta","Urgente")[priority-1],{priority=listOf("Normal","Alta","Urgente").indexOf(it)+1})}
        if(kind=="goal")PillChoices(listOf("Em andamento","Pausada","Concluída"),status,{status=it})
    }}
}
