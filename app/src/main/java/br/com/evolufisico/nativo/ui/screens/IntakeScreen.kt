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

private data class Question(val id:String,val text:String)
private val common = listOf(
    Question("objective", "Principal objetivo e expectativas"),
    Question("history", "Doenças diagnosticadas relatadas"),
    Question("medication", "Medicamentos e tratamento em andamento (relato)"),
    Question("pain", "Dor, lesões, cirurgias e restrições"),
    Question("medical_clearance", "Recebeu alguma recomendação ou restrição médica para atividades físicas?"),
    Question("symptoms", "Já apresentou dor no peito, desmaios ou falta de ar incomum ao esforço?"),
    Question("sleep", "Média de horas de sono, qualidade e horários"),
    Question("work", "Profissão, rotina e tempo sentado"),
    Question("nutrition", "Rotina alimentar e acompanhamento nutricional"),
    Question("hydration", "Hidratação habitual"),
    Question("stress", "Estresse, descanso e recuperação"),
    Question("time", "Dias e horários disponíveis para atividade"),
    Question("limitations", "Limitações de mobilidade e equilíbrio"))
/** Original PAR-Q inspired yes/no screening, collected as a self-report, not a clearance. */
private val parq = listOf(
    Question("parq_1", "Profissional de saúde já recomendou que faça atividade física apenas com orientação?"),
    Question("parq_2", "Sente dor no peito durante esforço físico?"),
    Question("parq_3", "Sentiu dor no peito em repouso no último mês?"),
    Question("parq_4", "Apresenta tontura que cause perda de equilíbrio ou episódios de desmaio?"),
    Question("parq_5", "Tem condição óssea/articular que possa piorar com exercício?"),
    Question("parq_6", "Usa medicamentos para pressão arterial ou condição cardíaca?"),
    Question("parq_7", "Conhece outro motivo de saúde para não iniciar exercícios sem orientação?")
)
private val starter = listOf(
    Question("before_exercise", "Já praticou exercícios anteriormente?"),
    Question("activity", "Como é seu nível atual de atividade?"),
    Question("barriers", "Quais dificuldades impedem começar?"),
    Question("preferences", "Atividades que gostaria de experimentar"),
    Question("confidence", "Grau de confiança em iniciar uma rotina (0–10)"),
    Question("support", "Apoio familiar e condições de acesso ao local"),
    Question("starting_goal", "Primeira meta pequena e viável"))
private val intermediate = listOf(
    Question("experience", "Há quanto tempo pratica exercícios?"),
    Question("frequency", "Frequência e duração dos treinos atuais"),
    Question("split", "Divisão atual e grupos musculares"),
    Question("intensity", "Intensidade percebida e progressão de cargas"),
    Question("plateau", "Dificuldades, platôs e faltas recorrentes"),
    Question("exercise_history", "Lesões ou exercícios que causam desconforto"),
    Question("recovery", "Recuperação entre as sessões"),
    Question("short_term", "Metas para as próximas 8–12 semanas"))
private val advanced = listOf(
    Question("modality", "Modalidade esportiva e tempo de prática"),
    Question("periodization", "Periodização, macrociclo e mesociclo atual"),
    Question("volume", "Volume semanal de treino por grupo muscular"),
    Question("load", "Carga, RPE/RIR e progressões recentes"),
    Question("test", "Testes de desempenho, referências e datas"),
    Question("competition", "Calendário competitivo e metas de performance"),
    Question("fatigue", "Monitoramento de fadiga, sono e dor muscular"),
    Question("injury", "Histórico de afastamento e reabilitação"),
    Question("resources", "Equipamentos, recursos e restrições de agenda"),
    Question("strategy", "Estratégia de recuperação e ajustes planejados"))

@Composable
fun IntakeScreen(repo:AppRepository) {
    var editing by remember { mutableStateOf<Intake?>(null) }
    var deleting by remember { mutableStateOf<Intake?>(null) }
    var query by remember { mutableStateOf("") }
    if (deleting!=null) AlertDialog(onDismissRequest={deleting=null},title={Text("Excluir formulário?")},
        text={Text("A exclusão é permanente.")},
        confirmButton={TextButton(onClick={deleting?.let(repo::delete);deleting=null}){Text("Excluir")}},
        dismissButton={TextButton(onClick={deleting=null}){Text("Cancelar")}})
    if (editing!=null) IntakeEditor(editing!!,repo,onSave={repo.save(it);editing=null},onCancel={editing=null})
    else Screen {
        Heading("Formulários","Anamneses completas para iniciantes, intermediários e avançados, vinculadas aos perfis.")
        Button(onClick={editing=Intake()}){Text("+ Nova anamnese")}
        Field("Filtrar aluno",query,{query=it})
        val items=repo.intakes.filter{it.student.contains(query,true)}
        if(items.isEmpty())Text("Nenhum formulário encontrado.")
        items.forEach { item ->
            DataCard(item.student,"${item.level} • ${item.date} • ${item.answers.count{it.value.isNotBlank()}} respostas • ${item.objective}") {
                TextButton(onClick={editing=item}){Text("Abrir/editar")}
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
        Text("Vincular a aluno existente",style=MaterialTheme.typography.titleSmall)
        val students=repo.evaluations.distinctBy{it.studentId}
        val choices=studentChoices(students)
        if(choices.isNotEmpty())PillChoices(choices.map{it.second},choices.firstOrNull{it.first==studentId}?.second.orEmpty(),{chosen ->
            val student=students.firstOrNull{it.studentId==choices.firstOrNull{choice->choice.second==chosen}?.first}
            if(student!=null){name=student.name;studentId=student.studentId}
        })
        Field("Nome completo *",name,{name=it})
        DateField("Data",date,{date=it})
        PillChoices(listOf("Iniciante","Intermediário","Avançado"),level,{level=it})
        Text("Informações gerais e saúde",style=MaterialTheme.typography.titleMedium)
        common.forEach{q->Field(q.text,answers[q.id].orEmpty(),{answers[q.id]=it},lines=2)}
        HorizontalDivider()
        Text("Triagem de prontidão • PAR-Q (autodeclaração)",style=MaterialTheme.typography.titleMedium)
        parq.forEach { q ->
            Text(q.text,style=MaterialTheme.typography.bodyMedium)
            PillChoices(listOf("Não respondido", "Não", "Sim"),answers[q.id].orEmpty().ifBlank { "Não respondido" }) {
                answers[q.id]=if(it=="Não respondido") "" else it
            }
        }
        val alerts=parq.count { answers[it.id]=="Sim" }
        if(alerts>0)Text("Há $alerts resposta(s) positiva(s): encaminhe para avaliação de profissional de saúde antes de decidir sobre a prática. Este formulário não concede aptidão.",
            color=MaterialTheme.colorScheme.error)
        HorizontalDivider()
        Text("Questões de nível $level",style=MaterialTheme.typography.titleMedium)
        val questions=when(level){"Iniciante"->starter;"Intermediário"->intermediate;else->advanced}
        questions.forEach{q->Field(q.text,answers[q.id].orEmpty(),{answers[q.id]=it},lines=2)}
        Field("Observações e encaminhamentos",observations,{observations=it},lines=3)
        if(error.isNotEmpty())Text(error,color=MaterialTheme.colorScheme.error)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
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
