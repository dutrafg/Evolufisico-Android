package br.com.evolufisico.android

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Tokens da identidade visual: cores dos indicadores independentes do acento selecionado.
 * A paleta escura conserva distinção sem impor fundos claros à noite. */
data class MetricPaint(val background:Color,val ink:Color)
object VisualTokens {
    const val contentMaxWidthDp=1440
    const val phoneGutterDp=16
    const val tabletGutterDp=24
    const val cardRadiusDp=16
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
    fun panel(dark:Boolean)=if(dark)Color(0xFF1D2C40) else Color(0xFFECF3FB)
    /** Identidade por módulo; evita reutilizar a mesma cor para quase todo o catálogo. */
    fun metricIndexForRoute(route:String):Int=when(route) {
        "CLIENTS","ASSESSMENTS","CLIENT_SUMMARY","SEARCH" -> 0
        "FORMS","GOALS","SERVICES","NOTES" -> 1
        "AGENDA","EVOLUTION","TRENDS" -> 2
        else -> 3
    }
}

val CorporateTypography=Typography(
    headlineMedium=TextStyle(fontSize=26.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold),
    headlineSmall=TextStyle(fontSize=23.sp,lineHeight=30.sp,fontWeight=FontWeight.SemiBold),
    titleLarge=TextStyle(fontSize=21.sp,lineHeight=28.sp,fontWeight=FontWeight.SemiBold),
    titleMedium=TextStyle(fontSize=17.sp,lineHeight=25.sp,fontWeight=FontWeight.SemiBold),
    titleSmall=TextStyle(fontSize=15.sp,lineHeight=22.sp,fontWeight=FontWeight.SemiBold),
    bodyLarge=TextStyle(fontSize=16.sp,lineHeight=24.sp),
    bodyMedium=TextStyle(fontSize=14.sp,lineHeight=22.sp),
    bodySmall=TextStyle(fontSize=12.sp,lineHeight=18.sp),
    labelLarge=TextStyle(fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.Medium)
)
