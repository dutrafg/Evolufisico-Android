package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Mensagem pronta para compartilhar usando o seletor nativo Android. */
object AppointmentShareText {
    fun format(appointment:Appointment, clientName:String):String {
        val date=LocalDateTime.parse(appointment.startIso)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm",
                Locale.forLanguageTag("pt-BR")))
        return listOf(
            "EvoluFísico — Atendimento",
            "Cliente: "+clientName.trim(),
            "Data: "+date,
            "Serviço: "+appointment.service.trim(),
            "Duração: "+appointment.durationMinutes+" min",
            "Situação: "+appointment.status,
            appointment.notes.takeIf { it.isNotBlank() }?.let { "Observações: "+it.trim() }
        ).filterNotNull().joinToString("\n")
    }
}
