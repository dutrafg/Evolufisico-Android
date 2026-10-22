package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import java.util.Locale
import java.util.UUID

object ProfessionalTools {
    fun newSession(source:TrainingSession,today:LocalDate=LocalDate.now()):TrainingSession = source.copy(
        id=UUID.randomUUID().toString(),date=today.toString(),completed=false,effort=null,isTemplate=false,
        sets=source.sets.map{it.copy(id=UUID.randomUUID().toString(),done=false)})
    fun template(source:TrainingSession):TrainingSession = newSession(source).copy(
        isTemplate=true,studentId="",student="",title=("Modelo · "+source.title).take(120))
    /** External load volume; zero load does not estimate bodyweight or machine resistance. */
    fun completedVolume(item:TrainingSession):Double = if(item.isTemplate)0.0 else item.sets.filter{it.done}
        .sumOf{it.repetitions.toDouble()*it.loadKg}
    fun taskOrder(rows:List<TaskItem>)=rows.sortedWith(compareBy<TaskItem>{it.done}.thenByDescending{it.priority}.thenBy{it.dueDate}.thenBy{it.id})
    data class HabitSummary(val streak:Int,val achievedDays:Int,val loggedDays:Int)
    fun habitSummary(reference:HabitEntry,entries:List<HabitEntry>,today:LocalDate=LocalDate.now()):HabitSummary {
        fun norm(value:String)=value.trim().lowercase(Locale.ROOT)
        val valid=entries.filter{it.studentId==reference.studentId &&
            (it.studentId.isNotBlank() || norm(it.student)==norm(reference.student)) &&
            norm(it.title)==norm(reference.title) && norm(it.unit)==norm(reference.unit)}
            .mapNotNull{row->runCatching{WorkspaceRules.date(row.date)}.getOrNull()?.takeIf{it<=today}?.let{it to row}}
        val days=valid.groupBy{it.first}
        val hit=days.filterValues{rows->rows.any{it.second.value>=it.second.target && it.second.target>0 && it.second.value.isFinite() && it.second.target.isFinite()}}.keys
        var day=if(today in hit)today else today.minusDays(1)
        var streak=0
        while(day in hit){streak++;day=day.minusDays(1)}
        val start=today.minusDays(6)
        return HabitSummary(streak,hit.count{it>=start},days.keys.count{it>=start})
    }
    /** Always quote; neutralize leading formula triggers, including those after whitespace. */
    fun csvCell(value:String):String {
        val clean=value.filterNot{it.code in 0..8 || it.code in 11..12 || it.code in 14..31}
        val trigger=clean.trimStart{it.isWhitespace() || it=='\uFEFF' || it=='\u200B'}.firstOrNull() in listOf('=','+','-','@','\t','\r','\n') || clean.firstOrNull() in listOf('\t','\r','\n')
        val safe=if(trigger)"'"+clean else clean
        return "\""+safe.replace("\"","\"\"")+"\""
    }
    fun csv(headers:List<String>,rows:List<List<String>>):String {
        require(headers.isNotEmpty() && rows.all{it.size==headers.size}){"Colunas CSV inconsistentes."}
        return "\uFEFF"+(listOf(headers)+rows).joinToString("\r\n"){row->row.joinToString(";"){csvCell(it)}}+"\r\n"
    }
    fun notePreview(note:NotebookEntry,expanded:Boolean):String = if(expanded)note.body else note.body.take(240).let{if(note.body.length>240)it+"…" else it}
}
