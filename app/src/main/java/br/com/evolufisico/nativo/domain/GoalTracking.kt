package br.com.evolufisico.nativo.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Progress against a user-entered target. This is descriptive tracking, not clinical advice. */
object GoalTracking {
    data class Progress(val percent: Double, val remaining: Double, val targetDateDays: Long?)

    fun validateTarget(baseline: Double?, target: Double?, range: ClosedFloatingPointRange<Double>): Boolean {
        if (target == null) return true
        if (!target.isFinite() || target !in range || baseline == null || !baseline.isFinite() || baseline !in range) return false
        return kotlin.math.abs(baseline - target) >= 0.01
    }

    fun progress(baseline: Double?, current: Double?, target: Double?, date: LocalDate? = null): Progress? {
        if (baseline == null || current == null || target == null ||
            !baseline.isFinite() || !current.isFinite() || !target.isFinite()) return null
        val denominator = target - baseline
        if (kotlin.math.abs(denominator) < 0.01) return null
        val ratio = ((current - baseline) / denominator).coerceIn(0.0, 1.0)
        val remaining = if ((current-baseline)/denominator >= 1.0) 0.0 else kotlin.math.abs(target - current)
        val days = if (date == null) null else ChronoUnit.DAYS.between(LocalDate.now(), date)
        return Progress(ratio * 100.0, remaining, days)
    }

    fun summary(label: String, baseline: Double?, current: Double?, target: Double?, unit: String,
                date: LocalDate? = null): String {
        val progress = progress(baseline, current, target, date) ?: return "$label: dados insuficientes para acompanhar a meta."
        val deadline = progress.targetDateDays?.let {
            when { it < 0 -> " · prazo vencido há ${-it} dias"; it == 0L -> " · prazo hoje"; else -> " · ${it} dias restantes" }
        }.orEmpty()
        return "$label: ${"%.0f".format(java.util.Locale.ROOT, progress.percent)}% do percurso · faltam ${"%.1f".format(java.util.Locale.ROOT, progress.remaining)} $unit$deadline."
    }
}
