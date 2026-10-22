package br.com.evolufisico.nativo.data

import org.json.JSONObject
import java.util.UUID

/** User-authored local catalog. Sessions keep their own exercise text when this record changes. */
data class ExerciseDefinition(
    val id:String=UUID.randomUUID().toString(), val name:String="",
    val muscleGroup:String="Geral", val equipment:String="Sem equipamento",
    val instructions:String="", val archived:Boolean=false
) {
    fun toJson()=JSONObject().put("id",id).put("name",name).put("muscleGroup",muscleGroup)
        .put("equipment",equipment).put("instructions",instructions).put("archived",archived)
    companion object {
        fun fromJson(o:JSONObject)=ExerciseDefinition(o.getString("id"),o.getString("name"),
            o.optString("muscleGroup","Geral"),o.optString("equipment","Sem equipamento"),
            o.optString("instructions"),o.optBoolean("archived"))
    }
}
