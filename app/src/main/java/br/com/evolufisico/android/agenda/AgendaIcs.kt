package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** Exportação iCalendar RFC 5545, sem rede e com horários locais flutuantes. */
object AgendaIcs {
    private val format=DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    private val stampFormat=DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        .withZone(ZoneOffset.UTC)

    fun escape(value:String):String = value
        .replace("\\","\\\\")
        .replace("\r\n","\n").replace("\r","\n")
        .replace("\n","\\n")
        .replace(";","\\;").replace(",","\\,")

    /** Quebra linhas com CRLF seguido de espaço, inclusive UTF-8. */
    fun fold(line:String):String {
        val out=StringBuilder()
        var octets=0
        line.codePoints().forEach { code ->
            val text=String(Character.toChars(code))
            val size=text.toByteArray(Charsets.UTF_8).size
            if(octets+size>75) {
                out.append("\r\n ")
                octets=1
            }
            out.append(text)
            octets+=size
        }
        return out.toString()
    }

    fun generate(
        appointments:List<Appointment>,
        clientNames:Map<String,String>,
        now:Instant=Instant.now()
    ):ByteArray {
        require(appointments.size<=20000)
        val text=mutableListOf(
            "BEGIN:VCALENDAR","VERSION:2.0",
            "PRODID:-//EvoluFisico//Agenda Android//PT-BR",
            "CALSCALE:GREGORIAN","METHOD:PUBLISH"
        )
        appointments.filter { it.status=="Agendado" }
            .sortedBy { it.startIso }.forEach { a ->
                val start=LocalDateTime.parse(a.startIso)
                val safeId=a.id.filter { it.isLetterOrDigit() || it=='-' || it=='_' }.take(90)
                require(safeId.isNotEmpty())
                text+=listOf(
                    "BEGIN:VEVENT",
                    "UID:"+safeId+"@evolufisico.android",
                    "DTSTAMP:"+stampFormat.format(now),
                    "DTSTART:"+format.format(start),
                    "DTEND:"+format.format(start.plusMinutes(a.durationMinutes.toLong())),
                    "SUMMARY:"+escape(a.service+" - "+(clientNames[a.clientId]?:"Cliente")),
                    "DESCRIPTION:"+escape(a.notes),
                    "STATUS:CONFIRMED",
                    "END:VEVENT"
                )
            }
        text+="END:VCALENDAR"
        return (text.joinToString("\r\n") { fold(it) }+"\r\n")
            .toByteArray(Charsets.UTF_8)
    }
}
