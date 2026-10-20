package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import org.junit.Assert.*
import org.junit.Test

class AppointmentShareTextTest {
    @Test fun formatsLocalDateWithoutChangingAppointment() {
        val a=Appointment("1","c","2026-10-20T14:30",60,"Avaliação","Agendado")
        val message=AppointmentShareText.format(a,"Ana")
        assertTrue(message.contains("20/10/2026 às 14:30"))
        assertTrue(message.contains("Cliente: Ana"))
        assertTrue(message.contains("60 min"))
        assertFalse(message.contains("Observações:"))
        assertEquals("2026-10-20T14:30",a.startIso)
    }
}
