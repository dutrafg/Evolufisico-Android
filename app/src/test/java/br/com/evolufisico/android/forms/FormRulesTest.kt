package br.com.evolufisico.android.forms

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class FormRulesTest {
    private val today = LocalDate.parse("2026-10-20")
    private fun sample(template: String = "anamnese_iniciante"): FormResponse {
        val t = FormCatalog.find(template)!!
        return FormResponse("f-1","client-1",template,today.toString(),
            t.questions.associate { it.id to if (it.kind == AnswerKind.YES_NO) "Não" else "Relato fictício" })
    }
    @Test fun allTemplatesHaveUniqueQuestions() {
        assertEquals(5,FormCatalog.templates.size)
        assertEquals(5,FormCatalog.templates.map { it.id }.distinct().size)
        FormCatalog.templates.forEach { template ->
            assertTrue(template.questions.size >= 4)
            assertEquals(template.questions.size,template.questions.map { it.id }.distinct().size)
            assertNull(FormRules.validate(sample(template.id),setOf("client-1"),today))
        }
    }
    @Test fun invalidClientOrMissingRequiredIsRejected() {
        val form = sample()
        assertNotNull(FormRules.validate(form,emptySet(),today))
        assertNotNull(FormRules.validate(form.copy(answers=emptyMap()),setOf("client-1"),today))
        assertNotNull(FormRules.validate(form.copy(answers=form.answers+("alien" to "x")),setOf("client-1"),today))
    }
    @Test fun futureFormAndWrongYesNoAnswerRejected() {
        val form = sample("prontidao")
        assertNotNull(FormRules.validate(form.copy(dateIso="2027-01-01"),setOf("client-1"),today))
        assertNotNull(FormRules.validate(form.copy(answers=form.answers+("sintomas" to "Talvez")),setOf("client-1"),today))
    }
    @Test fun editsKeepClientAndId() {
        val form = sample()
        val edited = form.copy(answers=form.answers+("sono" to "8 horas"))
        assertEquals(form.id,edited.id)
        assertEquals(form.clientId,edited.clientId)
        assertNull(FormRules.validate(edited,setOf("client-1"),today))
    }
}
