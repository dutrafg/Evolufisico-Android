package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.domain.LayoutPolicy

@Composable fun <T> CardGrid(items:List<T>,minimumWidth:Double=250.0,maxColumns:Int=4,content:@Composable (T)->Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns=LayoutPolicy.cardColumns(maxWidth.value.toDouble(),LocalDensity.current.fontScale.toDouble(),minimumWidth,maxColumns)
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
            items.chunked(columns).forEach{row->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    row.forEach{item->Box(Modifier.weight(1f)){content(item)}}
                    repeat(columns-row.size){Spacer(Modifier.weight(1f))}
                }
            }
        }
    }
}

@Composable fun DashboardMetricCard(title:String,value:String,detail:String,icon:ImageVector,onClick:()->Unit) {
    ElevatedCard(onClick=onClick,modifier=Modifier.fillMaxWidth().heightIn(min=174.dp),shape=RoundedCornerShape(14.dp),
        elevation=CardDefaults.elevatedCardElevation(defaultElevation=2.dp),
        colors=CardDefaults.elevatedCardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Surface(shape=RoundedCornerShape(12.dp),color=MaterialTheme.colorScheme.primaryContainer) {
                    Icon(icon,null,Modifier.padding(12.dp).size(28.dp),tint=MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Text(value,Modifier.weight(1f).padding(start=12.dp),style=MaterialTheme.typography.headlineSmall,
                    textAlign=androidx.compose.ui.text.style.TextAlign.End)
            }
            Text(title,style=MaterialTheme.typography.titleMedium)
            Text(detail,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable fun CompactModuleCard(title:String,subtitle:String,icon:ImageVector,onClick:()->Unit,
    trailing:@Composable ()->Unit={}) {
    ElevatedCard(onClick=onClick,modifier=Modifier.fillMaxWidth().heightIn(min=88.dp),shape=RoundedCornerShape(12.dp),
        elevation=CardDefaults.elevatedCardElevation(defaultElevation=1.dp),
        colors=CardDefaults.elevatedCardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
            Surface(shape=RoundedCornerShape(10.dp),color=MaterialTheme.colorScheme.secondaryContainer) {
                Icon(icon,null,Modifier.padding(12.dp).size(24.dp),tint=MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(Modifier.weight(1f).padding(horizontal=12.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Text(title,style=MaterialTheme.typography.titleSmall)
                Text(subtitle,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            trailing()
        }
    }
}
