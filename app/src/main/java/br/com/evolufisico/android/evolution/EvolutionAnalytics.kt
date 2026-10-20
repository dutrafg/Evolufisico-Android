package br.com.evolufisico.android.evolution

import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.AssessmentRules
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.IsoFields
import kotlin.math.abs

/** Série descritiva, não interpreta melhora/piora clínica. */
enum class EvolutionMetric(val title: String, val unit: String) {
    WEIGHT("Peso", "kg"),
    BMI("IMC", "kg/m²"),
    WAIST("Cintura", "cm"),
    HIP("Quadril", "cm"),
    ARM("Braço", "cm"),
    THIGH("Coxa", "cm"),
    WHR("Relação cintura/quadril", "razão");

    fun value(a: Assessment): Double? = when (this) {
        WEIGHT -> a.weightKg
        BMI -> if (a.weightKg > 0.0 && a.heightCm > 0.0)
            AssessmentRules.bmi(a.weightKg, a.heightCm) else null
        WAIST -> a.waistCm
        HIP -> a.hipCm
        ARM -> a.armCm
        THIGH -> a.thighCm
        WHR -> AssessmentRules.waistHipRatio(a.waistCm, a.hipCm)
    }?.takeIf { it.isFinite() && it > 0.0 }
}

enum class EvolutionPeriod(val title: String) {
    ALL("Por avaliação"),
    MONTHLY("Mensal"),
    QUARTERLY("Trimestral")
}

data class EvolutionPoint(val date: LocalDate, val assessmentId: String, val value: Double)
data class EvolutionSeries(
    val metric: EvolutionMetric,
    val period: EvolutionPeriod,
    val points: List<EvolutionPoint>,
    val ignoredInvalid: Int
) {
    val first: Double? get() = points.firstOrNull()?.value
    val last: Double? get() = points.lastOrNull()?.value
    val absoluteChange: Double? get() = if (points.size < 2) null else last!! - first!!
    val percentChange: Double? get() =
        if (points.size < 2 || first == null || first == 0.0) null
        else (last!! - first!!) / first!! * 100.0
}

object EvolutionAnalytics {
    /**
     * Agrupamentos mensais/trimestrais selecionam a última avaliação que
     * possua aquela medida no período. Desempate determinístico pelo ID.
     * Os cálculos não misturam clientes e não projetam medições futuras.
     */
    fun build(
        all: List<Assessment>,
        clientId: String,
        metric: EvolutionMetric,
        period: EvolutionPeriod
    ): EvolutionSeries {
        require(clientId.isNotBlank())
        val selected = all.filter { it.clientId == clientId }
        var ignored = 0
        val valid = selected.mapNotNull { a ->
            val day = try { LocalDate.parse(a.dateIso) }
                      catch (_: DateTimeParseException) { null }
            val value = metric.value(a)
            if (day == null || value == null) {
                ignored++
                null
            } else {
                EvolutionPoint(day, a.id, value)
            }
        }.sortedWith(compareBy<EvolutionPoint> { it.date }.thenBy { it.assessmentId })
        val points = if (period == EvolutionPeriod.ALL) valid else
            valid.groupBy { point ->
                when (period) {
                    EvolutionPeriod.MONTHLY -> "${point.date.year}-${point.date.monthValue}"
                    EvolutionPeriod.QUARTERLY ->
                        "${point.date.year}-Q${point.date.get(IsoFields.QUARTER_OF_YEAR)}"
                    EvolutionPeriod.ALL -> error("Este agrupamento não deveria ser alcançado")
                }
            }.values.map { it.last() }.sortedWith(
                compareBy<EvolutionPoint> { it.date }.thenBy { it.assessmentId }
            )
        return EvolutionSeries(metric, period, points, ignored)
    }
}
