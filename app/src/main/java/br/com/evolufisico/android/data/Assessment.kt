package br.com.evolufisico.android.data

import java.time.LocalDate
import java.util.UUID
import kotlin.math.round

/** Dados sensíveis: nunca registrar em logs, analíticos ou crash reports. */
data class Assessment(
    val id: String,
    val clientId: String,
    val dateIso: String,
    val trainingLevel: String,
    val goal: String,
    val limitations: String,
    val weeklyFrequency: Int,
    val weightKg: Double,
    val heightCm: Double,
    val waistCm: Double?,
    val hipCm: Double?,
    val armCm: Double?,
    val thighCm: Double?,
    val healthHistory: String = "",
    val dailyHabits: String = "",
    val trainingExperience: String = "",
    val chestCm:Double?=null,
    val abdomenCm:Double?=null,
    val calfCm:Double?=null,
    val forearmCm:Double?=null
) {
    companion object {
        fun new(
            clientId: String, dateIso: String, trainingLevel: String,
            goal: String, limitations: String, weeklyFrequency: Int,
            weightKg: Double, heightCm: Double, waistCm: Double?,
            hipCm: Double?, armCm: Double?, thighCm: Double?
        ) = Assessment(UUID.randomUUID().toString(), clientId, dateIso, trainingLevel,
            goal, limitations, weeklyFrequency, weightKg, heightCm, waistCm, hipCm, armCm, thighCm)
    }
}

object AssessmentRules {
    val levels = listOf("Iniciante", "Intermediário", "Avançado")

    /** Aceita vírgula ou ponto como separador decimal; inválidos retornam NaN. */
    fun parseNumber(input: String, required: Boolean): Double? {
        val s = input.trim()
        if (s.isEmpty()) return if (required) Double.NaN else null
        if (s.count { it == ',' || it == '.' } > 1) return Double.NaN
        return s.replace(',', '.').toDoubleOrNull() ?: Double.NaN
    }

    fun validate(record: Assessment, knownClientIds: Set<String>): String? {
        if (record.clientId !in knownClientIds) return "Selecione um cliente válido."
        val date = runCatching { LocalDate.parse(record.dateIso) }.getOrNull()
            ?: return "Use a data no formato AAAA-MM-DD."
        if (date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.now().minusYears(120)))
            return "A data da avaliação deve ser válida e não pode ser futura."
        if (record.trainingLevel !in levels) return "Escolha um nível de atividade válido."
        if (record.goal.length > 200 || record.limitations.length > 600)
            return "O objetivo ou as observações ultrapassam o limite permitido."
        if (record.healthHistory.length > 700 ||
            record.dailyHabits.length > 700 || record.trainingExperience.length > 700)
            return "Os campos da anamnese devem ter até 700 caracteres."
        if (record.weeklyFrequency !in 0..7) return "A frequência semanal deve estar entre 0 e 7."
        if (!record.weightKg.isFinite() || record.weightKg !in 10.0..500.0)
            return "Informe um peso entre 10 e 500 kg."
        if (!record.heightCm.isFinite() || record.heightCm !in 50.0..250.0)
            return "Informe uma altura entre 50 e 250 cm."
        for ((label, value) in listOf("cintura" to record.waistCm, "quadril" to record.hipCm,
            "braço" to record.armCm, "coxa" to record.thighCm,
            "tórax" to record.chestCm, "abdômen" to record.abdomenCm,
            "panturrilha" to record.calfCm, "antebraço" to record.forearmCm)) {
            if (value != null && (!value.isFinite() || value !in 10.0..300.0))
                return "A medida de $label deve estar entre 10 e 300 cm."
        }
        return null
    }

    fun bmi(weightKg: Double, heightCm: Double): Double {
        require(weightKg.isFinite() && weightKg > 0 && heightCm.isFinite() && heightCm > 0)
        val meters = heightCm / 100.0
        return round((weightKg / (meters * meters)) * 10.0) / 10.0
    }

    /** Relação cintura/quadril. Não é diagnóstico clínico. */
    fun waistHipRatio(waistCm: Double?, hipCm: Double?): Double? {
        if (waistCm == null || hipCm == null || !waistCm.isFinite()
            || !hipCm.isFinite() || waistCm <= 0 || hipCm <= 0) return null
        return round((waistCm / hipCm) * 100.0) / 100.0
    }
}
