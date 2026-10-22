package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Intake
import java.time.LocalDate
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.evolufisico.nativo.domain.*

@Composable
fun IntakeScreen(repo:AppRepository,initialClientId:String="",onPdf:(Intake)->Unit={}) {
    var editing by remember { mutableStateOf<Intake?>(null) }
    var deleting by remember { mutableStateOf<Intake?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var filterId by rememberSaveable(initialClientId){mutableStateOf(initialClientId)}
    var filterName by rememberSaveable(initialClientId){mutableStateOf(WorkspaceRules.latest(repo.evaluations.toList()).firstOrNull{it.studentId==initialClientId}?.name.orEmpty())}
    var error by remember{mutableStateOf("")}
    ActionError(error){error=""}
    deleting?.let{item->ConfirmDelete("formulário",{deleting=null},{repo.delete(item)})}
    if (editing!=null) IntakeEditor(editing!!,repo,onSave={repo.save(it);editing=null},onCancel={editing=null})
    else Screen {
        Heading("Formulários","Anamneses completas para iniciantes, intermediários e avançados, vinculadas aos perfis.")
        Button(onClick={editing=Intake(studentId=filterId,student=filterName)}){Text("+ Nova anamnese")}
        ClientPicker(repo,filterId,filterName,emptyLabel="Todos os clientes",clearLabel="Todos os clientes"){id,name->filterId=id;filterName=name}
        Field("Buscar cliente, nível ou objetivo",query,{query=it})
        val items=repo.intakes.filter{(filterId.isBlank() || it.studentId==filterId) && WorkspaceRules.search(it.student+" "+it.level+" "+it.objective,query)}
        if(items.isEmpty())Text("Nenhum formulário encontrado.")
        PagedRecords(items,query+filterId,{it.id}) { item ->
            DataCard(item.student,"${item.level} • ${item.date} • ${item.answers.count{it.value.isNotBlank()}} respostas • ${item.objective}") {
                TextButton(onClick={editing=item}){Text("Abrir/editar")}
                TextButton(onClick={runCatching{onPdf(item)}.onFailure{error=it.message ?: "Falha ao gerar PDF."}}){Text("Exportar PDF")}
                TextButton(onClick={deleting=item}){Text("Excluir")}
            }
        }
    }
}

@Composable
private fun IntakeEditor(original:Intake,repo:AppRepository,onSave:(Intake)->Unit,onCancel:()->Unit) {
    TrackEditing()
    var discardRequested by remember(original.id) { mutableStateOf(false) }
    BackHandler { discardRequested = true }
    if (discardRequested) AlertDialog(
        onDismissRequest = { discardRequested = false },
        title = { Text("Descartar alterações?") },
        text = { Text("Os dados desta edição que não foram salvos serão perdidos.") },
        confirmButton = { TextButton(onClick = onCancel) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { discardRequested = false }) { Text("Continuar editando") } }
    )
    var name by remember(original.id){ mutableStateOf(original.student) }
    var studentId by remember(original.id){ mutableStateOf(original.studentId) }
    var level by remember(original.id){ mutableStateOf(original.level) }
    var date by remember(original.id){ mutableStateOf(original.date) }
    var observations by remember(original.id){ mutableStateOf(original.observations) }
    var error by remember {mutableStateOf("")}
    val answers=remember(original.id){mutableStateMapOf<String,String>().apply{putAll(original.answers)}}
    Screen {
        Heading("Anamnese $level","Dados declarados pelo aluno. Sinais de alerta exigem avaliação profissional antes de prescrever atividade.")
        ClientPicker(repo,studentId,name){id,student->studentId=id;name=student}
        Field("Nome completo *",name,{name=it;studentId=""})
        Text("Ao digitar um nome manualmente, o vínculo é removido. Use o seletor para escolher um cliente por identificador.",style=MaterialTheme.typography.bodySmall)
        DateField("Data",date,{date=it})
        PillChoices(listOf("Iniciante","Intermediário","Avançado"),level,{level=it})
        Text("Informações gerais e saúde",style=MaterialTheme.typography.titleMedium)
        IntakeQuestionnaire.common.forEach{q->Field(q.text,answers[q.id].orEmpty(),{answers[q.id]=it},lines=2)}
        HorizontalDivider()
        Text("Triagem de prontidão • PAR-Q (autodeclaração)",style=MaterialTheme.typography.titleMedium)
        IntakeQuestionnaire.parq.forEach { q ->
            Text(q.text,style=MaterialTheme.typography.bodyMedium)
            PillChoices(listOf("Não respondido", "Não", "Sim"),answers[q.id].orEmpty().ifBlank { "Não respondido" }) {
                answers[q.id]=if(it=="Não respondido") "" else it
            }
        }
        val alerts=IntakeQuestionnaire.parq.count { answers[it.id]=="Sim" }
        if(alerts>0)Text("Há $alerts resposta(s) positiva(s): encaminhe para avaliação de profissional de saúde antes de decidir sobre a prática. Este formulário não concede aptidão.",
            color=MaterialTheme.colorScheme.error)
        HorizontalDivider()
        Text("Questões de nível $level",style=MaterialTheme.typography.titleMedium)
        val questions=when(level){"Iniciante"->IntakeQuestionnaire.starter;"Intermediário"->IntakeQuestionnaire.intermediate;else->IntakeQuestionnaire.advanced}
        questions.forEach{q->Field(q.text,answers[q.id].orEmpty(),{answers[q.id]=it},lines=2)}
        Field("Observações e encaminhamentos",observations,{observations=it},lines=3)
        if(error.isNotEmpty())Text(error,color=MaterialTheme.colorScheme.error)
        ActionButtons {
            OutlinedButton(onClick={discardRequested=true}){Text("Cancelar")}
            Button(onClick={
                error=when {
                    name.isBlank()->"Informe o nome do aluno."
                    runCatching{LocalDate.parse(date)}.isFailure->"Data inválida."
                    else->""
                }
                if(error.isEmpty())runCatching { onSave(original.copy(student=name.trim(),studentId=studentId,
                    date=date,level=level,objective=answers["objective"].orEmpty(),
                    healthHistory=answers["history"].orEmpty(),routine=answers["work"].orEmpty(),
                    observations=observations,answers=answers.toMap())) }.onFailure { error=it.message ?: "Não foi possível salvar. Tente novamente." }
            }){Text("Salvar formulário")}
        }
    }
}
