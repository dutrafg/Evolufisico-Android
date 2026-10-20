package br.com.evolufisico.android.ops

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/** Dados operacionais alpha (armazenados em JSON local). */
data class Appointment(
    val id: String, val clientId: String, val startIso: String,
    val durationMinutes: Int, val service: String, val status: String = "Agendado",
    val notes: String = ""
) {
    companion object {
        fun create(clientId: String, startIso: String, durationMinutes: Int,
            service: String, notes: String = "") =
            Appointment(UUID.randomUUID().toString(),clientId,startIso,
                durationMinutes,service.trim(),notes=notes.trim())
    }
}

data class ClientGoal(
    val id: String, val clientId: String, val title: String, val unit: String,
    val starting: Double, val target: Double, val current: Double,
    val deadlineIso: String, val completed: Boolean = false
) {
    companion object {
        fun create(clientId: String,title: String, unit: String, starting: Double,
            target: Double, deadlineIso: String) =
            ClientGoal(UUID.randomUUID().toString(),clientId,title.trim(),unit.trim(),
                starting,target,starting,deadlineIso)
    }
}

data class ServiceItem(
    val id: String, val name: String, val priceCents: Long,
    val durationMinutes: Int, val enabled: Boolean = true
) {
    companion object {
        fun create(name: String, priceCents: Long, durationMinutes: Int) =
            ServiceItem(UUID.randomUUID().toString(),name.trim(),priceCents,durationMinutes)
    }
}

data class ClientNote(
    val id: String, val clientId: String, val dateIso: String,
    val category: String, val title: String, val body: String,
    val pinned: Boolean = false
) {
    companion object {
        fun create(clientId: String, category: String, title: String, body: String) =
            ClientNote(UUID.randomUUID().toString(),clientId,LocalDate.now().toString(),
                category,title.trim(),body.trim())
    }
}

object OperationalRules {
    val noteCategories = listOf("Geral","Treino","Avaliação","Acompanhamento")
    fun note(n: ClientNote, clients: Set<String>): String? {
        if (n.id.isBlank() || n.clientId !in clients) return "Cliente inválido."
        if (runCatching { LocalDate.parse(n.dateIso) }.isFailure) return "Data inválida."
        if (n.category !in noteCategories) return "Categoria inválida."
        if (n.title.isBlank() || n.title.length > 120) return "Título inválido."
        if (n.body.isBlank() || n.body.length > 3000) return "Anotação deve ter até 3000 caracteres."
        return null
    }
    val appointmentStatuses = setOf("Agendado","Concluído","Cancelado")
    fun appointment(a: Appointment, clients: Set<String>): String? {
        if (a.id.isBlank() || a.clientId !in clients) return "Cliente inválido."
        if (runCatching { LocalDateTime.parse(a.startIso) }.isFailure)
            return "Data e hora inválidas (AAAA-MM-DDTHH:MM)."
        if (a.durationMinutes !in 15..480) return "Duração deve ser entre 15 e 480 minutos."
        if (a.service.isBlank() || a.service.length > 120) return "Informe o serviço."
        if (a.status !in appointmentStatuses) return "Situação não reconhecida."
        if (a.notes.length > 500) return "Observação excede 500 caracteres."
        return null
    }
    fun conflict(candidate: Appointment, list: List<Appointment>): Boolean {
        if (candidate.status == "Cancelado") return false
        val start = runCatching { LocalDateTime.parse(candidate.startIso) }.getOrNull()
            ?: return false
        val end = start.plusMinutes(candidate.durationMinutes.toLong())
        return list.any { old ->
            if (old.id == candidate.id || old.status == "Cancelado") false
            else {
                val other = runCatching { LocalDateTime.parse(old.startIso) }.getOrNull()
                other != null && start.isBefore(other.plusMinutes(old.durationMinutes.toLong())) &&
                    other.isBefore(end)
            }
        }
    }
    fun goal(g: ClientGoal, clients: Set<String>): String? {
        if (g.id.isBlank() || g.clientId !in clients) return "Cliente inválido."
        if (g.title.isBlank() || g.title.length > 100 || g.unit.isBlank() || g.unit.length > 16)
            return "Título ou unidade inválidos."
        if (listOf(g.starting,g.target,g.current).any { !it.isFinite() || it < 0 || it > 1_000_000 })
            return "Os valores devem ser finitos e positivos."
        if (g.target == g.starting) return "A meta deve ser diferente do valor inicial."
        if (runCatching { LocalDate.parse(g.deadlineIso) }.isFailure)
            return "Data de prazo inválida."
        return null
    }
    fun progress(g: ClientGoal): Int {
        if (g.completed) return 100
        val share = (g.current-g.starting)/(g.target-g.starting)*100.0
        return if (share.isFinite()) share.coerceIn(0.0,100.0).toInt() else 0
    }
    fun service(s: ServiceItem): String? {
        if (s.id.isBlank() || s.name.isBlank() || s.name.length > 100) return "Nome inválido."
        if (s.priceCents !in 0L..100_000_000L) return "Preço deve ser de 0 a R$ 1.000.000."
        if (s.durationMinutes !in 15..480) return "Duração inválida."
        return null
    }
}
