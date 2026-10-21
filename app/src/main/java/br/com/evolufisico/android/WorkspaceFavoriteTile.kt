package br.com.evolufisico.android
import androidx.compose.foundation.layout.*
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
    Card(modifier=modifier.heightIn(min=132.dp),onClick=onOpen,
        shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Text(entry.title,modifier=Modifier.weight(1f),
                    style=MaterialTheme.typography.titleSmall,
                    fontWeight=FontWeight.SemiBold,maxLines=2,
                    overflow=TextOverflow.Ellipsis)
                IconButton(onClick=onRemove) {
                    Icon(Icons.Outlined.Star,
                        contentDescription="Remover "+entry.title+" do início",
                        tint=MaterialTheme.colorScheme.primary)
                }
            }
            Text(entry.subtitle,style=MaterialTheme.typography.bodySmall,
                maxLines=2,overflow=TextOverflow.Ellipsis,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
                Text("Abrir",style=MaterialTheme.typography.labelMedium,
                    color=MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.ChevronRight,contentDescription=null,
                    modifier=Modifier.size(18.dp),tint=MaterialTheme.colorScheme.primary)
            }
        }
    }
}
