package br.com.evolufisico.android.data

import java.time.LocalDate
import java.util.Locale

object ClientRules {
    fun validateName(value:String):String? {
        val normalized=value.trim()
        if(normalized.isBlank()) return "Informe o nome do cliente."
        if(normalized.length>80) return "Use no máximo 80 caracteres."
        return null
    }
    fun validateContact(client:Client,today:LocalDate=LocalDate.now()):String? {
        validateName(client.name)?.let { return it }
        val phone=client.phone.trim()
        if(phone.isNotEmpty()) {
            if(phone.length>32 || phone.any {
                    !(it.isDigit() || it in "()+- .")
                } || phone.count{it.isDigit()} !in 8..15)
                return "Informe um telefone válido (8 a 15 dígitos)."
        }
        val email=client.email.trim()
        if(email.isNotEmpty() && (email.length>120 ||
            !Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
                .matches(email))) return "Informe um e-mail válido."
        if(client.birthDateIso.isNotBlank()) {
            val date=runCatching { LocalDate.parse(client.birthDateIso.trim()) }.getOrNull()
                ?: return "Data de nascimento inválida (AAAA-MM-DD)."
            if(date.isAfter(today) || date.isBefore(LocalDate.of(1900,1,1)))
                return "Data de nascimento fora do período permitido."
        }
        return null
    }
}

object ClientSearch {
    fun matches(client:Client,query:String):Boolean {
        val term=query.trim()
        if(term.isEmpty()) return true
        return client.name.contains(term,ignoreCase=true) ||
            client.phone.contains(term,ignoreCase=true) ||
            client.email.contains(term,ignoreCase=true)
    }
}
