import br.com.evolufisico.nativo.domain.Metrics
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.AdvancedCalculations
import br.com.evolufisico.nativo.domain.AssessmentChecks
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.data.readLimited
import java.io.ByteArrayInputStream
import br.com.evolufisico.nativo.domain.Financial
import br.com.evolufisico.nativo.domain.IntegrityAuditor
import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.Charge
import br.com.evolufisico.nativo.data.Intake
import br.com.evolufisico.nativo.data.TaskItem
import kotlin.math.abs

private var assertions=0
fun eq(label:String,actual:Any?,expected:Any?){assertions++;check(actual==expected){"$label esperado=$expected obtido=$actual"}}
fun near(label:String,actual:Double?,expected:Double,tolerance:Double=.01){
    assertions++;check(actual!=null&&abs(actual-expected)<tolerance){"$label esperado=$expected obtido=$actual"}
}
fun main(){
    near("imc",Metrics.bmi(80.0,180.0),24.691358)
    eq("imc sem altura",Metrics.bmi(80.0,0.0),null)
    eq("imc peso negativo",Metrics.bmi(-30.0,173.0),null)
    eq("imc altura ausente",Metrics.bmi(30.0,null),null)
    near("rcq",Metrics.waistHipRatio(76.0,100.0),.76)
    eq("rcq medida zero",Metrics.waistHipRatio(76.0,0.0),null)
    near("massa gorda",Metrics.fatMassKg(90.0,20.0),18.0)
    eq("gordura percent excedido",Metrics.fatMassKg(90.0,101.0),null)
    near("aderencia ambos",Metrics.adherence(true,80,true,100),90.0)
    near("aderencia dieta n/a",Metrics.adherence(true,80,false,0),80.0)
    near("aderencia treino n/a",Metrics.adherence(false,0,true,90),90.0)
    eq("aderencia ambos n/a",Metrics.adherence(false,0,false,0),null)
    eq("aderencia vazio",Metrics.adherence(true,null,true,null),null)
    eq("aderencia nota negativa",Metrics.adherence(true,-5,false,null),null)
    eq("aderencia fora faixa",Metrics.adherence(true,105,false,null),null)
    near("tmb homem",Anthropometry.mifflinStJeor("Masculino",40,80.0,180.0),1730.0)
    near("tmb mulher",Anthropometry.mifflinStJeor("Feminino",40,80.0,180.0),1564.0)
    eq("tmb idade criança",Anthropometry.mifflinStJeor("Masculino",12,80.0,180.0),null)
    eq("tmb sexo ausente",Anthropometry.mifflinStJeor("Não informado",40,80.0,180.0),null)
    eq("tmb altura zero",Anthropometry.mifflinStJeor("Masculino",40,80.0,0.0),null)
    val measures=Anthropometry.sites.keys.associateWith {10.0}
    val p=Anthropometry.pollock7("Masculino",32,90.0,measures)
    check(p!=null); near("dobras soma",p.totalMm,70.0)
    eq("dobras incompletas",Anthropometry.pollock7("Masculino",32,90.0,emptyMap()),null)
    eq("dobras idade limite",Anthropometry.pollock7("Masculino",81,90.0,measures),null)
    eq("preco diaria",Financial.services["Diária"],2500L)
    eq("liquido",Financial.net(15000,2500),12500L)
    eq("liquido sem negativo",Financial.net(1000,1500),0L)
    eq("parcelas arredondadas",Financial.installments(1001,3),listOf(334L,334L,333L))
    eq("parcelas zero",Financial.installments(0,3),listOf(0L,0L,0L))
    near("desconto percentual",Financial.discountByPercent(15000,10.0).toDouble(),1500.0)
    eq("desconto 100",Financial.discountByPercent(15000,100.0),15000L)
    var rejected=false;try{Financial.installments(100,25)}catch(_:IllegalArgumentException){rejected=true};eq("parcelas inválidas",rejected,true)
    near("rce",AdvancedCalculations.waistHeightRatio(85.0,170.0),0.5)
    eq("rce sem altura",AdvancedCalculations.waistHeightRatio(85.0,null),null)
    near("bri",AdvancedCalculations.bri(85.0,170.0),3.2603,0.2)
    eq("bri faixa inválida",AdvancedCalculations.bri(1000.0,100.0),null)
    near("HB 1984 homem",AdvancedCalculations.harrisBenedictRevised("Masculino",40,80.0,180.0),1796.862,0.01)
    near("HB 1984 mulher",AdvancedCalculations.harrisBenedictRevised("Feminino",40,80.0,180.0),1571.793,0.01)
    near("HB 1919 homem",AdvancedCalculations.harrisBenedictClassic("Masculino",40,80.0,180.0),1796.995,0.01)
    eq("HB sexo não declarado",AdvancedCalculations.harrisBenedictClassic("N/A",40,80.0,180.0),null)
    near("Katch",AdvancedCalculations.katchMcArdle(80.0,20.0),1752.4)
    eq("Katch sem gordura",AdvancedCalculations.katchMcArdle(80.0,null),null)
    near("Gasto diário",AdvancedCalculations.dailyExpenditure(1730.0,1.55),2681.5)
    eq("PAL acima da faixa",AdvancedCalculations.dailyExpenditure(1730.0,3.0),null)
    near("peso alvo",AdvancedCalculations.weightForTargetFat(80.0,25.0,20.0),75.0)
    eq("peso alvo inválido",AdvancedCalculations.weightForTargetFat(80.0,25.0,100.0),null)
    near("Karvonen",AdvancedCalculations.karvonen(190,60,60),138.0)
    eq("Karvonen repouso acima máxima",AdvancedCalculations.karvonen(170,170,60),null)
    eq("Karvonen intensidade inválida",AdvancedCalculations.karvonen(190,60,100),null)
    val first = Evaluation(studentId="stuA",date="2026-06-01",weightKg=80.0,
        heightCm=175.0,trainingExperienced=false,dietExperienced=false)
    eq("sem alertas não aplicável",AssessmentChecks.review(first).size,0)
    val noAdherence = first.copy(trainingExperienced=true)
    eq("aderência aplicável não preenchida",AssessmentChecks.review(noAdherence).any{it.key=="treino"},true)
    val mismatch = first.copy(bodyFatPercent=16.0,bioimpedance=mapOf("pgordura" to 35.0, "peso" to 90.0))
    eq("alerta BIA gordura",AssessmentChecks.review(mismatch).any{it.key=="BIA"},true)
    eq("alerta peso BIA",AssessmentChecks.review(mismatch).any{it.key=="peso BIA"},true)
    val later=first.copy(id="later",date="2026-06-08",weightKg=89.0)
    eq("alerta digitação série temporal",AssessmentChecks.review(later,first).any{it.key=="variação de peso"},true)
    eq("não comparar identidades diferentes",AssessmentChecks.review(later,first.copy(studentId="stuB")).size,0)
    eq("cortesia não vira receita",Financial.billable(15000,0,true),0L)
    eq("parcela normal com desconto",Financial.billable(15000,1000,false),14000L)
    eq("leitura limitada no limite",ByteArrayInputStream(byteArrayOf(1,2,3)).readLimited(3).size,3)
    var tooBig = false
    try { ByteArrayInputStream(byteArrayOf(1,2,3,4)).readLimited(3) } catch(_: IllegalArgumentException) { tooBig=true }
    eq("leitura limitada impede excesso",tooBig,true)
    val cleanAudit=IntegrityAuditor.run(listOf(first.copy(name="Maria")), emptyList(), emptyList(), emptyList(), emptyList())
    eq("auditoria limpa: contagem",cleanAudit.recordCount,1)
    eq("auditoria limpa: ocorrências",cleanAudit.findings.size,0)
    val invoice = Charge(id="c-1",student="Cliente",amountCents=10000,discountCents=12000,
        dueDate="data-invalida",paid=true,exempt=true,installments=25)
    val appointment = Appointment(id="a-1",student="Cliente",date="2026-15-01",time="28:99",durationMinutes=-4)
    val intake = Intake(id="i-1",student="",date="2026-02-31")
    val task = TaskItem(id="t-1",title="",dueDate="2026-02-30")
    val corrupted = IntegrityAuditor.run(listOf(first,first.copy(id="later",weightKg=92.0,date="2026-06-08")),
        listOf(invoice),listOf(intake),listOf(appointment),listOf(task))
    eq("auditoria: contagem agregada",corrupted.recordCount,6)
    eq("auditoria: desconto",corrupted.findings.any{it.section=="Cobrança" && it.problem.contains("desconto")},true)
    eq("auditoria: parcelas",corrupted.findings.any{it.section=="Cobrança" && it.problem.contains("parcelas")},true)
    eq("auditoria: cortesia",corrupted.findings.any{it.section=="Cobrança" && it.problem.contains("Cortesia")},true)
    eq("auditoria: vencimento",corrupted.findings.any{it.section=="Cobrança" && it.problem.contains("vencimento")},true)
    eq("auditoria: hora",corrupted.findings.any{it.section=="Agenda" && it.problem.contains("Horário")},true)
    eq("auditoria: duração",corrupted.findings.any{it.section=="Agenda" && it.problem.contains("Duração")},true)
    eq("auditoria: anamnese",corrupted.findings.any{it.section=="Formulário" && it.problem.contains("Aluno")},true)
    eq("auditoria: tarefa",corrupted.findings.any{it.section=="Tarefa" && it.problem.contains("Título")},true)
    eq("auditoria: série temporal",corrupted.findings.any{it.section=="Histórico" && it.problem.contains("5 kg")},true)
    eq("auditoria: sem modificar dados",first.weightKg,80.0)
    val duplicate=IntegrityAuditor.run(listOf(first,first), emptyList(),emptyList(),emptyList(),emptyList())
    eq("auditoria: ID duplicado",duplicate.findings.any{it.problem.contains("duplicado")},true)
    val notApplicable=first.copy(trainingExperienced=false,trainingAdherence=0,dietExperienced=false,dietAdherence=0)
    eq("auditoria: n/a não zera escore",Metrics.adherence(notApplicable.trainingExperienced,notApplicable.trainingAdherence,
        notApplicable.dietExperienced,notApplicable.dietAdherence),null)
    eq("auditoria: diagnóstico não dá aptidão",cleanAudit.description().contains("não determina aptidão"),true)
    agendaTimelineTests()
    println("QA puro Kotlin: $assertions verificações PASSARAM.")
}
