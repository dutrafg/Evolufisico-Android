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
    @Test fun unknownShortcutIsIgnored() {
        assertTrue(WorkspaceFavorites.toggle(emptySet(),"UNLISTED").isEmpty())
    }
}
