package br.com.evolufisico.android.data

import org.json.JSONArray
import org.json.JSONObject

/** Compatível com as chamadas anteriores; nova gravação sem criptografia. */
class EncryptedAssessmentStore(context: android.content.Context) {
    private val file = PrototypeJsonStore(context, "assessments", "evolufisico.assessments.v1")

    fun load(): List<Assessment> {
        val root = file.load() ?: return emptyList()
        require(root.getInt("schema") == 1) { "Esquema de avaliações não suportado." }
        val records = root.getJSONArray("assessments")
        require(records.length() <= 20000)
        val entries = (0 until records.length()).map { fromJson(records.getJSONObject(it)) }
        require(entries.distinctBy { it.id }.size == entries.size)
        return entries
    }

    fun save(items: List<Assessment>) {
        require(items.size <= 20000 && items.distinctBy { it.id }.size == items.size)
        val array = JSONArray()
        items.forEach { item ->
            require(item.clientId.isNotBlank() && item.id.isNotBlank())
            array.put(toJson(item))
        }
        file.save(JSONObject().put("schema", 1).put("assessments", array))
    }

    private fun toJson(a: Assessment): JSONObject = JSONObject()
        .put("id", a.id).put("clientId", a.clientId).put("dateIso", a.dateIso)
        .put("trainingLevel", a.trainingLevel).put("goal", a.goal)
        .put("limitations", a.limitations).put("weeklyFrequency", a.weeklyFrequency)
        .put("weightKg", a.weightKg).put("heightCm", a.heightCm)
        .put("waistCm", a.waistCm ?: JSONObject.NULL).put("hipCm", a.hipCm ?: JSONObject.NULL)
        .put("armCm", a.armCm ?: JSONObject.NULL).put("thighCm", a.thighCm ?: JSONObject.NULL)
        .put("healthHistory", a.healthHistory).put("dailyHabits", a.dailyHabits)
        .put("trainingExperience", a.trainingExperience)
        .put("chestCm",a.chestCm?:JSONObject.NULL)
        .put("abdomenCm",a.abdomenCm?:JSONObject.NULL)
        .put("calfCm",a.calfCm?:JSONObject.NULL)
        .put("forearmCm",a.forearmCm?:JSONObject.NULL)

    private fun fromJson(j: JSONObject): Assessment = Assessment(
        id = j.getString("id"), clientId = j.getString("clientId"),
        dateIso = j.getString("dateIso"), trainingLevel = j.getString("trainingLevel"),
        goal = j.getString("goal"), limitations = j.getString("limitations"),
        weeklyFrequency = j.getInt("weeklyFrequency"),
        weightKg = j.getDouble("weightKg"), heightCm = j.getDouble("heightCm"),
        waistCm = optional(j, "waistCm"), hipCm = optional(j, "hipCm"),
        armCm = optional(j, "armCm"), thighCm = optional(j, "thighCm"),
        healthHistory = j.optString("healthHistory", ""),
        dailyHabits = j.optString("dailyHabits", ""),
        trainingExperience = j.optString("trainingExperience", ""),
        chestCm=optional(j,"chestCm"),abdomenCm=optional(j,"abdomenCm"),
        calfCm=optional(j,"calfCm"),forearmCm=optional(j,"forearmCm")
    )

    private fun optional(j: JSONObject, field: String): Double? =
        if (j.isNull(field)) null else j.getDouble(field)

}
