package br.com.evolufisico.android.insights

/** Ordem local e estável das categorias de alertas.
 * Não altera valores, datas, clientes ou o resultado da camada analítica.
 */
object InsightsOrder {
    val standard = InsightKind.entries.toList()

    fun parse(value:String?):List<InsightKind> {
        val saved=value.orEmpty().split(",").mapNotNull { code ->
            InsightKind.entries.firstOrNull { it.name==code.trim() }
        }.distinct()
        return saved + standard.filterNot { it in saved }
    }

    fun serialize(order:List<InsightKind>):String =
        parse(order.joinToString(",") { it.name }).joinToString(",") { it.name }

    fun move(order:List<InsightKind>,kind:InsightKind,direction:Int):List<InsightKind> {
        val current=parse(serialize(order)).toMutableList()
        if(direction !in listOf(-1,1)) return current
        val i=current.indexOf(kind)
        if(i<0 || i+direction !in current.indices) return current
        val other=current[i+direction]
        current[i+direction]=kind
        current[i]=other
        return current
    }

    fun sort(alerts:List<Insight>,order:List<InsightKind>):List<Insight> {
        val priorities=parse(serialize(order)).withIndex().associate { it.value to it.index }
        return alerts.sortedWith(
            compareBy<Insight> { priorities[it.kind] ?: Int.MAX_VALUE }
                .thenBy { it.dateIso }
                .thenBy { it.clientId }
                .thenBy { it.title }
        )
    }
}
