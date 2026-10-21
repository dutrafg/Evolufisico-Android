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
        assertEquals(1,WorkspaceLayoutRules.metricColumns(320))
        assertEquals(2,WorkspaceLayoutRules.metricColumns(840))
        assertEquals(4,WorkspaceLayoutRules.metricColumns(1150))
    }
    @Test fun beta6ResponsiveEdgesAndBreakpoints() {
        assertEquals(20,VisualLayout.edgePaddingDp(360))
        assertEquals(24,VisualLayout.edgePaddingDp(900))
        assertEquals(1440,VisualLayout.maxContentWidthDp())
        assertTrue(VisualLayout.isTablet(840))
        assertFalse(VisualLayout.isTablet(839))
        assertEquals(WorkspaceLayoutRules.gridColumns(950),VisualLayout.moduleColumns(950))
    }
    @Test fun invalidWidthDoesNotCreateImpossibleColumns() {
        assertEquals(1,WorkspaceLayoutRules.gridColumns(-2))
        assertEquals(1,WorkspaceLayoutRules.metricColumns(0))
    }
}
