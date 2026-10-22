package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import java.util.Locale

/** Observed external loads. Exercises are compared only by normalized name and permanent client ID. */
object TrainingInsights {
    data class Point(val date:String,val exercise:String,val sets:Int,val repetitions:Long,val maximumKg:Double,val volume:Double)
    data class Trend(val exercise:String,val current:Point,val previous:Point?) {
        val differenceKg:Double? get()=previous?.let{current.maximumKg-it.maximumKg}
    }
    private val spaces=Regex("[\\s\\p{Zs}]+")
    fun exerciseKey(value:String)=value.trim().replace(spaces," ").lowercase(Locale.ROOT)
    fun history(sessions:List<TrainingSession>,clientId:String,from:LocalDate,to:LocalDate,today:LocalDate=LocalDate.now()):List<Point> {
        require(from<=to){"Início posterior ao fim do período."}
        if(clientId.isBlank())return emptyList()
        val end=minOf(to,today)
        val rows=sessions.filter{!it.isTemplate && it.completed && it.studentId==clientId}
            .filter{runCatching{WorkspaceRules.date(it.date).let{date->date>=from && date<=end}}.getOrDefault(false)}
        return rows.flatMap{session->session.sets.filter{it.done && it.exercise.isNotBlank() && it.repetitions in 1..1000 && it.loadKg.isFinite() && it.loadKg in 0.0..2000.0}
            .map{Triple(session.date,exerciseKey(it.exercise),it)}}
            .groupBy{it.first to it.second}.map{(key,entries)->
                val sets=entries.map{it.third}
                Point(key.first,sets.map{it.exercise.trim().replace(spaces," ")}.sorted().first(),sets.size,
                    sets.sumOf{it.repetitions.toLong()},sets.maxOf{it.loadKg},sets.sumOf{it.loadKg*it.repetitions})
            }.sortedWith(compareBy<Point>{it.date}.thenBy{exerciseKey(it.exercise)})
    }
    fun trends(points:List<Point>):List<Trend> = points.groupBy{exerciseKey(it.exercise)}.values.map{rows->
        val ordered=rows.sortedBy{it.date}
        Trend(ordered.last().exercise,ordered.last(),ordered.dropLast(1).lastOrNull())
    }.sortedBy{exerciseKey(it.exercise)}
    fun csv(points:List<Point>):String {
        require(points.size<=20_000){"Exporte no máximo 20.000 linhas por vez; reduza o período."}
        return ProfessionalTools.csv(listOf("Data","Exercício","Séries marcadas","Repetições","Carga máxima externa (kg)","Volume externo (kg·rep)"),
            points.map{listOf(it.date,it.exercise,it.sets.toString(),it.repetitions.toString(),it.maximumKg.toString(),it.volume.toString())})
    }
    fun batch(exercise:String,count:Int,repetitions:Int,load:Double,existingCount:Int):List<WorkoutSet> {
        require(count in 1..10 && existingCount in 0..200 && count<=200-existingCount){"Adicione de 1 a 10 séries, até o limite total de 200."}
        val result=List(count){WorkoutSet(exercise=exercise.trim(),repetitions=repetitions,loadKg=load)}
        WorkspaceRules.validate(TrainingSession(title="Séries",sets=result))
        return result
    }
}
