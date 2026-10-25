package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate

/** Operational dashboard from the last Home script in the attached HTML. */
object HtmlHome {
    private val statSizes=mapOf("small" to 3,"medium" to 6,"large" to 12)
    private val panelSizes=mapOf("small" to 4,"medium" to 6,"large" to 8,"full" to 12)
    fun cardSpan(group:String,size:String):Int=when(group){"homeStats"->statSizes[size] ?: 3;"homeCards"->panelSizes[size] ?: 6;else->throw IllegalArgumentException("Grupo de layout inválido.")}

    data class Event(val id:String,val title:String,val date:String,val time:String,val section:String,val detail:String)
    data class Insight(val title:String,val text:String,val section:String)
    data class Summary(val todayEvents:Int,val overdue:Int,val pendingTasks:Int,val activeGoals:Int,val upcoming:List<Event>,val week:List<Pair<String,Int>>,val priorities:List<Insight>,val insights:List<Insight>)
    fun summarize(appointments:List<Appointment>,tasks:List<TaskItem>,notes:List<NotebookEntry>,goals:List<OrganizationGoal>,today:LocalDate=LocalDate.now()):Summary {
        val date=today.toString()
        fun valid(d:String)=runCatching{LocalDate.parse(d)}.isSuccess
        val appt=appointments.filter{it.status!="Cancelado"&&valid(it.date)}
        val events=appt.map{Event(it.id,it.student,it.date,it.time,"agenda",it.type)}+
            tasks.filter{!it.done&&valid(it.dueDate)}.map{Event(it.id,it.title,it.dueDate,it.time,"tasks",it.notes)}+
            goals.filter{it.status!="Concluída"&&valid(it.due)}.map{Event(it.id,it.title,it.due,"","goals",it.progress)}+
            notes.filter{!it.archived&&valid(it.reminder)}.map{Event(it.id,it.title,it.reminder,"","notes",it.body)}
        val lateTasks=tasks.filter{!it.done&&valid(it.dueDate)&&it.dueDate<date}
        val lateGoals=goals.filter{it.status!="Concluída"&&valid(it.due)&&it.due<date}
        val lateTaskIds=lateTasks.map{it.id}.toSet()
        val urgent=tasks.filter{!it.done&&it.priority>=2&&it.id !in lateTaskIds}
        val todays=events.count{it.date==date};val overdue=lateTasks.size+lateGoals.size
        val activeGoals=goals.count{it.status!="Concluída"}
        val priorities=buildList {
            if(lateTasks.isNotEmpty())add(Insight("${lateTasks.size} ${if(lateTasks.size==1)"tarefa" else "tarefas"} fora do prazo","Revise as datas ou conclua as pendências vencidas.","tasks"))
            if(lateGoals.isNotEmpty())add(Insight("${lateGoals.size} ${if(lateGoals.size==1)"meta" else "metas"} com prazo vencido","Atualize o acompanhamento e redefina os próximos passos.","goals"))
            if(urgent.isNotEmpty())add(Insight("${urgent.size} ${if(urgent.size==1)"tarefa" else "tarefas"} de alta prioridade","São as próximas pendências recomendadas para revisão.","tasks"))
            if(todays>0)add(Insight("$todays ${if(todays==1)"evento previsto" else "eventos previstos"} para hoje","Abra a agenda para conferir compromissos e lembretes.","agenda"))
            if(isEmpty())add(Insight("Sem alertas prioritários no momento","Nenhum prazo vencido ou tarefa urgente foi encontrado nos dados locais.","agenda"))
        }
        val insights=buildList {
            if(overdue>0)add(Insight("Revisar prazos vencidos","Há $overdue ${if(overdue==1)"registro atrasado" else "registros atrasados"}. Priorize essa revisão.",if(lateTasks.isNotEmpty())"tasks" else "goals"))
            if(overdue==0&&todays>0)add(Insight("Hoje está organizado","$todays ${if(todays==1)"item" else "itens"} no calendário de hoje, sem prazos vencidos.","agenda"))
            if(goals.isNotEmpty()&&goals.none{it.status=="Concluída"})add(Insight("Metas em acompanhamento","$activeGoals ${if(activeGoals==1)"meta ativa" else "metas ativas"}. Registre a evolução dos objetivos periodicamente.","goals"))
            if(notes.any{it.pinned&&!it.archived})add(Insight("Notas importantes fixadas","Você tem anotações destacadas para consulta rápida.","notes"))
            if(tasks.isEmpty()&&goals.isEmpty()&&notes.isEmpty()&&appt.isEmpty()){
                add(Insight("Estruture sua rotina","Comece cadastrando um compromisso, uma tarefa ou uma meta em Organização.","agenda"))
                add(Insight("Vincule informações aos clientes","Notas e objetivos podem ser associados aos clientes, sem duplicar seus cadastros.","notes"))
            }
            if(isEmpty())add(Insight("Acompanhamento em dia","Sem sinalizações adicionais nos registros atualmente disponíveis.","agenda"))
        }
        return Summary(todays,overdue,tasks.count{!it.done},activeGoals,events.filter{it.date>=date}.sortedWith(compareBy<Event>{it.date}.thenBy{it.time}).take(5),(0L..6L).map{offset->val d=today.plusDays(offset).toString();d to events.count{it.date==d}},priorities.take(4),insights.take(3))
    }
}
