package br.com.evolufisico.android.data

import java.time.Instant
import java.util.UUID

/** Dados de identificação antes de criar o cliente ou uma avaliação definitiva. */
data class PreRegistration(
    val id:String,
    val name:String,
    val email:String,
    val phone:String,
    val createdAtIso:String,
    val updatedAtIso:String
) {
    companion object {
        fun create(name:String,email:String,phone:String,now:Instant=Instant.now()):PreRegistration {
            val timestamp=now.toString()
            return PreRegistration(UUID.randomUUID().toString(),name.trim(),email.trim(),
                phone.trim(),timestamp,timestamp)
        }
    }
}

object PreRegistrationRules {
    fun validate(record:PreRegistration):String? {
        if(record.id.isBlank() || record.id.length>100) return "Identificador inválido."
        if(record.name.isBlank()) return "Nome obrigatório."
        if(record.email.isBlank()) return "E-mail obrigatório."
        val digits=record.phone.filter(Char::isDigit)
        if(digits.length !in 10..15) return "Telefone com DDD obrigatório."
        // Sem divergências com os requisitos do cliente definitivo.
        ClientRules.validateContact(Client(record.id,record.name,record.phone,record.email))
            ?.let { return it }
        val created=runCatching { Instant.parse(record.createdAtIso) }.getOrNull()
            ?: return "Data de criação inválida."
        val changed=runCatching { Instant.parse(record.updatedAtIso) }.getOrNull()
            ?: return "Data de atualização inválida."
        if(changed.isBefore(created)) return "Atualização anterior à criação."
        return null
    }
    fun validateAll(items:List<PreRegistration>) {
        require(items.size<=300) { "Limite de 300 pré-cadastros." }
        require(items.map { it.id }.distinct().size==items.size) { "IDs duplicados." }
        require(items.all { validate(it)==null }) { "Pré-cadastros inválidos." }
    }
}
