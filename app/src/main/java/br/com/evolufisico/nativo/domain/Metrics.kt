package br.com.evolufisico.nativo.domain


/** No clinically unvalidated body-fat formulas. Explicit sensor readings only. */
object Metrics {
    fun bmi(weightKg: Double?, heightCm: Double?): Double? {
        if (weightKg == null || heightCm == null || weightKg <= 0.0 || heightCm <= 0.0 || !weightKg.isFinite() || !heightCm.isFinite()) return null
        return weightKg / ((heightCm / 100.0) * (heightCm / 100.0))
    }
    fun waistHipRatio(waistCm: Double?, hipCm: Double?): Double? =
        if (waistCm == null || hipCm == null || waistCm <= 0 || hipCm <= 0 || !waistCm.isFinite() || !hipCm.isFinite()) null else waistCm / hipCm

    fun fatMassKg(weightKg: Double?, bodyFatPercent: Double?): Double? =
        if (weightKg == null || bodyFatPercent == null || weightKg <= 0 || !weightKg.isFinite() || !bodyFatPercent.isFinite() || bodyFatPercent !in 0.0..100.0) null
        else weightKg * bodyFatPercent / 100.0

    /** Null = no prior practice or no usable observations. Never penalize 'not applicable'. */
    fun adherence(trainingExperience: Boolean, training: Int?, dietExperience: Boolean, diet: Int?): Double? {
        val scores = buildList {
            if (trainingExperience && training != null && training in 0..100) add(training)
            if (dietExperience && diet != null && diet in 0..100) add(diet)
        }
        return if (scores.isEmpty()) null else scores.average()
    }

    fun oneDecimal(n: Double?): String = if (n == null || !n.isFinite()) "—" else
        "%.1f".format(java.util.Locale.forLanguageTag("pt-BR"), n)
}
