package br.com.evolufisico.nativo.domain

/** Permanent IDs only: never infer ownership from a display name. */
object ClientBackupScope {
    fun includes(kind: String, targetId: String, studentId: String,
        secondStudentId: String = "", secondStudent: String = "", isTemplate: Boolean = false): Boolean {
        require(targetId.isNotBlank()) { "Selecione um cliente" }
        if (studentId != targetId) return false
        if (kind == "training" && isTemplate) return false
        if (kind == "charge" && (secondStudentId.isNotBlank() || secondStudent.isNotBlank())) return false
        return kind in setOf("evaluation", "charge", "intake", "appointment", "task", "training", "habit", "note", "organizationGoal")
    }
}
