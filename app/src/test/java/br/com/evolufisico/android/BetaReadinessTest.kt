package br.com.evolufisico.android

import org.junit.Assert.*
import org.junit.Test

class BetaReadinessTest {
    @Test fun unverifiedCandidateMustNotBeReleased() {
        val evidence=BetaEvidence()
        assertEquals(10,BetaReadiness.pending(evidence).size)
        assertFalse(BetaReadiness.canRelease(evidence))
    }
    @Test fun ciAloneNeverApprovesRelease() {
        val evidence=BetaEvidence(successfulCi=true)
        assertFalse(BetaReadiness.canRelease(evidence))
        assertEquals(9,BetaReadiness.pending(evidence).size)
    }
    @Test fun completeEvidenceMustCoverEveryCriterion() {
        val e=BetaEvidence(true,true,true,true,true,true,true,true,true,true)
        assertTrue(BetaReadiness.canRelease(e))
        assertTrue(BetaReadiness.pending(e).isEmpty())
    }
}
