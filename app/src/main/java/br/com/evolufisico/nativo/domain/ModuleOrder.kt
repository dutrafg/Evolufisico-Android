package br.com.evolufisico.nativo.domain

/** Route IDs never change when the user rearranges their navigation. */
object ModuleOrder {
    val defaults=listOf(0,8,9,10,1,2,4,5,11,12,13,6,14,15,3,7)
    fun normalize(values:List<Int>):List<Int> = (values.filter{it in defaults}.distinct()+defaults).distinct()
    fun parse(value:String?):List<Int> = normalize(value.orEmpty().split(',').mapNotNull{it.toIntOrNull()})
    fun move(values:List<Int>,id:Int,offset:Int):List<Int> {
        val result=normalize(values).toMutableList()
        val source=result.indexOf(id)
        if(source<0 || offset !in listOf(-1,1))return result
        val target=source+offset
        if(target !in result.indices)return result
        result.removeAt(source);result.add(target,id)
        return result
    }
}
