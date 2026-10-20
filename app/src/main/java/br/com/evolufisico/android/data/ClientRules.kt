package br.com.evolufisico.android.data

object ClientRules {
    fun validateName(value: String): String? {
        val normalized = value.trim()
        if (normalized.isBlank()) return "Informe o nome do cliente."
        if (normalized.length > 80) return "Use no máximo 80 caracteres."
        return null
    }
}
