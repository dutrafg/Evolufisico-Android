package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
class QuickLaunchRulesTest {
 @Test fun favoriteOpensEditorsOnlyOnSupportedRoutes() {
  assertTrue(QuickLaunchRules.supportsCreate("ASSESSMENTS"))
  assertTrue(QuickLaunchRules.supportsCreate("FORMS"))
  assertFalse(QuickLaunchRules.supportsCreate("REPORTS"))
  assertEquals("Nova avaliação",QuickLaunchRules.label("ASSESSMENTS"))
  assertEquals("Novo formulário",QuickLaunchRules.label("FORMS"))
  assertEquals("Abrir função",QuickLaunchRules.label("EVOLUTION"))
 }
 @Test fun originalCatalogRoutesRemainAvailable() {
  assertTrue(WorkspaceModules.all.any{it.route=="ASSESSMENTS"})
  assertTrue(WorkspaceModules.all.any{it.route=="FORMS"})
 }
}
