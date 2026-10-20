package br.com.evolufisico.android
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
class WorkspaceDisplayTest {
 @Test fun initialsSupportEmptyAndCompoundNames() {
  assertEquals("CA",WorkspaceDisplay.initials("Camila Almeida"))
  assertEquals("M",WorkspaceDisplay.initials(" Maria "))
  assertEquals("?",WorkspaceDisplay.initials(""))
 }
 @Test fun dateFormattingSupportsTomorrow() {
  val d=LocalDate.parse("2026-10-20")
  assertEquals("Hoje · 10:30",WorkspaceDisplay.appointmentDate("2026-10-20T10:30",d))
  assertEquals("Amanhã · 10:30",WorkspaceDisplay.appointmentDate("2026-10-21T10:30",d))
  assertEquals("22/10 · 10:30",WorkspaceDisplay.appointmentDate("2026-10-22T10:30",d))
  assertEquals("invalid",WorkspaceDisplay.appointmentDate("invalid",d))
 }
}
