package br.com.evolufisico.nativo.domain

/** Native adaptation of ef-101-polish and the 1.0.19 liquid-navigation option. Decoration only; never touches data or PDFs. */
object VisualEffects {
    const val multiplier=1.2f
    const val liftDp=4f*multiplier
    const val rotationDegrees=.48f*multiplier*2f
    const val restingDepthDp=18f*multiplier
    const val activeDepthDp=28f*multiplier
    const val glowRadiusDp=15f*multiplier
    fun mode(value:String?)=value?.takeIf{it in setOf("advanced","essential","liquid")} ?: "advanced"
}
