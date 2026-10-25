package br.com.evolufisico.nativo.domain
import org.junit.Test
import org.junit.Assert.*
class WorkspaceSearchTest {
 @Test fun searchIgnoresAccentsAndCase(){val e=WorkspaceSearch.Entry("a","Módulos","Avaliações","João",1);assertEquals(listOf(e),WorkspaceSearch.find(listOf(e),"AVALIACOES joao"))}
 @Test fun wordsMustAllMatch(){val e=WorkspaceSearch.Entry("a","Notas","João","Retorno",8);assertTrue(WorkspaceSearch.find(listOf(e),"joao inexistente").isEmpty())}
 @Test fun categoryFiltersWithoutChangingRoute(){val e=WorkspaceSearch.Entry("a","Notas","Meta","Ana",8,section="notes",search="Meta");assertTrue(WorkspaceSearch.find(listOf(e),"Meta","Metas").isEmpty());assertEquals("notes",WorkspaceSearch.find(listOf(e),"Meta","Notas").single().section)}
 @Test fun resultLimitIsBoundedAndStable(){val es=(1..100).map{WorkspaceSearch.Entry("$it","Notas","Nota $it","",8)};val r=WorkspaceSearch.find(es,"");assertEquals(60,r.size);assertEquals("1",r.first().id);assertEquals("60",r.last().id)}
 @Test fun homonymResultsKeepIds(){val es=listOf("a","b").map{WorkspaceSearch.Entry(it,"Clientes","Ana","",1,client=it)};assertEquals(listOf("a","b"),WorkspaceSearch.find(es,"Ana").map{it.client})}
}
