package br.com.evolufisico.nativo.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Time positions for longitudinal charts; intervals must reflect real elapsed days. */
object ChartMath {
    fun datePositions(dates: List<String>): List<Float>? {
        if (dates.size < 2) return null
        val parsed = dates.map { runCatching { LocalDate.parse(it) }.getOrNull() ?: return null }
        if (parsed.zipWithNext().any { (a, b) -> !b.isAfter(a) }) return null
        val totalDays = ChronoUnit.DAYS.between(parsed.first(), parsed.last())
        if (totalDays <= 0L) return null
        return parsed.map {
            (ChronoUnit.DAYS.between(parsed.first(), it).toDouble() / totalDays).toFloat()
        }
    }
}
