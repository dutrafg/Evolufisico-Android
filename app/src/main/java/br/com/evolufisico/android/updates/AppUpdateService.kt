package br.com.evolufisico.android.updates

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import org.json.JSONArray
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Atualização pessoal fora da Play Store.
 *
 * Verifica o packageName, versionCode e a assinatura do APK antes de chamar
 * o instalador do Android. O SO decide a instalação e exige consentimento.
 */
data class AvailableRelease(
    val tag: String,
    val apkUrl: String,
    val sha256: String?,
    val sizeBytes: Long
)

class AppUpdateService(private val context: Context) {
    private val pkg = context.packageName
    private val outputDir = File(context.cacheDir, "apk-updates")
    private val maxSize = 250L * 1024 * 1024

    fun releaseFromGitHub(): AvailableRelease? {
        val connection = (URL(
            "https://api.github.com/repos/dutrafg/Evolufisico-Android/releases?per_page=10"
        ).openConnection() as HttpURLConnection).apply {
            connectTimeout = 12000
            readTimeout = 15000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "EvoluFisico-Android-AppUpdater")
        }
        val content = try {
            check(connection.responseCode == 200) { "Consulta de versões falhou (HTTP ${connection.responseCode})." }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
        val releases = JSONArray(content)
        for (i in 0 until releases.length()) {
            val release = releases.getJSONObject(i)
            if (release.optBoolean("draft", false)) continue
            val assets = release.optJSONArray("assets") ?: continue
            for (j in 0 until assets.length()) {
                val asset = assets.getJSONObject(j)
                val name = asset.optString("name", "")
                val size = asset.optLong("size", 0)
                if (!name.endsWith(".apk", ignoreCase = true) || size !in 1..maxSize) continue
                val location = asset.optString("browser_download_url")
                requireTrustedReleaseUrl(location)
                val digest = asset.optString("digest", "")
                    .takeIf { it.matches(Regex("sha256:[a-fA-F0-9]{64}")) }
                    ?.substringAfter(':')?.lowercase()
                return AvailableRelease(release.optString("tag_name", "Sem etiqueta"),
                    location, digest, size)
            }
        }
        return null
    }

    /**
     * Baixa para cache privado; nunca grava bytes executáveis nas pastas
     * com dados médicos. Não usa HTTP nem hosts arbitrários de origem.
     */
    fun download(release: AvailableRelease): File {
        requireTrustedReleaseUrl(release.apkUrl)
        require(release.sizeBytes in 1..maxSize)
        outputDir.mkdirs()
        val staged = File(outputDir, "evolufisico-update.tmp")
        val ready = File(outputDir, "evolufisico-update.apk")
        staged.delete()
        ready.delete()
        try {
            var url = URL(release.apkUrl)
            var connection: HttpURLConnection? = null
            for (redirect in 0..5) {
                requireTrustedDownloadHost(url)
                connection?.disconnect()
                connection = (url.openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = false
                    connectTimeout = 15000
                    readTimeout = 30000
                    setRequestProperty("User-Agent", "EvoluFisico-Android-AppUpdater")
                }
                val response = connection.responseCode
                if (response in listOf(301,302,303,307,308)) {
                    val target = connection.getHeaderField("Location")
                        ?: error("Redirecionamento inválido.")
                    url = URL(url, target)
                    if (redirect == 5) error("Excesso de redirecionamentos.")
                    continue
                }
                check(response == 200) { "Download indisponível (HTTP $response)." }
                val expectedLength = connection.contentLengthLong
                if (expectedLength > maxSize) error("APK excede o limite permitido.")
                var bytes = 0L
                val hash = MessageDigest.getInstance("SHA-256")
                connection.inputStream.use { input ->
                    staged.outputStream().use { output ->
                        val buffer = ByteArray(32768)
                        while (true) {
                            val n = input.read(buffer)
                            if (n < 0) break
                            bytes += n
                            require(bytes <= maxSize) { "APK excede 250 MB." }
                            output.write(buffer, 0, n)
                            hash.update(buffer, 0, n)
                        }
                        output.flush()
                    }
                }
                connection.disconnect()
                require(bytes == release.sizeBytes) { "Download incompleto." }
                val calculated = hash.digest().joinToString("") { "%02x".format(it) }
                if (release.sha256 != null) {
                    require(calculated.equals(release.sha256, ignoreCase = true)) {
                        "O SHA-256 do APK não corresponde ao publicado no GitHub."
                    }
                }
                check(staged.renameTo(ready)) { "Falha ao finalizar arquivo de atualização." }
                return ready
            }
            error("Não foi possível obter o arquivo.")
        } catch (problem: Exception) {
            staged.delete()
            ready.delete()
            throw problem
        }
    }

    fun importFromDocument(uri: Uri): File {
        require(uri.scheme == "content") { "Selecione um arquivo do provedor Android." }
        outputDir.mkdirs()
        val staged = File(outputDir, "evolufisico-update.tmp")
        val ready = File(outputDir, "evolufisico-update.apk")
        staged.delete()
        ready.delete()
        try {
            var length = 0L
            val input = context.contentResolver.openInputStream(uri)
                ?: error("Não foi possível ler o APK.")
            input.use { source ->
                staged.outputStream().use { target ->
                    val block = ByteArray(32768)
                    while (true) {
                        val n = source.read(block)
                        if (n < 0) break
                        length += n
                        require(length <= maxSize) { "APK muito grande." }
                        target.write(block, 0, n)
                    }
                }
            }
            check(length > 0) { "Arquivo vazio." }
            check(staged.renameTo(ready)) { "Falha ao preparar APK." }
            return ready
        } catch (problem: Exception) {
            staged.delete()
            ready.delete()
            throw problem
        }
    }

    /** Diagnóstico visível: o certificado é público; a chave privada jamais é lida. */
    @Suppress("DEPRECATION")
    fun installedVersionDiagnostics(): String = runCatching {
        val info = context.packageManager.getPackageInfo(pkg, PackageManager.GET_SIGNING_CERTIFICATES)
        val cert = info.signingInfo?.apkContentsSigners?.firstOrNull()
        val fp = cert?.toByteArray()?.let { certificateSha256(it) } ?: "não disponível"
        val debuggable = context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
        "Instalada: ${info.versionName} (código ${info.longVersionCode}) · " +
            (if (debuggable) "APK DEBUG" else "APK RELEASE") +
            " · certificado SHA-256: ${fp.take(16)}…"
    }.getOrElse { "Não foi possível consultar a assinatura instalada." }

    private fun certificateSha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { "%02X".format(it) }

    @Suppress("DEPRECATION")
    fun validate(file: File): String {
        val flags = PackageManager.GET_SIGNING_CERTIFICATES
        val packageManager = context.packageManager
        val candidate = packageManager.getPackageArchiveInfo(file.absolutePath, flags)
            ?: error("Este arquivo não é um APK Android válido.")
        require(candidate.packageName == pkg) {
            "O APK pertence a outro aplicativo. Nenhum dado foi alterado."
        }
        val installed = packageManager.getPackageInfo(pkg, flags)
        require(UpdatePolicy.isUpgrade(installed.longVersionCode, candidate.longVersionCode)) {
            "O APK não possui versãoCode superior à instalada (${installed.longVersionCode})."
        }
        val incoming = candidate.signingInfo?.apkContentsSigners
            ?: error("APK sem assinatura verificável.")
        val current = installed.signingInfo?.apkContentsSigners
            ?: error("Assinatura instalada indisponível.")
        val matching = UpdatePolicy.sameSigningCertificate(
            current.map { it.toByteArray() }, incoming.map { it.toByteArray() }
        )
        require(matching) {
            val installedFingerprint = current.firstOrNull()?.toByteArray()
                ?.let { certificateSha256(it) } ?: "indisponível"
            val candidateFingerprint = incoming.firstOrNull()?.toByteArray()
                ?.let { certificateSha256(it) } ?: "indisponível"
            "ASSINATURA INCOMPATÍVEL. Instalada: ${installedFingerprint.take(16)}… " +
                "Nova: ${candidateFingerprint.take(16)}…. " +
                "O GitHub Actions usou certificados DEBUG diferentes. Não existe atualização " +
                "direta segura entre essas assinaturas. Faça backup .efbak fora do app, " +
                "verifique o backup e só então planeje uma migração. " +
                "Para atualizações futuras use sempre o mesmo APK RELEASE assinado."
        }
        return "APK compatível: versãoCode ${installed.longVersionCode} → ${candidate.longVersionCode}."
    }

    fun installAllowed(): Boolean = context.packageManager.canRequestPackageInstalls()

    fun unknownSourcesSettings(): Intent = Intent(
        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
        Uri.parse("package:${context.packageName}")
    )

    @Suppress("DEPRECATION")
    fun installerIntent(file: File): Intent {
        validate(file)
        val contentUri = FileProvider.getUriForFile(
            context, "${context.packageName}.updates", file
        )
        return Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            clipData = ClipData.newUri(context.contentResolver, "Atualização EvoluFísico", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_RETURN_RESULT, false)
        }
    }

    private fun requireTrustedReleaseUrl(raw: String) {
        require(UpdatePolicy.validReleaseUrl(raw)) {
            "Origem de atualização não autorizada."
        }
    }

    private fun requireTrustedDownloadHost(url: URL) {
        val host = url.host.lowercase()
        require(url.protocol == "https" && host in setOf(
            "github.com", "objects.githubusercontent.com", "release-assets.githubusercontent.com"
        )) { "Redirecionamento de download não autorizado." }
    }
}
