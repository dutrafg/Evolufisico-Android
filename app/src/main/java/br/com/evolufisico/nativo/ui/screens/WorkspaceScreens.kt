package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.WorkspaceRules
import br.com.evolufisico.nativo.domain.ProfessionalTools
import java.time.LocalDate
import java.util.UUID

@Composable
fun WorkspaceEditor(title: String, onClose: () -> Unit, onSave: () -> Unit,
                    content: @Composable ColumnScope.() -> Unit) {
    TrackEditing()
    var error by remember { mutableStateOf("") }
    var discard by remember { mutableStateOf(false) }
    Dialog(onDismissRequest={discard=true},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        BackHandler { discard=true }
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing).imePadding()) {
                Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween) {
                    TextButton(onClick={discard=true}) { Text("Cancelar") }
                    Button(onClick={runCatching(onSave).onFailure {error=it.message ?: "Falha ao salvar. Tente novamente."}}) { Text("Salvar") }
                }
                Screen(bottomInset=24.dp) {
                    Heading(title)
                    if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
                    content()
                }
            }
        }
        if(discard) AlertDialog(onDismissRequest={discard=false},title={Text("Descartar alterações?")},
            text={Text("As alterações ainda não salvas serão perdidas.")},
            confirmButton={TextButton(onClick=onClose){Text("Descartar")}},
            dismissButton={TextButton(onClick={discard=false}){Text("Continuar editando")}})
    }
}

@Composable
fun ClientPicker(repo: AppRepository, studentId: String, student: String, emptyLabel:String="Vincular cliente (opcional)",clearLabel:String="Sem vínculo",onChange: (String,String)->Unit) {
    var open by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val selectedLabel=studentChoices(WorkspaceRules.latest(repo.evaluations.toList())).toMap()[studentId] ?: student
    OutlinedButton(onClick={open=true},modifier=Modifier.fillMaxWidth()) {
        Text(if(student.isBlank()) emptyLabel else "Cliente: $selectedLabel")
    }
    if(open) AlertDialog(onDismissRequest={open=false},title={Text("Selecionar cliente")},text={
        Column {
            Field("Buscar cliente",query,{query=it})
            LazyColumn(Modifier.heightIn(max=320.dp)) {
                val found=WorkspaceRules.latest(repo.evaluations.toList()).filter {WorkspaceRules.search(it.name,query)}
                items(studentChoices(found),key={it.first}) { (id,label) ->
                    TextButton(onClick={onChange(id,found.first{it.studentId==id}.name);open=false}) {
                        Text((if(id==studentId) "✓ " else "")+label)
                    }
                }
                if(found.isEmpty()) item { Text("Nenhum perfil encontrado. Cadastre uma avaliação para criar um perfil.") }
            }
        }
    },confirmButton={TextButton(onClick={open=false}){Text("Fechar")}},
        dismissButton={TextButton(onClick={onChange("","");open=false}){Text(clearLabel)}})
}

@Composable
fun ConfirmDelete(label: String, onDismiss: ()->Unit, onDelete: ()->Unit) {
    var error by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest=onDismiss,title={Text("Excluir $label?")},
        text={Text(error.ifBlank {"Esta ação remove apenas este registro e não pode ser desfeita."})},
        confirmButton={TextButton(onClick={runCatching {onDelete();onDismiss()}.onFailure {error=it.message ?: "Falha ao excluir."}}){Text("Excluir")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}

@Composable
fun ActionError(message: String, onDismiss: ()->Unit) {
    if(message.isNotBlank()) AlertDialog(onDismissRequest=onDismiss,title={Text("Não foi possível concluir")},
        text={Text(message)},confirmButton={TextButton(onClick=onDismiss){Text("Entendi")}})
}

@Composable
fun TasksScreen(repo: AppRepository, createRequest: Int=0,initialClientId:String="") {
    var editing by remember { mutableStateOf<TaskItem?>(null) }
    var deleting by remember { mutableStateOf<TaskItem?>(null) }
    var filterClient by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(if(initialClientId.isBlank())"Pendentes" else "Todas") }
    var error by remember { mutableStateOf("") }
    LaunchedEffect(createRequest) { if(createRequest>0) editing=TaskItem(studentId=filterClient,student=filterName) }
    ActionError(error) {error=""}
    deleting?.let { item -> ConfirmDelete("tarefa",{deleting=null},{repo.delete(item)}) }
    editing?.let { item -> key(item.id) {
        var title by remember { mutableStateOf(item.title) }
        var due by remember { mutableStateOf(item.dueDate) }
        var notes by remember { mutableStateOf(item.notes) }
        var priority by remember{mutableIntStateOf(item.priority)}
        var category by remember{mutableStateOf(item.category)}
        var studentId by remember{mutableStateOf(item.studentId)};var student by remember{mutableStateOf(item.student)}
        WorkspaceEditor("Tarefa",{editing=null},{repo.save(item.copy(title=title.trim(),dueDate=due,notes=notes,priority=priority,category=category.trim(),studentId=studentId,student=student));editing=null}) {
            Field("Título *",title,{title=it}); DateField("Prazo",due,{due=it})
            PillChoices(listOf("Normal","Alta","Urgente"),listOf("Normal","Alta","Urgente")[priority.coerceIn(1,3)-1]){priority=listOf("Normal","Alta","Urgente").indexOf(it)+1}
            Field("Categoria",category,{category=it})
            ClientPicker(repo,studentId,student){id,name->studentId=id;student=name}
            Field("Detalhes",notes,{notes=it},lines=4)
        }
    } }
    Screen {
        Heading("Tarefas","Pendências, prazos e organização do atendimento.")
        Button(onClick={editing=TaskItem(studentId=filterClient,student=filterName)}){Text("+ Nova tarefa")}
        val overdue=repo.tasks.count {WorkspaceRules.overdue(it)}
        DataCard("Sua organização","${repo.tasks.count{!it.done}} pendentes · $overdue atrasadas · ${repo.tasks.count{it.done}} concluídas")
        ClientPicker(repo,filterClient,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterClient=id;filterName=name}
        Field("Buscar título ou detalhes",query,{query=it})
        PillChoices(listOf("Pendentes","Hoje","Atrasadas","Urgentes","Vinculadas","Concluídas","Todas"),filter,{filter=it})
        val found=ProfessionalTools.taskOrder(repo.tasks.toList()).filter { task -> (filterClient.isBlank() || task.studentId==filterClient) && WorkspaceRules.search(task.title+" "+task.notes+" "+task.student+" "+task.category,query) && when(filter) {
            "Pendentes"->!task.done; "Hoje"->!task.done && task.dueDate==LocalDate.now().toString()
            "Atrasadas"->WorkspaceRules.overdue(task);"Urgentes"->!task.done && task.priority==3;"Vinculadas"->task.studentId.isNotBlank(); "Concluídas"->task.done; else->true
        }}
        if(found.isEmpty()) EmptyWorkspace("Nenhuma tarefa nesta visualização.")
        PagedRecords(found,query+"|"+filter+"|"+filterClient,{it.id}) { task ->
            DataCard(task.title,"${listOf("Normal","Alta","Urgente")[task.priority.coerceIn(1,3)-1]} · ${task.category} · ${task.student}\nPrazo: ${task.dueDate} · ${if(task.done) "Concluída" else if(WorkspaceRules.overdue(task)) "Atrasada" else "Pendente"}\n${task.notes}") {
                TextButton(onClick={runCatching{repo.save(task.copy(done=!task.done))}.onFailure{error=it.message.orEmpty()}}){Text(if(task.done)"Reabrir" else "Concluir")}
                TextButton(onClick={editing=task}){Text("Editar")}
                if(!task.done)TextButton(onClick={runCatching{repo.save(task.copy(dueDate=WorkspaceRules.date(task.dueDate).plusDays(1).toString()))}.onFailure{error=it.message.orEmpty()}}){Text("Adiar 1 dia")};TextButton(onClick={deleting=task}){Text("Excluir")}
            }
        }
    }
}

@Composable fun EmptyWorkspace(text: String) {
    OutlinedCard(Modifier.fillMaxWidth()) { Text(text,Modifier.padding(24.dp),style=MaterialTheme.typography.bodyLarge) }
}

@Composable
fun HabitsScreen(repo: AppRepository, createRequest: Int=0,initialClientId:String="") {
    var editing by remember { mutableStateOf<HabitEntry?>(null) };var deleting by remember { mutableStateOf<HabitEntry?>(null) }
    var filterClient by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
    var query by rememberSaveable { mutableStateOf("") };var filter by rememberSaveable { mutableStateOf(if(initialClientId.isBlank())"Hoje" else "Todos") }
    LaunchedEffect(createRequest){if(createRequest>0)editing=HabitEntry(studentId=filterClient,student=filterName)}
    deleting?.let{item->ConfirmDelete("registro de hábito",{deleting=null},{repo.delete(item)})}
    editing?.let{item->key(item.id){
        var title by remember{mutableStateOf(item.title)};var date by remember{mutableStateOf(item.date)}
        var value by remember{mutableStateOf(item.value.toString())};var target by remember{mutableStateOf(item.target.toString())}
        var unit by remember{mutableStateOf(item.unit)};var notes by remember{mutableStateOf(item.notes)}
        var id by remember{mutableStateOf(item.studentId)};var student by remember{mutableStateOf(item.student)}
        WorkspaceEditor("Registro de hábito",{editing=null},{
            val amount=value.replace(',','.').toDoubleOrNull() ?: error("Valor realizado inválido.")
            val goal=target.replace(',','.').toDoubleOrNull() ?: error("Meta inválida.")
            repo.save(item.copy(title=title.trim(),studentId=id,student=student,date=date,value=amount,target=goal,unit=unit.trim(),notes=notes));editing=null
        }) {
            Field("Hábito *",title,{title=it});ClientPicker(repo,id,student){newId,name->id=newId;student=name}
            DateField("Data",date,{date=it});NumericField("Quantidade realizada",value,{value=it},decimal=true)
            NumericField("Meta definida por você",target,{target=it},decimal=true);Field("Unidade (L, h, passos, vezes...)",unit,{unit=it})
            Field("Observações",notes,{notes=it},lines=3)
        }
    }}
    Screen {
        Heading("Hábitos","Diário de acompanhamento com metas informadas pelo usuário, sem recomendações automáticas de saúde.")
        Button(onClick={editing=HabitEntry(studentId=filterClient,student=filterName)}){Text("+ Registrar hábito")}
        ClientPicker(repo,filterClient,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterClient=id;filterName=name}
        Field("Buscar hábito ou cliente",query,{query=it})
        PillChoices(listOf("Hoje","7 dias","Todos","Meta atingida","Em andamento"),filter,{filter=it})
        val found=repo.habits.filter{(filterClient.isBlank() || it.studentId==filterClient) && WorkspaceRules.search(it.title+" "+it.student,query) && when(filter){
            "Hoje"->it.date==LocalDate.now().toString();"7 dias"->runCatching{WorkspaceRules.date(it.date) in LocalDate.now().minusDays(6)..LocalDate.now()}.getOrDefault(false);"Meta atingida"->it.value>=it.target;"Em andamento"->it.value<it.target;else->true}}
        if(found.isEmpty())EmptyWorkspace("Nenhum hábito nesta visualização.")
        PagedRecords(found,query+"|"+filter+"|"+filterClient,{it.id}){item->
            var history by rememberSaveable(item.id){mutableStateOf(false)}
            DataCard(item.title,"${item.student.ifBlank{"Sem vínculo"}} · ${item.date}\n${item.value} / ${item.target} ${item.unit}\n${item.notes}") {
                TextButton(onClick={editing=item}){Text("Editar")}
                TextButton(onClick={history=!history}){Text(if(history)"Ocultar histórico" else "Sequência / 7 dias")}
                TextButton(onClick={editing=item.copy(id=UUID.randomUUID().toString(),date=LocalDate.now().toString(),value=0.0)}){Text("Repetir hoje")}
                TextButton(onClick={deleting=item}){Text("Excluir")}
            }
            LinearProgressIndicator(progress={WorkspaceRules.habitProgress(item.value,item.target) ?: 0f},modifier=Modifier.fillMaxWidth())
            if(history){val summary=ProfessionalTools.habitSummary(item,repo.habits.toList())
                DataCard("Histórico deste hábito","${summary.streak} dias seguidos · ${summary.achievedDays} metas atingidas e ${summary.loggedDays} dias registrados nos últimos 7 dias. Datas futuras e duplicatas do dia não aumentam a sequência.")
            }
        }
    }
}

@Composable
fun NotebookScreen(repo: AppRepository, createRequest: Int=0, initialQuery:String="",initialClientId:String="") {
    var editing by remember{mutableStateOf<NotebookEntry?>(null)};var deleting by remember{mutableStateOf<NotebookEntry?>(null)}
    var filterClient by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
    var query by rememberSaveable(initialQuery){mutableStateOf(initialQuery)};var filter by rememberSaveable{mutableStateOf("Ativas")}
    var error by remember{mutableStateOf("")}
    LaunchedEffect(createRequest){if(createRequest>0)editing=NotebookEntry(studentId=filterClient,student=filterName)}
    ActionError(error){error=""}
    deleting?.let{item->ConfirmDelete("nota",{deleting=null},{repo.delete(item)})}
    editing?.let{item->key(item.id){
        var title by remember{mutableStateOf(item.title)};var body by remember{mutableStateOf(item.body)}
        var id by remember{mutableStateOf(item.studentId)};var student by remember{mutableStateOf(item.student)}
        var pinned by remember{mutableStateOf(item.pinned)}
        var category by remember{mutableStateOf(item.category)}
        WorkspaceEditor("Nota",{editing=null},{repo.save(item.copy(title=title.trim(),body=body,studentId=id,student=student,pinned=pinned,category=category.trim(),date=LocalDate.now().toString()));editing=null}) {
            Field("Título *",title,{title=it});ClientPicker(repo,id,student){newId,name->id=newId;student=name}
            Field("Categoria",category,{category=it})
            Field("Conteúdo",body,{body=it},lines=5)
            Row {Switch(checked=pinned,onCheckedChange={pinned=it});Text("Fixar no topo",Modifier.padding(12.dp))}
        }
    }}
    Screen {
        Heading("Notas","Anotações locais criptografadas, com busca e vínculo opcional ao cliente.")
        Button(onClick={editing=NotebookEntry(studentId=filterClient,student=filterName)}){Text("+ Nova nota")}
        ClientPicker(repo,filterClient,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterClient=id;filterName=name}
        Field("Buscar título, conteúdo ou cliente",query,{query=it})
        PillChoices(listOf("Ativas","Fixadas","Vinculadas","Arquivadas","Todas"),filter,{filter=it})
        val found=repo.notes.filter{(filterClient.isBlank() || it.studentId==filterClient) && WorkspaceRules.search(it.title+" "+it.body+" "+it.student+" "+it.category,query) && when(filter){"Ativas"->!it.archived;"Fixadas"->it.pinned && !it.archived;"Vinculadas"->it.studentId.isNotBlank() && !it.archived;"Arquivadas"->it.archived;else->true}}
        if(found.isEmpty())EmptyWorkspace("Nenhuma anotação encontrada.")
        PagedRecords(found,query+"|"+filter+"|"+filterClient,{it.id}){item->
            var expanded by rememberSaveable(item.id){mutableStateOf(false)}
            DataCard((if(item.pinned)"★ " else "")+item.title,"${item.date} · ${item.category} · ${item.student.ifBlank{"Geral"}}\n${ProfessionalTools.notePreview(item,expanded)}") {
            TextButton(onClick={editing=item}){Text("Editar")}
            if(item.body.length>240)TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Recolher" else "Ler completa")}
            TextButton(onClick={runCatching{repo.save(item.copy(archived=!item.archived))}.onFailure{error=it.message.orEmpty()}}){Text(if(item.archived)"Restaurar" else "Arquivar")}
            TextButton(onClick={runCatching{repo.save(item.copy(pinned=!item.pinned))}.onFailure{error=it.message.orEmpty()}}){Text(if(item.pinned)"Desafixar" else "Fixar")}
            TextButton(onClick={deleting=item}){Text("Excluir")}
        }}
    }
}
