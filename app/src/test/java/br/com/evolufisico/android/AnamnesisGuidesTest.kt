package br.com.evolufisico.android

import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.AssessmentRules
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AnamnesisGuidesTest {
    @Test fun hasDistinctPromptsForAllLevels() {
        val introductions = AssessmentRules.levels.map {
            AnamnesisGuides.forLevel(it).introduction
        }
        assertEquals(3, introductions.distinct().size)
        assertTrue(AnamnesisGuides.forLevel("Avançado").experienceExample.contains("volume"))
    }
    @Test fun extendedAnswersAreValidated() {
        val record = Assessment("id","one",LocalDate.now().toString(),
            "Iniciante","","",3,70.0,170.0,null,null,null,null,
            healthHistory = "a".repeat(701))
        assertNotNull(AssessmentRules.validate(record,setOf("one")))
        assertNull(AssessmentRules.validate(record.copy(healthHistory = "Sem informações."),
            setOf("one")))
    }
}
