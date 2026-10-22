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
fun HomeScreen(repository: AppRepository, openEvaluations: () -> Unit, openBilling: () -> Unit, onModule:(Int)->Unit={},onClient:(String)->Unit={onModule(8)}) {
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
        Heading("Visão geral", "Seu atendimento, clientes e prioridades em um só lugar.")
        WorkspaceOverview(repository,onModule,onClient)
        HorizontalDivider()
        Heading("Agenda de atendimentos")
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Button(onClick={editing=null;newEvent=true}){Text("+ Agendar rápido")}
            OutlinedButton(onClick={day=LocalDate.now().toString()}){Text("Hoje")}
        }
        DateField("Dia (AAAA-MM-DD)",day,{day=it})
        val appointments=repository.appointments.filter { it.date == day }
        if(appointments.isEmpty()) Text("Nenhum atendimento nesta data.")
        PagedRecords(appointments,day,{it.id}) { item ->
            DataCard("${item.time} · ${item.student.ifBlank { item.type }}", "${item.type} · ${item.durationMinutes} min · ${item.status} · ${item.location}") {
                TextButton(onClick={runCatching {
                    repository.save(item.copy(status=if(item.status=="Concluído")"Agendado" else "Concluído"))
                }.onFailure { actionError=it.message ?: "Falha ao atualizar atendimento." }}) {
                    Text(if(item.status=="Concluído")"Reabrir" else "Concluir")
                }
                TextButton(onClick={editing=item;newEvent=true}){Text("Editar")}
                TextButton(onClick={delete=item}){Text("Excluir")}
            }
        }
        val returns=repository.evaluations.groupBy { it.studentId }.values.mapNotNull { values ->
            values.maxWithOrNull(compareBy<Evaluation>{it.date}.thenBy{it.id})?.takeIf { it.nextAssessment.isNotBlank() && it.nextAssessment >= LocalDate.now().toString() }
        }.sortedBy { it.nextAssessment }.take(5)
        if(returns.isNotEmpty()) {
            HorizontalDivider(); Text("Próximas reavaliações",style=MaterialTheme.typography.titleMedium)
            returns.forEach { DataCard(it.name,"Prevista: ${it.nextAssessment}") }
        }
        HorizontalDivider()
        Heading("Tarefas")
        Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Field("Nova tarefa",taskText,{taskText=it})
            Button(enabled=taskText.isNotBlank(),onClick={runCatching{repository.save(TaskItem(title=taskText.trim()))}.onSuccess{taskText=""}.onFailure{actionError=it.message ?: "Falha ao salvar tarefa."}}){Text("Adicionar")}
        }
        PillChoices(listOf("Pendentes","Concluídas","Todas"),taskFilter,{taskFilter=it})
        val visibleTasks=ProfessionalTools.taskOrder(repository.tasks.filter{when(taskFilter){"Pendentes"->!it.done;"Concluídas"->it.done;else->true}})
        if(visibleTasks.isEmpty())Text("Nenhuma tarefa neste filtro.")
        PagedRecords(visibleTasks,taskFilter,{it.id}) { item ->
            DataCard(item.title,"${item.dueDate} · ${item.category} · ${if(item.done)"Concluída" else "Pendente"}") {
                TextButton(onClick={runCatching{repository.save(item.copy(done=!item.done))}.onFailure{actionError=it.message ?: "Falha ao atualizar tarefa."}}){Text(if(item.done)"Reabrir" else "Concluir")}
                TextButton(onClick={taskDelete=item}){Text("Excluir")}
            }
        }
        HorizontalDivider()
        Text("Últimas avaliações",style=MaterialTheme.typography.titleMedium)
        repository.evaluations.take(5).forEach {
            DataCard(it.name,"${it.date} · ${Metrics.oneDecimal(it.weightKg)} kg · IMC ${Metrics.oneDecimal(Metrics.bmi(it.weightKg,it.heightCm))}")
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
