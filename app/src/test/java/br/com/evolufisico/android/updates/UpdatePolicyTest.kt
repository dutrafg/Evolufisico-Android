package br.com.evolufisico.android.updates

import org.junit.Assert.*
import org.junit.Test

class UpdatePolicyTest {
    @Test fun acceptsOnlyOfficialReleaseAssetPath() {
        assertTrue(UpdatePolicy.validReleaseUrl(
            "https://github.com/dutrafg/Evolufisico-Android/releases/download/v0.10.0/Evolufisico.apk"
        ))
        assertFalse(UpdatePolicy.validReleaseUrl(
            "http://github.com/dutrafg/Evolufisico-Android/releases/download/v1/x.apk"
        ))
        assertFalse(UpdatePolicy.validReleaseUrl(
            "https://github.com.evil.com/dutrafg/Evolufisico-Android/releases/download/v1/x.apk"
        ))
        assertFalse(UpdatePolicy.validReleaseUrl(
            "https://github.com/other/repo/releases/download/v1/evil.apk"
        ))
        assertFalse(UpdatePolicy.validReleaseUrl(
            "https://github.com/dutrafg/Evolufisico-Android/releases/download/v1/x.apk?redirect=bad"
        ))
    }
    @Test fun rejectsPathTraversalAndMisleadingAssets() {
        val prefix="https://github.com/dutrafg/Evolufisico-Android/releases/download/"
        assertFalse(UpdatePolicy.validReleaseUrl(prefix+"v1.0.0/../../evil.apk"))
        assertFalse(UpdatePolicy.validReleaseUrl(prefix+"v1.0.0/%2e%2e%2fevil.apk"))
        assertFalse(UpdatePolicy.validReleaseUrl(prefix+"v1.0.0/unknown.txt"))
        assertFalse(UpdatePolicy.validReleaseUrl(prefix+"v1.0.0/asset.apk/extra"))
        assertFalse(UpdatePolicy.validReleaseUrl(prefix+"v1..2/app.apk"))
        assertTrue(UpdatePolicy.validReleaseUrl(prefix+"v1.0.0-beta.1/official.apk"))
    }
    @Test fun preventsDowngradesAndReinstallsOfSameVersion() {
        assertTrue(UpdatePolicy.isUpgrade(6,10))
        assertFalse(UpdatePolicy.isUpgrade(10,10))
        assertFalse(UpdatePolicy.isUpgrade(10,9))
    }
    @Test fun rejectsPartialMultiSignerMatches() {
        val a="first".toByteArray()
        val b="second".toByteArray()
        assertFalse(UpdatePolicy.sameSigningCertificate(listOf(a,b),listOf(a)))
        assertFalse(UpdatePolicy.sameSigningCertificate(listOf(a,b),
            listOf(a,"wrong".toByteArray())))
        assertTrue(UpdatePolicy.sameSigningCertificate(listOf(a,b),listOf(b,a)))
    }
    @Test fun rejectsIncompatibleSigningCertificate() {
        val old = "certificate-1".toByteArray()
        assertTrue(UpdatePolicy.sameSigningCertificate(listOf(old), listOf(old.clone())))
        assertFalse(UpdatePolicy.sameSigningCertificate(listOf(old),listOf("new-key".toByteArray())))
        assertFalse(UpdatePolicy.sameSigningCertificate(listOf(old),emptyList()))
    }
}
