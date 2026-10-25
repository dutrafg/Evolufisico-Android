package br.com.evolufisico.nativo.domain

import kotlin.math.abs
import kotlin.math.round

/** EF68 transitions and events. Predictions remain disabled as in the reference. */
object HtmlMonitoring {
    data class Transition(val from:Long,val to:Long,val delta:Map<String,Double?>)
    data class Event(val type:String,val title:String,val text:String)
    val metrics=linkedMapOf<String,(HtmlAnalysis.Point)->Double?>("Gordura" to {it.fat},"Massa magra" to {it.lean},"Cintura" to {it.waist},"Peso" to {it.weight})
    fun transitions(input:List<HtmlAnalysis.Point>):List<Transition> = input.associateBy{it.day}.values.sortedBy{it.day}.zipWithNext().map{(a,b)->
        Transition(a.day,b.day,metrics.mapValues{(name,get)->val x=get(a);val y=get(b);if(b.day-a.day<7||x==null||y==null||name=="Gordura"&&(a.method==null||a.method!=b.method))null else round((y-x)*100)/100})
    }
    fun events(input:List<HtmlAnalysis.Point>):List<Event> {
        val rows=input.associateBy{it.day}.values.sortedBy{it.day};val ts=transitions(rows);val out=mutableListOf<Event>()
        if(rows.size==1)out.add(Event("baseline","Primeira avaliação","Avaliação inicial registrada; ainda não há série temporal para comparar."))
        ts.lastOrNull()?.let{t->metrics.keys.filter{it!="Peso"}.forEach{k->val d=t.delta[k];if(d!=null&&abs(d)>=.5){val favorable=if(k=="Massa magra")d>.05 else d<-.05;out.add(Event(if(favorable)"improvement" else "change","$k ${if(favorable)"melhorou" else "mudou"}","$k: ${if(d>0)"+" else ""}$d ${if(k=="Gordura")"p.p." else if(k=="Cintura")"cm" else "kg"} desde ${java.time.LocalDate.ofEpochDay(t.from)}."))}}}
        val longSeries=rows.size>=4&&rows.last().day-rows.first().day>=60
        val slopes=HtmlAnalysis.analyze(rows).per30
        metrics.keys.filter{it!="Peso"}.forEach{k->
            val ds=ts.mapNotNull{it.delta[k]}.filter{abs(it)>.05}
            val ratio=if(ds.size>=2)maxOf(ds.count{it>0},ds.count{it<0}).toDouble()/ds.size else null
            if(longSeries&&ratio!=null&&ratio>=.8)out.add(Event("consistency","Evolução consistente","$k apresenta direção consistente em ${round(ratio*100).toInt()}% das transições."))
            if(longSeries&&ratio!=null&&ratio<.6&&ds.size>=3)out.add(Event("oscillation","Evolução oscilante","$k alternou direção entre avaliações."))
            if(longSeries&&slopes[k]?.let{abs(round(it*100)/100)<.05}==true)out.add(Event("plateau","Platô aparente","$k está praticamente estável na tendência recente."))
            if(ts.size>=2){val a=ts[ts.lastIndex-1].delta[k];val b=ts.last().delta[k];if(a!=null&&b!=null&&abs(a)>.2&&abs(b)>.2&&(a>0)!=(b>0))out.add(Event("reversal","Mudança de direção","$k mudou de direção entre as duas últimas transições."))}
        }
        if(ts.any{it.to-it.from>90})out.add(Event("gap","Intervalo longo entre avaliações","Foi identificado pelo menos um intervalo superior a 90 dias na série."))
        return out
    }
}
