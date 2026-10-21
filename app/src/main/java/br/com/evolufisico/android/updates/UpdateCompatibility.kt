package br.com.evolufisico.android.updates

enum class UpdateBlocker {
    NONE, DIFFERENT_APPLICATION, VERSION_NOT_NEWER, SIGNATURE_MISSING, SIGNATURE_MISMATCH
}
object UpdateCompatibility {
    fun diagnose(installedPackage:String,candidatePackage:String,
        installedCode:Long,candidateCode:Long,
        installedCerts:List<ByteArray>,candidateCerts:List<ByteArray>
    ):UpdateBlocker=when {
        installedPackage!=candidatePackage -> UpdateBlocker.DIFFERENT_APPLICATION
        !UpdatePolicy.isUpgrade(installedCode,candidateCode) -> UpdateBlocker.VERSION_NOT_NEWER
        installedCerts.isEmpty() || candidateCerts.isEmpty() -> UpdateBlocker.SIGNATURE_MISSING
        !UpdatePolicy.sameSigningCertificate(installedCerts,candidateCerts) ->
            UpdateBlocker.SIGNATURE_MISMATCH
        else -> UpdateBlocker.NONE
    }
}
