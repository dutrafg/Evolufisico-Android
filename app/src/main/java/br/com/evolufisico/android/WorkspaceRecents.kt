package br.com.evolufisico.android

/** Registra somente o nome público da tela, sem dados de clientes. */
object WorkspaceRecents {
    private const val maxEntries=3
    private val valid get() = WorkspaceModules.all.map { it.route }.toSet()

    fun parse(raw:String):List<String> = raw.split(",").filter { it in valid }
        .distinct().take(maxEntries)
    fun serialize(routes:List<String>):String = routes.filter { it in valid }
        .distinct().take(maxEntries).joinToString(",")
    fun open(routes:List<String>,route:String):List<String> {
        if(route !in valid) return routes
        return (listOf(route)+routes.filterNot { it==route }).take(maxEntries)
    }
}
