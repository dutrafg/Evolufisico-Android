package br.com.evolufisico.android.updates

import org.junit.Assert.*
import org.junit.Test

class ReleasePolicyTest {
    @Test fun ordersStableAboveReleaseCandidatesAndNewerVersions() {
        assertTrue(ReleasePolicy.newer("v0.40.0-alpha","0.30.0-alpha"))
        assertTrue(ReleasePolicy.newer("v1.0.0","1.0.0-rc3"))
        assertTrue(ReleasePolicy.newer("v1.0.0-rc2","1.0.0-beta"))
        assertFalse(ReleasePolicy.newer("v0.20.0-alpha","0.30.0-alpha"))
        assertFalse(ReleasePolicy.newer("v0.40.0-alpha","0.40.0-alpha"))
        assertNull(ReleasePolicy.parse("version-1.0.0"))
        assertNull(ReleasePolicy.parse("v999999999999.0.0"))
    }

    @Test fun parsesAndroidAlphaPreviewAndVersionOrdering() {
        val installed=ReleasePolicy.parse("38.0.0-alpha.38-preview")
        assertNotNull(installed)
        assertEquals(0,installed!!.channel)
        assertEquals(38,installed.channelNumber)
        assertFalse(ReleasePolicy.newer("38.0.0-alpha.38","38.0.0-alpha.38-preview"))
        assertTrue(ReleasePolicy.newer("38.0.0-beta.1","38.0.0-alpha.38-preview"))
        assertNotNull(ReleasePolicy.parse("v39.0.0-rc2"))
    }
    @Test fun fingerprintMustBeSingleHexLine() {
        assertEquals("A".repeat(64), ReleasePolicy.readFingerprint("a".repeat(64)+"\n"))
        assertNull(ReleasePolicy.readFingerprint("a".repeat(65)))
        assertNull(ReleasePolicy.readFingerprint("a".repeat(64)+"\nEXTRA"))
    }
    @Test fun onlyExactSingleLineChecksumAccepted() {
        val hash = "a".repeat(64)
        val filename = "Evolufisico-Android-release.apk"
        assertEquals(hash,ReleasePolicy.readChecksum("$hash  $filename\n",filename))
        assertNull(ReleasePolicy.readChecksum("$hash  evil.apk",filename))
        assertNull(ReleasePolicy.readChecksum("$hash  $filename\nmalicious",filename))
        assertNull(ReleasePolicy.readChecksum("abcd",filename))
    }
}
