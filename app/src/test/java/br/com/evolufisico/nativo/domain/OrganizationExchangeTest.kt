package br.com.evolufisico.nativo.domain
import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test
class OrganizationExchangeTest {
 @Test fun roundTripPreservesWebFields(){
  val n=NotebookEntry(id="n",title="Nota á",body="Texto",student="Ana",pinned=true,archived=true,reminder="2026-10-22")
  val g=OrganizationGoal(id="g",title="Meta",student="Ana",progress="Rever",due="2026-10-23",status="Pausada")
  val t=TaskItem(id="t",title="Tarefa",dueDate="2026-10-24",done=true,notes="Detalhes",priority=3,student="Ana",time="14:30")
  val a=Appointment(id="a",student="Ana",date="2026-10-25",time="10:00",durationMinutes=45,location="Sala",notes="Retorno",status="Concluído")
  val rows=OrganizationJsonImport.stage(OrganizationJsonImport.export(listOf(n),listOf(g),listOf(t),listOf(a)),emptyList()).associate{it.first to it.third}
  val restored=NotebookEntry.fromJson(rows.getValue("note"));assertEquals(n.body,restored.body);assertEquals(n.reminder,restored.reminder);assertTrue(restored.archived);assertTrue(restored.pinned)
  assertEquals(g,OrganizationGoal.fromJson(rows.getValue("organizationGoal")))
  assertEquals(t,TaskItem.fromJson(rows.getValue("task")))
  assertEquals(a,Appointment.fromJson(rows.getValue("appointment")))
 }
 @Test fun allAppointmentStatusesRoundTrip(){listOf("Agendado","Confirmado","Concluído","Cancelado","Faltou").forEach{s->val a=Appointment(student="Ana",status=s);val rows=OrganizationJsonImport.stage(OrganizationJsonImport.export(emptyList(),emptyList(),emptyList(),listOf(a)),emptyList());assertEquals(s,Appointment.fromJson(rows.single().third).status)}}
 @Test fun htmlTaskBodyAtLimitIsPreserved(){val task=TaskItem(title="T",notes="á".repeat(10000));val rows=OrganizationJsonImport.stage(OrganizationJsonImport.export(emptyList(),emptyList(),listOf(task),emptyList()),emptyList());assertEquals(task.notes,TaskItem.fromJson(rows.single().third).notes)}
 @Test(expected=IllegalArgumentException::class) fun htmlTaskBodyAboveLimitIsRejected(){WorkspaceRules.validate(TaskItem(title="T",notes="x".repeat(10001)))}
 @Test fun emptyWorkspaceExportsValidEnvelope(){val o=org.json.JSONObject(String(OrganizationJsonImport.export(emptyList(),emptyList(),emptyList(),emptyList())));assertEquals(1,o.getInt("version"));assertEquals(0,o.getJSONArray("tasks").length())}
 @Test(expected=IllegalArgumentException::class) fun exportRefusesInvalidTask(){OrganizationJsonImport.export(emptyList(),emptyList(),listOf(TaskItem(title="T",priority=4)),emptyList())}
 @Test(expected=IllegalArgumentException::class) fun exportRefusesOversizedFile(){OrganizationJsonImport.export(List(300){NotebookEntry(title="Nota",body="x".repeat(12000))},emptyList(),emptyList(),emptyList())}
}
