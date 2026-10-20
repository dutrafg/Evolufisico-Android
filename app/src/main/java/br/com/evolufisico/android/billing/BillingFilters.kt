package br.com.evolufisico.android.billing

import java.text.Normalizer
import java.time.LocalDate
import java.util.Locale

/** Filtros de leitura das cobranças sem alterar dados ou preços originais. */
object BillingFilters {
    private fun normalize(value:String):String = Normalizer.normalize(
        value,Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"")
        .lowercase(Locale.ROOT).trim()

    fun filter(
        charges:List<Charge>, clientNames:Map<String,String>,
        status:String, search:String, today:LocalDate,
        clientId:String?=null
    ):List<Charge> {
        val query=normalize(search)
        return charges.asSequence().filter { charge ->
            if(clientId!=null && charge.clientId!=clientId) return@filter false
            val due=runCatching { LocalDate.parse(charge.dueDateIso) }.getOrNull()
                ?: return@filter false
            val matches=when(status) {
                "Pendentes" -> !charge.isReceived
                "Atrasadas" -> !charge.isReceived && due.isBefore(today)
                "Próximos 7 dias" -> !charge.isReceived &&
                    !due.isBefore(today) && due.isBefore(today.plusDays(7))
                "Recebidas" -> charge.isReceived
                "Todas" -> true
                else -> false
            }
            matches && (query.isBlank() ||
                listOf(charge.description,clientNames[charge.clientId].orEmpty(),
                    charge.dueDateIso).any { normalize(it).contains(query) })
        }.sortedWith(compareBy<Charge> { it.dueDateIso }.thenBy { it.id })
            .toList()
    }
}
