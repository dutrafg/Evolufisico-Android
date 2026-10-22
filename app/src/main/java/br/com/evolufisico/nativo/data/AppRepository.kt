package br.com.evolufisico.nativo.data

import android.content.Context
import br.com.evolufisico.nativo.domain.Agenda
import br.com.evolufisico.nativo.domain.WorkspaceRules
import androidx.compose.runtime.mutableStateListOf
import br.com.evolufisico.nativo.system.AppointmentNotifications

class AppRepository(private val context: Context) {
    val store = CryptoStore(context)
    val evaluations = mutableStateListOf<Evaluation>()
    val charges = mutableStateListOf<Charge>()
    val intakes = mutableStateListOf<Intake>()
    val appointments = mutableStateListOf<Appointment>()
    val tasks = mutableStateListOf<TaskItem>()

    val trainingSessions = mutableStateListOf<TrainingSession>()
    val habits = mutableStateListOf<HabitEntry>()
    val notes = mutableStateListOf<NotebookEntry>()
    val exercises = mutableStateListOf<ExerciseDefinition>()

    init { refresh() }
    fun refresh() {
        val nextEvaluations=store.list("evaluation").map(Evaluation::fromJson).sortedWith(compareByDescending<Evaluation>{it.date}.thenByDescending{it.id})
        val nextCharges=store.list("charge").map(Charge::fromJson).sortedWith(compareByDescending<Charge>{it.dueDate}.thenBy{it.id})
        val nextIntakes=store.list("intake").map(Intake::fromJson).sortedWith(compareByDescending<Intake>{it.date}.thenBy{it.id})
        val nextAppointments=store.list("appointment").map(Appointment::fromJson).sortedWith(compareBy<Appointment>({it.date},{it.time},{it.id}))
        val nextTasks=store.list("task").map(TaskItem::fromJson).sortedWith(compareBy<TaskItem>{it.dueDate}.thenBy{it.id})
        val nextTraining=store.list("training").map { TrainingSession.fromJson(it).also(WorkspaceRules::validate) }.sortedWith(compareByDescending<TrainingSession>{it.date}.thenBy{it.id})
        val nextHabits=store.list("habit").map { HabitEntry.fromJson(it).also(WorkspaceRules::validate) }.sortedWith(compareByDescending<HabitEntry>{it.date}.thenBy{it.id})
        val nextNotes=store.list("note").map { NotebookEntry.fromJson(it).also(WorkspaceRules::validate) }.sortedWith(compareByDescending<NotebookEntry> { it.pinned }.thenByDescending { it.date }.thenBy{it.id})
        val nextExercises=store.list("exercise").map { ExerciseDefinition.fromJson(it).also(br.com.evolufisico.nativo.domain.ExerciseCatalog::validate) }.sortedBy{br.com.evolufisico.nativo.domain.TrainingInsights.exerciseKey(it.name)}
        androidx.compose.runtime.snapshots.Snapshot.withMutableSnapshot {
            evaluations.clear(); evaluations.addAll(nextEvaluations)
            charges.clear(); charges.addAll(nextCharges)
            intakes.clear(); intakes.addAll(nextIntakes)
            appointments.clear(); appointments.addAll(nextAppointments)
            tasks.clear(); tasks.addAll(nextTasks)
            trainingSessions.clear(); trainingSessions.addAll(nextTraining)
            habits.clear(); habits.addAll(nextHabits)
            notes.clear(); notes.addAll(nextNotes)
            exercises.clear(); exercises.addAll(nextExercises)
        }
    }
    fun rescheduleReminders() { appointments.forEach { runCatching{AppointmentNotifications.schedule(context,it)} } }
    fun save(item: Evaluation) { br.com.evolufisico.nativo.domain.RecordRules.validate(item); store.put("evaluation", item.id, item.toJson()); refresh() }
    fun save(item: Charge) { br.com.evolufisico.nativo.domain.RecordRules.validate(item); store.put("charge", item.id, item.toJson()); refresh() }
    fun save(item: Intake) { br.com.evolufisico.nativo.domain.RecordRules.validate(item); store.put("intake", item.id, item.toJson()); refresh() }
    fun save(item: Appointment) = saveAppointments(listOf(item))
    fun saveAppointments(items: List<Appointment>) {
        Agenda.checkBatch(items, appointments.toList())
        store.putAppointments(items)
        refresh()
        // Reminder scheduling cannot undo a committed visit or report a false save failure.
        items.forEach { runCatching { AppointmentNotifications.schedule(context, it) } }
    }
    fun save(item: TaskItem) { WorkspaceRules.validate(item); store.put("task", item.id, item.toJson()); refresh() }
    fun save(item: TrainingSession) { WorkspaceRules.validate(item); store.put("training",item.id,item.toJson()); refresh() }
    fun save(item: HabitEntry) { WorkspaceRules.validate(item); store.put("habit",item.id,item.toJson()); refresh() }
    fun save(item: NotebookEntry) { WorkspaceRules.validate(item); store.put("note",item.id,item.toJson()); refresh() }
    fun save(item: ExerciseDefinition) {
        br.com.evolufisico.nativo.domain.ExerciseCatalog.checkUnique(item,exercises.toList())
        store.put("exercise",item.id,item.toJson());refresh()
    }
    fun delete(item: ExerciseDefinition) { store.delete("exercise",item.id);refresh() }
    fun delete(item: TrainingSession) { store.delete("training",item.id); refresh() }
    fun delete(item: HabitEntry) { store.delete("habit",item.id); refresh() }
    fun delete(item: NotebookEntry) { store.delete("note",item.id); refresh() }
    fun delete(item: Appointment) {
        store.delete("appointment",item.id);refresh()
        // Cancellation failure must not present a committed deletion as failed.
        runCatching{AppointmentNotifications.cancel(context,item.id)}
    }
    fun delete(item: TaskItem) { store.delete("task", item.id); refresh() }
    fun delete(item: Evaluation) { store.delete("evaluation", item.id); refresh() }
    fun delete(item: Charge) { store.delete("charge", item.id); refresh() }
    fun delete(item: Intake) { store.delete("intake", item.id); refresh() }
}
