package br.com.evolufisico.nativo.data

import org.json.JSONObject
import org.json.JSONArray
import java.util.UUID
import br.com.evolufisico.nativo.domain.WorkspaceRules

data class OrganizationGoal(val id:String=UUID.randomUUID().toString(),val title:String="",val studentId:String="",val student:String="",val progress:String="",val due:String="",val status:String="Em andamento") {
    fun validate(){require(id.isNotBlank() && id.length<=128);require(title.isNotBlank() && title.length<=140);require(progress.length<=10000 && student.length<=200 && studentId.length<=128);if(due.isNotBlank())WorkspaceRules.date(due);require(status in listOf("Em andamento","Pausada","Concluída"))}
    fun toJson()=JSONObject().put("id",id).put("title",title).put("studentId",studentId).put("student",student).put("progress",progress).put("due",due).put("status",status)
    companion object {fun fromJson(o:JSONObject)=OrganizationGoal(o.getString("id"),o.getString("title"),o.optString("studentId"),o.optString("student"),o.optString("progress"),o.optString("due"),o.optString("status","Em andamento"))}
}
data class FormTemplate(val id:String=UUID.randomUUID().toString(),val title:String="",val level:String="Iniciante",val description:String="",val questions:List<String> = emptyList()) {
    fun validate(){require(id.isNotBlank() && id.length<=128);require(title.isNotBlank() && title.length<=200);require(level in listOf("Iniciante","Intermediário","Avançado"));require(description.length<=2000);require(questions.size in 1..60 && questions.all{it.isNotBlank() && it.length<=1000})}
    fun toJson()=JSONObject().put("id",id).put("title",title).put("level",level).put("description",description).put("questions",JSONArray().apply{questions.forEach{put(it)}})
    fun draft(studentId:String,student:String)=Intake(studentId=studentId,student=student,level=level,answers=buildMap {
        put("_template",title);put("_templateId",id);questions.forEachIndexed { n,q -> put("_label_$n",q);put("response_$n","") }
    })
    companion object {fun fromJson(o:JSONObject):FormTemplate {val a=o.getJSONArray("questions");require(a.length() in 1..60);return FormTemplate(o.getString("id"),o.getString("title"),o.getString("level"),o.optString("description"),List(a.length()){a.getString(it)})}}
}
