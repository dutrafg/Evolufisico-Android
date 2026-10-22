package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate
import java.util.UUID

private data class SetDraft(val id:String=UUID.randomUUID().toString(),val exercise:String="",val repetitions:String="10",val load:String="0",val done:Boolean=false) {
    fun parse():WorkoutSet=WorkoutSet(id,exercise.trim(),repetitions.toIntOrNull() ?: error("Repetições inválidas."),
        load.replace(',','.').toDoubleOrNull() ?: error("Carga inválida."),done)
}

@Composable
fun StructuredTrainingScreen(repo:AppRepository,createRequest:Int=0,onTrainingPdf:(TrainingSession)->Unit={},onExportCsv:(String,String)->Unit={_,_->}) {
    var editing by remember{mutableStateOf<TrainingSession?>(null)}
    var deleting by remember{mutableStateOf<TrainingSession?>(null)}
    var finishing by remember{mutableStateOf<TrainingSession?>(null)}
    var query by rememberSaveable{mutableStateOf("")}
    var view by rememberSaveable{mutableStateOf("Sessões")}
    var filter by rememberSaveable{mutableStateOf("Todos")}
    var filterClient by rememberSaveable{mutableStateOf("")}
    var filterClientName by rememberSaveable{mutableStateOf("")}
    var period by rememberSaveable{mutableStateOf("Todos os períodos")}
    var error by remember{mutableStateOf("")}
    LaunchedEffect(createRequest){if(createRequest>0){view="Sessões";editing=TrainingSession()}}
    ActionError(error){error=""}
    deleting?.let{item->ConfirmDelete(if(item.isTemplate)"modelo" else "treino",{deleting=null},{repo.delete(item)})}
    finishing?.let{item->AlertDialog(onDismissRequest={finishing=null},title={Text("Concluir treino?")},
        text={Text(if(item.sets.any{!it.done})"Há séries não marcadas. Você pode concluir a sessão; as séries manterão sua situação atual." else "A sessão será registrada como concluída.")},
        confirmButton={TextButton(onClick={runCatching{repo.save(item.copy(completed=true))}.onSuccess{finishing=null}.onFailure{error=it.message.orEmpty()}}){Text("Confirmar conclusão")}},
        dismissButton={TextButton(onClick={finishing=null}){Text("Continuar treino")}})}
    editing?.let{item->key(item.id){
        var title by remember{mutableStateOf(item.title)};var date by remember{mutableStateOf(item.date)}
        var duration by remember{mutableStateOf(item.durationMinutes.toString())};var text by remember{mutableStateOf(item.exercises)}
        var notes by remember{mutableStateOf(item.notes)};var effort by remember{mutableStateOf(item.effort?.toString().orEmpty())}
        var id by remember{mutableStateOf(item.studentId)};var student by remember{mutableStateOf(item.student)}
        val sets=remember{mutableStateListOf<SetDraft>().also{list->list.addAll(item.sets.map{SetDraft(it.id,it.exercise,it.repetitions.toString(),it.loadKg.toString(),it.done)})}}
        WorkspaceEditor(if(item.isTemplate)"Modelo de treino" else "Sessão de treino",{editing=null},{
            require(effort.isBlank() || effort.toIntOrNull() in 0..10){"Esforço deve ser de 0 a 10 ou vazio."}
            repo.save(item.copy(title=title.trim(),date=date,durationMinutes=duration.toIntOrNull() ?: 0,
                exercises=text,notes=notes,studentId=if(item.isTemplate)"" else id,student=if(item.isTemplate)"" else student,
                effort=if(item.isTemplate)null else effort.toIntOrNull(),sets=sets.map{it.parse().let{row->if(item.isTemplate)row.copy(done=false) else row}}));editing=null
        }) {
            Field("Nome *",title,{title=it})
            if(!item.isTemplate){ClientPicker(repo,id,student){newId,name->id=newId;student=name};DateField("Data",date,{date=it})}
            NumericField("Duração (min)",duration,{duration=it})
            Heading("Séries estruturadas","Cadastre uma linha por série. Carga externa em kg; zero significa sem carga externa informada.")
            sets.forEachIndexed{index,row->key(row.id){
                OutlinedCard(Modifier.fillMaxWidth()) {Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                    Text("Série ${index+1}",style=MaterialTheme.typography.titleSmall)
                    Field("Exercício *",row.exercise,{sets[index]=row.copy(exercise=it)})
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)){NumericField("Repetições",row.repetitions,{sets[index]=row.copy(repetitions=it)})}
                        Box(Modifier.weight(1f)){NumericField("Carga (kg)",row.load,{sets[index]=row.copy(load=it)},decimal=true)}
                    }
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        TextButton(enabled=index>0,onClick={val current=sets.removeAt(index);sets.add(index-1,current)}){Text("Subir")}
                        TextButton(enabled=index<sets.lastIndex,onClick={val current=sets.removeAt(index);sets.add(index+1,current)}){Text("Descer")}
                        TextButton(enabled=sets.size<200,onClick={sets.add(index+1,row.copy(id=UUID.randomUUID().toString(),done=false))}){Text("Duplicar")}
                        TextButton(onClick={sets.removeAt(index)}){Text("Remover")}
                    }
                }}
            }}
            OutlinedButton(enabled=sets.size<200,onClick={sets.add(SetDraft())}){Text("+ Adicionar série (${sets.size}/200)")}
            var bulkExercise by remember{mutableStateOf("")}
            var bulkCount by remember{mutableStateOf("3")}
            var bulkRepetitions by remember{mutableStateOf("10")}
            var bulkLoad by remember{mutableStateOf("0")}
            var bulkError by remember{mutableStateOf("")}
            Heading("Adicionar séries em lote","Preenche várias séries do mesmo exercício. Revise cada linha antes de salvar o treino.")
            Field("Exercício do lote",bulkExercise,{bulkExercise=it})
            NumericField("Quantidade (1–10)",bulkCount,{bulkCount=it})
            NumericField("Repetições por série",bulkRepetitions,{bulkRepetitions=it})
            NumericField("Carga externa por série (kg)",bulkLoad,{bulkLoad=it},decimal=true)
            OutlinedButton(enabled=sets.size<200,onClick={runCatching{
                val added=TrainingInsights.batch(bulkExercise,bulkCount.toIntOrNull() ?: 0,
                    bulkRepetitions.toIntOrNull() ?: 0,bulkLoad.replace(',','.').toDoubleOrNull() ?: Double.NaN,sets.size)
                sets.addAll(added.map{SetDraft(it.id,it.exercise,it.repetitions.toString(),it.loadKg.toString(),false)})
                bulkError=""
            }.onFailure{bulkError=it.message ?: "Lote inválido."}}){Text("Adicionar lote")}
            if(bulkError.isNotBlank())Text(bulkError,color=MaterialTheme.colorScheme.error)
            Field("Instruções complementares / treino antigo",text,{text=it},lines=4)
            if(!item.isTemplate)NumericField("Esforço percebido (0–10, opcional)",effort,{effort=it})
            Field("Observações",notes,{notes=it},lines=3)
        }
    }}
    Screen {
        Heading("Treinos","Séries, repetições, cargas externas e modelos reutilizáveis por cliente.")
        PillChoices(listOf("Sessões","Modelos","Evolução de cargas"),view,{view=it})
        if(view=="Evolução de cargas") {
            TrainingProgression(repo,onExportCsv)
            return@Screen
        }
        Button(onClick={editing=TrainingSession(isTemplate=view=="Modelos")}){Text(if(view=="Modelos")"+ Novo modelo" else "+ Nova sessão")}
        val sessions=repo.trainingSessions.filterNot{it.isTemplate}
        DataCard("Acompanhamento","${sessions.size} sessões · ${sessions.count{it.completed}} concluídas · ${repo.trainingSessions.count{it.isTemplate}} modelos")
        if(view=="Sessões")RestTimer()
        Field("Buscar nome, cliente ou exercício",query,{query=it})
        if(view=="Sessões") {
            ClientPicker(repo,filterClient,filterClientName,emptyLabel="Filtrar cliente (todos)",clearLabel="Todos os clientes"){id,name->filterClient=id;filterClientName=name}
            PillChoices(listOf("Todos os períodos","30 dias","90 dias"),period,{period=it})
            PillChoices(listOf("Todos","Planejados","Concluídos","Hoje"),filter,{filter=it})
        }
        val found=repo.trainingSessions.filter { item->item.isTemplate==(view=="Modelos") &&
            WorkspaceRules.search(item.title+" "+item.student+" "+item.exercises+" "+item.sets.joinToString(" "){it.exercise},query) &&
            (view=="Modelos" || ((filterClient.isBlank() || item.studentId==filterClient) &&
                (period=="Todos os períodos" || DashboardInsights.inPeriod(item.date,LocalDate.now(),if(period=="30 dias")30 else 90)))) &&
            (view=="Modelos" || when(filter){"Planejados"->!item.completed;"Concluídos"->item.completed;"Hoje"->item.date==LocalDate.now().toString();else->true})}
        if(found.isEmpty())EmptyWorkspace("Nenhum ${if(view=="Modelos")"modelo" else "treino"} neste filtro.")
        if(view=="Sessões" && found.isNotEmpty())Text("${found.size} sessões neste filtro · ${found.count{it.completed}} concluídas",style=MaterialTheme.typography.bodySmall)
        PagedRecords(found,query+view+filter+filterClient+period,{it.id}){item->
            var expanded by rememberSaveable(item.id){mutableStateOf(false)}
            DataCard(item.title,"${if(item.isTemplate)"Modelo reutilizável" else item.student.ifBlank{"Sem vínculo"}+" · "+item.date}\n${item.durationMinutes} min · ${item.sets.count{it.done}} / ${item.sets.size} séries · ${if(item.completed)"Concluído" else "Planejado"}\n${item.exercises.take(300)}") {
                if(item.isTemplate)TextButton(onClick={editing=ProfessionalTools.newSession(item)}){Text("Usar modelo")}
                else TextButton(onClick={if(item.completed)runCatching{repo.save(item.copy(completed=false))}.onFailure{error=it.message.orEmpty()} else finishing=item}){Text(if(item.completed)"Reabrir" else "Concluir")}
                TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Recolher" else "Detalhes / séries")}
                TextButton(enabled=!item.completed,onClick={editing=item}){Text(if(item.completed)"Reabra para editar" else "Editar")}
                TextButton(onClick={runCatching{onTrainingPdf(item)}.onFailure{error=it.message ?: "Falha ao gerar PDF."}}){Text("Exportar PDF")}
                if(!item.isTemplate)TextButton(onClick={editing=ProfessionalTools.newSession(item)}){Text("Duplicar sessão")}
                if(!item.isTemplate)TextButton(onClick={editing=ProfessionalTools.template(item)}){Text("Salvar como modelo")}
                TextButton(onClick={deleting=item}){Text("Excluir")}
            }
            if(expanded)OutlinedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                Text(item.notes.ifBlank{"Sem observações."})
                Text("Volume externo das séries marcadas: ${"%.1f".format(java.util.Locale.forLanguageTag("pt-BR"),ProfessionalTools.completedVolume(item))} kg·rep")
                if(item.sets.isNotEmpty())LinearProgressIndicator(progress={item.sets.count{it.done}.toFloat()/item.sets.size},modifier=Modifier.fillMaxWidth())
                item.sets.forEachIndexed{index,row->Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    if(!item.isTemplate)Checkbox(checked=row.done,enabled=!item.completed,onCheckedChange={checked->
                        runCatching{repo.save(item.copy(sets=item.sets.map{if(it.id==row.id)it.copy(done=checked) else it}))}.onFailure{error=it.message.orEmpty()}
                    })
                    Text("${index+1}. ${row.exercise} · ${row.repetitions} rep · ${row.loadKg} kg",Modifier.weight(1f))
                }}
                if(!item.isTemplate && !item.completed && item.sets.isNotEmpty() && item.sets.all{it.done})Text("Todas as séries estão marcadas. Use Concluir para encerrar a sessão.",color=MaterialTheme.colorScheme.primary)
            }}
        }
    }
}
