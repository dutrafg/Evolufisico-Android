package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

/** Matriz algorítmica: NÃO equivale a screenshots nem testes completos de UI. */
class Beta11VisualRegressionTest {
    @Test fun oneHundredFiftyThousandResponsiveAndThemeCombinations() {
        val fontScales=listOf(1.0f,1.15f,1.3f,1.5f,2.0f)
        var tested=0
        for(width in 320..1319) {
            for(palette in CorporatePalettes.options) {
                for(dark in listOf(false,true)) {
                    for(fontScale in fontScales) {
                        val columns=WorkspaceLayoutRules.gridColumns(width,fontScale)
                        val metrics=WorkspaceLayoutRules.metricColumns(width,fontScale)
                        val available=WorkspaceLayoutRules.usableWidthDp(width)
                        val minimum=WorkspaceLayoutRules.minimumTileDp(fontScale)
                        assertTrue(columns in 1..4)
                        assertTrue(metrics in listOf(1,2,4))
                        assertTrue(available>0)
                        assertTrue(VisualLayout.edgePaddingDp(width)>=16)
                        if(columns>1) {
                            val cell=(available-10*(columns-1))/columns
                            assertTrue("Módulos estreitos em $width dp / fonte $fontScale: $cell dp",
                                cell>=minimum)
                        }
                        if(metrics>1) {
                            val cell=(available-10*(metrics-1))/metrics
                            assertTrue("Indicadores estreitos em $width dp / fonte $fontScale",
                                cell>=minimum)
                        }
                        assertTrue(columns<=WorkspaceLayoutRules.gridColumns(width,1f))
                        assertTrue(metrics<=WorkspaceLayoutRules.metricColumns(width,1f))
                        assertNotEquals(CorporatePalettes.scheme(palette.id,dark).onSurface,
                                        CorporatePalettes.scheme(palette.id,dark).surface)
                        tested++
                    }
                }
            }
        }
        assertEquals(150_000,tested)
    }
    @Test fun everyCatalogEntryReceivesAStableAccent() {
        assertEquals(11,WorkspaceModules.all.map { it.route }.distinct().size)
        WorkspaceModules.all.forEach { module ->
            assertTrue(VisualTokens.metricIndexForRoute(module.route) in 0..3)
        }
        assertEquals(0,VisualTokens.metricIndexForRoute("ASSESSMENTS"))
        assertEquals(1,VisualTokens.metricIndexForRoute("FORMS"))
        assertEquals(2,VisualTokens.metricIndexForRoute("EVOLUTION"))
    }
}
