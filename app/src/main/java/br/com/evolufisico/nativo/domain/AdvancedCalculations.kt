package br.com.evolufisico.nativo.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow

/** Educational equations; estimates only, not an automated diagnosis or prescription. */
object AdvancedCalculations {
    private fun valid(v: Double?, min: Double, max: Double): Double? =
        v?.takeIf { it.isFinite() && it in min..max }

    /** Relação cintura-estatura: measured in the same length units. */
    fun waistHeightRatio(waistCm: Double?, heightCm: Double?): Double? {
        val waist = valid(waistCm, 20.0, 300.0) ?: return null
        val height = valid(heightCm, 80.0, 250.0) ?: return null
        return waist / height
    }

    /** Body Roundness Index according to Thomas et al. Measurements in cm. */
    fun bri(waistCm: Double?, heightCm: Double?): Double? {
        val waist = valid(waistCm, 20.0, 300.0) ?: return null
        val height = valid(heightCm, 80.0, 250.0) ?: return null
        val radicand = 1.0 - (waist / (PI * height)).pow(2)
        if (radicand !in 0.0..1.0) return null
        return 364.2 - 365.5 * kotlin.math.sqrt(radicand)
    }

    /** Harris-Benedict 1984 revised equation, valid for adults. */
    fun harrisBenedictRevised(sex: String, age: Int?, weightKg: Double?, heightCm: Double?): Double? {
        val w = valid(weightKg, 20.0, 400.0) ?: return null
        val h = valid(heightCm, 100.0, 250.0) ?: return null
        val years = age?.takeIf { it in 18..120 } ?: return null
        return when (sex) {
            "Masculino", "M" -> 88.362 + 13.397 * w + 4.799 * h - 5.677 * years
            "Feminino", "F" -> 447.593 + 9.247 * w + 3.098 * h - 4.330 * years
            else -> null
        }
    }

    /** Historical Harris-Benedict 1919 equation, for comparison. */
    fun harrisBenedictClassic(sex: String, age: Int?, weightKg: Double?, heightCm: Double?): Double? {
        val w = valid(weightKg, 20.0, 400.0) ?: return null
        val h = valid(heightCm, 100.0, 250.0) ?: return null
        val years = age?.takeIf { it in 18..120 } ?: return null
        return when (sex) {
            "Masculino", "M" -> 66.473 + 13.7516 * w + 5.0033 * h - 6.755 * years
            "Feminino", "F" -> 655.0955 + 9.5634 * w + 1.8496 * h - 4.6756 * years
            else -> null
        }
    }

    /** Katch-McArdle from measured/estimated fat-free mass, not total body mass. */
    fun katchMcArdle(weightKg: Double?, fatPercent: Double?): Double? {
        val w = valid(weightKg, 20.0, 400.0) ?: return null
        val fat = valid(fatPercent, 0.0, 80.0) ?: return null
        return 370.0 + 21.6 * w * (1 - fat / 100.0)
    }

    /** Energy expenditure estimate, PAL is a chosen multiplier rather than an observed result. */
    fun dailyExpenditure(basalKcal: Double?, factor: Double?): Double? {
        val basal = valid(basalKcal, 500.0, 6000.0) ?: return null
        val pal = valid(factor, 1.0, 2.4) ?: return null
        return basal * pal
    }

    /** Target weight holds fat-free mass constant; does not assert actual future body composition. */
    fun weightForTargetFat(weightKg: Double?, currentFatPercent: Double?, targetFatPercent: Double?): Double? {
        val w = valid(weightKg, 20.0, 400.0) ?: return null
        val current = valid(currentFatPercent, 0.0, 80.0) ?: return null
        val target = valid(targetFatPercent, 0.0, 80.0) ?: return null
        return w * (1.0 - current / 100.0) / (1.0 - target / 100.0)
    }

    /** The heart-rate reserve formula is NOT a substitute for a supervised exercise assessment. */
    fun karvonen(maxHeartRate: Int?, restingHeartRate: Int?, intensityPercent: Int?): Double? {
        val max = maxHeartRate?.takeIf { it in 80..230 } ?: return null
        val rest = restingHeartRate?.takeIf { it in 30..150 && it < max } ?: return null
        val pct = intensityPercent?.takeIf { it in 40..90 } ?: return null
        return rest + (max - rest) * (pct / 100.0)
    }
}
