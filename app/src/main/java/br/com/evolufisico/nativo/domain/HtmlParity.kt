package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Product scope pinned to the user attached HTML; old record types remain readable for data preservation. */
object HtmlParity {
    val modules=listOf("Início","Avaliações","Evolução","Calculadora","Formulários","Análises","Cobranças","Configurações","Organização","Perfil Unificado")
    val navigation=listOf(0,9,8,1,2,3,4,5,6,7)
    val panels=listOf("agenda","calendario","tarefas","retornos")
    fun panelOrder(raw:String?)=(raw.orEmpty().split(',').filter{it in panels}.distinct()+panels).distinct()
    fun move(order:List<String>,id:String,offset:Int):List<String> {
        val list=panelOrder(order.joinToString(",")).toMutableList();val at=list.indexOf(id)
        if(at<0 || offset !in listOf(-1,1) || at+offset !in list.indices)return list
        list.removeAt(at);list.add(at+offset,id);return list
    }
    fun eligible(history:List<Evaluation>):Boolean {
        if(history.isEmpty() || history.any{it.studentId.isBlank()} || history.map{it.studentId}.distinct().size!=1)return false
        val days=history.mapNotNull{runCatching{LocalDate.parse(it.date)}.getOrNull()}.distinct().sorted()
        return days.size>=3 && ChronoUnit.DAYS.between(days.first(),days.last())>=21
    }
    fun sortName(value:String)=java.text.Normalizer.normalize(value,java.text.Normalizer.Form.NFD).replace(Regex("\\p{M}"), "").lowercase(java.util.Locale.ROOT)
    fun recordOrder(ids:List<String>,raw:String?)=(raw.orEmpty().split(',').filter{it in ids}.distinct()+ids).distinct()
    fun moveRecords(ids:List<String>,raw:String?,id:String,offset:Int):List<String> {
        val list=recordOrder(ids,raw).toMutableList();val i=list.indexOf(id)
        if(i<0 || offset !in listOf(-1,1) || i+offset !in list.indices)return list
        list.removeAt(i);list.add(i+offset,id);return list
    }
    fun individualBackupKind(kind:String)=kind in setOf("evaluation","intake","appointment","task","note","organizationGoal")
}
