package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class AdvancedCalculationsTest {
    @Test fun waistHeightRatioUsesSameUnitsAndBoundaries() {
        assertEquals(.5,AdvancedCalculations.waistHeightRatio(80.0,160.0)!!,.0001)
        assertNull(AdvancedCalculations.waistHeightRatio(0.0,160.0))
        assertNull(AdvancedCalculations.waistHeightRatio(80.0,Double.NaN))
    }
    @Test fun briRequiresValidMeasurementsAndStaysFinite() {
        assertTrue(AdvancedCalculations.bri(80.0,170.0)!!.isFinite())
        assertNull(AdvancedCalculations.bri(19.0,170.0))
        assertNull(AdvancedCalculations.bri(80.0,0.0))
    }
    @Test fun revisedHarrisBenedictSupportsSexAliases() {
        assertEquals(AdvancedCalculations.harrisBenedictRevised("Masculino",30,80.0,180.0),AdvancedCalculations.harrisBenedictRevised("M",30,80.0,180.0))
        assertEquals(AdvancedCalculations.harrisBenedictRevised("Feminino",30,60.0,165.0),AdvancedCalculations.harrisBenedictRevised("F",30,60.0,165.0))
    }
    @Test fun metabolicEquationsRejectUnknownSexAndInvalidAge() {
        assertNull(AdvancedCalculations.harrisBenedictRevised("X",30,80.0,180.0))
        assertNull(AdvancedCalculations.harrisBenedictClassic("M",17,80.0,180.0))
    }
    @Test fun classicHarrisBenedictProducesAdultEstimate() {
        assertTrue(AdvancedCalculations.harrisBenedictClassic("F",30,60.0,165.0)!!>1000)
    }
    @Test fun katchMcArdleUsesLeanMassAndFatBounds() {
        assertEquals(370.0+21.6*80.0*.75,AdvancedCalculations.katchMcArdle(80.0,25.0)!!,.0001)
        assertNull(AdvancedCalculations.katchMcArdle(80.0,81.0))
    }
    @Test fun dailyExpenditureRequiresPlausibleFactor() {
        assertEquals(3000.0,AdvancedCalculations.dailyExpenditure(2000.0,1.5)!!,.0001)
        assertNull(AdvancedCalculations.dailyExpenditure(2000.0,Double.POSITIVE_INFINITY))
    }
    @Test fun targetWeightDoesNotInventValuesAtImpossibleBodyFat() {
        assertEquals(80.0,AdvancedCalculations.weightForTargetFat(100.0,20.0,0.0)!!,.0001)
        assertNull(AdvancedCalculations.weightForTargetFat(100.0,20.0,100.0))
    }
    @Test fun karvonenRejectsRestingRateAtOrAboveMaximum() {
        assertNull(AdvancedCalculations.karvonen(180,180,60))
        assertNull(AdvancedCalculations.karvonen(180,181,60))
        assertEquals(120.0,AdvancedCalculations.karvonen(180,60,50)!!,.0001)
    }
    @Test fun invalidInputsNeverReturnNanOrInfinity() {
        val values=listOf(
            AdvancedCalculations.waistHeightRatio(Double.NaN,160.0),AdvancedCalculations.bri(80.0,Double.POSITIVE_INFINITY),
            AdvancedCalculations.harrisBenedictRevised("M",30,Double.NaN,180.0),AdvancedCalculations.katchMcArdle(Double.POSITIVE_INFINITY,10.0),
            AdvancedCalculations.dailyExpenditure(Double.NaN,1.2),AdvancedCalculations.weightForTargetFat(70.0,Double.NEGATIVE_INFINITY,20.0))
        assertTrue(values.all { it==null || it.isFinite() })
    }
}
