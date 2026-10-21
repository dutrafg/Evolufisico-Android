package br.com.evolufisico.android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationContrastTokensTest {
    @Test fun desktopPaletteNavigationKeepsReadableTextAndSelectedIndicators() {
        for(palette in CorporatePalettes.options) {
            for(dark in listOf(false,true)) {
                val scheme=CorporatePalettes.scheme(palette.id,dark)
                val nav=NavigationContrastTokens.forScheme(scheme)
                val inactive=NavigationContrastTokens.contrast(nav.inactiveInk,nav.background)
                val selected=NavigationContrastTokens.contrast(nav.selectedInk,nav.selectedBackground)
                assertTrue("Texto inativo sem contraste WCAG AA: ${palette.id}/$dark ($inactive)",
                    inactive>=4.5)
                assertTrue("Item selecionado sem contraste WCAG AA: ${palette.id}/$dark ($selected)",
                    selected>=4.5)
                assertNotEquals("Navegação não deve confundir-se com superfície do conteúdo",
                    scheme.surface,nav.background)
            }
        }
    }

    @Test fun existingPaletteIdsAndThemeSelectionRemainUnchanged() {
        assertEquals(15,CorporatePalettes.options.size)
        for(palette in CorporatePalettes.options) {
            val light=CorporatePalettes.scheme(palette.id,false)
            val dark=CorporatePalettes.scheme(palette.id,true)
            assertEquals(light.primary,CorporatePalettes.byId(palette.id).deep)
            assertEquals(dark.primary,CorporatePalettes.byId(palette.id).bright)
        }
    }
}
