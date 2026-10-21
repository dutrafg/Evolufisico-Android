package br.com.evolufisico.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.material3.ColorScheme

/** Tokens da identidade visual: cores dos indicadores independentes do acento selecionado.
 * A paleta escura conserva distinção sem impor fundos claros à noite. */
data class MetricPaint(val background:Color,val ink:Color)
object VisualTokens {
    const val contentMaxWidthDp=1100
    const val phoneGutterDp=18
    const val tabletGutterDp=24
    const val cardRadiusDp=18
    fun dark(scheme:ColorScheme)=scheme.background.luminance()<0.4f
    fun metric(position:Int,dark:Boolean):MetricPaint {
        val light=listOf(
            MetricPaint(Color(0xFFE7F2FF),Color(0xFF18548E)),
            MetricPaint(Color(0xFFE3F5EC),Color(0xFF126D50)),
            MetricPaint(Color(0xFFFFF2DB),Color(0xFF895900)),
            MetricPaint(Color(0xFFFBE8EE),Color(0xFF943552))
        )
        val night=listOf(
            MetricPaint(Color(0xFF193751),Color(0xFFB1D8FF)),
            MetricPaint(Color(0xFF163F37),Color(0xFFA2F1CD)),
            MetricPaint(Color(0xFF48371D),Color(0xFFFFDEA2)),
            MetricPaint(Color(0xFF482736),Color(0xFFFFB7CD))
        )
        return (if(dark)night else light)[position.coerceIn(0,3)]
    }
    fun panel(dark:Boolean)=if(dark)Color(0xFF28313D) else Color(0xFFF0ECF3)
}
