package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.Evaluation
import java.time.LocalDate

object EvaluationTimeline {
    data class Choice(val id: String, val label: String)
    data class Point(val x: Float, val y: Float)

    /** IDs, rather than dates, distinguish two check-ins taken on the same day. */
    fun choices(history: List<Evaluation>): List<Choice> {
        val counts = history.groupingBy { it.date }.eachCount()
        val seen = mutableMapOf<String, Int>()
        return history.map {
            val index = (seen[it.date] ?: 0) + 1
            seen[it.date] = index
            Choice(it.id, if ((counts[it.date] ?: 0) > 1) "${it.date} • $index" else it.date)
        }
    }

    /** Coordinates preserve elapsed calendar time; equal dates share the same X. */
    fun points(series: List<Pair<String, Double>>): List<Point> {
        if (series.isEmpty() || series.any { !it.second.isFinite() }) return emptyList()
        val days = series.map { runCatching { LocalDate.parse(it.first).toEpochDay() }.getOrNull()
            ?: return emptyList() }
        val first = days.minOrNull()!!; val last = days.maxOrNull()!!
        val min = series.minOf { it.second }; val max = series.maxOf { it.second }
        return series.mapIndexed { index, (_, value) ->
            Point(if (first == last) .5f else ((days[index] - first).toDouble() / (last - first)).toFloat(),
                if (min == max) .5f else (1.0 - (value - min) / (max - min)).toFloat())
        }
    }
}
