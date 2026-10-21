package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.search.GlobalSearch
import br.com.evolufisico.android.search.SearchKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GlobalSearchScreen(manager:SecureBackupManager, onOpen:(String)->Unit) {
    var snapshot by remember(manager){mutableStateOf<BackupSnapshot?>(null)}
    var error by remember(manager){mutableStateOf<String?>(null)}
    var loading by remember(manager){mutableStateOf(true)}
    var query by remember {mutableStateOf("")}
    var kind by remember {mutableStateOf<SearchKind?>(null)}
    LaunchedEffect(manager) {
        loading=true
        withContext(Dispatchers.IO){runCatching{manager.summary()}}.onSuccess {
            snapshot=it;error=null
        }.onFailure {error="Falha ao pesquisar dados locais: "+(it.message?:"Erro")}
        loading=false
    }
    val results=remember(snapshot,query,kind) {
        snapshot?.let { GlobalSearch.search(it,query,kind) } ?: emptyList()
    }
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    LazyColumn(Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp),
        contentPadding=PaddingValues(horizontal=VisualLayout.edgePaddingDp(
            LocalConfiguration.current.screenWidthDp).dp,vertical=20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Busca global",style=MaterialTheme.typography.headlineSmall)
            Text("Pesquise nomes, serviços, metas, datas e lançamentos locais.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(value=query,onValueChange={query=it.take(120)},
                modifier=Modifier.fillMaxWidth(),singleLine=true,
                label={Text("Buscar no aplicativo")})
            Row(Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                FilterChip(selected=kind==null,onClick={kind=null},label={Text("Todos")})
                SearchKind.entries.forEach { option ->
                    FilterChip(selected=kind==option,onClick={kind=option},
                        label={Text(option.label)})
                }
            }
            if(loading) Text("Carregando registros...")
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            if(query.isNotBlank() && !loading && error==null) {
                Text("${results.size} resultado(s)"+if(results.size==100)" (limite de 100)" else "",
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if(results.isEmpty() && query.isNotBlank() && !loading && error==null) {
            item { Text("Nenhum registro encontrado. Experimente outra palavra ou filtro.") }
        }
        items(results,key={it.kind.name+":"+it.id}) { result ->
            Card(onClick={onOpen(result.kind.section)},modifier=Modifier.fillMaxWidth(),
                shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp)) {
                Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text(result.kind.label,color=MaterialTheme.colorScheme.primary,
                        style=MaterialTheme.typography.labelMedium)
                    Text(result.title,style=MaterialTheme.typography.titleMedium)
                    Text(result.detail,color=MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Abrir módulo →",style=MaterialTheme.typography.labelMedium,
                        color=MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    }
}
