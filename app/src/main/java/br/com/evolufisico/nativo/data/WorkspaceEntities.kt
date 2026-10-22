package br.com.evolufisico.nativo.data

import org.json.JSONObject
import org.json.JSONArray
import java.time.LocalDate
import java.util.UUID

data class WorkoutSet(
    val id:String=UUID.randomUUID().toString(),val exercise:String="",
    val repetitions:Int=10,val loadKg:Double=0.0,val done:Boolean=false
) {
    fun toJson()=JSONObject().put("id",id).put("exercise",exercise).put("repetitions",repetitions).put("loadKg",loadKg).put("done",done)
    companion object {
        fun fromJson(o:JSONObject)=WorkoutSet(o.getString("id"),o.getString("exercise"),o.getInt("repetitions"),o.getDouble("loadKg"),o.optBoolean("done"))
    }
}

/** Native workspace records share the existing encrypted store and backup envelope. */
data class TrainingSession(
    val id: String = UUID.randomUUID().toString(), val title: String = "",
    val studentId: String = "", val student: String = "",
    val date: String = LocalDate.now().toString(), val durationMinutes: Int = 45,
    val exercises: String = "", val notes: String = "", val completed: Boolean = false,
    val effort: Int? = null, val sets:List<WorkoutSet> = emptyList(), val isTemplate:Boolean=false
) {
    fun toJson() = JSONObject().put("id",id).put("title",title).put("studentId",studentId)
        .put("student",student).put("date",date).put("durationMinutes",durationMinutes)
        .put("exercises",exercises).put("notes",notes).put("completed",completed)
        .put("effort",effort ?: JSONObject.NULL).put("isTemplate",isTemplate)
        .put("sets",JSONArray().apply{sets.forEach{put(it.toJson())}})
    companion object {
        fun fromJson(o: JSONObject) = TrainingSession(o.getString("id"),o.getString("title"),
            o.optString("studentId"),o.optString("student"),o.getString("date"),
            o.getInt("durationMinutes"),o.optString("exercises"),o.optString("notes"),
            o.optBoolean("completed"),o.optionalInt("effort"),
            if(!o.has("sets"))emptyList() else o.getJSONArray("sets").let{array->
                require(array.length()<=200){"Treino acima do limite de 200 séries."}
                (0 until array.length()).map{WorkoutSet.fromJson(array.getJSONObject(it))}
            },o.optBoolean("isTemplate"))
    }
}

data class HabitEntry(
    val id: String = UUID.randomUUID().toString(), val title: String = "",
    val studentId: String = "", val student: String = "",
    val date: String = LocalDate.now().toString(), val value: Double = 0.0,
    val target: Double = 1.0, val unit: String = "vezes", val notes: String = ""
) {
    fun toJson() = JSONObject().put("id",id).put("title",title).put("studentId",studentId)
        .put("student",student).put("date",date).put("value",value).put("target",target)
        .put("unit",unit).put("notes",notes)
    companion object {
        fun fromJson(o: JSONObject) = HabitEntry(o.getString("id"),o.getString("title"),
            o.optString("studentId"),o.optString("student"),o.getString("date"),
            o.getDouble("value"),o.getDouble("target"),o.getString("unit"),o.optString("notes"))
    }
}

data class NotebookEntry(
    val id: String = UUID.randomUUID().toString(), val title: String = "",
    val body: String = "", val studentId: String = "", val student: String = "",
    val date: String = LocalDate.now().toString(), val pinned: Boolean = false,
    val category:String="Geral",val archived:Boolean=false
) {
    fun toJson() = JSONObject().put("id",id).put("title",title).put("body",body)
        .put("studentId",studentId).put("student",student).put("date",date).put("pinned",pinned)
        .put("category",category).put("archived",archived)
    companion object {
        fun fromJson(o: JSONObject) = NotebookEntry(o.getString("id"),o.getString("title"),
            o.optString("body"),o.optString("studentId"),o.optString("student"),
            o.getString("date"),o.optBoolean("pinned"),o.optString("category","Geral"),o.optBoolean("archived"))
    }
}
