package br.com.evolufisico.nativo.domain

object LayoutPolicy {
    /** Larger Android font settings reduce the number of dashboard columns. */
    fun columns(widthDp:Double,fontScale:Double):Int {
        require(widthDp.isFinite() && widthDp>0 && fontScale.isFinite() && fontScale>0)
        return (widthDp/(160.0*fontScale.coerceAtLeast(1.0))).toInt().coerceIn(1,3)
    }
    fun cardColumns(widthDp:Double,fontScale:Double,minimumWidth:Double=250.0,maxColumns:Int=4):Int {
        require(widthDp.isFinite() && widthDp>0 && fontScale.isFinite() && fontScale>0)
        require(minimumWidth.isFinite() && minimumWidth>0 && maxColumns in 1..6)
        val effectiveMinimum=minimumWidth*fontScale.coerceAtLeast(1.0)
        return ((widthDp+12.0)/(effectiveMinimum+12.0)).toInt().coerceIn(1,maxColumns)
    }
    fun pageBounds(total:Int,page:Int,size:Int=30):IntRange {
        require(total>=0 && size>0)
        if(total==0)return IntRange.EMPTY
        val start=page.coerceIn(0,(total-1)/size)*size
        return start until minOf(total.toLong(),start.toLong()+size).toInt()
    }
}
