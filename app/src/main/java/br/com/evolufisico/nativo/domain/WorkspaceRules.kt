package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*
import java.time.LocalDate
import java.text.Normalizer
import java.util.Locale

/** Explicit validation shared by editors, repository writes and backup restoration. */
object WorkspaceRules {
    fun date(value: String): LocalDate {
        require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(value)) { "Use uma data no formato AAAA-MM-DD." }
        return try { LocalDate.parse(value) } catch (_: Exception) { error("Informe uma data válida.") }
    }
    private fun identity(id: String, studentId: String, student: String) {
        require(id.isNotBlank() && id.length <= 128) { "Identificador inválido." }
        require(studentId.length <= 128 && student.length <= 200) { "Vínculo de cliente inválido." }
        require(studentId.isBlank() || student.isNotBlank()) { "Informe o nome do cliente vinculado." }
    }
    private fun title(value: String) {
        require(value.isNotBlank() && value.length <= 120) { "O título deve ter de 1 a 120 caracteres." }
    }
    fun validate(item: TrainingSession) {
        identity(item.id,item.studentId,item.student); title(item.title); date(item.date)
        require(item.durationMinutes in 1..720) { "Duração deve estar entre 1 e 720 minutos." }
        require(item.effort == null || item.effort in 0..10) { "Esforço deve estar entre 0 e 10." }
        require(item.sets.size<=200 && item.sets.map{it.id}.distinct().size==item.sets.size){"Séries acima do limite ou com identificador duplicado."}
        item.sets.forEach { row ->
            require(row.id.isNotBlank() && row.id.length<=128 && row.exercise.isNotBlank() && row.exercise.length<=120){"Informe um exercício válido para cada série."}
            require(row.repetitions in 1..1000 && row.loadKg.isFinite() && row.loadKg in 0.0..2000.0){"Repetições (1–1000) ou carga (0–2000 kg) inválidas."}
        }
        require(!item.isTemplate || (!item.completed && item.sets.none{it.done} && item.effort==null)){"Modelos não podem conter execução concluída."}
        require(item.exercises.length <= 12000 && item.notes.length <= 4000) { "Texto do treino acima do limite." }
    }
    fun validate(item: HabitEntry) {
        identity(item.id,item.studentId,item.student); title(item.title); date(item.date)
        require(item.value.isFinite() && item.value in 0.0..1_000_000.0) { "Valor deve estar entre 0 e 1000000." }
        require(item.target.isFinite() && item.target > 0.0 && item.target <= 1_000_000.0) { "Meta deve ser positiva, até 1000000." }
        require(item.unit.isNotBlank() && item.unit.length <= 30) { "Informe uma unidade de até 30 caracteres." }
        require(item.notes.length <= 4000) { "Observação acima de 4000 caracteres." }
    }
    fun validate(item: NotebookEntry) {
        identity(item.id,item.studentId,item.student); title(item.title); date(item.date)
        require(item.category.isNotBlank() && item.category.length<=60){"Categoria deve ter até 60 caracteres."}
        require(item.body.length <= 12000) { "Nota acima de 12000 caracteres." }
    }
    fun validate(item: TaskItem) {
        require(item.id.isNotBlank() && item.id.length <= 128) { "Identificador inválido." }
        title(item.title); date(item.dueDate)
        identity(item.id,item.studentId,item.student)
        require(item.priority in 1..3){"Prioridade inválida."}
        require(item.category.isNotBlank() && item.category.length<=60){"Categoria deve ter até 60 caracteres."}
        require(item.notes.length <= 4000) { "Observação acima de 4000 caracteres." }
    }
    fun habitProgress(value: Double, target: Double): Float? {
        if (!value.isFinite() || !target.isFinite() || value < 0 || target <= 0) return null
        return (value / target).coerceIn(0.0, 1.0).toFloat()
    }
    fun search(text: String, query: String): Boolean {
        fun normalize(value: String) = Normalizer.normalize(value,Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"),"").lowercase(Locale.ROOT)
        val haystack=normalize(text)
        return normalize(query).trim().split(Regex("\\s+")).all { it in haystack }
    }
    fun latest(evaluations: List<Evaluation>): List<Evaluation> = evaluations.groupBy { it.studentId }
        .values.mapNotNull { rows -> rows.maxWithOrNull(compareBy<Evaluation> { it.date }.thenBy { it.id }) }
        .sortedBy { it.name.lowercase(Locale.ROOT) }
    fun overdue(task: TaskItem, today: LocalDate = LocalDate.now()) =
        !task.done && runCatching { date(task.dueDate) < today }.getOrDefault(false)

    fun audit(training:List<TrainingSession>, habits:List<HabitEntry>, notes:List<NotebookEntry>, clientIds:Set<String>):List<String> {
        val issues=mutableListOf<String>()
        fun <T> inspect(label:String,rows:List<T>,id:(T)->String,client:(T)->String,validate:(T)->Unit) {
            if(rows.map(id).distinct().size!=rows.size)issues.add("$label: identificadores duplicados.")
            rows.forEachIndexed { index,item ->
                runCatching{validate(item)}.onFailure{issues.add("$label ${index+1}: ${it.message}")}
                if(client(item).isNotBlank() && client(item) !in clientIds)issues.add("$label ${index+1}: cliente vinculado sem avaliação cadastrada.")
            }
        }
        inspect("Treino",training,{it.id},{it.studentId},::validate)
        inspect("Hábito",habits,{it.id},{it.studentId},::validate)
        inspect("Nota",notes,{it.id},{it.studentId},::validate)
        return issues
    }
}
