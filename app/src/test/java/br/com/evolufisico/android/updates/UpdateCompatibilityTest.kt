package br.com.evolufisico.android.updates
import org.junit.Assert.*
import org.junit.Test

class UpdateCompatibilityTest {
    private val cert="release-certificate".toByteArray()
    private fun evaluate(
        pkg:String="br.com.evolufisico.android",version:Long=196,
        certs:List<ByteArray> = listOf(cert)
    ):UpdateBlocker = UpdateCompatibility.diagnose(
        "br.com.evolufisico.android",pkg,195L,version,listOf(cert),certs)
    @Test fun compatibleIfPackageSignerAndVersionMatch() {
        assertEquals(UpdateBlocker.NONE,evaluate())
    }
    @Test fun differentPackageIsNotSameInstallation() {
        assertEquals(UpdateBlocker.DIFFERENT_APPLICATION,
            evaluate(pkg="br.com.evolufisico.android.beta2"))
    }
    @Test fun updateCannotUseSameOrLowerVersionCode() {
        assertEquals(UpdateBlocker.VERSION_NOT_NEWER,evaluate(version=195))
        assertEquals(UpdateBlocker.VERSION_NOT_NEWER,evaluate(version=194))
    }
    @Test fun missingOrMismatchedSigningBlocksUpdate() {
        assertEquals(UpdateBlocker.SIGNATURE_MISSING,evaluate(certs=emptyList()))
        assertEquals(UpdateBlocker.SIGNATURE_MISMATCH,
            evaluate(certs=listOf("temporary".toByteArray())))
    }
}
