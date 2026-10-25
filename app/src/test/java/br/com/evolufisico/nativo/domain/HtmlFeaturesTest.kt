package br.com.evolufisico.nativo.domain
import br.com.evolufisico.nativo.data.*
import org.junit.Assert.*
import org.junit.Test

class HtmlFeaturesTest {
    private fun rows(methods:List<String> = listOf("Pollock","Pollock","Pollock"))=listOf(0L,14L,28L).mapIndexed{i,d->HtmlAnalysis.Point(d,30.0-i*2,60.0+i,90.0-i*2,90.0-i,methods[i])}
    @Test fun allNineTemplatesAreValid(){assertEquals(9,HtmlFormLibrary.presets.size);HtmlFormLibrary.presets.forEach{it.validate()};assertEquals(listOf(8,8,8,8,8,8,9,10,10),HtmlFormLibrary.presets.map{it.questions.size})}
    @Test fun eachLevelHasThreeTemplates(){listOf("Iniciante","Intermediário","Avançado").forEach{level->assertEquals(3,HtmlFormLibrary.presets.count{it.level==level})}}
    @Test fun responseSnapshotsQuestionWording(){val t=HtmlFormLibrary.presets.first();val draft=t.draft("client","Ana");assertEquals(t.title,draft.answers["_template"]);assertEquals(t.questions.first(),draft.answers["_label_0"]);assertEquals("client",draft.studentId);assertEquals(0,HtmlFormLibrary.responseCount(draft));assertEquals(1,HtmlFormLibrary.responseCount(draft.copy(answers=draft.answers+("response_0" to "Resposta"))))}
    @Test(expected=IllegalArgumentException::class) fun emptyCustomTemplateIsRejected(){FormTemplate(title="Teste").validate()}
    @Test(expected=IllegalArgumentException::class) fun oversizedCustomTemplateIsRejected(){FormTemplate(title="Teste",questions=List(61){"Pergunta"}).validate()}
    @Test fun descriptiveGoalIsSeparateFromAnthropometry(){OrganizationGoal(title="Participar da consulta",progress="Aguardando confirmação",status="Pausada").validate()}
    @Test(expected=IllegalArgumentException::class) fun malformedGoalDateIsRejected(){OrganizationGoal(title="Meta",due="2026-02-30").validate()}
    @Test(expected=IllegalArgumentException::class) fun unknownGoalStatusIsRejected(){OrganizationGoal(title="Meta",status="Inventado").validate()}
    @Test fun cardLayoutRetainsNewAndRemovesDeletedRecords(){assertEquals(listOf("b","a","c"),HtmlParity.recordOrder(listOf("a","b","c"),"gone,b,b"))}
    @Test fun cardLayoutMoveAndBounds(){assertEquals(listOf("b","a","c"),HtmlParity.moveRecords(listOf("a","b","c"),null,"b",-1));assertEquals(listOf("a","b"),HtmlParity.moveRecords(listOf("a","b"),null,"a",-1))}
    @Test fun ef70SlopeUsesActualDates(){val a=HtmlAnalysis.analyze(rows());assertEquals(-60.0/14,a.per30["Gordura"]!!,1e-9);assertNotNull(a.trajectory);assertNotNull(a.sufficiency)}
    @Test fun ef70TooCloseLastCollectionBlocksScore(){val p=rows().toMutableList();p[2]=p[2].copy(day=19);val r=HtmlAnalysis.analyze(p);assertNull(r.trajectory)}
    @Test fun ef70MethodChangeRemovesFatSlopeAndLimitsQuality(){val r=HtmlAnalysis.analyze(rows(listOf("Pollock","BIA","BIA")));assertNull(r.per30["Gordura"]);assertTrue(r.sufficiency!!<=45)}
    @Test fun ef70PairedDivergenceCapsQuality(){val p=rows().toMutableList();p[2]=p[2].copy(pairedDelta=9.0);assertTrue(HtmlAnalysis.analyze(p).sufficiency!!<=40)}
    @Test fun ef70NoMadeUpMetrics(){val r=HtmlAnalysis.analyze(rows().map{it.copy(fat=null,lean=null,waist=null,weight=null)});assertNull(r.trajectory);assertTrue(r.per30.values.all{it==null})}
    @Test fun ef70DuplicateDaysCannotUnlockHistory(){assertNull(HtmlAnalysis.analyze(listOf(rows()[0],rows()[0],rows()[2])).trajectory)}
    @Test fun pdfContainsEditableFieldsAndReadonlyIdentity(){val bytes=FillableIntakePdf.generate(HtmlFormLibrary.presets.first().draft("client","Álvaro"));val target=java.io.File("build/reports/qa/anamnese-fillable.pdf");target.parentFile?.mkdirs();target.writeBytes(bytes);val text=bytes.toString(Charsets.ISO_8859_1);assertTrue(text.startsWith("%PDF-1.4"));assertTrue(text.contains("/AcroForm"));assertTrue(text.contains("/T (resposta_0)"));assertTrue(text.contains("/Ff 1"));assertTrue(text.contains("/Ff 4096"));assertTrue(text.contains("/Count 2"))}
    @Test(expected=IllegalArgumentException::class) fun pdfCannotLoseClientIdentity(){FillableIntakePdf.generate(HtmlFormLibrary.presets.first().draft("",""))}
    @Test fun longQuestionsGenerateExtraPagesWithoutLosingText(){val draft=FormTemplate(title="Modelo",questions=List(8){"W".repeat(1000)}).draft("id","Aluno");val text=FillableIntakePdf.generate(draft).toString(Charsets.ISO_8859_1);assertTrue(text.contains("/Count 8"));assertTrue(text.contains("/T (resposta_7)"))}
}
