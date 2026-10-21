package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
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
