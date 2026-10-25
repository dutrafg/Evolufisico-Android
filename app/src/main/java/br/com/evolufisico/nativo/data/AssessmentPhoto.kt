package br.com.evolufisico.nativo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * Stores small JPEG data URLs so assessment photos remain part of local records and portable backups.
 * Images are resized before encoding to keep database and backup sizes bounded.
 */
object AssessmentPhoto {
    private const val PREFIX = "data:image/jpeg;base64,"
    private const val MAX_EDGE = 1200
    private const val MAX_ENCODED_CHARS = 1_500_000

    fun fromUri(context: Context, uri: Uri): String {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val scale = minOf(1.0, MAX_EDGE.toDouble() / max(width, height).coerceAtLeast(1))
            decoder.setTargetSize((width * scale).toInt().coerceAtLeast(1), (height * scale).toInt().coerceAtLeast(1))
            decoder.isMutableRequired = false
        }
        val stream = ByteArrayOutputStream()
        check(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) { "Não foi possível otimizar a foto." }
        val encoded = PREFIX + Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
        require(encoded.length <= MAX_ENCODED_CHARS) { "A foto ainda ficou muito grande. Escolha uma imagem menor." }
        return encoded
    }

    fun decode(dataUrl: String): Bitmap? = runCatching {
        require(dataUrl.startsWith(PREFIX))
        val bytes = Base64.decode(dataUrl.removePrefix(PREFIX), Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
