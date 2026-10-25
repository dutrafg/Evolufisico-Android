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


data class AnalysisTrend(
    val first: Double,
    val latest: Double,
    val absoluteChange: Double,
    val relativeChangePercent: Double?,
    val changePer30Days: Double,
    val directionChanged: Boolean
)

/** Descriptive time-series helpers. These never emit clinical classifications or predictions. */
object AnalysisMath {
    fun coverageDays(dates: List<String>): Long? {
        val parsed = dates.map { runCatching { java.time.LocalDate.parse(it) }.getOrNull() ?: return null }
        if (parsed.size < 2 || parsed.distinct().size != parsed.size) return null
        return java.time.temporal.ChronoUnit.DAYS.between(parsed.minOrNull(), parsed.maxOrNull())
    }

    fun isEligibleWindow(dates: List<String>): Boolean =
        dates.size in 3..4 && dates.distinct().size == dates.size && (coverageDays(dates) ?: 0L) >= 21L

    fun completeness(total: Int, present: Int): Int? {
        if (total <= 0 || present !in 0..total) return null
        return kotlin.math.round(present * 100.0 / total).toInt()
    }

    fun trend(dates: List<String>, values: List<Double?>): AnalysisTrend? {
        if (dates.size !in 3..4 || dates.size != values.size || dates.distinct().size != dates.size) return null
        val ordered = dates.zip(values).map { (date, value) ->
            val parsed = runCatching { java.time.LocalDate.parse(date) }.getOrNull() ?: return null
            val checked = value?.takeIf { it.isFinite() } ?: return null
            parsed to checked
        }.sortedBy { it.first }
        val days = java.time.temporal.ChronoUnit.DAYS.between(ordered.first().first, ordered.last().first)
        if (days < 21) return null
        val changes = ordered.zipWithNext { a, b -> b.second - a.second }
        val directions = changes.map { it.compareTo(0.0) }.filter { it != 0 }
        val first = ordered.first().second
        val latest = ordered.last().second
        val delta = latest - first
        return AnalysisTrend(
            first = oneDecimal(first),
            latest = oneDecimal(latest),
            absoluteChange = oneDecimal(delta),
            relativeChangePercent = if (first == 0.0) null else oneDecimal(delta / kotlin.math.abs(first) * 100.0),
            changePer30Days = oneDecimal(delta / days * 30.0),
            directionChanged = directions.distinct().size > 1
        )
    }

    fun report(studentName: String, assessments: List<Assessment>): String {
        val rows = assessments.distinctBy { it.date }.sortedBy { it.date }
        val dates = rows.map { it.date }
        val days = coverageDays(dates)
        val presentSlots = rows.sumOf { row ->
            listOf(row.weightKg, row.bmi, row.waistCm, row.hipCm, row.bodyFatPercent, row.pollock7?.bodyFatPercent)
                .count { it != null }
        }
        val totalSlots = rows.size * 6
        val completeness = completeness(totalSlots, presentSlots)
        val lines = mutableListOf(
            "Aluno: $studentName",
            "Avaliações: ${rows.size} em datas distintas; período ${dates.firstOrNull() ?: "—"} a ${dates.lastOrNull() ?: "—"}; ${days?.let { "$it dias" } ?: "intervalo indisponível"}.",
            "Completude documental: ${completeness?.let { "$it% ($presentSlots de $totalSlots campos possíveis)" } ?: "não calculável"}. Isso mede preenchimento, não precisão clínica."
        )
        if (!isEligibleWindow(dates)) {
            lines += "Dados insuficientes para tendências: são necessárias três a quatro datas distintas cobrindo pelo menos 21 dias."
        } else {
            val measures = listOf(
                Triple("Peso", "kg", rows.map { it.weightKg }),
                Triple("IMC", "", rows.map { it.bmi }),
                Triple("Cintura", "cm", rows.map { it.waistCm }),
                Triple("Quadril", "cm", rows.map { it.hipCm }),
                Triple("Gordura corporal registrada (método não informado)", "%", rows.map { it.bodyFatPercent }),
                Triple("Gordura estimada por Pollock 7", "%", rows.map { it.pollock7?.bodyFatPercent })
            )
            lines += "Mudanças descritivas (primeira → última avaliação; taxa por 30 dias é aritmética, sem projeção):"
            measures.forEach { (label, unit, values) ->
                val result = trend(dates, values)
                if (result == null) lines += "$label: dados incompletos para comparar todas as datas."
                else {
                    val sign = if (result.absoluteChange > 0) "+" else ""
                    val suffix = if (unit.isBlank()) "" else " $unit"
                    val reversal = if (result.directionChanged) " · mudança de direção entre intervalos" else ""
                    lines += "$label: ${number(result.first)}$suffix → ${number(result.latest)}$suffix (${sign}${number(result.absoluteChange)}$suffix; ${sign}${number(result.changePer30Days)}$suffix por 30 dias)$reversal."
                }
            }
        }
        lines += "Bioimpedância não é registrada atualmente no formulário Android; não foi feita comparação Pollock/BIA."
        lines += "Confirme protocolo, equipamento e condições de coleta. Este resumo é descritivo, exploratório e não diagnóstico; não prevê resultados nem recomenda tratamento."
        return lines.joinToString("\n")
    }

    private fun number(value: Double): String = java.text.NumberFormat.getNumberInstance(java.util.Locale("pt", "BR")).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }.format(value)
}
