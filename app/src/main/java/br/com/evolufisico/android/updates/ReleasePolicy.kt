package br.com.evolufisico.android.updates

/** Compara tags oficiais de release. Sem rede, fácil de testar. */
data class ReleaseTag(
    val major: Int, val minor: Int, val patch: Int,
    val channel: Int, val channelNumber: Int
) : Comparable<ReleaseTag> {
    override fun compareTo(other: ReleaseTag): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch },
            { it.channel }, { it.channelNumber })
}

object ReleasePolicy {
    private val pattern = Regex("""^v?(\d+)\.(\d+)\.(\d+)(?:-(alpha|beta|rc)(?:[.-]?(\d+))?)?(?:-preview)?$""")
    fun parse(text: String): ReleaseTag? {
        val match = pattern.matchEntire(text) ?: return null
        return runCatching {
            val (maj, min, patch, channel, number) = match.destructured
            ReleaseTag(maj.toInt(),min.toInt(),patch.toInt(),
                when (channel) { "alpha" -> 0; "beta" -> 1; "rc" -> 2; else -> 3 },
                if (number.isEmpty()) 0 else number.toInt())
        }.getOrNull()
    }
    fun newer(tag: String, installed: String): Boolean {
        val candidate = parse(tag) ?: return false
        val old = parse(installed)
        return old == null || candidate > old
    }

    fun readFingerprint(text: String): String? =
        text.trimEnd('\n').takeIf {
            it.matches(Regex("[A-Fa-f0-9]{64}")) && !text.contains('\r')
        }?.uppercase()

    /** GitHub sidecar padronizado: 64 hex + dois espaços + nome exato. */
    fun readChecksum(text: String, fileName: String): String? {
        if (text.length > 256 || text.contains('\u0000') || text.contains('\r')) return null
        val lines = text.trimEnd('\n').split('\n')
        if (lines.size != 1) return null
        val expected = Regex("""^([a-fA-F0-9]{64})  ([A-Za-z0-9._-]+)$""")
            .matchEntire(lines.single()) ?: return null
        if (expected.groupValues[2] != fileName) return null
        return expected.groupValues[1].lowercase()
    }
}
