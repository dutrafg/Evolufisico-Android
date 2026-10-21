package br.com.evolufisico.android.data

/** Conversão pura e idempotente: nunca remove o rascunho antes do save do cliente. */
object PreRegistrationConversion {
    fun prepare(current:List<Client>,draft:PreRegistration):List<Client> {
        PreRegistrationRules.validate(draft)?.let { error(it) }
        val candidate=Client(draft.id,draft.name,draft.phone,draft.email)
        ClientRules.validateContact(candidate)?.let { error(it) }
        val existing=current.firstOrNull { it.id==candidate.id }
        if(existing!=null) {
            require(existing==candidate) {
                "Já existe outro cliente com o mesmo identificador."
            }
            return current
        }
        require(current.none { it.email.isNotBlank() &&
            it.email.equals(candidate.email,ignoreCase=true) }) {
            "Já existe um cliente com este e-mail."
        }
        return current+candidate
    }
}
