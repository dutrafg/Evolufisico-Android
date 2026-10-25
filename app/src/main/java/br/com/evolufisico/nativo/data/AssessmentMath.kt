package br.com.evolufisico.nativo.data

import kotlin.math.ln
import kotlin.math.round

data class BodyFatEstimate(
    val sumMm: Double,
    val density: Double,
    val bodyFatPercent: Double,
    val fatMassKg: Double,
    val leanMassKg: Double,
    val wasClamped: Boolean
)

object AssessmentMath {
    fun bmi(weightKg: Double, heightCm: Double): Double? {
        if (!weightKg.isFinite() || !heightCm.isFinite()) return null
        if (weightKg <= 0.0 || heightCm <= 0.0) return null
        val heightM = heightCm / 100.0
        if (heightM !in 0.5..2.7 || weightKg !in 2.0..500.0) return null
        return oneDecimal(weightKg / (heightM * heightM))
    }

    fun parseNumber(value: String): Double? =
        value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }

    fun navyBodyFat(sex: String, heightCm: Double, waistCm: Double, neckCm: Double, hipCm: Double?): Double? {
        if (!listOf(heightCm, waistCm, neckCm).all { it.isFinite() && it > 0.0 }) return null
        val circumference = when (sex) {
            "Masculino" -> waistCm - neckCm
            "Feminino" -> waistCm + (hipCm?.takeIf { it.isFinite() && it > 0.0 } ?: return null) - neckCm
            else -> return null
        }
        if (circumference <= 0.0 || heightCm !in 80.0..250.0) return null
        val logarithm = ln(circumference) / ln(10.0)
        val heightLog = ln(heightCm) / ln(10.0)
        val denominator = if (sex == "Masculino") 1.0324 - 0.19077 * logarithm + 0.15456 * heightLog
            else 1.29579 - 0.35004 * logarithm + 0.22100 * heightLog
        if (denominator <= 0.0) return null
        return (495.0 / denominator - 450.0).takeIf { it in 1.0..70.0 }?.let(::oneDecimal)
    }

    fun rfmBodyFat(sex: String, heightCm: Double, waistCm: Double): Double? {
        if (sex !in listOf("Masculino", "Feminino") || !heightCm.isFinite() || !waistCm.isFinite()) return null
        if (heightCm <= 0.0 || waistCm <= 0.0) return null
        val base = if (sex == "Masculino") 64.0 else 76.0
        return (base - 20.0 * (heightCm / waistCm)).takeIf { it in 1.0..70.0 }?.let(::oneDecimal)
    }

    fun pollock7(sex: String, ageYears: Int, weightKg: Double, foldsMm: List<Double?>): BodyFatEstimate? {
        if (sex !in listOf("Masculino", "Feminino") || ageYears !in 18..80) return null
        if (!weightKg.isFinite() || weightKg !in 2.0..500.0 || foldsMm.size != 7) return null
        val folds = foldsMm.map { it?.takeIf { value -> value.isFinite() && value in 1.0..100.0 } ?: return null }
        val sum = folds.sum()
        val density = if (sex == "Masculino") {
            1.112 - (0.00043499 * sum) + (0.00000055 * sum * sum) - (0.00028826 * ageYears)
        } else {
            1.097 - (0.00046971 * sum) + (0.00000056 * sum * sum) - (0.00012828 * ageYears)
        }
        if (!density.isFinite() || density <= 0.0) return null
        val rawPercent = ((4.95 / density) - 4.50) * 100.0
        if (!rawPercent.isFinite()) return null
        val wasClamped = rawPercent < 3.0 || rawPercent > 60.0
        val percent = rawPercent.coerceIn(3.0, 60.0)
        val fatMass = weightKg * percent / 100.0
        return BodyFatEstimate(
            sumMm = oneDecimal(sum),
            density = round(density * 10000.0) / 10000.0,
            bodyFatPercent = oneDecimal(percent),
            fatMassKg = oneDecimal(fatMass),
            leanMassKg = oneDecimal(weightKg - fatMass),
            wasClamped = wasClamped
        )
    }

    fun harrisBenedict(sex: String, weightKg: Double, heightCm: Double, ageYears: Int): Double? {
        if (sex !in listOf("Masculino", "Feminino") || !validAdult(weightKg, heightCm, ageYears)) return null
        val result = if (sex == "Masculino") 88.362 + 13.397 * weightKg + 4.799 * heightCm - 5.677 * ageYears
            else 447.593 + 9.247 * weightKg + 3.098 * heightCm - 4.330 * ageYears
        return result.takeIf { it > 0.0 }?.let(::oneDecimal)
    }

    fun mifflinStJeor(sex: String, weightKg: Double, heightCm: Double, ageYears: Int): Double? {
        if (sex !in listOf("Masculino", "Feminino") || !validAdult(weightKg, heightCm, ageYears)) return null
        val result = 10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears + if (sex == "Masculino") 5.0 else -161.0
        return result.takeIf { it > 0.0 }?.let(::oneDecimal)
    }

    fun maximumHeartRate(ageYears: Int): Int? =
        if (ageYears in 18..100) oneDecimal(208.0 - 0.7 * ageYears).toInt() else null

    private fun validAdult(weightKg: Double, heightCm: Double, ageYears: Int): Boolean =
        weightKg.isFinite() && heightCm.isFinite() && weightKg in 2.0..500.0 && heightCm in 80.0..250.0 && ageYears in 18..100

    private fun oneDecimal(value: Double): Double = round(value * 10.0) / 10.0
}
