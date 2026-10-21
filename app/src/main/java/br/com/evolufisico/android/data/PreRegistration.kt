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
        if(record.name.isBlank() || record.name.length>120) return "Nome obrigatório (até 120 caracteres)."
        if(record.email.length !in 5..200 || !record.email.contains('@') ||
            !record.email.substringAfter('@').contains('.'))
            return "E-mail inválido."
        val digits=record.phone.filter(Char::isDigit)
        if(digits.length !in 10..15) return "Telefone com DDD obrigatório."
        if(record.phone.length>40) return "Telefone muito longo."
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
