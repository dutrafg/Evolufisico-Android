package br.com.evolufisico.android.forms

import br.com.evolufisico.android.data.PrototypeJsonStore
import org.json.JSONArray
import org.json.JSONObject

/** Modelo anterior de API, grava agora JSON legível. */
class EncryptedFormStore(context: android.content.Context) {
    private val file = PrototypeJsonStore(context, "forms", "evolufisico.forms.v1")

    @Synchronized
    fun load(): List<FormResponse> {
        val root = file.load() ?: return emptyList()
        require(root.getInt("schema") == 1) { "Versão de formulários desconhecida." }
        return FormsJson.fromArray(root.getJSONArray("forms")).also {
            require(it.distinctBy { entry -> entry.id }.size == it.size)
        }
    }

    @Synchronized
    fun save(items: List<FormResponse>, clientIds: Set<String>) {
        require(items.size <= 20000 && items.distinctBy { it.id }.size == items.size)
        items.forEach { entry -> require(FormRules.validate(entry, clientIds) == null) {
            FormRules.validate(entry, clientIds) ?: "Formulário inválido."
        } }
        file.save(JSONObject().put("schema", 1).put("forms", FormsJson.toArray(items)))
    }
}

object FormsJson {
    fun toArray(items: List<FormResponse>) = JSONArray().apply {
        items.forEach { item ->
            val answers = JSONObject()
            item.answers.forEach { (key, value) -> answers.put(key, value) }
            put(JSONObject().put("id",item.id).put("clientId",item.clientId)
                .put("templateId",item.templateId).put("dateIso",item.dateIso)
                .put("answers",answers))
        }
    }
    fun fromArray(array: JSONArray): List<FormResponse> {
        require(array.length() <= 20000)
        return (0 until array.length()).map { i ->
            val json = array.getJSONObject(i)
            val answers = json.getJSONObject("answers")
            require(answers.length() <= 30)
            FormResponse(json.getString("id"),json.getString("clientId"),
                json.getString("templateId"),json.getString("dateIso"),
                answers.keys().asSequence().associateWith { answers.getString(it) })
        }
    }
}
