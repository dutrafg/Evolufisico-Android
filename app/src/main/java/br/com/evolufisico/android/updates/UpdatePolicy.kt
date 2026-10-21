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

    fun isUpgrade(currentVersion: Long, candidateVersion: Long): Boolean =
        currentVersion >= 0 && candidateVersion > currentVersion

    fun sameSigningCertificate(
        installed: List<ByteArray>, candidate: List<ByteArray>
    ): Boolean =
        installed.isNotEmpty() && candidate.isNotEmpty() &&
            candidate.any { incoming -> installed.any {
                current -> MessageDigest.isEqual(incoming, current)
            } }
}
