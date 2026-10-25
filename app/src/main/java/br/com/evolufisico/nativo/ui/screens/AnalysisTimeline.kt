package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate

@Composable internal fun AnalysisTimeline(rows:List<HtmlAnalysis.Point>) {
    var metric by remember{mutableStateOf("Peso")}
    Heading("Série temporal e transições · EF68")
    PillChoices(HtmlMonitoring.metrics.keys.toList(),metric,{metric=it})
    val get=HtmlMonitoring.metrics.getValue(metric)
    val unit=when(metric){"Gordura"->"%";"Cintura"->"cm";else->"kg"}
    val values=rows.mapNotNull(get)
    val color=MaterialTheme.colorScheme.primary
    val axis=MaterialTheme.colorScheme.outlineVariant
    val mixed=metric=="Gordura"&&rows.filter{it.fat!=null}.map{it.method}.distinct().size>1
    if(mixed)Text("Métodos diferentes: pontos são exibidos sem conectar a curva de gordura.")
    if(values.isNotEmpty()&&rows.size>1){
        val description=rows.joinToString("; "){"${LocalDate.ofEpochDay(it.day)}: ${Metrics.oneDecimal(get(it))} $unit"}
        Canvas(Modifier.fillMaxWidth().height(160.dp).semantics{contentDescription="$metric. $description"}){
            val padding=12.dp.toPx();val lo=values.min();val span=(values.max()-lo).takeIf{it>0} ?: 1.0
            val days=(rows.last().day-rows.first().day).coerceAtLeast(1)
            fun xy(p:HtmlAnalysis.Point,v:Double)=Offset(padding+((p.day-rows.first().day).toFloat()/days)*(size.width-2*padding),size.height-padding-((v-lo)/span*(size.height-2*padding)).toFloat())
            drawLine(axis,Offset(padding,size.height-padding),Offset(size.width-padding,size.height-padding),1.dp.toPx())
            if(!mixed)rows.zipWithNext().forEach{(a,b)->val va=get(a);val vb=get(b);if(va!=null&&vb!=null)drawLine(color,xy(a,va),xy(b,vb),2.dp.toPx())}
            rows.forEach{p->get(p)?.let{drawCircle(color,4.dp.toPx(),xy(p,it))}}
        }
        Text("Valores observados; distância horizontal proporcional aos dias. Não há projeção futura.",style=MaterialTheme.typography.bodySmall)
    }else Text("Sem medidas válidas para desenhar esta série.")
    rows.forEach{p->Text("${LocalDate.ofEpochDay(p.day)} · ${Metrics.oneDecimal(get(p))} $unit${if(metric=="Gordura")" · ${p.method ?: "Sem método"}" else ""}")}
    HtmlMonitoring.transitions(rows).forEach{t->val d=t.delta[metric];Text("${LocalDate.ofEpochDay(t.from)} → ${LocalDate.ofEpochDay(t.to)}: "+(d?.let{"${if(it>0)"+" else ""}${Metrics.oneDecimal(it)} ${if(metric=="Gordura")"p.p." else unit}"} ?: "Sem comparação válida (dados, intervalo ou método)."),style=MaterialTheme.typography.bodySmall)}
    HtmlMonitoring.events(rows).forEach{e->DataCard(e.title,e.text)}
}
