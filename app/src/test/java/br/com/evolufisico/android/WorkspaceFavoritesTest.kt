package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class WorkspaceFavoritesTest {
    @Test fun persistsOnlyKnownRoutesWithoutDuplication() {
        val saved=WorkspaceFavorites.parse("ASSESSMENTS,ASSESSMENTS,INVALID,REPORTS")
        assertEquals(setOf("ASSESSMENTS","REPORTS"),saved)
        assertEquals(saved,WorkspaceFavorites.parse(WorkspaceFavorites.serialize(saved)))
    }
    @Test fun neverStoresMoreThanFour() {
        var set=emptySet<String>()
        for(module in WorkspaceModules.all) set=WorkspaceFavorites.toggle(set,module.route)
        assertEquals(4,set.size)
        assertEquals(set,set+setOf("ASSESSMENTS").filter { it in set })
        val old=set.first()
        assertFalse(old in WorkspaceFavorites.toggle(set,old))
    }
    @Test fun reordersFavoritesWithinBounds() {
        val initial=linkedSetOf("GOALS","FORMS","REPORTS")
        val moved=WorkspaceFavorites.move(initial,"REPORTS",-1)
        assertEquals(listOf("GOALS","REPORTS","FORMS"),moved.toList())
        assertEquals(initial,WorkspaceFavorites.move(initial,"GOALS",-1))
        assertEquals(initial,WorkspaceFavorites.move(initial,"UNKNOWN",1))
        assertEquals(moved,WorkspaceFavorites.parse(WorkspaceFavorites.serialize(moved)))
    }
    @Test fun unknownShortcutIsIgnored() {
        assertTrue(WorkspaceFavorites.toggle(emptySet(),"UNLISTED").isEmpty())
    }
}
