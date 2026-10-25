package br.com.evolufisico.nativo.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(repo: AppRepository, createRequest: Int=0, initialDate:String="",onOpenRecord:(String,String)->Unit={_,_->}) {
    val safeInitial=runCatching{WorkspaceRules.date(initialDate)}.getOrNull()?.takeIf{it.year in 1..9999} ?: LocalDate.now()
    var selected by rememberSaveable(initialDate){mutableStateOf(safeInitial.toString())}
    var monthText by rememberSaveable(initialDate){mutableStateOf(YearMonth.from(safeInitial).toString())}
    var filter by rememberSaveable{mutableStateOf("Todos")}
    var editing by remember{mutableStateOf<Appointment?>(null)}
    var deleting by remember{mutableStateOf<Appointment?>(null)}
    var error by remember{mutableStateOf("")}
    var new by remember{mutableStateOf(false)}
    LaunchedEffect(createRequest){if(createRequest>0){editing=Appointment(date=selected);new=true}}
    ActionError(error){error=""}
    deleting?.let{item->ConfirmDelete("atendimento",{deleting=null},{repo.delete(item)})}
    editing?.let{item->key(item.id){
        DialogScreen {
            AppointmentEditor(item,repo.evaluations.distinctBy{it.studentId},new,onCancel={editing=null},onSave={appointment,repeat->
                runCatching{repo.saveAppointments(Agenda.recurrence(appointment,repeat))}.fold(
                    onSuccess={editing=null;null},onFailure={it.message ?: "Falha ao salvar atendimento."})
            })
        }
    }}
    Screen {
        Heading("Agenda","Calendário mensal, recorrência e detecção de conflito de horários.")
        Button(onClick={editing=Appointment(date=selected);new=true}){Text("+ Agendar")}
        val month=runCatching{YearMonth.parse(monthText)}.getOrNull()?.takeIf{it.year in 1..9999} ?: YearMonth.now()
        Text("${month.month.getDisplayName(TextStyle.SHORT,Locale.forLanguageTag("pt-BR"))} ${month.year}",Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.titleMedium)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
            TextButton(enabled=month>YearMonth.of(1,1),onClick={monthText=month.minusMonths(1).toString();selected=month.minusMonths(1).atDay(1).toString()}){Text("Anterior")}
            TextButton(enabled=month<YearMonth.of(9999,12),onClick={monthText=month.plusMonths(1).toString();selected=month.plusMonths(1).atDay(1).toString()}){Text("Próximo")}
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val calendarWidth=maxOf(maxWidth,350.dp)
            Row(Modifier.horizontalScroll(androidx.compose.foundation.rememberScrollState())) {
                Column(Modifier.width(calendarWidth)) {
        Row(Modifier.fillMaxWidth()){
            listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom").forEach{label->
                Text(label,Modifier.weight(1f).semantics{contentDescription=label},textAlign=androidx.compose.ui.text.style.TextAlign.Center,
                    style=MaterialTheme.typography.labelSmall)
            }
        }
        val offset=month.atDay(1).dayOfWeek.value-1
        val occupied=(repo.appointments.filter{it.status !in listOf("Cancelado","Faltou")}.map{it.date}+repo.tasks.map{it.dueDate}+repo.organizationGoals.map{it.due}+repo.notes.filter{!it.archived}.map{it.reminder}).filter{it.isNotBlank()}.groupingBy{it}.eachCount()
        val cells=(offset+month.lengthOfMonth()+6)/7*7
        (0 until cells step 7).forEach{week->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(2.dp)) {
                (0..6).forEach{column->
                    val number=week+column-offset+1
                    if(number !in 1..month.lengthOfMonth())Spacer(Modifier.weight(1f).heightIn(min=64.dp))
                    else {
                        val date=month.atDay(number).toString()
                        val active=date==selected
                        Surface(modifier=Modifier.weight(1f).heightIn(min=64.dp).semantics {contentDescription="Data $date, ${occupied[date] ?: 0} atendimentos";this.selected=active},shape=MaterialTheme.shapes.small,
                            color=if(active)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border=if(date==LocalDate.now().toString()) androidx.compose.foundation.BorderStroke(1.dp,MaterialTheme.colorScheme.primary) else null,
                            onClick={selected=date}) {
                            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                                Text(number.toString())
                                if((occupied[date] ?: 0)>0)Text("•",color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
                }
            }
        }
        TextButton(onClick={selected=LocalDate.now().toString();monthText=YearMonth.now().toString()}){Text("Voltar para hoje")}
        Text("Selecionado: $selected",style=MaterialTheme.typography.titleMedium)
        repo.tasks.filter{it.dueDate==selected}.forEach{record->DataCard("Tarefa · ${record.title}","${record.time} · ${if(record.done)"Concluída" else "Pendente"}"){TextButton(onClick={onOpenRecord("task",record.id)}){Text("Abrir tarefa")}}}
        repo.organizationGoals.filter{it.due==selected}.forEach{record->DataCard("Meta · ${record.title}",record.status){TextButton(onClick={onOpenRecord("goal",record.id)}){Text("Abrir meta")}}}
        repo.notes.filter{!it.archived && it.reminder==selected}.forEach{record->DataCard("Nota · ${record.title}","Lembrete"){TextButton(onClick={onOpenRecord("note",record.id)}){Text("Abrir nota")}}}
        val selectedItems=repo.appointments.filter{it.date==selected}
        val activeCount=selectedItems.count{it.status !in listOf("Cancelado","Faltou")}
        Text("$activeCount atendimentos ativos · ${selectedItems.count{it.status=="Concluído"}} concluídos · ${selectedItems.count{it.status=="Cancelado"}} cancelados",
            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        PillChoices(listOf("Todos","Agendado","Confirmado","Concluído","Faltou","Cancelado"),filter,{filter=it})
        val found=selectedItems.filter{filter=="Todos" || it.status==filter}
        if(found.isEmpty())EmptyWorkspace("Nenhum atendimento nesta data e filtro.")
        PagedRecords(found,selected+filter,{it.id}){item->DataCard("${item.time} · ${item.student}","${item.type} · ${item.durationMinutes} min · ${item.status}\n${item.location}\n${item.notes}") {
            TextButton(onClick={editing=item;new=false}){Text("Editar")}
            if(item.status!="Concluído")TextButton(onClick={runCatching{repo.save(item.copy(status="Concluído"))}.onFailure{error=it.message.orEmpty()}}){Text("Concluir")}
            TextButton(onClick={deleting=item}){Text("Excluir")}
        }}
    }
}

@Composable fun DialogScreen(content: @Composable ()->Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest={},properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
        Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding()){content()}
    }
}

