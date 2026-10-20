package br.com.evolufisico.android

/**
 * Durante desenvolvimento é útil compartilhar capturas para QA.
 * Ao publicar versão estável (ex.: 1.0.0) protege a janela.
 * Alpha, beta e release candidates mantêm capturas liberadas.
 */
object ScreenshotPolicy {
    private val stableVersion = Regex("""^[0-9]+\.[0-9]+\.[0-9]+$""")
    fun shouldBlock(versionName: String): Boolean =
        stableVersion.matches(versionName.trim())
}
