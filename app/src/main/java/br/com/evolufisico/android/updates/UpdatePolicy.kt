package br.com.evolufisico.android.updates

import java.net.URI
import java.security.MessageDigest

/** Validação pura para permitir testes unitários sem Android. */
object UpdatePolicy {
    fun validReleaseUrl(text: String): Boolean {
        val uri = runCatching { URI(text) }.getOrNull() ?: return false
        return uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals("github.com", ignoreCase = true) &&
            uri.port == -1 && uri.userInfo == null &&
            uri.rawPath.startsWith("/dutrafg/Evolufisico-Android/releases/download/") &&
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
