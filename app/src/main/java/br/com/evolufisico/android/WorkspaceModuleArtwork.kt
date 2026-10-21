package br.com.evolufisico.android

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/** Mapa único dos ícones dos módulos nas duas áreas de navegação. */
object WorkspaceModuleArtwork {
    fun icon(route:String):ImageVector=when(route) {
        "ASSESSMENTS" -> Icons.Outlined.Assignment
        "FORMS" -> Icons.Outlined.Description
        "EVOLUTION","TRENDS" -> Icons.Outlined.Insights
        "GOALS" -> Icons.Outlined.Flag
        "NOTES" -> Icons.Outlined.NoteAlt
        "SERVICES" -> Icons.Outlined.FitnessCenter
        "INSIGHTS" -> Icons.Outlined.QueryStats
        "SEARCH" -> Icons.Outlined.Search
        "REPORTS" -> Icons.Outlined.Summarize
        else -> Icons.Outlined.Groups
    }
}
