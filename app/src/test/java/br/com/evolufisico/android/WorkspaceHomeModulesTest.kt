package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
class WorkspaceHomeModulesTest {
    @Test fun favoriteAppearsOnHomeAndUnfavoriteReturnsToCatalog() {
        val original=WorkspaceFavorites.parse("GOALS,ASSESSMENTS,FORMS")
        assertEquals(listOf("GOALS","ASSESSMENTS","FORMS"),
            WorkspaceHomeModules.selected(original).map{it.route})
        val after=WorkspaceFavorites.toggle(original,"ASSESSMENTS")
        assertEquals(listOf("GOALS","FORMS"),
            WorkspaceHomeModules.selected(after).map{it.route})
        assertTrue(WorkspaceModules.all.any{it.route=="ASSESSMENTS"})
    }
    @Test fun unknownRoutesCannotAppearOnHome() {
        assertTrue(WorkspaceHomeModules.selected(setOf("FAKE")).isEmpty())
        assertTrue(WorkspaceHomeModules.selected(emptySet()).isEmpty())
    }
}
