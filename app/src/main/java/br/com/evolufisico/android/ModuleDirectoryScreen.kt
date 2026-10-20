package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    fun filter(query:String):List<WorkspaceModule> {
        val key=normalize(query)
        return if(key.isEmpty()) all else all.filter { module ->
            listOf(module.title,module.subtitle,module.category).any { normalize(it).contains(key) }
        }
    }
}

@Composable
fun ModuleDirectoryScreen(onNavigate:(String)->Unit) {
    var query by remember { mutableStateOf("") }
    val modules=WorkspaceModules.filter(query)
    LazyColumn(modifier=Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item {
            Text("Módulos",style=MaterialTheme.typography.headlineSmall)
            Text("Acesse as funções por área, sem sobrecarregar a tela inicial.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(query,{query=it.take(80)},modifier=Modifier.fillMaxWidth(),
                label={Text("Buscar módulo")},singleLine=true)
        }
        WorkspaceModules.categories.forEach { category ->
            val entries=modules.filter { it.category==category }
            if(entries.isNotEmpty()) {
                item { Text(category,style=MaterialTheme.typography.titleMedium,
                    color=MaterialTheme.colorScheme.primary) }
                items(entries.size) { index ->
                    val entry=entries[index]
                    Card(modifier=Modifier.fillMaxWidth(),
                        shape=RoundedCornerShape(12.dp),
                        onClick={onNavigate(entry.route)}) {
                        Column(Modifier.padding(16.dp),
                            verticalArrangement=Arrangement.spacedBy(3.dp)) {
                            Text(entry.title,style=MaterialTheme.typography.titleSmall)
                            Text(entry.subtitle,style=MaterialTheme.typography.bodySmall,
                                color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        if(modules.isEmpty()) item { Text("Nenhum módulo encontrado.") }
    }
}
