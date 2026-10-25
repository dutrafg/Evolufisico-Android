package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate
import kotlin.math.*

/** EF70 mathematics transcribed from the pinned HTML, not a clinical accuracy score. */
object HtmlAnalysis {
    data class Point(val day:Long,val fat:Double?,val lean:Double?,val waist:Double?,val weight:Double?,val method:String?,val pairedDelta:Double?=null)
    data class Result(val trajectory:Int?,val sufficiency:Int?,val per30:Map<String,Double?>,val consistency:Map<String,Double?>,val signals:List<String> = emptyList(),val actions:List<String> = emptyList())
    private fun valid(v:Double?,low:Double,high:Double)=v?.takeIf{it.isFinite()&&it in low..high}
    fun point(e:Evaluation):Point {
        val pollock=Anthropometry.pollock7(e.sex,e.age,e.weightKg,e.skinfolds)
        val f=valid(pollock?.fatPercent,3.0,65.0);val b=valid(e.bodyFatPercent,3.0,65.0)
        val w=valid(e.weightKg,20.0,350.0)
        val lean=if(f!=null && w!=null)valid(w*(1-f/100),10.0,250.0)?.takeIf{it<=w} else null
        return Point(LocalDate.parse(e.date).toEpochDay(),f?:b,lean,valid(e.waistCm?:e.perimeters["cintura"]?:e.perimeters["abdomen"],40.0,200.0),w,if(f!=null)"Pollock" else if(b!=null)"BIA" else null,if(f!=null&&b!=null)abs(f-b) else null)
    }
    fun analyze(input:List<Point>):Result {
        val rows=input.associateBy{it.day}.values.sortedBy{it.day}
        val metrics=linkedMapOf<String,(Point)->Double?>("Gordura" to {it.fat},"Massa magra" to {it.lean},"Cintura" to {it.waist},"Peso" to {it.weight})
        val methods=rows.filter{it.fat!=null}.mapNotNull{it.method}.distinct()
        val consistency=metrics.mapValues{(key,get)->
            val deltas=rows.zipWithNext().mapNotNull{(a,b)->
                val x=get(a);val y=get(b)
                if(b.day-a.day<7 || x==null||y==null || key=="Gordura"&&(a.method==null||a.method!=b.method))null else (y-x).takeIf{abs(it)>=0.05}
            }
            if(deltas.size<2)null else max(deltas.count{it>0},deltas.count{it<=0}).toDouble()/deltas.size
        }
        val slopes=metrics.mapValues{(key,get)->
            val points=rows.filter{get(it)!=null}
            if(points.size<3 || points.last().day-points.first().day<21 || key=="Gordura"&&methods.size>1)null
            else {
                val meanX=points.map{(it.day-points.first().day).toDouble()}.average();val meanY=points.map{get(it)!!}.average()
                val denominator=points.sumOf{((it.day-points.first().day)-meanX).pow(2)}
                if(denominator==0.0)null else points.sumOf{((it.day-points.first().day)-meanX)*(get(it)!!-meanY)}/denominator*30
            }
        }
        if(rows.size<3 || rows.last().day-rows.first().day<21)return Result(null,null,slopes,consistency)
        val lastGap=rows.last().day-rows[rows.lastIndex-1].day
        val parts=metrics.filterKeys{it!="Peso"}.mapNotNull{(key,get)->
            val first=get(rows.first());val last=get(rows.last());val weight=if(key=="Cintura")20 else 35
            if(first==null||last==null||first==0.0||key=="Gordura"&&methods.size>1)null else {
                val change=last-first;val favorable=if(key=="Massa magra")change>0 else change<0
                val shift=min(45.0,abs(change/first)*300)
                (50+(if(favorable)shift else -shift)) to weight
            }
        }
        val cs=consistency.filterKeys{it!="Peso"}.values.filterNotNull().takeIf{it.isNotEmpty()}?.average() ?: 0.5
        val range=if(rows.size>=5)100 else 80
        val score=if(lastGap<7||parts.isEmpty())null else floor(parts.sumOf{it.first*it.second}/parts.sumOf{it.second}*.75+cs*100*.15+range*.10+0.5).toInt().coerceIn(0,100)
        val n=min(100.0,if(rows.size>=6)100.0 else rows.size*16.7)
        val span=min(100.0,max(20.0,(rows.last().day-rows.first().day)/180.0*100))
        val complete=rows.count{it.fat!=null&&it.lean!=null}.toDouble()/rows.size*100
        var quality=floor(n*.45+span*.30+complete*.25+0.5).toInt()
        if(lastGap<7)quality=min(quality,40)
        if(methods.size>1)quality=min(quality,45)
        if((rows.last().pairedDelta ?: 0.0)>=8)quality=min(quality,40)
        val signals=mutableListOf<String>()
        if(lastGap>=7) {
            val a=rows[rows.lastIndex-1];val b=rows.last()
            fun delta(x:Double?,y:Double?)=if(x==null||y==null)null else y-x
            val dg=if(a.method!=null&&a.method==b.method)delta(a.fat,b.fat) else null
            val dm=delta(a.lean,b.lean);val dc=delta(a.waist,b.waist);val dp=delta(a.weight,b.weight)
            if(dg!=null&&dm!=null&&dc!=null&&dg<-.5&&dm>.3&&dc<-.5)signals.add("Padrão de recomposição corporal: gordura e cintura reduziram enquanto a massa magra aumentou na última transição.")
            if(dp!=null&&dg!=null&&abs(dp)<.8&&abs(dg)>.5)signals.add("Mudança de composição sem grande mudança de peso.")
            if(rows.size>=4&&rows.last().day-rows.first().day>=60)slopes.filterKeys{it!="Peso"}.forEach{(key,value)->if(value!=null&&abs(value)<.05)signals.add("Estabilidade recente: $key apresenta baixa inclinação na série recente.")}
        }
        val actions=mutableListOf<String>()
        if(quality<55)actions.add("Acompanhar novas avaliações para aumentar a confiança da tendência.")
        if(signals.any{it.startsWith("Estabilidade")})actions.add("Revisar a evolução recente deste indicador e observar a próxima avaliação.")
        consistency.forEach{(key,value)->if(value!=null&&value<.6&&rows.size>=4)actions.add("Observar a próxima transição porque $key apresenta oscilação.")}
        if(score!=null&&score>=75&&quality>=60)actions.add("Registrar o marco de evolução e manter o acompanhamento da trajetória atual.")
        return Result(score,quality.coerceIn(0,100),slopes,consistency,signals,actions.take(4))
    }
}
