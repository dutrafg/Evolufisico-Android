package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
class CorporatePalettesTest {
    @Test fun fifteenPalettesPreserveOldIdsAndAddFiveDesktopShades() {
        assertEquals(15,CorporatePalettes.options.size)
        assertEquals(15,CorporatePalettes.options.map{it.id}.distinct().size)
        assertEquals(listOf("verde","azul","roxo","laranja","rosa"),
            CorporatePalettes.options.take(5).map{it.id})
        assertEquals(listOf("verde-escuro","azul-escuro","roxo-escuro",
            "laranja-escuro","rosa-escuro"),
            CorporatePalettes.options.takeLast(5).map { it.id })
        assertEquals(listOf("ciano","turquesa","indigo","vermelho","grafite"),
            CorporatePalettes.options.drop(5).take(5).map { it.id })
    }
    @Test fun strongDesktopPalettesUseReadableText() {
        CorporatePalettes.options.takeLast(5).forEach { palette ->
            val light=CorporatePalettes.scheme(palette.id,false)
            val dark=CorporatePalettes.scheme(palette.id,true)
            assertEquals(palette.deep,light.primary)
            assertEquals(palette.bright,dark.primary)
            assertEquals(androidx.compose.ui.graphics.Color.White,dark.onPrimary)
            assertEquals(androidx.compose.ui.graphics.Color.White,dark.onPrimaryContainer)
        }
    }
    private fun contrast(foreground:Color,background:Color):Double {
        val f=foreground.luminance().toDouble()
        val b=background.luminance().toDouble()
        return (maxOf(f,b)+0.05)/(minOf(f,b)+0.05)
    }
    @Test fun desktopStrongAccentsKeepWcagAaForTextInBothThemes() {
        for(palette in CorporatePalettes.options.takeLast(5)) {
            for(dark in listOf(false,true)) {
                val scheme=CorporatePalettes.scheme(palette.id,dark)
                assertTrue("Primary too low-contrast: ${palette.id}/$dark",
                    contrast(scheme.onPrimary,scheme.primary)>=4.5)
                assertTrue("Primary container too low-contrast: ${palette.id}/$dark",
                    contrast(scheme.onPrimaryContainer,scheme.primaryContainer)>=4.5)
            }
        }
    }
    @Test fun unknownPaletteFallsBackToBlue() {
        assertEquals("azul",CorporatePalettes.byId("invalid").id)
        assertEquals("verde",CorporatePalettes.byId("verde").id)
    }
    @Test fun lightAndDarkUseSameSelection() {
        val color=CorporatePalettes.byId("roxo")
        assertEquals(color.deep,CorporatePalettes.scheme("roxo",false).primary)
        assertEquals(color.bright,CorporatePalettes.scheme("roxo",true).primary)
    }
}
