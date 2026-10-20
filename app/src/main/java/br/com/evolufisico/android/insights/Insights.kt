package br.com.evolufisico.android.insights

import br.com.evolufisico.android.backup.BackupSnapshot
import java.time.LocalDate
import java.time.LocalDateTime

enum class InsightKind(val label:String) {
    BILLING("Cobranças"),APPOINTMENT("Agenda"),GOAL("Metas")
}
data class Insight(
    val kind:InsightKind,val clientId:String,val title:String,
    val dateIso:String,val amountCents:Long=0
)
data class InsightSummary(
    val overdueCents:Long,val dueSoonCents:Long,val lateGoals:Int,
    val upcomingAppointments:Int,val alerts:List<Insight>
)

/** Alertas locais de operação, sem inferências de saúde ou julgamentos pessoais. */
object Insights {
    fun calculate(snapshot:BackupSnapshot,now:LocalDate=LocalDate.now()):InsightSummary {
        val alerts=mutableListOf<Insight>()
        var overdue=0L
        var soon=0L
        var lateGoals=0
        var appointments=0
        snapshot.charges.filterNot{it.isReceived}.forEach { c ->
            val date=runCatching{LocalDate.parse(c.dueDateIso)}.getOrNull()?:return@forEach
            when {
                date.isBefore(now) -> {
                    overdue += c.amountCents
                    alerts += Insight(InsightKind.BILLING,c.clientId,
                        "Cobrança atrasada: "+c.description,c.dueDateIso,c.amountCents)
                }
                !date.isAfter(now.plusDays(7)) -> {
                    soon += c.amountCents
                    alerts += Insight(InsightKind.BILLING,c.clientId,
                        "Cobrança vence em breve: "+c.description,c.dueDateIso,c.amountCents)
                }
            }
        }
        snapshot.operations.goals.filterNot{it.completed}.forEach { g ->
            val date=runCatching{LocalDate.parse(g.deadlineIso)}.getOrNull()?:return@forEach
            if(date.isBefore(now))lateGoals++
            if(!date.isAfter(now.plusDays(14))) {
                alerts += Insight(InsightKind.GOAL,g.clientId,
                    if(date.isBefore(now))"Meta com prazo vencido: "+g.title
                    else "Prazo de meta próximo: "+g.title,g.deadlineIso)
            }
        }
        snapshot.operations.appointments.filter{it.status=="Agendado"}.forEach { a ->
            val date=runCatching{LocalDateTime.parse(a.startIso).toLocalDate()}.getOrNull()
                ?:return@forEach
            if(!date.isBefore(now)&&!date.isAfter(now.plusDays(7))) {
                appointments++
                alerts += Insight(InsightKind.APPOINTMENT,a.clientId,
                    "Atendimento agendado: "+a.service,a.startIso)
            } else if(date.isBefore(now)) {
                alerts += Insight(InsightKind.APPOINTMENT,a.clientId,
                    "Atendimento com data passada: "+a.service,a.startIso)
            }
        }
        return InsightSummary(overdue,soon,lateGoals,appointments,
            alerts.sortedWith(compareBy<Insight>{it.dateIso.take(10)}.thenBy{it.kind.ordinal}))
    }
}
