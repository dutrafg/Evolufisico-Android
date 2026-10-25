package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import br.com.evolufisico.nativo.domain.Agenda

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.evolufisico.nativo.domain.ProfessionalTools
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.Metrics
import br.com.evolufisico.nativo.domain.Financial
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun OrganizationWorkspaceScreen(repository:AppRepository,onClient:(String)->Unit) {
    var editing by remember { mutableStateOf<Appointment?>(null) }
    var newEvent by remember { mutableStateOf(false) }
    var delete by remember { mutableStateOf<Appointment?>(null) }
    var taskText by rememberSaveable { mutableStateOf("") }
    var day by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var taskDelete by remember { mutableStateOf<TaskItem?>(null) }
    var taskFilter by rememberSaveable { mutableStateOf("Pendentes") }
    taskDelete?.let{item->ConfirmDelete("tarefa",{taskDelete=null},{repository.delete(item)})}
    var actionError by remember { mutableStateOf("") }
    if(actionError.isNotBlank()) AlertDialog(onDismissRequest={actionError=""},
        title={Text("Não foi possível atualizar")},text={Text(actionError)},
        confirmButton={TextButton(onClick={actionError=""}){Text("OK")}})
    if (delete != null) AlertDialog(onDismissRequest={delete=null},title={Text("Excluir agendamento?")},
        confirmButton={TextButton(onClick={runCatching {delete?.let(repository::delete)}.onSuccess{delete=null}.onFailure{actionError=it.message ?: "Falha ao excluir."}}){Text("Excluir")}},
        dismissButton={TextButton(onClick={delete=null}){Text("Cancelar")}})
    if(newEvent) AppointmentEditor(editing ?: remember(newEvent) { Appointment() },
        students=repository.evaluations.distinctBy { it.studentId },
        isNew=editing==null,
        onCancel={newEvent=false},onSave={appointment,repeat ->
            runCatching {
                repository.saveAppointments(Agenda.recurrence(appointment, repeat))
            }.fold(onSuccess={newEvent=false;null},
                onFailure={it.message ?: "Não foi possível salvar. Tente novamente."})
        })
    else Screen {
        val context=androidx.compose.ui.platform.LocalContext.current
        val prefs=remember{context.getSharedPreferences("web-dashboard",android.content.Context.MODE_PRIVATE)}
        val professionalPrefs=remember{context.getSharedPreferences("professional",android.content.Context.MODE_PRIVATE)}
        val professional=professionalPrefs.getString("name","").orEmpty()
        val greeting=when(LocalTime.now().hour){in 5..11->"Bom dia";in 12..17->"Boa tarde";else->"Boa noite"}
        Heading("Agenda e acompanhamento","Planejamento de compromissos, tarefas e reavaliações.")
        var organizing by rememberSaveable{mutableStateOf(false)}
        var order by remember{mutableStateOf(br.com.evolufisico.nativo.domain.HtmlParity.panelOrder(prefs.getString("order",null)))}
        OutlinedButton(onClick={organizing=!organizing}){Text(if(organizing)"Concluir organização" else "Organizar painel")}
        if(organizing)TextButton(onClick={order=br.com.evolufisico.nativo.domain.HtmlParity.panels;prefs.edit().clear().apply();organizing=false}){Text("Restaurar painel")}
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val available=maxWidth
            FlowRow(horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                order.forEach { id -> key(id) {
                    var full by remember(organizing){mutableStateOf(prefs.getBoolean("full-$id",false))}
                    var height by remember(organizing){mutableIntStateOf(prefs.getInt("height-$id",0).coerceIn(0,900))}
                    val width=if(available>=720.dp && !full)(available-12.dp)/2 else available
                    EffectsCard(Modifier.width(width).heightIn(min=height.dp)) {
                        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                            val label=when(id){"agenda"->"Agenda rápida";"calendario"->"Calendário e agenda";"tarefas"->"Tarefas do dia";else->"Reavaliações próximas"}
                            Text(label,style=MaterialTheme.typography.titleMedium)
                            if(organizing)ActionButtons {
                                TextButton(enabled=order.indexOf(id)>0,onClick={order=br.com.evolufisico.nativo.domain.HtmlParity.move(order,id,-1);prefs.edit().putString("order",order.joinToString(",")).apply()}){Text("Subir")}
                                TextButton(enabled=order.indexOf(id)<order.lastIndex,onClick={order=br.com.evolufisico.nativo.domain.HtmlParity.move(order,id,1);prefs.edit().putString("order",order.joinToString(",")).apply()}){Text("Descer")}
                                TextButton(onClick={full=!full;prefs.edit().putBoolean("full-$id",full).apply()}){Text(if(full)"Largura normal" else "Largura total")}
                                TextButton(enabled=height<900,onClick={height=(height+80).coerceAtMost(900);prefs.edit().putInt("height-$id",height).apply()}){Text("Aumentar")}
                                TextButton(enabled=height>0,onClick={height=(height-80).coerceAtLeast(0);prefs.edit().putInt("height-$id",height).apply()}){Text("Diminuir")}
                            }
                            when(id) {
                                "agenda" -> {
                                    Text("Avaliações, reavaliações, retornos e atendimentos.")
                                    Button(onClick={editing=null;newEvent=true}){Text("Agendar rápido")}
                                }
                                "calendario" -> {
                                    var expanded by rememberSaveable{mutableStateOf(false)}
                                    val today=LocalDate.now().toString()
                                    Text("${repository.appointments.count{it.date==today && it.status !in listOf("Cancelado","Faltou")}} atendimentos hoje")
                                    OutlinedButton(onClick={expanded=!expanded}){Text(if(expanded)"Recolher calendário" else "Abrir calendário e agenda")}
                                    if(expanded) androidx.compose.ui.window.Dialog(onDismissRequest={expanded=false},properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
                                        Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                                            Column {
                                                TextButton(onClick={expanded=false}){Text("Voltar ao Início")}
                                                Box(Modifier.weight(1f)){CalendarScreen(repository)}
                                            }
                                        }
                                    }
                                }
                                "tarefas" -> {
                                    Field("Nova tarefa",taskText,{taskText=it})
                                    Button(enabled=taskText.isNotBlank(),onClick={runCatching{repository.save(TaskItem(title=taskText.trim()))}.onSuccess{taskText=""}.onFailure{actionError=it.message ?: "Falha ao salvar tarefa."}}){Text("Adicionar")}
                                    PillChoices(listOf("Pendentes","Concluídas","Todas"),taskFilter,{taskFilter=it})
                                    val visibleTasks=ProfessionalTools.taskOrder(repository.tasks.filter{when(taskFilter){"Pendentes"->!it.done;"Concluídas"->it.done;else->true}})
                                    if(visibleTasks.isEmpty())Text("Nenhuma tarefa neste filtro.")
                                    PagedRecords(visibleTasks,taskFilter,{it.id}) { item ->
                                        DataCard(item.title,"${item.dueDate} · ${if(item.done)"Concluída" else "Pendente"}") {
                                            TextButton(onClick={runCatching{repository.save(item.copy(done=!item.done))}.onFailure{actionError=it.message ?: "Falha ao atualizar tarefa."}}){Text(if(item.done)"Reabrir" else "Concluir")}
                                            TextButton(onClick={taskDelete=item}){Text("Excluir")}
                                        }
                                    }
                                }
                                "retornos" -> {
                                    val returns=br.com.evolufisico.nativo.domain.WorkspaceRules.latest(repository.evaluations.toList()).filter{it.nextAssessment.isNotBlank()}.sortedBy{it.nextAssessment}
                                    if(returns.isEmpty())Text("Nenhuma reavaliação prevista.")
                                    returns.take(8).forEach { e -> DataCard(e.name,"Prevista: ${e.nextAssessment}") {
                                        TextButton(onClick={onClient(e.studentId)}){Text("Abrir avaliações")}
                                    }}
                                }
                            }
                        }
                    }
                }}
            }
        }
    }
}

@Composable
fun AppointmentEditor(initial:Appointment,students:List<Evaluation>,isNew:Boolean,
    onCancel:()->Unit,onSave:(Appointment,Int)->String?) {
    TrackEditing()
    var discardRequested by remember(initial.id) { mutableStateOf(false) }
    BackHandler { discardRequested = true }
    if (discardRequested) AlertDialog(
        onDismissRequest = { discardRequested = false },
        title = { Text("Descartar alterações?") },
        text = { Text("Os dados desta edição que não foram salvos serão perdidos.") },
        confirmButton = { TextButton(onClick = onCancel) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { discardRequested = false }) { Text("Continuar editando") } }
    )
    var student by remember(initial.id){mutableStateOf(initial.student)}
    var date by remember(initial.id){mutableStateOf(initial.date)}
    var time by remember(initial.id){mutableStateOf(initial.time)}
    var duration by remember(initial.id){mutableStateOf(initial.durationMinutes.toString())}
    var type by remember(initial.id){mutableStateOf(initial.type)}
    var status by remember(initial.id){mutableStateOf(initial.status)}
    var location by remember(initial.id){mutableStateOf(initial.location)}
    var notes by remember(initial.id){mutableStateOf(initial.notes)}
    var weeks by remember(initial.id){mutableStateOf("1")}
    var error by remember(initial.id){mutableStateOf("")}
    Screen {
        Heading("Agendamento", "Horário local do aparelho · agendamentos recorrentes são registros individuais.")
        val choices=studentChoices(students)
        var selectedId by remember(initial.id){mutableStateOf(initial.studentId)}
        if(choices.isNotEmpty())PillChoices(choices.map{it.second},choices.firstOrNull{it.first==selectedId}?.second.orEmpty(),{label ->
            selectedId=choices.firstOrNull{it.second==label}?.first.orEmpty()
            student=students.firstOrNull{it.studentId==selectedId}?.name.orEmpty()
        })
        Field("Aluno/contato",student,{student=it;selectedId=""})
        DateField("Data",date,{date=it}); Field("Hora (HH:MM)",time,{time=it})
        Field("Duração (min)",duration,{duration=it}); Field("Local",location,{location=it})
        PillChoices(listOf("Avaliação","Reavaliação","Retorno","Atendimento","Outro"),type,{type=it})
        PillChoices(listOf("Agendado","Confirmado","Concluído","Faltou","Cancelado"),status,{status=it})
        if(isNew) Field("Repetir por quantas semanas (1–12)",weeks,{weeks=it})
        Field("Observações",notes,{notes=it},lines=3)
        if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick={discardRequested=true}){Text("Cancelar")}
            Button(onClick={
                error=when {
                    student.isBlank()->"Informe aluno ou contato."
                    runCatching{LocalDate.parse(date)}.isFailure->"Data inválida."
                    runCatching{LocalTime.parse(time)}.isFailure->"Hora inválida (HH:MM)."
                    duration.toIntOrNull() !in 1..720 ->"Duração deve estar entre 1 e 720 minutos."
                    weeks.toIntOrNull() !in 1..12 ->"Repetição deve estar entre 1 e 12."
                    else->""
                }
                if(error.isEmpty())error=onSave(initial.copy(student=student.trim(),date=date,time=time,
                    durationMinutes=duration.toInt(),type=type,status=status,location=location,notes=notes,
                    studentId=selectedId),if(isNew)weeks.toInt() else 1).orEmpty()
            }){Text("Salvar")}
        }
    }
}
