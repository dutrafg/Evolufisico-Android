package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class WorkspaceNavigationTest {
    @Test fun moduleScreensReturnToDirectoryAndDirectoryReturnsHome() {
        assertEquals("MODULES",WorkspaceNavigation.backTarget("ASSESSMENTS"))
        assertEquals("MODULES",WorkspaceNavigation.backTarget("REPORTS"))
        assertEquals("HOME",WorkspaceNavigation.backTarget("MODULES"))
        assertEquals("HOME",WorkspaceNavigation.backTarget("CLIENTS"))
    }
    @Test fun bottomSelectionTracksModuleContext() {
        assertEquals("MODULES",WorkspaceNavigation.bottomSelection("ASSESSMENTS"))
        assertEquals("MODULES",WorkspaceNavigation.bottomSelection("SEARCH"))
        assertEquals("AGENDA",WorkspaceNavigation.bottomSelection("AGENDA"))
        assertNull(WorkspaceNavigation.bottomSelection("SETTINGS"))
    }
}
