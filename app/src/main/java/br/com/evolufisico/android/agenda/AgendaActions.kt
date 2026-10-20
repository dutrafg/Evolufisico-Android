package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import br.com.evolufisico.android.ops.OperationalRules
import java.time.LocalDateTime
import java.util.UUID

/** Ações puras e verificadas, mantendo o atendimento original intocado. */
object AgendaActions {
    fun duplicateNextWeek(
        original:Appointment, existing:List<Appointment>, clients:Set<String>,
        id:String=UUID.randomUUID().toString()
    ):Appointment {
        val date=LocalDateTime.parse(original.startIso).plusWeeks(1)
        val candidate=original.copy(id=id,startIso=date.toString(),status="Agendado")
        require(candidate.id!=original.id && existing.none{it.id==candidate.id}) {
            "Identificador do atendimento duplicado."
        }
        require(OperationalRules.appointment(candidate,clients)==null) {
            "Atendimento inválido para este cliente."
        }
        require(!OperationalRules.conflict(candidate,existing)) {
            "Horário já ocupado. Escolha outra data pela edição de atendimento."
        }
        return candidate
    }

    fun changeStatus(
        original:Appointment, nextStatus:String, existing:List<Appointment>
    ):Appointment {
        require(nextStatus in OperationalRules.appointmentStatuses) {
            "Situação inválida."
        }
        require(existing.any{it.id==original.id}) {
            "Atendimento não encontrado."
        }
        val updated=original.copy(status=nextStatus)
        if (nextStatus=="Agendado") require(!OperationalRules.conflict(updated,existing)) {
            "Existe conflito para reabrir este atendimento."
        }
        return updated
    }
}
