package br.com.evolufisico.nativo.domain
import org.junit.Assert.*
import org.junit.Test
class HtmlMonitoringTest {
 @Test fun thirtySixVectorsMatchPinnedJavascript(){
  val cases=org.json.JSONArray(java.io.File("../qa/html/ef68-golden.json").readText())
  val labels=mapOf("gordura" to "Gordura","massaMagra" to "Massa magra","cintura" to "Cintura","peso" to "Peso")
  for(i in 0 until cases.length()){
   val o=cases.getJSONObject(i);val source=o.getJSONArray("rows")
   val rows=(0 until source.length()).map{j->val r=source.getJSONObject(j)
    fun n(k:String)=if(r.isNull(k))null else r.getDouble(k)
    HtmlAnalysis.Point(r.getLong("day"),n("fat"),n("lean"),n("waist"),n("weight"),if(r.isNull("method"))null else r.getString("method"))}
   val events=o.getJSONArray("events");assertEquals("events case $i",(0 until events.length()).map{events.getString(it)},HtmlMonitoring.events(rows).map{it.type}.sorted())
   val transitions=HtmlMonitoring.transitions(rows);val expected=o.getJSONArray("deltas");assertEquals(expected.length(),transitions.size)
   transitions.forEachIndexed{j,t->val d=expected.getJSONObject(j);labels.forEach{(key,label)->if(d.isNull(key))assertNull(t.delta[label]) else assertEquals("delta case $i $j $key",d.getDouble(key),t.delta[label]!!,1e-9)}}
  }
 }
 private fun p(day:Long,f:Double=30.0)=HtmlAnalysis.Point(day,f,60.0,90.0,90.0,"Pollock")
 @Test fun intervalAndMethodBlockComparisons(){assertTrue(HtmlMonitoring.transitions(listOf(p(0),p(6))).single().delta.values.all{it==null});val t=HtmlMonitoring.transitions(listOf(p(0),p(14).copy(method="BIA"))).single();assertNull(t.delta["Gordura"]);assertEquals(0.0,t.delta["Peso"]!!,0.0)}
 @Test fun baselineAndGapEvents(){assertEquals("baseline",HtmlMonitoring.events(listOf(p(0))).single().type);assertTrue(HtmlMonitoring.events(listOf(p(0),p(91))).any{it.type=="gap"})}
 @Test fun consistentDecreaseAndPlateaus(){val rows=listOf(0L,20L,40L,60L).mapIndexed{i,d->p(d,30.0-i)};val es=HtmlMonitoring.events(rows);assertTrue(es.any{it.type=="improvement"});assertTrue(es.any{it.type=="consistency"});assertTrue(es.any{it.type=="plateau"})}
 @Test fun reversalNeedsTwoComparableTransitions(){assertTrue(HtmlMonitoring.events(listOf(p(0,30.0),p(14,28.0),p(28,29.0))).any{it.type=="reversal"})}
 @Test fun duplicateDaysDoNotCreateTransitions(){assertEquals(1,HtmlMonitoring.transitions(listOf(p(0),p(0),p(14))).size)}
}
