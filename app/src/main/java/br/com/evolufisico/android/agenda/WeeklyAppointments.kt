package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationalRules
import java.time.LocalDateTime
import java.util.UUID

/**
 * Geração pontual de atendimentos semanais, sem mudar o banco nem depender
 * de notificações. Recusa toda a série se um dos horários colidir.
 */
object WeeklyAppointments {
    fun plan(
        original: Appointment,
        existing: List<Appointment>,
        quantity: Int,
        idProvider: () -> String = { UUID.randomUUID().toString() }
    ): List<Appointment> {
        require(quantity in 1..12) { "Escolha de 1 a 12 atendimentos." }
        require(original.status == "Agendado") {
            "Somente atendimentos agendados podem ser replicados."
        }
        val first=runCatching { LocalDateTime.parse(original.startIso) }.getOrNull()
            ?: throw IllegalArgumentException("Informe data e hora válidas.")
        val generated=(0 until quantity).map { i ->
            if(i==0) original else original.copy(
                id=idProvider(),
                startIso=first.plusWeeks(i.toLong()).toString()
            )
        }
        require(generated.map { it.id }.distinct().size==generated.size) {
            "IDs repetidos nesta série."
        }
        val universe=existing.toMutableList()
        generated.forEach { candidate ->
            require(!OperationalRules.conflict(candidate,universe)) {
                "Conflito de agenda no dia "+candidate.startIso.substringBefore('T')+
                    ". Nenhum atendimento foi incluído."
            }
            universe+=candidate
        }
        return generated
    }
}
