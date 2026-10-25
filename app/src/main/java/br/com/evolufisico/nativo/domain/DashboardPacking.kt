package br.com.evolufisico.nativo.domain

/** Integer pixel grid with twelve lanes. Each card goes above the shortest free region. */
object DashboardPacking {
    data class Cell(val x:Int,val y:Int,val width:Int,val height:Int,val span:Int)
    fun span(group:String,size:String,widthDp:Float,fontScale:Float):Int {
        require(widthDp>0 && widthDp.isFinite() && fontScale>0 && fontScale.isFinite())
        val scale=fontScale.coerceAtLeast(1f)
        if(widthDp<600f) return if(group=="homeStats"&&size=="small"&&widthDp>=280f*scale)6 else 12
        val wanted=HtmlHome.cardSpan(group,size)
        val minimum=if(group=="homeStats")130f*scale else 250f*scale
        val options=when(wanted){3->listOf(3,6,12);4->listOf(4,6,12);6->listOf(6,12);8->listOf(8,12);else->listOf(12)}
        return options.first{it>=wanted && ((widthDp+12f)*it/12f-12f>=minimum || it==12)}
    }
    fun width(total:Int,gap:Int,span:Int):Int {
        require(total>0&&gap>=0&&span in 1..12)
        return (((total.toLong()+gap)*span/12)-gap).toInt().coerceAtLeast(1)
    }
    fun pack(total:Int,gap:Int,spans:List<Int>,heights:List<Int>):List<Cell> {
        require(total>0&&gap>=0&&spans.size==heights.size&&heights.all{it>=0}&&spans.all{it in 1..12})
        val lanes=IntArray(12)
        return spans.mapIndexed{index,span->
            val lane=(0..12-span).minWith(compareBy<Int>{start->(start until start+span).maxOf{lanes[it]}}.thenBy{it})
            val y=(lane until lane+span).maxOf{lanes[it]}
            val height=heights[index]
            (lane until lane+span).forEach{lanes[it]=y+height+gap}
            Cell(((total.toLong()+gap)*lane/12).toInt(),y,width(total,gap,span),height,span)
        }
    }
    fun move(order:List<String>,id:String,target:String):List<String> {
        val from=order.indexOf(id);val to=order.indexOf(target)
        if(from<0||to<0||from==to)return order
        return order.toMutableList().apply{removeAt(from);add(to,id)}
    }
}
