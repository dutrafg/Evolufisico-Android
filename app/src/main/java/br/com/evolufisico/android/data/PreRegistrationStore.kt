package br.com.evolufisico.android.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Rascunhos independentes do cadastro definitivo.
 * Usa o mesmo armazenamento atômico local da Beta 11, que ainda é JSON sem criptografia.
 */
class PreRegistrationStore(context:Context) {
    private val file=PrototypeJsonStore(context,"pre_registrations","evolufisico.prereg.legacy")

    @Synchronized fun load():List<PreRegistration> {
        val root=file.load() ?: return emptyList()
        require(root.getInt("schema")==1) { "Formato de pré-cadastros não suportado." }
        val rows=root.getJSONArray("preRegistrations")
        require(rows.length()<=300) { "Quantidade inválida." }
        return parseArray(rows).also(PreRegistrationRules::validateAll)
    }

    @Synchronized fun save(items:List<PreRegistration>) {
        PreRegistrationRules.validateAll(items)
        file.save(JSONObject().put("schema",1)
            .put("preRegistrations",toArray(items)))
        check(load()==items) { "A gravação do pré-cadastro não foi confirmada." }
    }

    companion object {
        fun toArray(items:List<PreRegistration>):JSONArray=JSONArray().apply {
            items.forEach { a ->
                put(JSONObject().put("id",a.id).put("name",a.name)
                    .put("email",a.email).put("phone",a.phone)
                    .put("createdAtIso",a.createdAtIso)
                    .put("updatedAtIso",a.updatedAtIso))
            }
        }

        fun parseArray(rows:JSONArray):List<PreRegistration> {
            require(rows.length()<=300) { "Quantidade inválida." }
            return (0 until rows.length()).map { i ->
                val j=rows.getJSONObject(i)
                PreRegistration(j.getString("id"),j.getString("name"),
                    j.getString("email"),j.getString("phone"),
                    j.getString("createdAtIso"),j.getString("updatedAtIso"))
            }.also(PreRegistrationRules::validateAll)
        }
    }
}
