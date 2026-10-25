package br.com.evolufisico.nativo.data

import org.json.JSONObject
import br.com.evolufisico.nativo.domain.*

/** Interchange with the pinned HTML. Import remains additive and never overwrites. */
object OrganizationJsonImport {
    fun export(notes:List<NotebookEntry>,goals:List<OrganizationGoal>,tasks:List<TaskItem>,agenda:List<Appointment>):ByteArray {
        require(notes.size+goals.size+tasks.size+agenda.size<=10000){"Mais de 10000 registros."}
        notes.forEach{WorkspaceRules.validate(it)};goals.forEach{it.validate()};tasks.forEach{WorkspaceRules.validate(it)};agenda.forEach{Agenda.validate(it)}
        fun array(rows:List<JSONObject>)=org.json.JSONArray(rows)
        val statuses=mapOf("Agendado" to "agendado","Confirmado" to "confirmado","Concluído" to "concluido","Cancelado" to "cancelado","Faltou" to "faltou")
        val root=JSONObject().put("app","EvoluFísico2").put("environment","evolufisico2-organization").put("version",1).put("exportedAt",java.time.Instant.now().toString())
        root.put("organization",JSONObject().put("notes",array(notes.map{n->JSONObject().put("id",n.id).put("title",n.title).put("body",n.body).put("client",n.student).put("pinned",n.pinned).put("archived",n.archived).put("reminder",n.reminder)}))
            .put("goals",array(goals.map{g->JSONObject().put("id",g.id).put("title",g.title).put("client",g.student).put("progress",g.progress).put("due",g.due).put("status",g.status)})))
        root.put("tasks",array(tasks.map{t->JSONObject().put("id",t.id).put("name",t.title).put("date",t.dueDate).put("due",t.dueDate).put("time",t.time).put("body",t.notes).put("client",t.student).put("done",t.done).put("priority",listOf("Normal","Alta","Urgente")[t.priority-1])}))
        root.put("agenda",array(agenda.map{a->JSONObject().put("id",a.id).put("student",a.student).put("date",a.date).put("time",a.time).put("duration",a.durationMinutes).put("type",a.type).put("local",a.location).put("note",a.notes).put("status",statuses.getValue(a.status))}))
        return root.toString(2).toByteArray(Charsets.UTF_8).also{require(it.size<=3_000_000){"JSON acima de 3 MB; use o backup nativo."}}
    }

    fun stage(bytes:ByteArray,profiles:List<Evaluation>):List<Triple<String,String,JSONObject>> {
        require(bytes.size in 1..3_000_000){"JSON vazio ou acima de 3 MB."}
        val root=JSONObject(String(bytes,Charsets.UTF_8).removePrefix("\uFEFF"))
        require(root.getString("environment")=="evolufisico2-organization" && root.getInt("version")==1){"Formato de organização incompatível."}
        val organization=root.getJSONObject("organization");val rows=mutableListOf<Triple<String,String,JSONObject>>()
        val latest=WorkspaceRules.latest(profiles)
        fun client(name:String)=latest.filter{HtmlParity.sortName(it.name.trim())==HtmlParity.sortName(name.trim())}.singleOrNull()?.studentId.orEmpty()
        fun append(kind:String,id:String,value:JSONObject){require(rows.size<10000);rows.add(Triple(kind,id,value))}
        fun each(owner:JSONObject,key:String,block:(JSONObject)->Unit){val array=owner.getJSONArray(key);require(array.length()<=10000);for(i in 0 until array.length())block(array.getJSONObject(i))}
        each(organization,"notes"){o->val name=o.optString("client");val n=NotebookEntry(id=o.getString("id"),title=o.getString("title"),body=o.optString("body"),student=name,studentId=client(name),pinned=o.optBoolean("pinned"),archived=o.optBoolean("archived"),reminder=o.optString("reminder"));WorkspaceRules.validate(n);append("note",n.id,n.toJson())}
        each(organization,"goals"){o->val name=o.optString("client");val g=OrganizationGoal(o.getString("id"),o.getString("title"),client(name),name,o.optString("progress"),o.optString("due"),o.optString("status","Em andamento"));g.validate();append("organizationGoal",g.id,g.toJson())}
        each(root,"tasks"){o->val name=o.optString("client");val priority=when(o.optString("priority","Normal")){"Normal"->1;"Alta"->2;"Urgente"->3;else->error("Prioridade de tarefa inválida.")};val t=TaskItem(id=o.getString("id"),title=o.getString("name"),dueDate=o.optString("date").ifBlank{o.getString("due")},done=o.optBoolean("done"),notes=o.optString("body"),student=name,studentId=client(name),priority=priority,time=o.optString("time"));WorkspaceRules.validate(t);append("task",t.id,t.toJson())}
        each(root,"agenda"){o->val name=o.getString("student");val status=when(o.optString("status","agendado")){"agendado"->"Agendado";"confirmado"->"Confirmado";"concluido"->"Concluído";"cancelado"->"Cancelado";"faltou"->"Faltou";else->error("Situação de agenda inválida.")};val a=Appointment(id=o.getString("id"),student=name,studentId=client(name),date=o.getString("date"),time=o.getString("time"),durationMinutes=o.optInt("duration",60),type=o.optString("type","Atendimento"),location=o.optString("local"),notes=o.optString("note"),status=status);Agenda.validate(a);append("appointment",a.id,a.toJson())}
        require(rows.isNotEmpty()){"Arquivo sem registros."};return rows
    }
}
