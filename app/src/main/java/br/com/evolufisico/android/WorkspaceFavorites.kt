package br.com.evolufisico.android

/** No máximo quatro atalhos pessoais no diretório, nunca na tela inicial. */
object WorkspaceFavorites {
    private const val maxItems=4
    private val valid get()=WorkspaceModules.all.map { it.route }.toSet()
    fun parse(serialized:String):Set<String> = serialized.split(',')
        .filter { it in valid }.distinct().take(maxItems).toSet()
    fun serialize(items:Set<String>):String = items.filter { it in valid }
        .take(maxItems).joinToString(",")
    fun toggle(items:Set<String>,route:String):Set<String> {
        if(route !in valid) return items
        if(route in items) return items-route
        if(items.size>=maxItems) return items
        return items+route
    }
}
