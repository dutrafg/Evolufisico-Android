package br.com.evolufisico.android.timeline

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.forms.FormCatalog
import java.time.LocalDate

enum class TimelineKind(val label:String) {
    ASSESSMENT("Avaliação"), FORM("Formulário"),
    APPOINTMENT("Atendimento"), CHARGE("Cobrança"), GOAL("Meta")
}
data class TimelineEvent(
    val dateIso:String,val kind:TimelineKind,val title:String,val detail:String=""
)

/** Linha do tempo descritiva, gerada sob demanda a partir dos dados existentes. */
object ClientTimeline {
    fun build(snapshot:BackupSnapshot,clientId:String):List<TimelineEvent> {
        require(snapshot.clients.any{it.id==clientId}) { "Cliente não encontrado." }
        val events=mutableListOf<TimelineEvent>()
        snapshot.assessments.filter{it.clientId==clientId}.forEach { a ->
            events += TimelineEvent(a.dateIso,TimelineKind.ASSESSMENT,
                "Avaliação "+a.trainingLevel, "Peso "+a.weightKg+" kg")
        }
        snapshot.forms.filter{it.clientId==clientId}.forEach { f ->
            events += TimelineEvent(f.dateIso,TimelineKind.FORM,
                FormCatalog.find(f.templateId)?.title ?: f.templateId)
        }
        snapshot.operations.appointments.filter{it.clientId==clientId}.forEach { a ->
            events += TimelineEvent(a.startIso.take(10),TimelineKind.APPOINTMENT,
                a.service,a.status+" · "+a.startIso.substringAfter('T', ""))
        }
        snapshot.charges.filter{it.clientId==clientId}.forEach { c ->
            events += TimelineEvent(c.dueDateIso,TimelineKind.CHARGE,
                c.description, if(c.isReceived)"Recebida" else "Pendente")
            if(c.receivedDateIso!=null && c.receivedDateIso!=c.dueDateIso)
                events += TimelineEvent(c.receivedDateIso,TimelineKind.CHARGE,
                    "Recebimento: "+c.description, "Confirmado")
        }
        snapshot.operations.goals.filter{it.clientId==clientId}.forEach { g ->
            events += TimelineEvent(g.deadlineIso,TimelineKind.GOAL,
                g.title,if(g.completed)"Concluída" else "Prazo")
        }
        return events.filter { runCatching{LocalDate.parse(it.dateIso)}.isSuccess }
            .sortedWith(compareByDescending<TimelineEvent>{it.dateIso}.thenBy{it.kind.ordinal})
    }
}
