package br.com.evolufisico.nativo.data

import java.io.ByteArrayInputStream
import java.io.InputStream
import org.junit.Assert.*
import org.junit.Test

class ReadLimitsTest {
    @Test fun acceptsExactLimit() {
        assertArrayEquals(byteArrayOf(1,2),ByteArrayInputStream(byteArrayOf(1,2)).readLimited(2))
    }
    @Test(expected=IllegalArgumentException::class) fun rejectsOverflow() {
        ByteArrayInputStream(byteArrayOf(1,2,3)).readLimited(2)
    }
    @Test fun zeroBulkReadFallsBackToSingleByteAndFinishes() {
        val source=object:InputStream() {
            var value=0
            override fun read():Int=if(value<3) value++ else -1
            override fun read(b:ByteArray,off:Int,len:Int):Int=0
        }
        assertArrayEquals(byteArrayOf(0,1,2),source.readLimited(3))
    }
    @Test(expected=IllegalArgumentException::class) fun fallbackEnforcesLimit() {
        val source=object:InputStream() {
            override fun read():Int=42
            override fun read(b:ByteArray,off:Int,len:Int):Int=0
        }
        source.readLimited(2)
    }
    @Test fun emptyInputReturnsEmptyArray() {
        assertArrayEquals(byteArrayOf(),ByteArrayInputStream(byteArrayOf()).readLimited(1))
    }
}
