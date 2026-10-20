package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
class WorkspaceGridTest {
 @Test fun phoneUsesTwoAndTabletThreeColumns(){
  assertEquals(1,WorkspaceGrid.columns(320))
  assertEquals(2,WorkspaceGrid.columns(360))
  assertEquals(2,WorkspaceGrid.columns(699))
  assertEquals(3,WorkspaceGrid.columns(700))
  assertEquals(3,WorkspaceGrid.columns(1200))
 }
 @Test fun catalogStillContainsAllUniqueRoutes() {
  assertEquals(11,WorkspaceModules.all.map{it.route}.distinct().size)
 }
}
