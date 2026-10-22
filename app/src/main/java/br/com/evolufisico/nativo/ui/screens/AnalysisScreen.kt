package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.AssessmentChecks
import br.com.evolufisico.nativo.domain.Metrics

private val explain=linkedMapOf(
    "IMC" to "Índice de massa corporal: peso dividido pela altura em metros ao quadrado. Não mede diretamente a gordura.",
    "RCQ" to "Relação entre a circunferência da cintura e a do quadril. Depende da técnica de mensuração.",
    "Gordura aferida" to "Percentual introduzido a partir da bioimpedância ou outro equipamento. Não é estimado a partir do IMC.",
    "Pollock 7" to "Estimativa por sete dobras cutâneas, sexo e idade (Jackson & Pollock/Siri). Não equivale a um diagnóstico.",
    "Aderência" to "Média dos percentuais válidos somente quando treino e/ou dieta são aplicáveis. Quem nunca treinou ou nunca seguiu dieta não recebe zero por isso.",
    "Variação" to "Diferença entre a primeira e a última medição válida do mesmo aluno. É mostrada sem atribuir causa clínica.",
    "Completude" to "Quantidade de métricas com registros utilizáveis. Dados ausentes não são convertidos em zero."
)

@Composable
private fun HelpMetric(label:String,value:String,explanation:String) {
    var open by remember {mutableStateOf(false)}
    if(open)AlertDialog(onDismissRequest={open=false},title={Text(label)},text={Text(explanation)},
        confirmButton={TextButton(onClick={open=false}){Text("Entendi")}})
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=7.dp),
            verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(label,style=MaterialTheme.typography.labelLarge)
                Text(value,style=MaterialTheme.typography.titleMedium)
            }
            // Fixed square tap target: doesn't stretch on 320dp phones.
            IconButton(modifier=Modifier.size(44.dp),onClick={open=true}) {
                Icon(Icons.AutoMirrored.Outlined.HelpOutline,contentDescription="O que significa $label?",modifier=Modifier.size(21.dp))
            }
        }
    }
}
@Composable
fun AnalysisScreen(repo:AppRepository)=Screen {
    Heading("Análises","Painel auditável: resultados, tendência, adesão e explicação de cada indicador.")
    val pupils=repo.evaluations.distinctBy{it.studentId}
    var selected by remember{mutableStateOf("")}
    if(selected.isEmpty()&&pupils.isNotEmpty())selected=pupils.first().studentId
    val choices=studentChoices(pupils)
    if(choices.isNotEmpty())PillChoices(choices.map{it.second},choices.firstOrNull{it.first==selected}?.second.orEmpty()){
        label->selected=choices.firstOrNull{it.second==label}?.first.orEmpty()
    }
    val history=repo.evaluations.filter{it.studentId==selected}.sortedBy{it.date}
    if(history.isEmpty())Text("Nenhuma análise disponível. Cadastre uma avaliação primeiro.")
    else {
        val last=history.last();val first=history.first()
        val alerts=AssessmentChecks.review(last,history.dropLast(1).lastOrNull())
        DataCard(last.name,"Última avaliação ${last.date} • ${history.size} registros")
        val bmi=Metrics.bmi(last.weightKg,last.heightCm)
        val ratio=Metrics.waistHipRatio(last.waistCm,last.hipCm)
        val fold=Anthropometry.pollock7(last.sex,last.age,last.weightKg,last.skinfolds)
        val adherence=Metrics.adherence(last.trainingExperienced,last.trainingAdherence,
            last.dietExperienced,last.dietAdherence)
        HelpMetric("IMC",Metrics.oneDecimal(bmi),explain.getValue("IMC"))
        HelpMetric("RCQ",Metrics.oneDecimal(ratio),explain.getValue("RCQ"))
        HelpMetric("Gordura aferida","${Metrics.oneDecimal(last.bodyFatPercent)} %",explain.getValue("Gordura aferida"))
        HelpMetric("Pollock 7","${Metrics.oneDecimal(fold?.fatPercent)} %",explain.getValue("Pollock 7"))
        HelpMetric("Aderência",adherence?.let{"${Metrics.oneDecimal(it)} %"}?:"Não avaliável",explain.getValue("Aderência"))
        if(!last.trainingExperienced)Text("Treino: não se aplica — aluno nunca treinou.")
        if(!last.dietExperienced)Text("Dieta: não se aplica — aluno nunca seguiu dieta.")
        if(history.size>1){
            fun diff(a:Double?,b:Double?):String=
                if(a==null||b==null) "Sem duas medições comparáveis" else
                    "${if(b-a>0)"+" else ""}${Metrics.oneDecimal(b-a)}"
            HorizontalDivider()
            Heading("Mudanças desde a primeira avaliação")
            HelpMetric("Variação de peso","${diff(first.weightKg,last.weightKg)} kg",explain.getValue("Variação"))
            HelpMetric("Variação de cintura","${diff(first.waistCm,last.waistCm)} cm",explain.getValue("Variação"))
            HelpMetric("Variação de gordura aferida","${diff(first.bodyFatPercent,last.bodyFatPercent)} p.p.",explain.getValue("Variação"))
        }
        val completeness=listOf(last.weightKg,last.heightCm,last.waistCm,last.hipCm,
            last.bodyFatPercent,fold?.fatPercent,adherence).count{it!=null}
        HelpMetric("Completude","$completeness de 7 indicadores",explain.getValue("Completude"))
        HorizontalDivider()
        Text("Conferência de consistência",style=MaterialTheme.typography.titleMedium)
        if(alerts.isEmpty())Text("Nenhum alerta de consistência entre os dados disponíveis.")
        else alerts.forEach { Text("• ${it.explanation}",style=MaterialTheme.typography.bodySmall,
            color=MaterialTheme.colorScheme.error) }
        Text("Parecer do profissional",style=MaterialTheme.typography.titleMedium)
        Text(last.notes.ifBlank{"Nenhum parecer registrado."})
        Text("Limites: medidas e estimativas precisam de revisão por profissional habilitado. Não há interpretação diagnóstica automática.",
            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
