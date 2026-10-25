package br.com.evolufisico.nativo.domain
import org.junit.Assert.*
import org.junit.Test
import org.json.JSONArray
class HtmlLaboratoryTest {
 @Test fun equationsMatchPinnedJavaScript(){
  val array=JSONArray(java.io.File("../qa/html/ef77-golden.json").readText())
  for(index in 0 until array.length()){
   val o=array.getJSONObject(index)
   val r=HtmlLaboratory.calculate(HtmlLaboratory.Input(o.getString("sex"),o.getInt("age"),o.getDouble("height"),o.getDouble("weight"),o.getDouble("neck"),o.getDouble("waist"),o.getDouble("hip"),o.getDouble("pollock"),o.getDouble("bia")))
   val methods=o.getJSONObject("methods");assertEquals(methods.keys().asSequence().toSet(),r.methods.keys);r.methods.forEach{(key,value)->assertEquals(methods.getDouble(key),value,1e-9)}
   mapOf("mean" to r.mean,"amplitude" to r.amplitude,"reference" to r.reference,"absi" to r.absi,"ffmi" to r.ffmi,"normalizedFfmi" to r.normalizedFfmi,"water" to r.water,"fcmax" to r.fcmax).forEach{(k,v)->if(o.isNull(k))assertNull("$index $k",v) else assertEquals("$index $k",o.getDouble(k),v!!,1e-9)}
  }
 }
 private fun input()=HtmlLaboratory.Input("M",35,180.0,80.0,37.0,85.0,100.0,20.0,21.0)
 @Test fun divergenceBlocksComposite(){val r=HtmlLaboratory.calculate(input().copy(pollock=5.0,bia=50.0));assertNull(r.reference);assertNull(r.ffmi);assertNull(r.normalizedFfmi);assertNotNull(r.mean)}
 @Test fun missingSexDoesNotBecomeMale(){val r=HtmlLaboratory.calculate(input().copy(sex="Não informado"));assertFalse("US Navy" in r.methods);assertFalse("RFM" in r.methods);assertNull(r.water)}
 @Test fun minorsDoNotReceiveAdultBodyEquations(){val r=HtmlLaboratory.calculate(input().copy(age=16));assertTrue(r.minor);assertEquals(setOf("Bioimpedância"),r.methods.keys);assertNull(r.water)}
 @Test fun invalidValuesAreNotClamped(){val r=HtmlLaboratory.calculate(input().copy(height=Double.NaN,weight=401.0,pollock=61.0,bia=-1.0));assertTrue(r.methods.isEmpty());assertNull(r.absi);assertNull(r.ffmi)}
 @Test fun emptyInputDoesNotManufactureMetrics(){val r=HtmlLaboratory.calculate(HtmlLaboratory.Input("",null,null,null,null,null,null,null,null));assertTrue(r.methods.isEmpty());assertNull(r.fcmax);assertNull(r.reference)}
}
