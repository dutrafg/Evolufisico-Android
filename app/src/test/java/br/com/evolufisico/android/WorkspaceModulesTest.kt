package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class WorkspaceModulesTest {
    @Test fun allRoutesAreUniqueAndOrganized() {
        assertEquals(WorkspaceModules.all.size,WorkspaceModules.all.map{it.route}.toSet().size)
        assertEquals(11,WorkspaceModules.all.size)
        assertTrue(WorkspaceModules.all.all { it.category in WorkspaceModules.categories })
    }
    @Test fun screensBelongToModulesAndNotHome() {
        assertTrue(WorkspaceModules.all.any { it.route=="ASSESSMENTS" })
        assertTrue(WorkspaceModules.all.any { it.route=="REPORTS" })
        assertTrue(WorkspaceModules.all.any { it.route=="FORMS" })
        assertTrue(WorkspaceModules.all.none { it.route in setOf("HOME","CLIENTS","AGENDA","BILLING") })
    }
    @Test fun normalizedSearchAndEmptyResults() {
        assertEquals("EVOLUTION",WorkspaceModules.filter("EVOLUCAO").single().route)
        assertEquals("CLIENT_SUMMARY",WorkspaceModules.filter("visao operacional").single().route)
        assertTrue(WorkspaceModules.filter("inexistente").isEmpty())
    }
}
