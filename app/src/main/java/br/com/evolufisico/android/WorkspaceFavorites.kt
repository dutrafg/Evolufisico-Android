package br.com.evolufisico.android

/** Até quatro favoritos sincronizados entre Início e Módulos. */
object WorkspaceFavorites {
    private const val maxItems=4
    private val valid get()=WorkspaceModules.all.map { it.route }.toSet()
    fun parse(serialized:String):Set<String> = serialized.split(',')
        .filter { it in valid }.distinct().take(maxItems).toSet()
    fun serialize(items:Set<String>):String = items.filter { it in valid }
        .take(maxItems).joinToString(",")
    fun move(items:Set<String>,route:String,direction:Int):Set<String> {
        if(direction !in setOf(-1,1)) return items
        val ordered=items.toMutableList()
        val old=ordered.indexOf(route)
        if(old<0 || old+direction !in ordered.indices) return items
        val swapped=ordered[old+direction]
        ordered[old+direction]=route
        ordered[old]=swapped
        return LinkedHashSet(ordered)
    }
    fun toggle(items:Set<String>,route:String):Set<String> {
        if(route !in valid) return items
        if(route in items) return items-route
        if(items.size>=maxItems) return items
        return items+route
    }
}
