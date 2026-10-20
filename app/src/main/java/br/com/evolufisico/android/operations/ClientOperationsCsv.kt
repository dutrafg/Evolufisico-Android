package br.com.evolufisico.android.operations

/** CSV de controle operacional para uso offline em planilhas. */
object ClientOperationsCsv {
    private fun safe(value:String):String {
        val trimmed=value.replace("\u0000","").replace("\r\n","\n").replace("\r","\n")
        val starts=trimmed.trimStart().firstOrNull()
        val v=if(starts in listOf('=','+','-','@','\t')) "'"+trimmed else trimmed
        return if(v.any { it==';'||it=='"'||it=='\n' }||v.startsWith(' ')||v.endsWith(' '))
            "\""+v.replace("\"","\"\"")+"\"" else v
    }
    fun generate(rows:List<ClientOperations>):ByteArray {
        require(rows.size<=20000)
        val header=listOf("Cliente","Agendados","Concluídos","Faltas","Cancelados",
            "Comparecimento %","Metas ativas","Metas concluídas",
            "A receber centavos","Em atraso centavos","Recebido centavos")
        val data=rows.map { c ->
            listOf(c.name,c.scheduled.toString(),c.completed.toString(),
                c.missed.toString(),c.cancelled.toString(),
                c.attendancePercent?.toString().orEmpty(),
                c.activeGoals.toString(),c.completedGoals.toString(),
                c.pendingCents.toString(),c.overdueCents.toString(),c.receivedCents.toString())
        }
        return ("\uFEFF"+(listOf(header)+data).joinToString("\r\n") { row ->
            row.joinToString(";"){safe(it)}
        }+"\r\n").toByteArray(Charsets.UTF_8)
    }
}
