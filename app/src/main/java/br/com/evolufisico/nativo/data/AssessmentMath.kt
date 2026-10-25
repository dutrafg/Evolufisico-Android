package br.com.evolufisico.nativo.data

import kotlin.math.round

object AssessmentMath {
    fun bmi(weightKg: Double, heightCm: Double): Double? {
        if (!weightKg.isFinite() || !heightCm.isFinite()) return null
        if (weightKg <= 0.0 || heightCm <= 0.0) return null
        val heightM = heightCm / 100.0
        if (heightM !in 0.5..2.7 || weightKg !in 2.0..500.0) return null
        return round((weightKg / (heightM * heightM)) * 10.0) / 10.0
    }

    fun parseNumber(value: String): Double? =
        value.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }
}
