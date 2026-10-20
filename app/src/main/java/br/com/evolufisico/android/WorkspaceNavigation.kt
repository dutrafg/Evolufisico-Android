package br.com.evolufisico.android

/** Navegação previsível para botão de voltar e barra inferior, sem dependência de Android. */
object WorkspaceNavigation {
    private val primary=setOf("HOME","CLIENTS","AGENDA","BILLING","MODULES")
    fun backTarget(route:String):String = if(route in primary || route=="SETTINGS")
        "HOME" else "MODULES"
    fun bottomSelection(route:String):String? = when {
        route=="SETTINGS" -> null
        route in primary -> route
        WorkspaceModules.all.any { it.route==route } -> "MODULES"
        else -> null
    }
}
