package br.com.evolufisico.android
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

object WorkspaceHomeModules {
    fun selected(favorites:Set<String>):List<WorkspaceModule> =
        favorites.mapNotNull { route -> WorkspaceModules.all.firstOrNull { it.route==route } }
}
@Composable
fun WorkspaceFavoriteTile(entry:WorkspaceModule,modifier:Modifier=Modifier,
    onOpen:()->Unit,onRemove:()->Unit) {
    val accent=VisualTokens.metric(
        VisualTokens.metricIndexForRoute(entry.route),
        VisualTokens.dark(MaterialTheme.colorScheme))
    Card(modifier=modifier.heightIn(min=196.dp),onClick=onOpen,
        shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
        elevation=CardDefaults.cardElevation(defaultElevation=1.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).background(accent.background,RoundedCornerShape(12.dp)),
                    contentAlignment=Alignment.Center) {
                    Icon(WorkspaceModuleArtwork.icon(entry.route),contentDescription=null,
                        tint=accent.ink,modifier=Modifier.size(24.dp))
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick=onRemove,modifier=Modifier.size(48.dp)) {
                    Icon(Icons.Outlined.Star,
                        contentDescription="Remover "+entry.title+" dos favoritos",
                        tint=MaterialTheme.colorScheme.primary)
                }
            }
            Text(entry.title,style=MaterialTheme.typography.titleSmall,
                fontWeight=FontWeight.SemiBold,maxLines=2,
                overflow=TextOverflow.Ellipsis)
            Text(entry.subtitle,style=MaterialTheme.typography.bodySmall,
                maxLines=2,overflow=TextOverflow.Ellipsis,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.End) {
                Text(QuickLaunchRules.label(entry.route),
                    style=MaterialTheme.typography.labelMedium,
                    maxLines=2,overflow=TextOverflow.Ellipsis,
                    color=MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.ChevronRight,contentDescription=null,
                    modifier=Modifier.size(18.dp),tint=MaterialTheme.colorScheme.primary)
            }
        }
    }
}
