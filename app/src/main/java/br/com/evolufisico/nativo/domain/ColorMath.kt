package br.com.evolufisico.nativo.domain

import kotlin.math.pow
object ColorMath {
    fun luminance(argb:Int):Double {
        fun channel(shift:Int):Double {val c=((argb ushr shift) and 255)/255.0;return if(c<=0.04045)c/12.92 else ((c+0.055)/1.055).pow(2.4)}
        return 0.2126*channel(16)+0.7152*channel(8)+0.0722*channel(0)
    }
    fun contrast(a:Int,b:Int):Double {
        val x=luminance(a);val y=luminance(b)
        return (maxOf(x,y)+0.05)/(minOf(x,y)+0.05)
    }
}
