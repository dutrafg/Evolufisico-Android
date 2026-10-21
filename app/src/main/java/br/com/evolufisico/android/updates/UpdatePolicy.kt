package br.com.evolufisico.android.updates

import java.net.URI
import java.security.MessageDigest

/** Validação pura para permitir testes unitários sem Android. */
object UpdatePolicy {
    fun validReleaseUrl(text: String): Boolean {
        val uri = runCatching { URI(text) }.getOrNull() ?: return false
        val prefix="/dutrafg/Evolufisico-Android/releases/download/"
        val path=uri.rawPath ?: return false
        val rest=path.removePrefix(prefix)
        val parts=rest.split('/')
        val safeSegment=Regex("[A-Za-z0-9][A-Za-z0-9._-]{0,99}")
        val validAsset=Regex("[A-Za-z0-9][A-Za-z0-9._-]{0,199}\\.apk")
        return uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals("github.com", ignoreCase = true) &&
            uri.port == -1 && uri.userInfo == null && path.startsWith(prefix) &&
            parts.size==2 && parts[0].matches(safeSegment) &&
            !parts[0].contains("..") &&
            parts[1].matches(validAsset) && !parts[1].contains("..") &&
            uri.rawQuery == null && uri.rawFragment == null
    }

    /** Metadados assinados do mesmo lançamento (nunca outro host ou arquivo arbitrário). */
    fun validReleaseMetadataUrl(text: String): Boolean {
        val suffixes=listOf(
            "Evolufisico-Android-release.apk.sha256",
            "Evolufisico-Android-release.cert.sha256"
        )
        return suffixes.any { suffix ->
            validReleaseUrl(text.removeSuffix(".sha256")) &&
                text.endsWith("/"+suffix)
        }
    }

    fun isUpgrade(currentVersion: Long, candidateVersion: Long): Boolean =
        currentVersion >= 0 && candidateVersion > currentVersion

    fun sameSigningCertificate(
        installed: List<ByteArray>, candidate: List<ByteArray>
    ): Boolean {
        if(installed.isEmpty() || installed.size!=candidate.size) return false
        val unmatched=candidate.toMutableList()
        return installed.all { original ->
            val idx=unmatched.indexOfFirst { incoming ->
                MessageDigest.isEqual(original,incoming)
            }
            if(idx<0) false else { unmatched.removeAt(idx);true }
        } && unmatched.isEmpty()
    }
}
