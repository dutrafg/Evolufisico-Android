package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import java.text.Normalizer
import java.util.Locale

data class WorkspaceModule(
    val route:String, val title:String, val subtitle:String, val category:String
)

object WorkspaceModules {
    val all=listOf(
        WorkspaceModule("ASSESSMENTS","Avaliações","Medidas e avaliações por cliente","Atendimento"),
        WorkspaceModule("FORMS","Formulários","Anamnese e questionários","Atendimento"),
        WorkspaceModule("EVOLUTION","Evolução","Histórico, gráficos e PDF","Atendimento"),
        WorkspaceModule("GOALS","Metas","Objetivos e acompanhamento","Atendimento"),
        WorkspaceModule("NOTES","Anotações","Observações e registros","Atendimento"),
        WorkspaceModule("SERVICES","Serviços","Catálogo de atendimentos","Gestão"),
        WorkspaceModule("CLIENT_SUMMARY","Resumo por cliente","Visão operacional individual","Gestão"),
        WorkspaceModule("INSIGHTS","Indicadores","Alertas e prazos","Gestão"),
        WorkspaceModule("SEARCH","Busca global","Localizar informações em todos os módulos","Gestão"),
        WorkspaceModule("REPORTS","Relatórios","Exportar planilhas e documentos","Relatórios e análise"),
        WorkspaceModule("TRENDS","Comparativos","Gráficos mensais e resultados","Relatórios e análise")
    )
    val categories=listOf("Atendimento","Gestão","Relatórios e análise")
    private fun normalize(value:String):String = Normalizer.normalize(
        value,Normalizer.Form.NFD).replace(Regex("\\p{M}+"),"")
        .lowercase(Locale.ROOT).trim()
    fun filter(query:String,selectedCategory:String="Todos"):List<WorkspaceModule> {
        val key=normalize(query)
        return all.filter { module ->
            (selectedCategory=="Todos" || module.category==selectedCategory) &&
                (key.isEmpty() || listOf(module.title,module.subtitle,module.category)
                    .any { normalize(it).contains(key) })
        }
    }
}

object WorkspaceGrid {
    fun columns(screenWidthDp:Int):Int = when {
        screenWidthDp<360 -> 1
        screenWidthDp<700 -> 2
        screenWidthDp<1000 -> 3
        else -> 4
    }
}

@Composable
fun ModuleDirectoryScreen(favorites:Set<String>,onFavoritesChange:(Set<String>)->Unit,
    onNavigate:(String)->Unit) {
    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todos") }
    val context=LocalContext.current
    val prefs=remember(context) {
        context.getSharedPreferences("module_shortcuts",android.content.Context.MODE_PRIVATE)
    }
    var recents by remember(prefs) {
        mutableStateOf(WorkspaceRecents.parse(prefs.getString("recent_routes","").orEmpty()))
    }
    fun navigate(route:String) {
        recents=WorkspaceRecents.open(recents,route)
        prefs.edit().putString("recent_routes",WorkspaceRecents.serialize(recents)).apply()
        onNavigate(route)
    }
    fun saveFavorites(updated:Set<String>) { onFavoritesChange(updated) }
    fun toggle(route:String) { saveFavorites(WorkspaceFavorites.toggle(favorites,route)) }
    fun move(route:String,offset:Int) {
        saveFavorites(WorkspaceFavorites.move(favorites,route,offset))
    }
    val columns=WorkspaceLayoutRules.gridColumns(LocalConfiguration.current.screenWidthDp)
    val filtered=WorkspaceModules.filter(search,category)
    val featured=if(search.isBlank()&&category=="Todos") favorites.mapNotNull { id ->
        WorkspaceModules.all.firstOrNull { it.route==id }
    } else emptyList()
    val recent=if(search.isBlank()&&category=="Todos") recents.mapNotNull { id ->
        WorkspaceModules.all.firstOrNull{it.route==id}
    }.filterNot { it in featured } else emptyList()

    LazyColumn(modifier=Modifier.fillMaxSize(),
        contentPadding=PaddingValues(horizontal=16.dp,vertical=16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Encontre suas ferramentas por área.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Marque até 4 favoritos e organize a ordem pelas setas.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(value=search,onValueChange={search=it.take(80)},
                modifier=Modifier.fillMaxWidth(),singleLine=true,
                leadingIcon={Icon(Icons.Outlined.Search,contentDescription=null)},
                placeholder={Text("Buscar módulo")})
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                (listOf("Todos")+WorkspaceModules.categories).forEach { filter ->
                    FilterChip(selected=category==filter,onClick={category=filter},
                        label={Text(filter)})
                }
            }
        }
        if(featured.isNotEmpty()) {
            item { ModuleHeading("★ Favoritos",featured.size) }
            items(featured.chunked(columns).size) { index ->
                ModuleGridRow(featured.chunked(columns)[index],columns,favorites,::navigate,::toggle,
                    moveUp={id->move(id,-1)},moveDown={id->move(id,1)})
            }
        }
        if(recent.isNotEmpty()) {
            item { ModuleHeading("Acessados recentemente",recent.size) }
            items(recent.chunked(columns).size) { index ->
                ModuleGridRow(recent.chunked(columns)[index],columns,favorites,::navigate,::toggle)
            }
        }
        if(filtered.any { it !in featured && it !in recent }) {
            item {
                Text("Outros módulos",style=MaterialTheme.typography.titleMedium,
                    fontWeight=FontWeight.Bold)
            }
        }
        WorkspaceModules.categories.forEach { group ->
            val entries=filtered.filter {
                it.category==group && it !in featured && it !in recent
            }
            if(entries.isNotEmpty()) {
                item { ModuleHeading(group,entries.size) }
                items(entries.chunked(columns).size) { index ->
                    ModuleGridRow(entries.chunked(columns)[index],columns,favorites,::navigate,::toggle)
                }
            }
        }
        if(filtered.isEmpty()) item {
            Text("Nenhum módulo encontrado. Tente outra busca.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ModuleHeading(name:String,count:Int) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        Text(name,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.SemiBold)
        Text(count.toString(),color=MaterialTheme.colorScheme.primary,
            style=MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ModuleGridRow(
    entries:List<WorkspaceModule>,columns:Int,favorites:Set<String>,
    onNavigate:(String)->Unit,onToggle:(String)->Unit,
    moveUp:((String)->Unit)?=null,moveDown:((String)->Unit)?=null
) {
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
        entries.forEach { module ->
            ModuleTile(module,module.route in favorites,Modifier.weight(1f),
                onOpen={onNavigate(module.route)},onFavorite={onToggle(module.route)},
                canFavorite=module.route in favorites || favorites.size<4,
                onUp=moveUp?.let { { it(module.route) } },
                onDown=moveDown?.let { { it(module.route) } })
        }
        repeat(columns-entries.size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun ModuleTile(
    module:WorkspaceModule,favorite:Boolean,modifier:Modifier,
    onOpen:()->Unit,onFavorite:()->Unit,canFavorite:Boolean,
    onUp:(()->Unit)?=null,onDown:(()->Unit)?=null
) {
    val icon=when(module.route) {
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
    Card(modifier=modifier.heightIn(min=150.dp),onClick=onOpen,
        shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement=Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Icon(icon,contentDescription=null,tint=MaterialTheme.colorScheme.primary,
                    modifier=Modifier.size(24.dp))
                Spacer(Modifier.weight(1f))
                IconButton(onClick=onFavorite,enabled=canFavorite,
                    modifier=Modifier.size(40.dp)) {
                    Icon(if(favorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                        contentDescription=if(favorite) "Remover dos favoritos" else "Favoritar",
                        tint=if(favorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(module.title,style=MaterialTheme.typography.titleSmall,
                fontWeight=FontWeight.SemiBold,maxLines=2,overflow=TextOverflow.Ellipsis)
            Text(module.subtitle,style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines=2,overflow=TextOverflow.Ellipsis)
            if(onUp!=null||onDown!=null) {
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
                    IconButton(onClick={onUp?:{}},enabled=onUp!=null,
                        modifier=Modifier.size(30.dp)) {
                        Icon(Icons.Outlined.ArrowUpward,contentDescription="Subir favorito",
                            modifier=Modifier.size(18.dp))
                    }
                    IconButton(onClick={onDown?:{}},enabled=onDown!=null,
                        modifier=Modifier.size(30.dp)) {
                        Icon(Icons.Outlined.ArrowDownward,contentDescription="Descer favorito",
                            modifier=Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
