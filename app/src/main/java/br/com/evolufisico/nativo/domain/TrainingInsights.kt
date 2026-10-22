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
        data class Accumulator(var exercise:String,var sets:Int=0,var repetitions:Long=0,var maximumKg:Double=0.0,var volume:Double=0.0)
        val grouped=mutableMapOf<Pair<String,String>,Accumulator>()
        for(session in sessions) {
            if(session.isTemplate || !session.completed || session.studentId!=clientId)continue
            val date=runCatching{WorkspaceRules.date(session.date)}.getOrNull() ?: continue
            if(date<from || date>end)continue
            for(row in session.sets) {
                if(!row.done || row.exercise.isBlank() || row.repetitions !in 1..1000 || !row.loadKg.isFinite() || row.loadKg !in 0.0..2000.0)continue
                val label=row.exercise.trim().replace(spaces," ")
                val bucket=grouped.getOrPut(session.date to exerciseKey(label)){Accumulator(label)}
                bucket.exercise=minOf(bucket.exercise,label)
                bucket.sets++;bucket.repetitions+=row.repetitions
                bucket.maximumKg=maxOf(bucket.maximumKg,row.loadKg)
                bucket.volume+=row.loadKg*row.repetitions
            }
        }
        return grouped.map{(key,row)->Point(key.first,row.exercise,row.sets,row.repetitions,row.maximumKg,row.volume)}
            .sortedWith(compareBy<Point>{it.date}.thenBy{exerciseKey(it.exercise)})
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
    /** Reuse typed exercise values only; client names, notes and execution flags are not copied. */
    fun library(sessions:List<TrainingSession>):List<WorkoutSet> = sessions
        .sortedWith(compareByDescending<TrainingSession>{it.date}.thenBy{it.id}).asSequence()
        .flatMap{it.sets.asSequence()}
        .filter{it.exercise.isNotBlank() && it.repetitions in 1..1000 && it.loadKg.isFinite() && it.loadKg in 0.0..2000.0}
        .distinctBy{exerciseKey(it.exercise)}
        .map{it.copy(exercise=it.exercise.trim().replace(spaces," "),done=false)}
        .sortedBy{exerciseKey(it.exercise)}.toList()
    fun batch(exercise:String,count:Int,repetitions:Int,load:Double,existingCount:Int):List<WorkoutSet> {
        require(count in 1..10 && existingCount in 0..200 && count<=200-existingCount){"Adicione de 1 a 10 séries, até o limite total de 200."}
        val result=List(count){WorkoutSet(exercise=exercise.trim(),repetitions=repetitions,loadKg=load)}
        WorkspaceRules.validate(TrainingSession(title="Séries",sets=result))
        return result
    }
}
