package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class WorkspaceLayoutRulesTest {
    @Test fun smallPhonesNeverOverflowColumns() {
        assertEquals(1,WorkspaceLayoutRules.gridColumns(320))
        assertEquals(2,WorkspaceLayoutRules.gridColumns(360))
        assertEquals(2,WorkspaceLayoutRules.gridColumns(699))
    }
    @Test fun tabletAccountsForNavigationRailWidth() {
        assertEquals(3,WorkspaceLayoutRules.gridColumns(840))
        assertEquals(3,WorkspaceLayoutRules.gridColumns(1079))
        assertEquals(4,WorkspaceLayoutRules.gridColumns(1200))
        assertEquals(760,WorkspaceLayoutRules.contentWidthDp(840))
    }
    @Test fun indicatorsUseTwoOrFourColumnsByActualRoom() {
        assertEquals(2,WorkspaceLayoutRules.metricColumns(840))
        assertEquals(4,WorkspaceLayoutRules.metricColumns(1150))
    }
    @Test fun invalidWidthDoesNotCreateImpossibleColumns() {
        assertEquals(1,WorkspaceLayoutRules.gridColumns(-2))
        assertEquals(2,WorkspaceLayoutRules.metricColumns(0))
    }
}
