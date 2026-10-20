package br.com.evolufisico.android.billing

import java.time.LocalDate
import java.util.UUID

/** Usar centavos inteiros para preservar precisão financeira. */
data class Charge(
    val id: String,
    val clientId: String,
    val description: String,
    val amountCents: Long,
    val dueDateIso: String,
    val receivedDateIso: String? = null
) {
    val isReceived: Boolean get() = receivedDateIso != null
    companion object {
        fun new(clientId: String, description: String, amountCents: Long, dueDateIso: String) =
            Charge(UUID.randomUUID().toString(), clientId, description.trim(), amountCents, dueDateIso)
    }
}

object ChargeRules {
    private const val LIMIT_CENTS = 100_000_000L
    fun parseReais(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return null
        val normalized = trimmed.replace(',', '.')
        if (!Regex("""\d{1,7}(\.\d{1,2})?""").matches(normalized)) return null
        val parts = normalized.split('.')
        val reais = parts[0].toLongOrNull() ?: return null
        val cents = (parts.getOrNull(1) ?: "").padEnd(2, '0').toLongOrNull() ?: return null
        return (reais * 100L + cents).takeIf { it in 1..LIMIT_CENTS }
    }
    fun formatInput(cents: Long): String {
        require(cents >= 0)
        return (cents / 100).toString() + "," + (cents % 100).toString().padStart(2, '0')
    }
    fun validate(charge: Charge, clients: Set<String>): String? {
        if (charge.id.isBlank() || charge.clientId !in clients) return "Cliente inválido."
        if (charge.description.isBlank() || charge.description.length > 120)
            return "Descrição obrigatória (até 120 caracteres)."
        if (charge.amountCents !in 1..LIMIT_CENTS) return "Valor inválido."
        if (runCatching { LocalDate.parse(charge.dueDateIso) }.isFailure)
            return "Data de vencimento inválida (AAAA-MM-DD)."
        if (charge.receivedDateIso != null &&
            runCatching { LocalDate.parse(charge.receivedDateIso) }.isFailure)
            return "Data de recebimento inválida."
        return null
    }
    fun isOverdue(charge: Charge, today: LocalDate = LocalDate.now()): Boolean =
        !charge.isReceived &&
            LocalDate.parse(charge.dueDateIso).isBefore(today)

    fun monthlyTotal(items: List<Charge>, received: Boolean,
        month: java.time.YearMonth): Long =
        items.filter { it.isReceived == received }.filter { item ->
            val date = if (received) item.receivedDateIso else item.dueDateIso
            date != null && java.time.YearMonth.from(LocalDate.parse(date)) == month
        }.sumOf { it.amountCents }

    fun total(items: List<Charge>, received: Boolean): Long =
        items.filter { it.isReceived == received }.sumOf { it.amountCents }
}
