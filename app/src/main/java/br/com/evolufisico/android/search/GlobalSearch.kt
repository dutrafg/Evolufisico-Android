package br.com.evolufisico.android.search

import br.com.evolufisico.android.backup.BackupSnapshot
import java.text.Normalizer
import java.util.Locale

enum class SearchKind(val label: String, val section: String) {
    CLIENTS("Clientes","CLIENTS"),
    ASSESSMENTS("Avaliações","ASSESSMENTS"),
    FORMS("Formulários","FORMS"),
    APPOINTMENTS("Agenda","AGENDA"),
    GOALS("Metas","GOALS"),
    SERVICES("Serviços","SERVICES"),
    BILLING("Cobranças","BILLING"),
    NOTES("Anotações","NOTES")
}

data class SearchResult(val kind: SearchKind, val title: String,
    val detail: String, val id: String)

object GlobalSearch {
    private fun fold(value: String): String {
        val normalized=Normalizer.normalize(value,Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{M}+"),"").lowercase(Locale.ROOT)
    }
    fun search(
        source: BackupSnapshot, query: String, kind: SearchKind? = null, limit: Int = 100
    ): List<SearchResult> {
        require(limit in 1..500)
        val needle=fold(query.trim())
        if(needle.isBlank()) return emptyList()
        val names=source.clients.associate { it.id to it.name }
        fun name(id:String)=names[id] ?: "Cliente não encontrado"
        fun hit(kindValue:SearchKind, id:String, title:String, detail:String): SearchResult? {
            if(kind!=null && kind!=kindValue) return null
            return if(fold(title+" "+detail).contains(needle))
                SearchResult(kindValue,title,detail,id) else null
        }
        val found=mutableListOf<SearchResult>()
        source.clients.forEach { hit(SearchKind.CLIENTS,it.id,it.name,"Cadastro de cliente")?.let(found::add) }
        source.assessments.forEach {
            hit(SearchKind.ASSESSMENTS,it.id,name(it.clientId)+" · "+it.dateIso,
                it.trainingLevel+" · "+it.goal)?.let(found::add)
        }
        source.forms.forEach {
            hit(SearchKind.FORMS,it.id,name(it.clientId)+" · "+it.dateIso,
                "Modelo: "+it.templateId)?.let(found::add)
        }
        source.operations.appointments.forEach {
            hit(SearchKind.APPOINTMENTS,it.id,name(it.clientId)+" · "+it.service,
                it.startIso+" · "+it.status+" · "+it.notes)?.let(found::add)
        }
        source.operations.goals.forEach {
            hit(SearchKind.GOALS,it.id,name(it.clientId)+" · "+it.title,
                it.deadlineIso+" · "+if(it.completed)"Concluída" else "Em andamento")?.let(found::add)
        }
        source.operations.services.forEach {
            hit(SearchKind.SERVICES,it.id,it.name,
                "Duração: "+it.durationMinutes+" min · "+
                    if(it.enabled)"Ativo" else "Inativo")?.let(found::add)
        }
        source.operations.notes.forEach {
            hit(SearchKind.NOTES,it.id,name(it.clientId)+" · "+it.title,
                it.dateIso+" · "+it.category+" · "+it.body)?.let(found::add)
        }
        source.charges.forEach {
            hit(SearchKind.BILLING,it.id,name(it.clientId)+" · "+it.description,
                it.dueDateIso+" · "+if(it.isReceived)"Recebido" else "Pendente")?.let(found::add)
        }
        return found.sortedWith(compareBy<SearchResult> {
            if(fold(it.title).startsWith(needle)) 0 else 1
        }.thenBy { it.kind.ordinal }.thenBy { fold(it.title) }).take(limit)
    }
}
