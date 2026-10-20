package br.com.evolufisico.android.billing

import br.com.evolufisico.android.data.PrototypeJsonStore
import org.json.JSONArray
import org.json.JSONObject

/** Nome histórico para evitar refatoração da interface. Dados novos em JSON simples. */
class EncryptedChargeStore(context: android.content.Context) {
    private val file = PrototypeJsonStore(context, "charges", "evolufisico.charges.v1")

    fun load(): List<Charge> {
        val root = file.load() ?: return emptyList()
        require(root.getInt("schema") == 1) { "Formato de cobranças não suportado." }
        return ChargeJson.fromArray(root.getJSONArray("charges")).also {
            require(it.distinctBy { entry -> entry.id }.size == it.size)
        }
    }

    fun save(charges: List<Charge>, clientIds: Set<String>) {
        require(charges.size <= 20000 && charges.distinctBy { it.id }.size == charges.size)
        charges.forEach { require(ChargeRules.validate(it, clientIds) == null) }
        file.save(JSONObject().put("schema", 1).put("charges", ChargeJson.toArray(charges)))
    }
}

object ChargeJson {
    fun toArray(charges: List<Charge>): JSONArray = JSONArray().apply {
        charges.forEach { charge ->
            put(JSONObject().put("id", charge.id).put("clientId", charge.clientId)
                .put("description", charge.description).put("amountCents", charge.amountCents)
                .put("dueDateIso", charge.dueDateIso)
                .put("receivedDateIso", charge.receivedDateIso ?: JSONObject.NULL))
        }
    }

    fun fromArray(array: JSONArray): List<Charge> {
        require(array.length() <= 20000)
        return (0 until array.length()).map { i ->
            val entry = array.getJSONObject(i)
            Charge(entry.getString("id"), entry.getString("clientId"),
                entry.getString("description"), entry.getLong("amountCents"),
                entry.getString("dueDateIso"),
                if (entry.isNull("receivedDateIso")) null else entry.getString("receivedDateIso"))
        }
    }
}
