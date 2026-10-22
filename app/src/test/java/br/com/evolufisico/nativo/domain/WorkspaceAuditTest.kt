package br.com.evolufisico.nativo.domain
import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test
class WorkspaceAuditTest {
    @Test fun validWorkspaceHasNoIssues() {assertTrue(WorkspaceRules.audit(listOf(TrainingSession(title="A")),listOf(HabitEntry(title="H")),listOf(NotebookEntry(title="N")),emptySet()).isEmpty())}
    @Test fun linkedClientMustExist() {assertTrue(WorkspaceRules.audit(emptyList(),emptyList(),listOf(NotebookEntry(title="N",studentId="gone",student="Cliente")),emptySet()).single().contains("sem avaliação"))}
    @Test fun duplicateIdentityIsReported() {val item=NotebookEntry(title="N");assertTrue(WorkspaceRules.audit(emptyList(),emptyList(),listOf(item,item),emptySet()).any{it.contains("duplicados")})}
    @Test fun invalidDomainDataIsReported() {assertTrue(WorkspaceRules.audit(listOf(TrainingSession(title="A",durationMinutes=0)),emptyList(),emptyList(),emptySet()).single().contains("Duração"))}
}
