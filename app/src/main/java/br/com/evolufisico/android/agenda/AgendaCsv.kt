package br.com.evolufisico.android.agenda

import br.com.evolufisico.android.ops.Appointment

/** Relatório offline de atendimentos em CSV PT-BR para planilhas. */
object AgendaCsv {
    private fun safe(input:String):String {
        val clean=input.replace("\u0000","").replace("\r\n","\n").replace("\r","\n")
        val first=clean.trimStart().firstOrNull()
        val text=if(first!=null && first in "=+-@") "'"+clean else clean
        return if(text.any { it==';'||it=='"'||it=='\n'||it=='\t' } ||
            text.startsWith(' ') || text.endsWith(' '))
            "\""+text.replace("\"","\"\"")+"\"" else text
    }

    fun generate(appointments:List<Appointment>, clients:Map<String,String>):ByteArray {
        require(appointments.size<=20000) { "Limite de exportação excedido." }
        val header=listOf("ID","Cliente","Data","Horário","Duração (min)",
            "Serviço","Situação","Observações")
        val rows=appointments.sortedBy { it.startIso }.map { a ->
            listOf(a.id,clients[a.clientId]?:"Cliente indisponível",
                a.startIso.substringBefore('T'),a.startIso.substringAfter('T',""),
                a.durationMinutes.toString(),a.service,a.status,a.notes)
        }
        return ("\uFEFF"+(listOf(header)+rows).joinToString("\r\n") { row ->
            row.joinToString(";"){ safe(it) }
        }+"\r\n").toByteArray(Charsets.UTF_8)
    }
}
