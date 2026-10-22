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
fun ClientsScreen(repo: AppRepository, initialQuery: String="", initialClientId:String="", onReevaluate: (String)->Unit,
    onModule: (Int)->Unit,onClientModule:(Int,String)->Unit={index,_->onModule(index)},onClientPdf:(String)->Unit={},onClientBackup:(String,String)->Unit={_,_->}) {
    var query by rememberSaveable(initialQuery){mutableStateOf(initialQuery)}
    var selected by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var section by rememberSaveable(selected){mutableStateOf("Resumo")}
    var editing by remember{mutableStateOf<Evaluation?>(null)}
    var error by remember{mutableStateOf("")}
    var backupClient by remember { mutableStateOf<String?>(null) }
    var backupPassword by remember { mutableStateOf("") }
    backupClient?.let { id ->
        AlertDialog(onDismissRequest={backupClient=null;backupPassword=""},
            title={Text("Backup individual criptografado")},
            text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("Inclui registros vinculados por identificador, inclusive notas arquivadas. Exclui cobranças conjuntas, modelos de treino e registros sem vínculo. Restaure em Configurações; conflitos não são sobrescritos.")
                OutlinedTextField(value=backupPassword,onValueChange={backupPassword=it},label={Text("Senha: pelo menos 8 caracteres")},
                    singleLine=true,visualTransformation=androidx.compose.ui.text.input.PasswordVisualTransformation())
            }},
            confirmButton={TextButton(enabled=backupPassword.length>=8,onClick={
                val password=backupPassword;backupPassword="";backupClient=null
                runCatching{onClientBackup(id,password)}.onFailure{error=it.message ?: "Falha no backup."}
            }){Text("Exportar")}},
            dismissButton={TextButton(onClick={backupClient=null;backupPassword=""}){Text("Cancelar")}})
    }
    val latest=WorkspaceRules.latest(repo.evaluations.toList())
    val client=latest.firstOrNull{it.studentId==selected}
    ActionError(error){error=""}
    editing?.let{profile->key(profile.id){
        var name by remember{mutableStateOf(profile.name)}
        var phone by remember{mutableStateOf(profile.phone)}
        var email by remember{mutableStateOf(profile.email)}
        WorkspaceEditor("Editar contato",{editing=null},{
            repo.save(ClientWorkspace.editContact(profile,name,phone,email));editing=null
        }) {
            Text("Atualiza o contato na avaliação mais recente. Avaliações anteriores e os registros vinculados mantêm seu histórico; o identificador do cliente não muda.")
            Field("Nome *",name,{name=it});Field("Telefone",phone,{phone=it});Field("E-mail",email,{email=it})
        }
    }}
    Screen {
        Heading("Clientes","Dossiê vinculado por identificador. Pessoas com o mesmo nome mantêm históricos separados.")
        if(client==null) {
            Button(onClick={onReevaluate("")}){Text("+ Novo cliente / avaliação")}
            Field("Buscar nome, e-mail ou telefone",query,{query=it})
            val found=latest.filter{WorkspaceRules.search(it.name+" "+it.email+" "+it.phone,query)}
            DataCard("Base de clientes","${latest.size} perfis · ${repo.evaluations.size} avaliações")
            if(found.isEmpty())EmptyWorkspace("Nenhum perfil encontrado. Cadastre uma avaliação para começar.")
            val labels=studentChoices(latest).toMap()
            PagedRecords(found,query,{it.studentId}){item->DataCard(labels[item.studentId] ?: item.name,"Última avaliação: ${item.date}\n${item.phone} · ${item.email}") {
                TextButton(onClick={selected=item.studentId}){Text("Abrir dossiê")}
                TextButton(onClick={onReevaluate(item.studentId)}){Text("Reavaliar")}
            }}
        } else {
            val evaluations=repo.evaluations.toList();val charges=repo.charges.toList();val appointments=repo.appointments.toList()
            val tasks=repo.tasks.toList();val training=repo.trainingSessions.toList();val habits=repo.habits.toList();val notes=repo.notes.toList()
            val data=remember(client.studentId,evaluations,charges,appointments,tasks,training,habits,notes){
                ClientWorkspace.snapshot(client.studentId,evaluations,charges,appointments,tasks,training,habits,notes)
            }
            TextButton(onClick={selected=""}){Text("Voltar aos clientes")}
            Heading(studentChoices(latest).toMap()[client.studentId] ?: client.name)
            ActionButtons {
                OutlinedButton(onClick={editing=client}){Text("Editar contato")}
                OutlinedButton(onClick={runCatching{onClientPdf(client.studentId)}.onFailure{error=it.message ?: "Falha ao gerar resumo."}}){Text("PDF do dossiê")}
                OutlinedButton(onClick={backupClient=client.studentId}){Text("Backup individual")}
                Button(onClick={onReevaluate(client.studentId)}){Text("Nova avaliação")}
            }
            PillChoices(listOf("Resumo","Avaliações","Formulários","Agenda","Tarefas","Notas"),section,{section=it})
            when(section) {
                "Resumo"->{
                    DataCard("Cadastro","${client.age?.let{"$it anos"} ?: "Idade não informada"} · ${client.sex}\n${client.email}\n${client.phone}")
                    DataCard("Avaliação atual","${client.date} · ${Metrics.oneDecimal(client.weightKg)} kg · IMC ${Metrics.oneDecimal(Metrics.bmi(client.weightKg,client.heightCm))}") {
                        TextButton(onClick={onClientModule(2,client.studentId)}){Text("Evolução")}
                        TextButton(onClick={onClientModule(11,client.studentId)}){Text("Metas")}
                        TextButton(onClick={onClientModule(5,client.studentId)}){Text("Análises")}
                        TextButton(onClick={onClientModule(15,client.studentId)}){Text("Relatórios deste cliente")}
                    }
                    val pending=data.charges.filter{!it.paid && !it.exempt}
                    DataCard("Financeiro vinculado","${data.charges.size} lançamentos · pendente ${money(pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})}\nCobranças conjuntas são contadas pelo valor integral, sem rateio.") {
                        TextButton(onClick={onClientModule(6,client.studentId)}){Text("Abrir cobranças")}
                    }
                    DataCard("Treinos e hábitos","${data.training.size} sessões · ${data.training.count{it.completed}} concluídas · ${data.habits.size} registros de hábitos") {
                        TextButton(onClick={onClientModule(12,client.studentId)}){Text("Treinos deste cliente")}
                        TextButton(onClick={onClientModule(13,client.studentId)}){Text("Hábitos deste cliente")}
                    }
                }
                "Formulários"->{
                    val forms=repo.intakes.filter{it.studentId==client.studentId}
                    TextButton(onClick={onClientModule(4,client.studentId)}){Text("Abrir formulários deste cliente")}
                    if(forms.isEmpty())EmptyWorkspace("Sem formulários vinculados.")
                    PagedRecords(forms,selected,{it.id}){DataCard(it.date,"${it.level} · ${it.answers.count{entry->entry.value.isNotBlank()}} respostas")}
                }
                "Avaliações"->PagedRecords(data.evaluations,selected,{it.id}){DataCard(it.date,"Peso: ${Metrics.oneDecimal(it.weightKg)} kg · Gordura: ${Metrics.oneDecimal(it.bodyFatPercent)}%\n${it.notes}")}
                "Agenda"->{
                    if(data.appointments.isEmpty())EmptyWorkspace("Sem atendimentos vinculados.")
                    PagedRecords(data.appointments,selected,{it.id}){DataCard("${it.date} · ${it.time}","${it.type} · ${it.status} · ${it.location}")}
                }
                "Tarefas"->{
                    TextButton(onClick={onClientModule(10,client.studentId)}){Text("Editar tarefas deste cliente")}
                    if(data.tasks.isEmpty())EmptyWorkspace("Sem tarefas vinculadas.")
                    PagedRecords(data.tasks,selected,{it.id}){DataCard(it.title,"${it.dueDate} · ${if(it.done)"Concluída" else "Pendente"} · ${it.category}")}
                }
                "Notas"->{
                    TextButton(onClick={onClientModule(14,client.studentId)}){Text("Editar notas deste cliente")}
                    if(data.notes.isEmpty())EmptyWorkspace("Sem notas ativas vinculadas.")
                    PagedRecords(data.notes,selected,{it.id}){DataCard(it.title,ProfessionalTools.notePreview(it,false))}
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(repo: AppRepository, createRequest: Int=0, initialDate:String="") {
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
        val occupied=repo.appointments.filter{it.status !in listOf("Cancelado","Faltou")}.groupingBy{it.date}.eachCount()
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

@Composable
fun GoalsScreen(repo: AppRepository,initialClientId:String="") {
    var filterClient by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
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
        ClientPicker(repo,filterClient,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterClient=id;filterName=name}
        Field("Buscar cliente",query,{query=it});PillChoices(listOf("Todas","Com metas","Sem metas","Prazo vencido"),filter,{filter=it})
        val found=WorkspaceRules.latest(repo.evaluations.toList()).filter{e->
            val has=e.goalWeightKg!=null || e.goalBodyFatPercent!=null
            (filterClient.isBlank() || e.studentId==filterClient) && WorkspaceRules.search(e.name,query) && when(filter){"Com metas"->has;"Sem metas"->!has;"Prazo vencido"->has && e.goalDate.isNotBlank() && e.goalDate<LocalDate.now().toString();else->true}}
        if(found.isEmpty())EmptyWorkspace("Nenhum cliente neste filtro.")
        PagedRecords(found,query+filter+filterClient,{it.studentId}){e->
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
fun ReportsScreen(repo: AppRepository, onBillingPdf:()->Unit,onExportCsv:(String,String)->Unit,initialClientId:String="") {
    var filterId by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
    fun linked(id:String)=filterId.isBlank() || id==filterId
    val context=LocalContext.current
    var exportKind by rememberSaveable{mutableStateOf("Avaliações")}
    var includeNoteBody by rememberSaveable{mutableStateOf(false)}
    var includeNames by rememberSaveable{mutableStateOf(false)}
    var period by rememberSaveable{mutableStateOf("30 dias")}
    val today=LocalDate.now()
    val start=when(period){"7 dias"->today.minusDays(6);"30 dias"->today.minusDays(29);"90 dias"->today.minusDays(89);else->LocalDate.MIN}
    fun inPeriod(value:String)=runCatching{LocalDate.parse(value) in start..today}.getOrDefault(false)
    val evals=repo.evaluations.filter{linked(it.studentId) && inPeriod(it.date)}
    val visits=repo.appointments.filter{linked(it.studentId) && inPeriod(it.date)}
    val completed=visits.count{it.status=="Concluído"}
    val charges=repo.charges.filter{(linked(it.studentId) || (filterId.isNotBlank() && it.secondStudentId==filterId)) && inPeriod(it.dueDate)}
    val paid=charges.filter{it.paid && !it.exempt}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
    val pending=charges.filter{!it.paid && !it.exempt}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
    val sessions=repo.trainingSessions.filter{!it.isTemplate && it.completed && linked(it.studentId) && inPeriod(it.date)}
    val habits=repo.habits.filter{linked(it.studentId) && inPeriod(it.date)}
    val summary="EvoluFísico · Resumo $period até $today\n${evals.size} avaliações\n${visits.size} atendimentos; $completed concluídos\nRecebido: ${money(paid)}; pendente: ${money(pending)} (por vencimento)\n${sessions.size} treinos concluídos; ${sessions.sumOf{it.durationMinutes.toLong()}} minutos\n${habits.size} registros de hábitos; ${habits.count{it.value>=it.target}} metas atingidas"
    var error by remember{mutableStateOf("")}
    ActionError(error){error=""}
    Screen {
        Heading("Relatórios","Indicadores do período. Financeiro agrupado pela data de vencimento, sem previsão de receita.")
        ClientPicker(repo,filterId,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterId=id;filterName=name}
        if(filterId.isNotBlank())Text("Resumo e CSV filtrados pelo identificador. Cobranças conjuntas entram pelo valor integral, sem rateio.")
        PillChoices(listOf("7 dias","30 dias","90 dias","Todo período"),period,{period=it})
        DataCard("Avaliações","${evals.size} registros · ${evals.map{it.studentId}.distinct().size} clientes avaliados")
        DataCard("Atendimentos","${visits.size} agendados no período · $completed concluídos · ${visits.count{it.status=="Faltou"}} faltas")
        DataCard("Financeiro por vencimento","Recebido: ${money(paid)}\nPendente: ${money(pending)}\n${charges.count{it.exempt}} cortesias") {
            TextButton(onClick=onBillingPdf){Text("PDF financeiro completo")}
        }
        DataCard("Treinamento","${sessions.size} sessões concluídas · ${sessions.sumOf{it.durationMinutes.toLong()}} minutos informados · ${sessions.sumOf{ProfessionalTools.completedVolume(it)}} kg·rep de carga externa registrada")
        DataCard("Hábitos","${habits.size} registros · ${habits.count{it.value>=it.target}} metas atingidas")
        DataCard("Organização atual","${repo.tasks.count{linked(it.studentId) && !it.done}} tarefas pendentes · ${repo.tasks.count{linked(it.studentId) && WorkspaceRules.overdue(it)}} atrasadas · ${repo.notes.count{linked(it.studentId)}} notas")
        OutlinedButton(onClick={runCatching{
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,summary)},"Compartilhar resumo"))
        }.onFailure{error="Não foi possível abrir o compartilhamento."}}){Text("Compartilhar resumo sem nomes")}
        Heading("Exportar planilha CSV","Escolha o conjunto de dados do período. O arquivo é salvo pelo seletor Android; textos são protegidos contra interpretação como fórmula em planilhas.")
        PillChoices(listOf("Avaliações","Cobranças","Treinos","Séries","Hábitos","Tarefas","Agenda","Notas"),exportKind,{exportKind=it;includeNoteBody=false})
        if(exportKind=="Notas")Row(verticalAlignment=Alignment.CenterVertically){Text("Incluir conteúdo das notas",Modifier.weight(1f));Switch(includeNoteBody,onCheckedChange={includeNoteBody=it})}
        Row(verticalAlignment=Alignment.CenterVertically){Text("Incluir nomes de clientes",Modifier.weight(1f));Switch(includeNames,onCheckedChange={includeNames=it})}
        Button(onClick={runCatching{
            val csv=ExportTables.build(exportKind,evals,charges,repo.trainingSessions.filter{linked(it.studentId) && inPeriod(it.date)},habits,
                repo.tasks.filter{linked(it.studentId) && inPeriod(it.dueDate)},includeNames,visits,repo.notes.filter{linked(it.studentId) && inPeriod(it.date)},includeNoteBody)
            onExportCsv("EvoluFisico_${exportKind}_${today}.csv",csv)
        }.onFailure{error=it.message ?: "Falha ao preparar CSV."}}){Text("Salvar CSV do período")}
        Heading("Distribuição por serviço")
        if(charges.isEmpty())Text("Sem cobranças neste período.")
        val services=charges.groupBy{it.service}.entries.sortedBy{it.key}
        PagedRecords(services,period,{it.key}){entry->DataCard(entry.key,"${entry.value.size} lançamentos · ${money(entry.value.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)})}")}
    }
}
