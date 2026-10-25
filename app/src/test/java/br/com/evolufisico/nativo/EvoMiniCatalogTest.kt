package br.com.evolufisico.nativo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvoMiniCatalogTest {
    @Test fun allMiniApplicationsHaveStableUniqueIds() {
        val apps = EvoMiniCatalog.entries
        assertEquals(10, apps.size)
        assertEquals(apps.size, apps.map { it.id }.distinct().size)
        assertTrue(apps.none { it.title.isBlank() || it.description.isBlank() || it.category.isBlank() })
    }

    @Test fun storeCategoriesMatchAvailableApplications() {
        assertEquals("Todas", EvoMiniCatalog.categories.first())
        assertEquals(EvoMiniCatalog.entries.map { it.category }.distinct().toSet(),
            EvoMiniCatalog.categories.drop(1).toSet())
    }

    @Test fun unknownOrRemovedMiniAppsCannotBeRestoredAsInstalled() {
        val restored = EvoMiniCatalog.validIds(setOf("list", "bmi", "contador-html-antigo", "fake"))
        assertEquals(setOf("list", "bmi"), restored)
        assertFalse("fake" in restored)
        assertTrue(EvoMiniCatalog.validIds(emptySet()).isEmpty())
    }
}
