package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*

/** One coherent view of records linked by permanent ID; names never join clients. */
object ClientWorkspace {
    data class Snapshot(val profile:Evaluation,val evaluations:List<Evaluation>,val charges:List<Charge>,
        val appointments:List<Appointment>,val tasks:List<TaskItem>,val training:List<TrainingSession>,
        val habits:List<HabitEntry>,val notes:List<NotebookEntry>)
    fun snapshot(id:String,evaluations:List<Evaluation>,charges:List<Charge>,appointments:List<Appointment>,
        tasks:List<TaskItem>,training:List<TrainingSession>,habits:List<HabitEntry>,notes:List<NotebookEntry>):Snapshot {
        require(id.isNotBlank()){"Selecione um cliente."}
        val history=evaluations.filter{it.studentId==id}.sortedWith(compareByDescending<Evaluation>{it.date}.thenByDescending{it.id})
        val profile=history.firstOrNull() ?: error("Perfil do cliente não encontrado.")
        return Snapshot(profile,history,charges.filter{it.studentId==id || it.secondStudentId==id},
            appointments.filter{it.studentId==id}.sortedWith(compareByDescending<Appointment>{it.date}.thenBy{it.time}.thenBy{it.id}),
            ProfessionalTools.taskOrder(tasks.filter{it.studentId==id}),
            training.filter{!it.isTemplate && it.studentId==id}.sortedWith(compareByDescending<TrainingSession>{it.date}.thenBy{it.id}),
            habits.filter{it.studentId==id}.sortedWith(compareByDescending<HabitEntry>{it.date}.thenBy{it.id}),
            notes.filter{it.studentId==id && !it.archived}.sortedWith(compareByDescending<NotebookEntry>{it.pinned}.thenByDescending{it.date}.thenBy{it.id}))
    }
    fun editContact(profile:Evaluation,name:String,phone:String,email:String):Evaluation {
        val cleanName=name.trim();val cleanPhone=phone.trim();val cleanEmail=email.trim()
        require(cleanName.isNotBlank() && cleanName.length<=200){"Nome deve conter de 1 a 200 caracteres."}
        require(cleanPhone.length<=40 && cleanEmail.length<=200){"Contato acima do limite de tamanho."}
        require(listOf(cleanName,cleanPhone,cleanEmail).none{text->text.any{it.isISOControl()}}){"Contato não pode conter quebras de linha ou controles."}
        return profile.copy(name=cleanName,phone=cleanPhone,email=cleanEmail)
    }
}
