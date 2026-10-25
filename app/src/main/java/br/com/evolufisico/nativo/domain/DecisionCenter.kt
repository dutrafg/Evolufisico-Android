package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate

object DecisionCenter {
    data class WeightChange(val evaluation:Evaluation,val delta:Double)
    data class Summary(val clients:Int,val records:Int,val overdue:List<Evaluation>,val upcoming:List<Evaluation>,val missing:Int,val withHistory:Int,val changes:List<WeightChange>)
    private fun valid(value:String)=runCatching{LocalDate.parse(value)}.getOrNull()
    fun summarize(rows:List<Evaluation>,today:LocalDate):Summary {
        // Stable IDs preserve separation of homonyms; never merge clients by their name.
        val groups=rows.groupBy{it.studentId.ifBlank{"record:"+it.id}}
        val latest=groups.values.map{list->list.maxWith(compareBy<Evaluation>{valid(it.date)?.toString().orEmpty()}.thenBy{it.id})}
        val dated=latest.filter{valid(it.nextAssessment)!=null}
        val overdue=dated.filter{valid(it.nextAssessment)!!<today}.sortedBy{it.nextAssessment}
        val upcoming=dated.filter{valid(it.nextAssessment)!! in today..today.plusDays(14)}.sortedBy{it.nextAssessment}
        var history=0
        val changes=groups.values.mapNotNull{list->
            val sorted=list.filter{valid(it.date)!=null}.sortedWith(compareByDescending<Evaluation>{it.date}.thenByDescending{it.id})
            if(sorted.map{it.date}.distinct().size<2)return@mapNotNull null
            history++
            val newest=sorted.first();val previous=sorted.first{it.date<newest.date}
            val a=newest.weightKg;val b=previous.weightKg
            if(a==null||b==null||!a.isFinite()||!b.isFinite()||a<=0||b<=0||a>=500||b>=500)null else WeightChange(newest,a-b)
        }.sortedByDescending{it.evaluation.date}.take(4)
        return Summary(latest.size,rows.size,overdue,upcoming,latest.size-dated.size,history,changes)
    }
}
