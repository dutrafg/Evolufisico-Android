package br.com.evolufisico.android.ops

import android.content.Context
import br.com.evolufisico.android.data.PrototypeJsonStore
import org.json.JSONArray
import org.json.JSONObject

class OperationalStore(context: Context) {
    private val file = PrototypeJsonStore(context, "operations", "evolufisico.operations.legacy")
    @Synchronized
    fun load(): OperationsData = file.load()?.let(::decode) ?: OperationsData()

    @Synchronized
    fun save(data: OperationsData, clients: Set<String>) {
        validate(data,clients)
        file.save(encode(data))
    }
    companion object {
        fun encode(data: OperationsData) = JSONObject().put("schema",1)
            .put("appointments",JSONArray().apply {
                data.appointments.forEach { a ->
                    put(JSONObject().put("id",a.id).put("clientId",a.clientId)
                        .put("startIso",a.startIso).put("durationMinutes",a.durationMinutes)
                        .put("service",a.service).put("status",a.status).put("notes",a.notes))
                }
            })
            .put("goals",JSONArray().apply {
                data.goals.forEach { g ->
                    put(JSONObject().put("id",g.id).put("clientId",g.clientId)
                        .put("title",g.title).put("unit",g.unit).put("starting",g.starting)
                        .put("target",g.target).put("current",g.current)
                        .put("deadlineIso",g.deadlineIso).put("completed",g.completed))
                }
            })
            .put("services",JSONArray().apply {
                data.services.forEach { s ->
                    put(JSONObject().put("id",s.id).put("name",s.name)
                        .put("priceCents",s.priceCents).put("durationMinutes",s.durationMinutes)
                        .put("enabled",s.enabled))
                }
            })
            .put("notes",JSONArray().apply {
                data.notes.forEach { n ->
                    put(JSONObject().put("id",n.id).put("clientId",n.clientId)
                        .put("dateIso",n.dateIso).put("category",n.category)
                        .put("title",n.title).put("body",n.body).put("pinned",n.pinned))
                }
            })
        fun decode(root: JSONObject): OperationsData {
            require(root.optInt("schema",1)==1)
            val a=root.optJSONArray("appointments")?:JSONArray()
            val g=root.optJSONArray("goals")?:JSONArray()
            val s=root.optJSONArray("services")?:JSONArray()
            val n=root.optJSONArray("notes")?:JSONArray()
            require(a.length()<=20000 && g.length()<=20000 && s.length()<=20000 && n.length()<=20000)
            return OperationsData(
                (0 until a.length()).map { i ->
                    val j=a.getJSONObject(i)
                    Appointment(j.getString("id"),j.getString("clientId"),j.getString("startIso"),
                        j.getInt("durationMinutes"),j.getString("service"),
                        j.optString("status","Agendado"),j.optString("notes",""))
                },
                (0 until g.length()).map { i ->
                    val j=g.getJSONObject(i)
                    ClientGoal(j.getString("id"),j.getString("clientId"),j.getString("title"),
                        j.getString("unit"),j.getDouble("starting"),j.getDouble("target"),
                        j.getDouble("current"),j.getString("deadlineIso"),
                        j.optBoolean("completed",false))
                },
                (0 until s.length()).map { i ->
                    val j=s.getJSONObject(i)
                    ServiceItem(j.getString("id"),j.getString("name"),j.getLong("priceCents"),
                        j.getInt("durationMinutes"),j.optBoolean("enabled",true))
                },
                (0 until n.length()).map { i ->
                    val j=n.getJSONObject(i)
                    ClientNote(j.getString("id"),j.getString("clientId"),
                        j.getString("dateIso"),j.getString("category"),j.getString("title"),
                        j.getString("body"),j.optBoolean("pinned",false))
                }
            ).also { validate(it) }
        }
        fun validate(data: OperationsData, clients: Set<String>? = null) {
            val ids = listOf(data.appointments.map{it.id},data.goals.map{it.id},
                data.services.map{it.id},data.notes.map{it.id})
            require(ids.all { it.size==it.distinct().size }) {"Há IDs duplicados."}
            require(data.appointments.size<=20000 && data.goals.size<=20000 &&
                data.services.size<=20000 && data.notes.size<=20000)
            val effectiveClients=clients?: (data.appointments.map{it.clientId}+
                data.goals.map{it.clientId}+data.notes.map{it.clientId}).toSet()
            require(data.appointments.all { OperationalRules.appointment(it,effectiveClients)==null })
            require(data.goals.all { OperationalRules.goal(it,effectiveClients)==null })
            require(data.services.all { OperationalRules.service(it)==null })
            require(data.notes.all { OperationalRules.note(it,effectiveClients)==null })
        }
    }
}
data class OperationsData(
    val appointments: List<Appointment> = emptyList(),
    val goals: List<ClientGoal> = emptyList(),
    val services: List<ServiceItem> = emptyList(),
    val notes: List<ClientNote> = emptyList()
)
