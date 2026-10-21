package br.com.evolufisico.android

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Barra lateral e navegação inferior com contraste estável em todas as paletas.
 * Alinhado ao contraste reforçado do EvoluFísico Desktop beta.4.1.
 */
data class NavigationContrastPaint(
    val background:Color,
    val selectedBackground:Color,
    val selectedInk:Color,
    val inactiveInk:Color
)

object NavigationContrastTokens {
    fun forScheme(scheme:ColorScheme):NavigationContrastPaint {
        val isDark=VisualTokens.dark(scheme)
        return NavigationContrastPaint(
            background=if(isDark) Color(0xFF1D2C40) else Color(0xFFE9EFF7),
            selectedBackground=scheme.primaryContainer,
            selectedInk=scheme.onPrimaryContainer,
            inactiveInk=scheme.onSurfaceVariant
        )
    }

    fun contrast(a:Color,b:Color):Double {
        val la=a.luminance().toDouble()
        val lb=b.luminance().toDouble()
        return (maxOf(la,lb)+0.05)/(minOf(la,lb)+0.05)
    }
}
