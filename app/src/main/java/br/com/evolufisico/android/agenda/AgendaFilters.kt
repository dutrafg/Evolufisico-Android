package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.text.Normalizer
import java.time.LocalDate

/** Busca local, por cliente e por período, imune a diferenças de acentuação. */
object AgendaFilters {
    fun normalize(raw:String):String = Normalizer.normalize(raw,Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"),"").lowercase(java.util.Locale.ROOT).trim()

    fun filter(
        rows:List<Appointment>, clientNames:Map<String,String>,
        period:String, selectedDay:LocalDate?, today:LocalDate, search:String,
        selectedClientId:String?
    ):List<Appointment> {
        val term=normalize(search)
        return rows.asSequence().filter { a ->
            (selectedClientId==null || a.clientId==selectedClientId) &&
                AgendaPulse.matches(a,period,selectedDay,today) &&
                (term.isEmpty() || listOf(
                    a.service,a.startIso,a.notes,clientNames[a.clientId].orEmpty(),
                    a.status
                ).any { normalize(it).contains(term) })
        }.sortedWith(compareBy<Appointment> { it.startIso }.thenBy { it.id }).toList()
    }
}
