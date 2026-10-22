package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*

object ExportTables {
    fun build(kind:String,evaluations:List<Evaluation>,charges:List<Charge>,sessions:List<TrainingSession>,
              habits:List<HabitEntry>,tasks:List<TaskItem>,includeNames:Boolean=false,appointments:List<Appointment> = emptyList(),notes:List<NotebookEntry> = emptyList(),includeNoteBody:Boolean=false):String {
        fun names(name:String)=if(includeNames)listOf(name) else emptyList()
        val person=if(includeNames)listOf("Cliente") else emptyList()
        val headers:List<String>
        val rows:List<List<String>>
        val actual=sessions.filterNot{it.isTemplate}
        val rowCount=when(kind) {
            "Avaliações"->evaluations.size.toLong();"Cobranças"->charges.size.toLong();"Treinos"->actual.size.toLong()
            "Séries"->actual.sumOf{it.sets.size.toLong()};"Hábitos"->habits.size.toLong();"Tarefas"->tasks.size.toLong()
            "Agenda"->appointments.size.toLong();"Notas"->notes.size.toLong()
            else->error("Tipo de exportação não reconhecido.")
        }
        require(rowCount<=20_000L){"Exportação acima do limite de 20000 linhas. Selecione um período menor."}
        when(kind){
            "Avaliações"->{headers=person+listOf("Data","Peso kg","Altura cm","Gordura %")
                rows=evaluations.map{names(it.name)+listOf(it.date,it.weightKg?.toString().orEmpty(),it.heightCm?.toString().orEmpty(),it.bodyFatPercent?.toString().orEmpty())}}
            "Cobranças"->{headers=person+listOf("Vencimento","Serviço","Valor líquido centavos","Situação")
                rows=charges.map{names(it.student)+listOf(it.dueDate,it.service,Financial.billable(it.amountCents,it.discountCents,it.exempt).toString(),if(it.exempt)"Cortesia" else if(it.paid)"Pago" else "Pendente")}}
            "Treinos"->{headers=person+listOf("Data","Título","Minutos","Séries marcadas","Concluído","Volume externo kg.rep")
                rows=actual.map{names(it.student)+listOf(it.date,it.title,it.durationMinutes.toString(),it.sets.count{it.done}.toString(),it.completed.toString(),ProfessionalTools.completedVolume(it).toString())}}
            "Séries"->{
                headers=person+listOf("Data","Treino","Exercício","Repetições","Carga kg","Marcada")
                rows=actual.flatMap{session->session.sets.map{names(session.student)+listOf(session.date,session.title,it.exercise,it.repetitions.toString(),it.loadKg.toString(),it.done.toString())}}}
            "Hábitos"->{headers=person+listOf("Data","Hábito","Realizado","Meta","Unidade")
                rows=habits.map{names(it.student)+listOf(it.date,it.title,it.value.toString(),it.target.toString(),it.unit)}}
            "Tarefas"->{headers=person+listOf("Prazo","Título","Categoria","Prioridade","Concluída")
                rows=tasks.map{names(it.student)+listOf(it.dueDate,it.title,it.category,it.priority.toString(),it.done.toString())}}
            "Agenda"->{headers=person+listOf("Data","Hora","Tipo","Duração min","Situação","Local")
                rows=appointments.map{names(it.student)+listOf(it.date,it.time,it.type,it.durationMinutes.toString(),it.status,it.location)}}
            "Notas"->{headers=person+listOf("Data","Título","Categoria","Fixada","Arquivada")+(if(includeNoteBody)listOf("Conteúdo") else emptyList())
                rows=notes.map{names(it.student)+listOf(it.date,it.title,it.category,it.pinned.toString(),it.archived.toString())+(if(includeNoteBody)listOf(it.body) else emptyList())}}
            else->error("Tipo de exportação não reconhecido.")
        }
        require(rows.size<=20000){"Exportação acima do limite. Selecione um período menor."}
        return ProfessionalTools.csv(headers,rows)
    }
}
