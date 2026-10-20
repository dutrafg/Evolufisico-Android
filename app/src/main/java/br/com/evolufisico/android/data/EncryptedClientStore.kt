package br.com.evolufisico.android.data

import org.json.JSONArray
import org.json.JSONObject

/** Nome histórico mantido por compatibilidade de chamadas; nova escrita em JSON. */
class EncryptedClientStore(context: android.content.Context) {
    private val file = PrototypeJsonStore(context, "clients", "evolufisico.clients.v1")

    fun load(): List<Client> {
        val root = file.load() ?: return emptyList()
        require(root.getInt("schema") == 1) { "Formato de clientes não suportado." }
        val arr = root.getJSONArray("clients")
        require(arr.length() <= 20000)
        return (0 until arr.length()).map { i ->
            val j = arr.getJSONObject(i)
            Client(j.getString("id"), j.getString("name"))
        }.also { entries -> require(entries.distinctBy { it.id }.size == entries.size) }
    }

    fun save(clients: List<Client>) {
        require(clients.size <= 20000 && clients.distinctBy { it.id }.size == clients.size)
        require(clients.all { it.id.isNotBlank() && ClientRules.validateName(it.name) == null })
        val items = JSONArray().apply {
            clients.forEach { put(JSONObject().put("id", it.id).put("name", it.name)) }
        }
        file.save(JSONObject().put("schema", 1).put("clients", items))
    }
}
