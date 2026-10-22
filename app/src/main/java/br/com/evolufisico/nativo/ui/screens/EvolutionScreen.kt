package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.Metrics
import br.com.evolufisico.nativo.domain.EvaluationTimeline
import br.com.evolufisico.nativo.domain.GoalTracking
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private data class Metric(val name:String,val unit:String,val get:(Evaluation)->Double?)
private val metrics=listOf(
    Metric("Peso","kg",{it.weightKg}),
    Metric("IMC","kg/m²",{Metrics.bmi(it.weightKg,it.heightCm)}),
    Metric("Gordura aferida","%",{it.bodyFatPercent}),
    Metric("Gordura Pollock 7","%",{Anthropometry.pollock7(it.sex,it.age,it.weightKg,it.skinfolds)?.fatPercent}),
    Metric("Massa gorda","kg",{Metrics.fatMassKg(it.weightKg,it.bodyFatPercent)}),
    Metric("Massa magra","kg",{it.weightKg?.let{w->Metrics.fatMassKg(w,it.bodyFatPercent)?.let{fat->w-fat}}}),
    Metric("Cintura","cm",{it.waistCm}),
    Metric("Quadril","cm",{it.hipCm}),
    Metric("RCQ","",{Metrics.waistHipRatio(it.waistCm,it.hipCm)}),
    Metric("Braço direito","cm",{it.perimeters["braco_dir"]}),
    Metric("Coxa direita","cm",{it.perimeters["coxa_dir"]}),
    Metric("Aderência","%",{Metrics.adherence(it.trainingExperienced,it.trainingAdherence,it.dietExperienced,it.dietAdherence)})
)

@Composable
fun EvolutionScreen(repo:AppRepository,onPdf:(String,List<Evaluation>)->Unit,initialClientId:String="")=Screen {
    Heading("Evolução","Gráficos por indicador, linha do tempo e comparações entre avaliações do mesmo aluno.")
    val students=repo.evaluations.distinctBy{it.studentId}
    var selected by rememberSaveable(initialClientId) {mutableStateOf(initialClientId)}
    LaunchedEffect(students.map { it.studentId }) {
        if(students.none { it.studentId==selected }) selected=students.firstOrNull()?.studentId.orEmpty()
    }
    if(students.isEmpty()) Text("Nenhuma avaliação cadastrada.")
    val choices=studentChoices(students)
    if(choices.isNotEmpty())PillChoices(choices.map{it.second},choices.firstOrNull{it.first==selected}?.second.orEmpty()){
        label -> selected=choices.firstOrNull{it.second==label}?.first.orEmpty()
    }
    val history=br.com.evolufisico.nativo.domain.EvaluationTimeline.ordered(repo.evaluations.filter{it.studentId==selected})
    if(history.isNotEmpty()){
        val name=history.last().name
        DataCard("Histórico de $name","${history.size} avaliações de ${history.first().date} a ${history.last().date}") {
            TextButton(onClick={onPdf(name,history)}){Text("PDF completo")}
        }
        val latest=history.last()
        if(latest.goalWeightKg!=null || latest.goalBodyFatPercent!=null) {
            Heading("Metas de ${latest.name}")
            val goalDate=latest.goalDate.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            latest.goalWeightKg?.let { target -> DataCard("Meta de peso · $target kg",
                GoalTracking.summary("Peso",latest.goalWeightBaselineKg,latest.weightKg,target,"kg",goalDate)+
                    if(latest.goalNotes.isBlank()) "" else "\n${latest.goalNotes}") }
            latest.goalBodyFatPercent?.let { target -> DataCard("Meta de gordura · $target%",
                GoalTracking.summary("Gordura corporal",latest.goalBodyFatBaselinePercent,latest.bodyFatPercent,target,"p.p.",goalDate)) }
        }
        if(history.size<2)Text("Cadastre ao menos mais uma avaliação para realizar comparações.")
        var metricIndex by remember {mutableIntStateOf(0)}
        PillChoices(metrics.map{it.name},metrics[metricIndex].name){label->metricIndex=metrics.indexOfFirst{it.name==label}.coerceAtLeast(0)}
        val metric=metrics[metricIndex]
        val series=history.mapNotNull{e->metric.get(e)?.takeIf{it.isFinite()}?.let{v->Triple(e.date,v,e.id)}}
        DataCard(metric.name, "${series.size} medições disponíveis" + if(series.isNotEmpty()) " • Última: ${Metrics.oneDecimal(series.last().second)} ${metric.unit}" else "")
        if(series.size>=2){
            NativeTrendChart(series.map{it.first to it.second})
            Text("${series.first().first} → ${series.last().first} • eixo horizontal proporcional aos dias",
                style=MaterialTheme.typography.bodySmall)
            val delta=series.last().second-series.first().second
            Text("Variação no período: ${if(delta>0)"+" else ""}${Metrics.oneDecimal(delta)} ${metric.unit}")
            Text("De ${series.first().first} até ${series.last().first}; não são estimados valores faltantes.",style=MaterialTheme.typography.bodySmall)
        } else Text("São necessárias duas medições válidas deste indicador.")
        HorizontalDivider()
        Heading("Comparar duas avaliações")
        val evaluationChoices=EvaluationTimeline.choices(history)
        var start by remember(selected,history.map { it.id }){mutableStateOf(history.first().id)}
        var end by remember(selected,history.map { it.id }){mutableStateOf(history.last().id)}
        PillChoices(evaluationChoices.map{it.label},evaluationChoices.firstOrNull{it.id==start}?.label.orEmpty()){
            label->start=evaluationChoices.first{it.label==label}.id
        }
        Text("Avaliação final",style=MaterialTheme.typography.titleSmall)
        PillChoices(evaluationChoices.map{it.label},evaluationChoices.firstOrNull{it.id==end}?.label.orEmpty()){
            label->end=evaluationChoices.first{it.label==label}.id
        }
        val earlier=history.firstOrNull{it.id==start};val later=history.firstOrNull{it.id==end}
        if(earlier!=null&&later!=null){
            val days=runCatching{ChronoUnit.DAYS.between(LocalDate.parse(earlier.date),LocalDate.parse(later.date))}.getOrNull()
            if(days!=null && days<0) Text("Selecione uma avaliação final igual ou posterior à inicial.",
                color=MaterialTheme.colorScheme.error)
            else Text("Intervalo: ${days ?: "—"} dias")
            metrics.forEach{m->
                val a=m.get(earlier);val b=m.get(later)
                if(a!=null || b!=null) DataCard(m.name,
                    "${Metrics.oneDecimal(a)} → ${Metrics.oneDecimal(b)} ${m.unit}   |   Δ ${Metrics.oneDecimal(if(a!=null&&b!=null)b-a else null)} ${m.unit}")
            }
        }
        HorizontalDivider()
        Heading("Linha do tempo")
        history.asReversed().forEach{e->DataCard(e.date,
            "Peso ${Metrics.oneDecimal(e.weightKg)} kg • Gordura ${Metrics.oneDecimal(e.bodyFatPercent)} % • Cintura ${Metrics.oneDecimal(e.waistCm)} cm • ${e.phase}")}
    }
}

@Composable
private fun NativeTrendChart(series:List<Pair<String,Double>>){
    val points=EvaluationTimeline.points(series)
    if(points.size<2)return
    val line=MaterialTheme.colorScheme.primary
    val border=MaterialTheme.colorScheme.outlineVariant
    Canvas(Modifier.fillMaxWidth().height(195.dp).padding(18.dp)) {
        val pts=points.map { Offset(size.width*it.x,size.height*it.y) }
        drawLine(border,Offset(0f,0f),Offset(size.width,0f),strokeWidth=2f)
        drawLine(border,Offset(0f,size.height),Offset(size.width,size.height),strokeWidth=2f)
        pts.zipWithNext().forEach{(a,b)->drawLine(line,a,b,strokeWidth=4f,cap=StrokeCap.Round)}
        pts.forEach{drawCircle(line,radius=5f,center=it)}
    }
}
