package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/** Quality-control alerts only. None of these are medical diagnoses or automatic risk scores. */
data class QualityAlert(val key: String, val explanation: String)
object AssessmentChecks {
    fun review(current: Evaluation, earlier: Evaluation? = null): List<QualityAlert> {
        val result = mutableListOf<QualityAlert>()
        fun issue(key: String, detail: String) { result.add(QualityAlert(key, detail)) }
        fun metric(key: String, value: Double?, range: ClosedFloatingPointRange<Double>) {
            if (value != null && (!value.isFinite() || value !in range)) issue(key, "Confira $key: valor fora dos limites de entrada utilizados pelo aplicativo.")
        }
        metric("peso", current.weightKg, 20.0..400.0)
        metric("altura", current.heightCm, 100.0..250.0)
        metric("cintura", current.waistCm, 20.0..250.0)
        metric("quadril", current.hipCm, 30.0..250.0)
        metric("gordura aferida", current.bodyFatPercent, 0.0..100.0)
        current.perimeters.forEach { (k,v) -> metric("perímetro $k",v,1.0..300.0) }
        current.skinfolds.forEach { (k,v) -> metric("dobra $k",v,0.1..150.0) }
        current.bioimpedance.forEach { (k,v) -> if (!v.isFinite()) issue("bioimpedância $k", "Valor não numérico na bioimpedância.") }
        val reportedFat = current.bodyFatPercent
        val instrumentFat = current.bioimpedance["pgordura"]
        if (reportedFat != null && instrumentFat != null &&
            reportedFat in 0.0..100.0 && instrumentFat in 0.0..100.0 && abs(reportedFat - instrumentFat) > 5)
            issue("BIA", "Gordura informada e campo de BIA diferem mais de 5 pontos percentuais; confira a transcrição.")
        val instrumentWeight = current.bioimpedance["peso"]
        if (current.weightKg != null && instrumentWeight != null &&
            current.weightKg > 0 && instrumentWeight > 0 && abs(current.weightKg - instrumentWeight) > 3)
            issue("peso BIA", "Pesos registrados na avaliação e na BIA diferem mais de 3 kg; confira datas e equipamentos.")
        val bmi = Metrics.bmi(current.weightKg, current.heightCm)
        val instrumentBmi = current.bioimpedance["imc"]
        if (bmi != null && instrumentBmi != null && instrumentBmi > 0 && abs(bmi - instrumentBmi) > 1)
            issue("IMC BIA", "IMC calculado e IMC transcrito do aparelho diferem mais de 1 unidade.")
        val folds = Anthropometry.pollock7(current.sex,current.age,current.weightKg,current.skinfolds)
        if (folds?.clamped == true) issue("Pollock", "Resultado Pollock limitado a 3–60%; revise as sete dobras.")
        if ((folds?.totalMm ?: 0.0) > 280.0) issue("dobras", "Soma das dobras superior a 280 mm; confirme técnica e pertinência do protocolo.")
        if (current.trainingExperienced && current.trainingAdherence == null) issue("treino", "Treino aplicável, mas a aderência não foi medida. Não contabilizada como zero.")
        if (current.dietExperienced && current.dietAdherence == null) issue("dieta", "Plano alimentar aplicável, mas a aderência não foi medida. Não contabilizada como zero.")
        if (earlier != null && earlier.studentId == current.studentId) {
            val from = runCatching { LocalDate.parse(earlier.date) }.getOrNull()
            val to = runCatching { LocalDate.parse(current.date) }.getOrNull()
            if (from != null && to != null) {
                val elapsed = ChronoUnit.DAYS.between(from,to)
                if (elapsed <= 0) issue("datas", "A avaliação anterior deve anteceder a atual para permitir uma comparação temporal.")
                if (elapsed > 0 && current.weightKg != null && earlier.weightKg != null &&
                    abs(current.weightKg - earlier.weightKg) / elapsed * 7.0 > 5.0)
                    issue("variação de peso", "Mudança superior a 5 kg por semana entre registros; confira as datas e a digitação.")
            }
        }
        return result.distinctBy { it.key }
    }
}
