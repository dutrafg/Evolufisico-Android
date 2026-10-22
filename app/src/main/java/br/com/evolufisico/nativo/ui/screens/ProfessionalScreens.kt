package br.com.evolufisico.nativo.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
fun ClientsScreen(repo: AppRepository, initialQuery: String="", initialClientId:String="", onReevaluate: (String)->Unit, onModule: (Int)->Unit) {
    var query by rememberSaveable(initialQuery){mutableStateOf(initialQuery)}
    var selected by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    val latest=WorkspaceRules.latest(repo.evaluations.toList())
    val client=latest.firstOrNull{it.studentId==selected}
    Screen {
        Heading("Clientes","Dossiê vinculado por identificador. Pessoas com o mesmo nome mantêm históricos separados.")
        if(client==null) {
            Button(onClick={onReevaluate("")}){Text("+ Novo cliente / avaliação")}
            Field("Buscar nome, e-mail ou telefone",query,{query=it})
            val found=latest.filter{WorkspaceRules.search(it.name+" "+it.email+" "+it.phone,query)}
            DataCard("Base de clientes","${latest.size} perfis · ${repo.evaluations.size} avaliações")
            if(found.isEmpty())EmptyWorkspace("Nenhum perfil encontrado. Cadastre uma avaliação para começar.")
            val labels=studentChoices(found).toMap()
            PagedRecords(found,query,{it.studentId}){item->DataCard(labels[item.studentId] ?: item.name,"Última avaliação: ${item.date}\n${item.phone} · ${item.email}") {
                TextButton(onClick={selected=item.studentId}){Text("Abrir dossiê")}
                TextButton(onClick={onReevaluate(item.studentId)}){Text("Reavaliar")}
            }}
        } else {
            TextButton(onClick={selected=""}){Text("Voltar aos clientes")}
            Heading(client.name)
            DataCard("Cadastro","${client.age?.let{"$it anos"} ?: "Idade não informada"} · ${client.sex}\n${client.email}\n${client.phone}")
            DataCard("Avaliação atual","${client.date} · ${Metrics.oneDecimal(client.weightKg)} kg · IMC ${Metrics.oneDecimal(Metrics.bmi(client.weightKg,client.heightCm))}") {
                TextButton(onClick={onReevaluate(client.studentId)}){Text("Nova avaliação")}
                TextButton(onClick={onModule(2)}){Text("Evolução")}
                TextButton(onClick={onModule(11)}){Text("Metas")}
            }
            val entries=repo.charges.filter{it.studentId==client.studentId || it.secondStudentId==client.studentId}
            val pending=entries.filter{!it.paid && !it.exempt}
            DataCard("Financeiro","${entries.size} lançamentos vinculados · pendente ${money(pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})}") {
                TextButton(onClick={onModule(6)}){Text("Abrir cobranças")}
            }
            Heading("Histórico")
            repo.evaluations.filter{it.studentId==client.studentId}.forEach{DataCard(it.date,"Peso: ${Metrics.oneDecimal(it.weightKg)} kg · Gordura: ${Metrics.oneDecimal(it.bodyFatPercent)}%\n${it.notes}")}
            Heading("Atendimentos")
            val visits=repo.appointments.filter{it.studentId==client.studentId}
            if(visits.isEmpty())Text("Sem atendimentos vinculados.")
            visits.forEach{DataCard("${it.date} · ${it.time}","${it.type} · ${it.status} · ${it.location}")}
            Heading("Tarefas vinculadas")
            val clientTasks=ProfessionalTools.taskOrder(repo.tasks.filter{it.studentId==client.studentId})
            if(clientTasks.isEmpty())Text("Nenhuma tarefa vinculada.")
            clientTasks.take(10).forEach{DataCard(it.title,"${it.dueDate} · ${if(it.done)"Concluída" else "Pendente"} · ${it.category}")}
            Heading("Treinos e hábitos")
            val training=repo.trainingSessions.filter{!it.isTemplate && it.studentId==client.studentId}
            DataCard("Acompanhamento","${training.size} treinos · ${training.count{it.completed}} concluídos · ${repo.habits.count{it.studentId==client.studentId}} registros de hábitos") {
                TextButton(onClick={onModule(12)}){Text("Treinos")};TextButton(onClick={onModule(13)}){Text("Hábitos")}
            }
            val notes=repo.notes.filter{it.studentId==client.studentId && !it.archived}
            if(notes.isNotEmpty())Heading("Notas vinculadas")
            notes.forEach{DataCard(it.title,it.body)}
        }
    }
}

@Composable
fun CalendarScreen(repo: AppRepository, createRequest: Int=0, initialDate:String="") {
    var selected by rememberSaveable(initialDate){mutableStateOf(initialDate.ifBlank{LocalDate.now().toString()})}
    var monthText by rememberSaveable(initialDate){mutableStateOf(YearMonth.from(LocalDate.parse(initialDate.ifBlank{LocalDate.now().toString()})).toString())}
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
        val month=YearMonth.parse(monthText)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
            TextButton(onClick={monthText=month.minusMonths(1).toString();selected=month.minusMonths(1).atDay(1).toString()}){Text("Anterior")}
            Text("${month.month.getDisplayName(TextStyle.SHORT,Locale.forLanguageTag("pt-BR"))} ${month.year}",style=MaterialTheme.typography.titleMedium)
            TextButton(onClick={monthText=month.plusMonths(1).toString();selected=month.plusMonths(1).atDay(1).toString()}){Text("Próximo")}
        }
        Row(Modifier.fillMaxWidth()){
            listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom").forEach{label->
                Text(label,Modifier.weight(1f).semantics{contentDescription=label},textAlign=androidx.compose.ui.text.style.TextAlign.Center,
                    style=MaterialTheme.typography.labelSmall)
            }
        }
        val offset=month.atDay(1).dayOfWeek.value-1
        val occupied=repo.appointments.filter{it.status !in listOf("Cancelado","Faltou")}.groupingBy{it.date}.eachCount()
        val cells=(offset+month.lengthOfMonth()+6)/7*7
        (0 until cells step 7).forEach{week->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(2.dp)) {
                (0..6).forEach{column->
                    val number=week+column-offset+1
                    if(number !in 1..month.lengthOfMonth())Spacer(Modifier.weight(1f).aspectRatio(1f))
                    else {
                        val date=month.atDay(number).toString()
                        val active=date==selected
                        Surface(modifier=Modifier.weight(1f).aspectRatio(1f).semantics {contentDescription="Data $date, ${occupied[date] ?: 0} atendimentos";this.selected=active},shape=MaterialTheme.shapes.small,
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
        TextButton(onClick={selected=LocalDate.now().toString();monthText=YearMonth.now().toString()}){Text("Voltar para hoje")}
        Text("Selecionado: $selected",style=MaterialTheme.typography.titleMedium)
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
        Surface(Modifier.fillMaxSize().systemBarsPadding().imePadding()){content()}
    }
}

@Composable
fun GoalsScreen(repo: AppRepository) {
    var query by rememberSaveable{mutableStateOf("")};var filter by rememberSaveable{mutableStateOf("Todas")}
    var editing by remember{mutableStateOf<Evaluation?>(null)}
    editing?.let{item->key(item.id){
        var weight by remember{mutableStateOf(item.goalWeightKg?.toString().orEmpty())}
        var fat by remember{mutableStateOf(item.goalBodyFatPercent?.toString().orEmpty())}
        var due by remember{mutableStateOf(item.goalDate)};var notes by remember{mutableStateOf(item.goalNotes)}
        WorkspaceEditor("Metas · ${item.name}",{editing=null},{
            fun optional(value:String):Double?=if(value.isBlank())null else value.replace(',','.').toDoubleOrNull() ?: error("Meta numérica inválida.")
            val w=optional(weight);val f=optional(fat)
            val wb=if(w==item.goalWeightKg)item.goalWeightBaselineKg ?: item.weightKg else item.weightKg
            val fb=if(f==item.goalBodyFatPercent)item.goalBodyFatBaselinePercent ?: item.bodyFatPercent else item.bodyFatPercent
            require(GoalTracking.validateTarget(wb,w,20.0..400.0)){"Meta de peso exige peso atual válido e alvo diferente da linha de base."}
            require(GoalTracking.validateTarget(fb,f,0.0..100.0)){"Meta de gordura exige percentual atual válido e alvo diferente da linha de base."}
            require(notes.length<=500){"Use até 500 caracteres nas observações da meta."}
            if(due.isNotBlank())require(WorkspaceRules.date(due)>=WorkspaceRules.date(item.date)){"Prazo anterior à avaliação."}
            repo.save(item.copy(goalWeightKg=w,goalWeightBaselineKg=if(w==null)null else wb,
                goalBodyFatPercent=f,goalBodyFatBaselinePercent=if(f==null)null else fb,
                goalDate=if(w==null && f==null)"" else due,goalNotes=notes));editing=null
        }) {
            Text("Peso atual: ${Metrics.oneDecimal(item.weightKg)} kg · Gordura atual: ${Metrics.oneDecimal(item.bodyFatPercent)}%")
            Field("Peso-alvo (kg, opcional)",weight,{weight=it});Field("Gordura-alvo (%, opcional)",fat,{fat=it})
            DateField("Prazo (opcional)",due,{due=it});Field("Notas da meta",notes,{notes=it},lines=3)
            Text("Deixe os dois alvos vazios para remover as metas desta avaliação.")
        }
    }}
    Screen {
        Heading("Metas","Acompanhe objetivos definidos pelo profissional e pelo cliente. O progresso é descritivo.")
        Field("Buscar cliente",query,{query=it});PillChoices(listOf("Todas","Com metas","Sem metas","Prazo vencido"),filter,{filter=it})
        val found=WorkspaceRules.latest(repo.evaluations.toList()).filter{e->
            val has=e.goalWeightKg!=null || e.goalBodyFatPercent!=null
            WorkspaceRules.search(e.name,query) && when(filter){"Com metas"->has;"Sem metas"->!has;"Prazo vencido"->has && e.goalDate.isNotBlank() && e.goalDate<LocalDate.now().toString();else->true}}
        if(found.isEmpty())EmptyWorkspace("Nenhum cliente neste filtro.")
        PagedRecords(found,query+filter,{it.studentId}){e->
            val due=runCatching{LocalDate.parse(e.goalDate)}.getOrNull()
            DataCard(e.name,GoalTracking.summary("Peso",e.goalWeightBaselineKg,e.weightKg,e.goalWeightKg,"kg",due)+"\n"+
                GoalTracking.summary("Gordura",e.goalBodyFatBaselinePercent,e.bodyFatPercent,e.goalBodyFatPercent,"%",due)+"\n"+e.goalNotes) {
                TextButton(onClick={editing=e}){Text("Definir / editar metas")}
            }
            GoalTracking.progress(e.goalWeightBaselineKg,e.weightKg,e.goalWeightKg)?.let{p->LinearProgressIndicator(progress={(p.percent/100).toFloat()},modifier=Modifier.fillMaxWidth())}
        }
    }
}

@Composable
fun ReportsScreen(repo: AppRepository, onBillingPdf:()->Unit,onExportCsv:(String,String)->Unit) {
    val context=LocalContext.current
    var exportKind by rememberSaveable{mutableStateOf("Avaliações")}
    var includeNames by rememberSaveable{mutableStateOf(false)}
    var period by rememberSaveable{mutableStateOf("30 dias")}
    val today=LocalDate.now()
    val start=when(period){"7 dias"->today.minusDays(6);"30 dias"->today.minusDays(29);"90 dias"->today.minusDays(89);else->LocalDate.MIN}
    fun inPeriod(value:String)=runCatching{LocalDate.parse(value) in start..today}.getOrDefault(false)
    val evals=repo.evaluations.filter{inPeriod(it.date)}
    val visits=repo.appointments.filter{inPeriod(it.date)}
    val completed=visits.count{it.status=="Concluído"}
    val charges=repo.charges.filter{inPeriod(it.dueDate)}
    val paid=charges.filter{it.paid && !it.exempt}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
    val pending=charges.filter{!it.paid && !it.exempt}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
    val sessions=repo.trainingSessions.filter{!it.isTemplate && it.completed && inPeriod(it.date)}
    val habits=repo.habits.filter{inPeriod(it.date)}
    val summary="EvoluFísico · Resumo $period até $today\n${evals.size} avaliações\n${visits.size} atendimentos; $completed concluídos\nRecebido: ${money(paid)}; pendente: ${money(pending)} (por vencimento)\n${sessions.size} treinos concluídos; ${sessions.sumOf{it.durationMinutes}} minutos\n${habits.size} registros de hábitos; ${habits.count{it.value>=it.target}} metas atingidas"
    var error by remember{mutableStateOf("")}
    ActionError(error){error=""}
    Screen {
        Heading("Relatórios","Indicadores do período. Financeiro agrupado pela data de vencimento, sem previsão de receita.")
        PillChoices(listOf("7 dias","30 dias","90 dias","Todo período"),period,{period=it})
        DataCard("Avaliações","${evals.size} registros · ${evals.map{it.studentId}.distinct().size} clientes avaliados")
        DataCard("Atendimentos","${visits.size} agendados no período · $completed concluídos · ${visits.count{it.status=="Faltou"}} faltas")
        DataCard("Financeiro por vencimento","Recebido: ${money(paid)}\nPendente: ${money(pending)}\n${charges.count{it.exempt}} cortesias") {
            TextButton(onClick=onBillingPdf){Text("PDF financeiro completo")}
        }
        DataCard("Treinamento","${sessions.size} sessões concluídas · ${sessions.sumOf{it.durationMinutes}} minutos informados · ${sessions.sumOf{ProfessionalTools.completedVolume(it)}} kg·rep de carga externa registrada")
        DataCard("Hábitos","${habits.size} registros · ${habits.count{it.value>=it.target}} metas atingidas")
        DataCard("Organização atual","${repo.tasks.count{!it.done}} tarefas pendentes · ${repo.tasks.count{WorkspaceRules.overdue(it)}} atrasadas · ${repo.notes.size} notas")
        OutlinedButton(onClick={runCatching{
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,summary)},"Compartilhar resumo"))
        }.onFailure{error="Não foi possível abrir o compartilhamento."}}){Text("Compartilhar resumo sem nomes")}
        Heading("Exportar planilha CSV","Escolha o conjunto de dados do período. O arquivo é salvo pelo seletor Android; textos são protegidos contra interpretação como fórmula em planilhas.")
        PillChoices(listOf("Avaliações","Cobranças","Treinos","Séries","Hábitos","Tarefas"),exportKind,{exportKind=it})
        Row(verticalAlignment=Alignment.CenterVertically){Text("Incluir nomes de clientes",Modifier.weight(1f));Switch(includeNames,onCheckedChange={includeNames=it})}
        Button(onClick={runCatching{
            val csv=ExportTables.build(exportKind,evals,charges,repo.trainingSessions.filter{inPeriod(it.date)},habits,
                repo.tasks.filter{inPeriod(it.dueDate)},includeNames)
            onExportCsv("EvoluFisico_${exportKind}_${today}.csv",csv)
        }.onFailure{error=it.message ?: "Falha ao preparar CSV."}}){Text("Salvar CSV do período")}
        Heading("Distribuição por serviço")
        if(charges.isEmpty())Text("Sem cobranças neste período.")
        charges.groupBy{it.service}.forEach{(service,rows)->DataCard(service,"${rows.size} lançamentos · ${money(rows.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})}")}
    }
}
