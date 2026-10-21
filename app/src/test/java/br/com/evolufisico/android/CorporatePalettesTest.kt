package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
class CorporatePalettesTest {
    @Test fun tenPalettesHaveUniqueIdsAndOriginalFive() {
        assertEquals(10,CorporatePalettes.options.size)
        assertEquals(10,CorporatePalettes.options.map{it.id}.distinct().size)
        assertEquals(listOf("verde","azul","roxo","laranja","rosa"),
            CorporatePalettes.options.take(5).map{it.id})
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
