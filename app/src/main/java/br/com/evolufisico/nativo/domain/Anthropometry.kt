package br.com.evolufisico.nativo.domain

import kotlin.math.pow

/** 7-site Jackson & Pollock density followed by Siri. Not a clinical diagnosis. */
data class SkinfoldResult(val totalMm: Double, val density: Double, val fatPercent: Double,
    val fatMassKg: Double, val leanMassKg: Double, val clamped: Boolean)

object Anthropometry {
    val sites = linkedMapOf("triceps" to "Tríceps", "subescapular" to "Subescapular",
        "peitoral" to "Peitoral", "axilar" to "Axilar média", "suprailiaca" to "Supra-ilíaca",
        "abdominal" to "Abdominal", "coxa" to "Coxa")
    val perimeters = linkedMapOf("pescoco" to "Pescoço", "ombros" to "Ombros",
        "abdomen" to "Abdômen", "braco_dir" to "Braço direito",
        "braco_esq" to "Braço esquerdo", "coxa_dir" to "Coxa direita",
        "coxa_esq" to "Coxa esquerda", "panturrilha" to "Panturrilha", "cintura" to "Cintura", "quadril" to "Quadril")
    val bioFields = linkedMapOf("peso" to "Peso BIA (kg)", "pgordura" to "Gordura BIA (%)",
        "pmusculo" to "Músculo BIA (%)", "imc" to "IMC do aparelho",
        "gordura_kg" to "Massa de gordura aferida (kg)",
        "mme" to "Massa muscular esquelética / magra do aparelho (kg)",
        "tmb" to "TMB aferida (kcal)", "visceral" to "Gordura visceral (índice)",
        "idade_corporal" to "Idade corporal", "rcq" to "RCQ do aparelho")

    fun pollock7(sex: String, age: Int?, weight: Double?, folds: Map<String, Double>): SkinfoldResult? {
        if (sex !in setOf("Masculino", "Feminino", "M", "F") || age == null || age !in 18..80 ||
            weight == null || !weight.isFinite() || weight <= 0) return null
        val measures = sites.keys.map { folds[it] ?: return null }
        if (measures.any { !it.isFinite() || it <= 0 || it > 150 }) return null
        val sum = measures.sum()
        val density = if (sex == "Masculino" || sex == "M")
            1.112 - .00043499*sum + .00000055*sum.pow(2) - .00028826*age
        else 1.097 - .00046971*sum + .00000056*sum.pow(2) - .00012828*age
        if (density <= 0 || !density.isFinite()) return null
        val raw = (4.95 / density - 4.50) * 100.0
        val bounded = raw.coerceIn(3.0, 60.0)
        return SkinfoldResult(sum, density, bounded, weight * bounded / 100.0,
            weight * (1 - bounded/100.0), raw !in 3.0..60.0)
    }

    /** Mifflin-St Jeor, adults only, when sex and anthropometric inputs are available. */
    fun mifflinStJeor(sex: String, age: Int?, weightKg: Double?, heightCm: Double?): Double? {
        if (age == null || age !in 18..120 || weightKg == null || heightCm == null ||
            weightKg <= 0 || heightCm <= 0 || !weightKg.isFinite() || !heightCm.isFinite()) return null
        val constant = when(sex) { "Masculino", "M" -> 5.0; "Feminino", "F" -> -161.0; else -> return null }
        return (10*weightKg + 6.25*heightCm - 5*age + constant).takeIf { it > 0 }
    }

    fun weightRange(heightCm: Double?): Pair<Double,Double>? {
        if (heightCm == null || heightCm <= 0 || !heightCm.isFinite()) return null
        val h2 = (heightCm / 100.0).pow(2)
        return 20.0 * h2 to 24.9 * h2
    }
}
