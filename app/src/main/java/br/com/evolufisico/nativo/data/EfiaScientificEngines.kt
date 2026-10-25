package br.com.evolufisico.nativo.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Offline, deterministic adaptation of the five EFIA reference engines (EF78-EF82).
 * They read the same immutable Assessment records as the rest of the native app.
 * Never manufacture measurement provenance, SEM, clinical thresholds, adherence or causation.
 */
internal object EfiaScientificEngines {
    private val measures = linkedMapOf(
        "peso" to ("Peso" to "kg"),
        "cintura" to ("Cintura" to "cm"),
        "gordura" to ("Gordura corporal" to "p.p."),
        "massaMagra" to ("Massa livre de gordura" to "kg")
    )
    internal data class Point(
        val assessment: Assessment,
        val day: LocalDate,
        val measures: Map<String, Double?>,
        val fatMethod: String?,
        val device: String
    )
    data class Delta(
        val change: Double?,
        val mdc95: Double?,
        val detectable: Boolean?,
        val comparable: Boolean,
        val reason: String
    )
    data class Trend(
        val status: String,
        val robust30: Double?,
        val linear30: Double?,
        val outlierDates: List<String>,
        val reason: String
    )
    data class Balance(
        val date: String,
        val status: String,
        val differenceKg: Double?
    )
    data class Result(
        val eligible: Boolean,
        val spanDays: Long,
        val warnings: List<String>,
        val ef78QualityIssues: List<String>,
        val ef79Changes: Map<String, Delta>,
        val ef80Balances: List<Balance>,
        val ef81RecordedFacts: List<String>,
        val ef82Trends: Map<String, Trend>
    ) {
        fun asText(): String = buildString {
            appendLine("Auditoria EF78–EF82 — análise local, descritiva e não diagnóstica.")
            warnings.forEach { appendLine("Atenção: " + it) }
            if (!eligible) {
                appendLine("Resultados comparativos bloqueados: mínimo de três datas, período de 21 dias e sete dias entre as duas últimas coletas.")
                return@buildString
            }
            appendLine("EF78 — qualidade e protocolos")
            if (ef78QualityIssues.isEmpty()) appendLine("Nenhuma inconsistência encontrada nos campos avaliados; padronização não implica acurácia.")
            else ef78QualityIssues.forEach { appendLine("• " + it) }
            appendLine("EF79 — diferenças observadas; não representam mudança mínima detectável sem SEM documentado")
            ef79Changes.forEach { (key, delta) ->
                val label = measures[key]?.first ?: key
                appendLine("• " + label + ": " + (delta.change?.let { format(it) + " " + (measures[key]?.second ?: "") } ?: "indisponível") + " — " + delta.reason)
            }
            appendLine("EF80 — coerência e procedência")
            if (ef80Balances.isEmpty()) appendLine("Nenhuma relação de composição verificável registrada.")
            ef80Balances.forEach {
                appendLine("• " + it.date + ": " + it.status + (it.differenceKg?.let { d -> " (diferença aritmética " + format(d) + " kg)" } ?: ""))
            }
            appendLine("EF81 — contexto informado")
            if (ef81RecordedFacts.isEmpty()) appendLine("Sem campos estruturados de eventos/adesão; não há inferência causal a partir de observações livres.")
            else ef81RecordedFacts.forEach { appendLine("• " + it) }
            appendLine("EF82 — tendências robusta e linear")
            ef82Trends.forEach { (key, trend) ->
                val label = measures[key]?.first ?: key
                appendLine("• " + label + ": " + trend.status + "; " + trend.reason)
                if (trend.robust30 != null && trend.linear30 != null) {
                    appendLine("  Taxas exploratórias: Theil–Sen " + format(trend.robust30) + " e mínimos quadrados " + format(trend.linear30) + " por 30 dias; sem projeção.")
                }
            }
            appendLine("As estimativas derivadas não são medidas independentes; confira sempre métodos, equipamento e padronização da coleta.")
        }
    }

    fun analyze(input: List<Assessment>, sem: Map<String, Double> = emptyMap(), semSources: Map<String, String> = emptyMap()): Result {
        val warnings = mutableListOf<String>()
        val uniqueIds = input.map { it.id }
        val validDates = input.map { runCatching { LocalDate.parse(it.date) }.getOrNull() }
        val distinctDates = input.map { it.date }.distinct().size == input.size
        if (input.size !in 3..4) warnings += "Selecione três ou quatro avaliações."
        if (input.map { it.studentId }.distinct().size > 1) warnings += "Avaliações de alunos diferentes não podem ser combinadas."
        if (uniqueIds.distinct().size != uniqueIds.size) warnings += "Há registros duplicados."
        if (validDates.any { it == null } || !distinctDates) warnings += "Datas inválidas ou duplicadas."
        val dates = validDates.filterNotNull().sorted()
        val span = if (dates.size > 1) ChronoUnit.DAYS.between(dates.first(), dates.last()) else 0L
        if (span < 21L) warnings += "Período inferior a 21 dias."
        if (dates.size >= 2 && ChronoUnit.DAYS.between(dates[dates.size - 2], dates.last()) < 7L) {
            warnings += "As duas últimas coletas estão separadas por menos de sete dias."
        }
        // EFIA reference validity ranges: weight 20..350, waist 40..200, fat 2..65, lean 10..250.
        val points = input.mapNotNull { row ->
            val day = runCatching { LocalDate.parse(row.date) }.getOrNull() ?: return@mapNotNull null
            val method = when {
                row.bioimpedanceBodyFatPercent != null -> "bia"
                row.pollock7 != null -> "pollock7"
                row.pollock3 != null -> "pollock3"
                else -> null
            }
            val fat = when (method) {
                "bia" -> row.bioimpedanceBodyFatPercent
                "pollock7" -> row.pollock7?.bodyFatPercent
                "pollock3" -> row.pollock3?.bodyFatPercent
                else -> null // Unknown method never becomes a comparable estimate.
            }
            val lean = if (method == "bia") row.bioimpedanceLeanMassKg else null
            fun inRange(number: Double?, min: Double, max: Double) = number?.takeIf { it.isFinite() && it in min..max }
            Point(row, day, mapOf(
                "peso" to inRange(row.weightKg, 20.0, 350.0),
                "cintura" to inRange(row.waistCm, 40.0, 200.0),
                "gordura" to inRange(fat, 2.0, 65.0),
                "massaMagra" to inRange(lean, 10.0, 250.0)
            ), method, if (method == "bia") row.bioimpedanceDevice.trim() else "")
        }.sortedBy { it.day }
        if (points.size != input.size) warnings += "Existe avaliação com data inválida."
        if (points.none { p -> p.measures.values.any { it != null } }) warnings += "Nenhuma medida utilizável."
        val eligible = warnings.isEmpty() && points.any { p ->
            p.measures.keys.any { k -> points.count { it.measures[k] != null } >= 3 }
        }

        // EF80 is run first: invalid mass can suppress all derived composition comparisons.
        val balances = points.mapNotNull { p ->
            val weight = p.measures["peso"] ?: return@mapNotNull null
            val lean = p.measures["massaMagra"] ?: return@mapNotNull null
            val fat = p.measures["gordura"]
            when {
                lean > weight -> Balance(p.assessment.date, "incompatível: massa livre maior que peso", round2(lean - weight))
                fat == null -> null
                else -> {
                    val difference = abs(lean - weight * (1.0 - fat / 100.0))
                    val tolerance = max(1.0, weight * 0.015) // transcription screening, never clinical MDC
                    Balance(p.assessment.date, if (difference > tolerance) "revisar procedência; relação entre medições não confirmada"
                        else "coerência aritmética, sem confirmação de mesma aferição", round2(difference))
                }
            }
        }
        val invalidComposition = balances.any { it.status.startsWith("incompatível") }
        fun sameSource(key: String): Boolean {
            if (key !in listOf("gordura", "massaMagra")) return true
            val selected = points.filter { it.measures[key] != null }
            if (selected.isEmpty() || invalidComposition) return false
            if (selected.any { it.fatMethod == null }) return false
            if (selected.any { it.fatMethod == "bia" && it.device.isEmpty() }) return false
            return selected.map { it.fatMethod + "|" + it.device }.distinct().size == 1
        }
        val quality = mutableListOf<String>()
        val protocols = points.filter { it.measures["gordura"] != null }.map { it.fatMethod }.distinct()
        if (protocols.size > 1) quality += "Métodos de gordura diferentes ao longo do histórico."
        val devices = points.map { it.device }.filter { it.isNotBlank() }.distinct()
        if (devices.size > 1) quality += "Equipamentos de bioimpedância diferentes."
        if (points.any { it.fatMethod == "bia" && it.device.isBlank() }) quality += "BIA sem aparelho identificado; comparação de composição bloqueada."
        if (points.any { it.assessment.bodyFatPercent != null }) quality += "Gordura informada sem protocolo permanece independente da estimativa."
        if (points.any { p -> p.measures.keys.any { p.measures[it] == null } }) quality += "Há dados ausentes para um ou mais indicadores."
        if (invalidComposition) quality += "Massa livre maior que peso; comparações de composição bloqueadas."
        quality += "Padronização e origem da coleta não foram confirmadas por campos estruturados."

        // EF79: only comparable, eligible and complete edges can produce a difference.
        // No SEM or MDC is synthesized: a professional must provide BOTH numeric SEM and provenance.
        val deltas = measures.keys.associateWith { key ->
            val present = points.count { it.measures[key] != null }
            val comparable = sameSource(key)
            val canCompare = eligible && present >= 3 && comparable &&
                points.firstOrNull()?.measures?.get(key) != null && points.lastOrNull()?.measures?.get(key) != null
            val rawChange = if (canCompare) points.last().measures.getValue(key)!! - points.first().measures.getValue(key)!! else null
            val semValue = sem[key]?.takeIf { it.isFinite() && it > 0.0 }
            val sourced = semSources[key]?.trim()?.takeIf { it.length > 3 }
            val mdc = if (semValue != null && sourced != null) 1.96 * sqrt(2.0) * semValue else null
            Delta(rawChange?.let(::round2), mdc?.let(::round2),
                if (rawChange != null && mdc != null) abs(rawChange) > mdc else null,
                comparable,
                when {
                    !eligible -> "janela ou identidade inválida"
                    !comparable -> "método ou aparelho não comparável"
                    !canCompare -> "série insuficiente ou extremidades ausentes"
                    mdc == null -> "MDC indisponível: SEM e fonte não informados"
                    else -> "SEM com fonte informada; validação técnica externa necessária"
                })
        }

        // EF81 requires explicit structured context; free text notes never becomes adherence evidence.
        val facts = points.mapNotNull { point ->
            point.assessment.contextEvent.takeIf { it in setOf(
                "Início de treino", "Interrupção de treino",
                "Início de plano alimentar", "Interrupção de plano alimentar"
            ) }?.let { point.assessment.date + ": " + it + " (registro explícito, sem causalidade presumida)" }
        }

        // EF82: Theil-Sen and OLS are exploratory. With 3-4 samples, stability and outliers
        // are deliberately NOT certified (the reference requires at least five).
        val trends = measures.keys.associateWith { key ->
            val selected = points.filter { it.measures[key] != null }
            if (!eligible || selected.size < 3 || !sameSource(key) ||
                ChronoUnit.DAYS.between(selected.firstOrNull()?.day ?: LocalDate.MIN, selected.lastOrNull()?.day ?: LocalDate.MIN) < 21
            ) Trend("insuficiente", null, null, emptyList(), "Série insuficiente ou métodos não comparáveis.")
            else {
                val t0 = selected.first().day
                val coords = selected.map { ChronoUnit.DAYS.between(t0, it.day).toDouble() to it.measures.getValue(key)!! }
                val slopes = coords.indices.flatMap { i ->
                    (i + 1 until coords.size).mapNotNull { j ->
                        val interval = coords[j].first - coords[i].first
                        if (interval <= 0) null else (coords[j].second - coords[i].second) / interval
                    }
                }
                val robust = median(slopes)
                val avgX = coords.map { it.first }.average()
                val avgY = coords.map { it.second }.average()
                val denominator = coords.sumOf { (x, _) -> (x - avgX) * (x - avgX) }
                val linear = if (denominator > 0) coords.sumOf { (x, y) -> (x - avgX) * (y - avgY) } / denominator else null
                Trend("preliminar", robust?.let { round2(it * 30) }, linear?.let { round2(it * 30) },
                    emptyList(), "Somente " + selected.size + " datas; não certificar estabilidade, causalidade ou valores atípicos.")
            }
        }
        return Result(eligible, span, warnings.distinct(), quality.distinct(), deltas, balances, facts, trends)
    }

    private fun round2(v: Double) = round(v * 100.0) / 100.0
    private fun format(v: Double) = java.text.NumberFormat.getNumberInstance(java.util.Locale("pt", "BR")).format(v)
    private fun median(values: List<Double>): Double? {
        if (values.isEmpty()) return null
        val ordered = values.sorted()
        return (ordered[(ordered.size - 1) / 2] + ordered[ordered.size / 2]) / 2.0
    }
}
