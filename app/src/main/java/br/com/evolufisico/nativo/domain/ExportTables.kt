package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*

object ExportTables {
    fun build(kind:String,evaluations:List<Evaluation>,charges:List<Charge>,sessions:List<TrainingSession>,
              habits:List<HabitEntry>,tasks:List<TaskItem>,includeNames:Boolean=false):String {
        fun names(name:String)=if(includeNames)listOf(name) else emptyList()
        val person=if(includeNames)listOf("Cliente") else emptyList()
        val headers:List<String>
        val rows:List<List<String>>
        val actual=sessions.filterNot{it.isTemplate}
        when(kind){
            "Avaliações"->{headers=person+listOf("Data","Peso kg","Altura cm","Gordura %")
                rows=evaluations.map{names(it.name)+listOf(it.date,it.weightKg?.toString().orEmpty(),it.heightCm?.toString().orEmpty(),it.bodyFatPercent?.toString().orEmpty())}}
            "Cobranças"->{headers=person+listOf("Vencimento","Serviço","Valor líquido centavos","Situação")
                rows=charges.map{names(it.student)+listOf(it.dueDate,it.service,Financial.billable(it.amountCents,it.discountCents,it.exempt).toString(),if(it.exempt)"Cortesia" else if(it.paid)"Pago" else "Pendente")}}
            "Treinos"->{headers=person+listOf("Data","Título","Minutos","Séries marcadas","Concluído","Volume externo kg.rep")
                rows=actual.map{names(it.student)+listOf(it.date,it.title,it.durationMinutes.toString(),it.sets.count{it.done}.toString(),it.completed.toString(),ProfessionalTools.completedVolume(it).toString())}}
            "Séries"->{require(actual.sumOf{it.sets.size}<=20000){"Há mais de 20000 séries. Selecione um período menor."}
                headers=person+listOf("Data","Treino","Exercício","Repetições","Carga kg","Marcada")
                rows=actual.flatMap{session->session.sets.map{names(session.student)+listOf(session.date,session.title,it.exercise,it.repetitions.toString(),it.loadKg.toString(),it.done.toString())}}}
            "Hábitos"->{headers=person+listOf("Data","Hábito","Realizado","Meta","Unidade")
                rows=habits.map{names(it.student)+listOf(it.date,it.title,it.value.toString(),it.target.toString(),it.unit)}}
            "Tarefas"->{headers=person+listOf("Prazo","Título","Categoria","Prioridade","Concluída")
                rows=tasks.map{names(it.student)+listOf(it.dueDate,it.title,it.category,it.priority.toString(),it.done.toString())}}
            else->error("Tipo de exportação não reconhecido.")
        }
        require(rows.size<=20000){"Exportação acima do limite. Selecione um período menor."}
        return ProfessionalTools.csv(headers,rows)
    }
}
