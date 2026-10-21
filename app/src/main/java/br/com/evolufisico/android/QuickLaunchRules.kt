package br.com.evolufisico.android

/** An action request is transient. No personal data or editor contents are persisted. */
data class QuickLaunch(val route:String,val id:Int)
object QuickLaunchRules {
    private val createActions=mapOf(
        "ASSESSMENTS" to "Nova avaliação",
        "FORMS" to "Novo formulário"
    )
    fun supportsCreate(route:String):Boolean=route in createActions
    fun label(route:String):String=createActions[route]?:"Abrir função"
}
