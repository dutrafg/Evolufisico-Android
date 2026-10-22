package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class AnthropometryTest {
    private val folds=Anthropometry.sites.keys.associateWith { 15.0 }
    @Test fun pollockRequiresAllSevenSites() {
        assertNotNull(Anthropometry.pollock7("Masculino",30,80.0,folds))
        assertNull(Anthropometry.pollock7("Masculino",30,80.0,folds - "coxa"))
    }
    @Test fun pollockRejectsUnknownSexAndOutOfProtocolAge() {
        assertNull(Anthropometry.pollock7("Outro",30,80.0,folds))
        assertNull(Anthropometry.pollock7("M",17,80.0,folds))
        assertNull(Anthropometry.pollock7("M",81,80.0,folds))
    }
    @Test fun pollockRejectsNonPositiveOrNonFiniteMeasurements() {
        assertNull(Anthropometry.pollock7("F",30,80.0,folds + ("coxa" to 0.0)))
        assertNull(Anthropometry.pollock7("F",30,80.0,folds + ("coxa" to Double.NaN)))
        assertNull(Anthropometry.pollock7("F",30,Double.POSITIVE_INFINITY,folds))
    }
    @Test fun pollockMassesReconcileToBodyWeight() {
        val result=Anthropometry.pollock7("F",30,80.0,folds)!!
        assertEquals(80.0,result.fatMassKg+result.leanMassKg,.0001)
    }
    @Test fun pollockClampsImplausibleOutputAndFlagsIt() {
        val result=Anthropometry.pollock7("M",30,80.0,Anthropometry.sites.keys.associateWith { 150.0 })
        assertNotNull(result)
        assertTrue(result!!.fatPercent in 3.0..60.0)
        assertTrue(result.clamped)
    }
    @Test fun mifflinSupportsOnlyAdultInputsWithKnownSex() {
        assertEquals(1780.0,Anthropometry.mifflinStJeor("Masculino",30,80.0,180.0)!!,.0001)
        assertNull(Anthropometry.mifflinStJeor("Não informado",30,80.0,180.0))
        assertNull(Anthropometry.mifflinStJeor("Feminino",17,60.0,165.0))
    }
    @Test fun mifflinRejectsNonPositiveAndNonFiniteValues() {
        assertNull(Anthropometry.mifflinStJeor("F",30,0.0,165.0))
        assertNull(Anthropometry.mifflinStJeor("F",30,60.0,Double.NaN))
    }
    @Test fun weightRangeRequiresPositiveFiniteHeight() {
        val range=Anthropometry.weightRange(200.0)!!
        assertEquals(80.0,range.first,.0001);assertEquals(99.6,range.second,.0001)
        assertNull(Anthropometry.weightRange(0.0));assertNull(Anthropometry.weightRange(Double.NaN))
    }
}
