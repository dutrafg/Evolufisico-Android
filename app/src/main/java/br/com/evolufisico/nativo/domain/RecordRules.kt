package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*

/** Structural rules used before saving and before any restore transaction starts. */
object RecordRules {
    private fun id(value:String) { require(value.isNotBlank() && value.length<=128) { "Identificador inválido." } }
    private fun contact(studentId:String,student:String) {
        require(studentId.length<=128 && student.isNotBlank() && student.length<=200) { "Informe um contato de até 200 caracteres." }
    }
    fun validate(e:Evaluation) {
        id(e.id);id(e.studentId);contact(e.studentId,e.name);WorkspaceRules.date(e.date)
        if(e.nextAssessment.isNotBlank())WorkspaceRules.date(e.nextAssessment)
        if(e.goalDate.isNotBlank())WorkspaceRules.date(e.goalDate)
        require(e.age==null || e.age in 1..120) { "Idade inválida." }
        require(e.energy==null || e.energy in 0..10) { "Energia inválida." }
        require(e.trainingAdherence==null || e.trainingAdherence in 0..100) { "Aderência de treino inválida." }
        require(e.dietAdherence==null || e.dietAdherence in 0..100) { "Aderência alimentar inválida." }
        val numbers=listOf(e.weightKg,e.heightCm,e.waistCm,e.hipCm,e.bodyFatPercent,
            e.goalWeightKg,e.goalWeightBaselineKg,e.goalBodyFatPercent,e.goalBodyFatBaselinePercent)
        require(numbers.all{it==null || it.isFinite()} &&
            (e.perimeters.values+e.skinfolds.values+e.bioimpedance.values).all{it.isFinite()}) { "Medida não numérica." }
        require(e.notes.length<=12000 && e.goalNotes.length<=500) { "Observações acima do limite." }
    }
    fun validate(c:Charge) {
        id(c.id);contact(c.studentId,c.student);WorkspaceRules.date(c.dueDate)
        require(c.secondStudentId.length<=128 && c.secondStudent.length<=200 &&
            (c.secondStudentId.isBlank() || c.secondStudent.isNotBlank())) { "Segundo contato inválido." }
        require(c.studentId.isBlank() || c.secondStudentId.isBlank() || c.studentId!=c.secondStudentId) { "Uma cobrança em dupla precisa de clientes diferentes." }
        require(c.amountCents in 0..1_000_000_000_000L && c.discountCents in 0..c.amountCents) { "Valor ou desconto inválido." }
        require(c.installments in 1..24 && !(c.paid && c.exempt)) { "Parcelas ou situação de pagamento inválidas." }
        require(c.service.isNotBlank() && c.service.length<=200 && c.paymentMethod.isNotBlank() && c.paymentMethod.length<=80) { "Serviço ou forma de pagamento inválidos." }
        if(c.paidDate.isNotBlank())WorkspaceRules.date(c.paidDate)
        require(c.notes.length<=4000) { "Observações acima de 4000 caracteres." }
    }
    fun validate(i:Intake) {
        id(i.id);contact(i.studentId,i.student);WorkspaceRules.date(i.date)
        require(i.level in listOf("Iniciante","Intermediário","Avançado")) { "Nível do formulário inválido." }
        require(listOf(i.objective,i.healthHistory,i.routine,i.observations).all{it.length<=12000}) { "Texto do formulário acima do limite." }
        require(i.answers.size<=200 && i.answers.all{it.key.isNotBlank() && it.key.length<=128 && it.value.length<=12000}) { "Respostas do formulário acima do limite." }
    }
}
