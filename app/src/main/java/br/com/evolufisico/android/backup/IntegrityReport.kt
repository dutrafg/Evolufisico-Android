package br.com.evolufisico.android.backup

import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.Client

/**
 * Verifica consistência referencial sem alterar registros e sem
 * usar nomes como identificadores. Pure Kotlin para testes de regressão.
 */
object IntegrityReport {
    fun inspect(clients: List<Client>, assessments: List<Assessment>, charges: List<Charge>): String? {
        val ids = clients.map { it.id }
        if (ids.any { it.isBlank() } || ids.distinct().size != ids.size)
            return "IDs de clientes inválidos ou duplicados."
        val known = ids.toSet()
        if (assessments.any { it.id.isBlank() || it.clientId !in known } ||
            assessments.map { it.id }.distinct().size != assessments.size)
            return "Avaliações sem cliente, inválidas ou duplicadas."
        if (charges.any { it.id.isBlank() || it.clientId !in known } ||
            charges.map { it.id }.distinct().size != charges.size)
            return "Cobranças sem cliente, inválidas ou duplicadas."
        return null
    }
}
