package br.com.evolufisico.nativo.data

import java.io.ByteArrayOutputStream
import java.io.InputStream

/** Bound memory use when the user selects a document of unknown size. Android 10 compatible. */
fun InputStream.readLimited(maxBytes: Int): ByteArray {
    require(maxBytes in 1..25_000_000)
    val sink = ByteArrayOutputStream(minOf(maxBytes,8192))
    val buffer = ByteArray(8192)
    while(true) {
        val size = read(buffer)
        if(size == -1) break
        if(size == 0) continue
        require(sink.size().toLong() + size.toLong() <= maxBytes.toLong()) { "Arquivo excede $maxBytes bytes" }
        sink.write(buffer,0,size)
    }
    return sink.toByteArray()
}
