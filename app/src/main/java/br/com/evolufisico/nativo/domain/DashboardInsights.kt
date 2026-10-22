package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate

/** Descriptive counts from saved records. No inferred clinical or abandonment risk. */
object DashboardInsights {
    data class Period(val evaluations:Int,val completedTraining:Int,val habitEntries:Int,val achievedHabits:Int)
    fun inPeriod(value:String,today:LocalDate,days:Int):Boolean {
        require(days in 1..366)
        val date=runCatching{WorkspaceRules.date(value)}.getOrNull() ?: return false
        return !date.isAfter(today) && !date.isBefore(today.minusDays(days-1L))
    }
    fun period(evaluations:List<Evaluation>,training:List<TrainingSession>,habits:List<HabitEntry>,today:LocalDate,days:Int):Period {
        require(days in 1..366)
        val entries=habits.filter{inPeriod(it.date,today,days)}
        return Period(evaluations.count{inPeriod(it.date,today,days)},
            training.count{!it.isTemplate && it.completed && inPeriod(it.date,today,days)},entries.size,
            entries.count{it.value.isFinite() && it.target.isFinite() && it.target>0 && it.value>=it.target})
    }
    fun dueReassessments(evaluations:List<Evaluation>,today:LocalDate):List<Evaluation> = evaluations
        .filter{runCatching{WorkspaceRules.date(it.date)<=today}.getOrDefault(false)}
        .groupBy{it.studentId}.values.mapNotNull{rows->rows.maxWithOrNull(compareBy<Evaluation>{it.date}.thenBy{it.id})}
        .filter{runCatching{WorkspaceRules.date(it.nextAssessment)<=today}.getOrDefault(false)}
        .sortedWith(compareBy<Evaluation>{it.nextAssessment}.thenBy{it.name}.thenBy{it.studentId})
}
