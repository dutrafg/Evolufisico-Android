import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.domain.Agenda
import br.com.evolufisico.nativo.domain.EvaluationTimeline

fun agendaTimelineTests() {
    val event=Appointment(id="a", student="Teste", date="2026-12-31", time="23:30", durationMinutes=60)
    val next=event.copy(id="b",date="2027-01-01",time="00:00")
    eq("agenda cruza meia-noite",Agenda.overlaps(event,next),true)
    eq("agenda limite adjacente",Agenda.overlaps(event,next.copy(time="00:30")),false)
    eq("agenda mesmo ID",Agenda.overlaps(event,event),false)
    eq("agenda cancelado",Agenda.overlaps(event,next.copy(status="Cancelado")),false)
    eq("agenda concluído",Agenda.overlaps(event,next.copy(status="Concluído")),false)
    eq("agenda faltou",Agenda.overlaps(event,next.copy(status="Faltou")),false)
    eq("agenda confirmado ocupa",Agenda.overlaps(event,next.copy(status="Confirmado")),true)
    val series=Agenda.recurrence(event,12)
    eq("recorrência quantidade",series.size,12)
    eq("recorrência IDs",series.map{it.id}.distinct().size,12)
    eq("recorrência preserva original",series.first(),event)
    eq("recorrência ano novo",series[1].date,"2027-01-07")
    eq("recorrência horário",series.all{it.time=="23:30"},true)
    fun rejects(label:String,action:()->Unit) {
        eq(label,runCatching(action).exceptionOrNull() is IllegalArgumentException,true)
    }
    rejects("lote conflito tardio") { Agenda.checkBatch(series,listOf(series.last().copy(id="occupied"))) }
    rejects("lote IDs duplicados") { Agenda.checkBatch(listOf(event,event),emptyList()) }
    rejects("lote conflito interno") { Agenda.checkBatch(listOf(event,next),emptyList()) }
    Agenda.checkBatch(listOf(event),listOf(event))
    eq("edição não conflita consigo",event.id,"a")
    Agenda.checkBatch(series,emptyList())
    eq("lote válido não modifica original",event.date,"2026-12-31")
    rejects("recorrência zero") { Agenda.recurrence(event,0) }
    rejects("recorrência 13") { Agenda.recurrence(event,13) }
    rejects("agenda data inexistente") { Agenda.validate(event.copy(date="2026-02-30")) }
    rejects("agenda hora inválida") { Agenda.validate(event.copy(time="24:00")) }
    rejects("agenda segundos não aceitos") { Agenda.validate(event.copy(time="09:00:30")) }
    rejects("agenda duração zero") { Agenda.validate(event.copy(durationMinutes=0)) }
    rejects("agenda contato vazio") { Agenda.validate(event.copy(student=" ")) }
    rejects("agenda situação desconhecida") { Agenda.validate(event.copy(status="???")) }
    val records=listOf(Evaluation(id="one",date="2026-10-01"),Evaluation(id="two",date="2026-10-01"),
        Evaluation(id="three",date="2026-10-22"))
    val choices=EvaluationTimeline.choices(records)
    eq("comparação rótulos exclusivos",choices.map{it.label}.distinct().size,3)
    eq("comparação IDs preservados",choices.map{it.id},listOf("one","two","three"))
    val points=EvaluationTimeline.points(listOf("2026-10-01" to 70.0,"2026-10-02" to 71.0,"2026-10-11" to 72.0))
    near("gráfico intervalos proporcionais",points[1].x.toDouble(),.1,.0001)
    near("gráfico primeiro valor",points[0].y.toDouble(),1.0,.0001)
    near("gráfico último valor",points[2].y.toDouble(),0.0,.0001)
    val flat=EvaluationTimeline.points(listOf("2026-10-01" to 70.0,"2026-10-01" to 70.0))
    eq("gráfico mesmo dia centralizado",flat.map{it.x},listOf(.5f,.5f))
    eq("gráfico constante centralizado",flat.map{it.y},listOf(.5f,.5f))
    eq("gráfico recusa não finito",EvaluationTimeline.points(listOf("2026-10-01" to Double.NaN)),emptyList<EvaluationTimeline.Point>())
    eq("gráfico recusa data inválida",EvaluationTimeline.points(listOf("2026-02-30" to 70.0)),emptyList<EvaluationTimeline.Point>())
    eq("gráfico vazio",EvaluationTimeline.points(emptyList()),emptyList<EvaluationTimeline.Point>())
}
