package br.com.evolufisico.android.billing

/** Exportação financeira offline com valores monetários em centavos exatos. */
object BillingCsv {
    private fun safe(value:String):String {
        val cleaned=value.replace("\u0000","").replace("\r\n","\n").replace("\r","\n")
        val first=cleaned.trimStart().firstOrNull()
        val guarded=if(first!=null && (first in "=+-@" || first=='\t')) "'"+cleaned else cleaned
        return if(guarded.any { it==';'||it=='"'||it=='\n'||it=='\t' } ||
            guarded.startsWith(' ') || guarded.endsWith(' '))
            "\""+guarded.replace("\"","\"\"")+"\"" else guarded
    }
    fun generate(charges:List<Charge>,clientNames:Map<String,String>):ByteArray {
        require(charges.size<=20000)
        val header=listOf("ID","Cliente","Descrição","Valor em centavos",
            "Vencimento","Situação","Data do recebimento")
        val rows=charges.sortedWith(compareBy<Charge>{it.dueDateIso}.thenBy{it.id})
            .map { c ->
                listOf(c.id,clientNames[c.clientId]?:"Cliente indisponível",
                    c.description,c.amountCents.toString(),c.dueDateIso,
                    if(c.isReceived) "Recebida" else "Pendente",
                    c.receivedDateIso.orEmpty())
            }
        return ("\uFEFF"+(listOf(header)+rows).joinToString("\r\n") { row ->
            row.joinToString(";") { safe(it) }
        }+"\r\n").toByteArray(Charsets.UTF_8)
    }
}
