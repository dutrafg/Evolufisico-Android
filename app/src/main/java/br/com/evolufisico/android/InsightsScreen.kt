package br.com.evolufisico.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.insights.InsightKind
import br.com.evolufisico.android.insights.InsightSummary
import br.com.evolufisico.android.insights.Insights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InsightsScreen(
    manager:SecureBackupManager,
    onAgenda:()->Unit,onBilling:()->Unit,onGoals:()->Unit
) {
    var summary by remember { mutableStateOf<InsightSummary?>(null) }
    var names by remember { mutableStateOf<Map<String,String>>(emptyMap()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<InsightKind?>(null) }
    LaunchedEffect(manager,revision) {
        loading=true
        val fetched=withContext(Dispatchers.IO) {
            runCatching { manager.summary() }
        }
        fetched.onSuccess {
            summary=Insights.calculate(it)
            names=it.clients.associate{client->client.id to client.name}
            error=null
        }.onFailure {
            summary=null;error="Falha ao ler indicadores: "+(it.localizedMessage?:"Erro.")
        }
        loading=false
    }
    val money=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val visible=summary?.alerts?.filter{selected==null||it.kind==selected}.orEmpty()
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Pendências e indicadores",style=MaterialTheme.typography.titleLarge)
            Text("Resumo de agenda, prazos e cobranças sem alterar registros.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick={revision++},enabled=!loading,
                modifier=Modifier.fillMaxWidth()) {
                Text(if(loading)"Atualizando..." else "Atualizar indicadores")
            }
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }
        summary?.let { data ->
            item {
                Card(modifier=Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp),
                        verticalArrangement=Arrangement.spacedBy(7.dp)) {
                        Text("Resumo atual",style=MaterialTheme.typography.titleMedium)
                        Text("Cobranças atrasadas: "+money.format(data.overdueCents/100.0))
                        Text("Vencimentos nos próximos 7 dias: "+money.format(data.dueSoonCents/100.0))
                        Text("Atendimentos nos próximos 7 dias: "+data.upcomingAppointments)
                        Text("Metas com prazo vencido: "+data.lateGoals)
                    }
                }
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                    listOf<InsightKind?>(null,InsightKind.BILLING,
                        InsightKind.APPOINTMENT,InsightKind.GOAL).forEach { kind ->
                        FilterChip(
                            selected=kind==selected,onClick={selected=kind},
                            label={Text(kind?.label?:"Todos")}
                        )
                    }
                }
            }
            if(visible.isEmpty()) item {
                Text("Nenhuma pendência nesta seleção.",
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(visible) { notice ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp),
                        verticalArrangement=Arrangement.spacedBy(5.dp)) {
                        Text(notice.title,style=MaterialTheme.typography.titleSmall)
                        Text(names[notice.clientId]?:"Cliente não encontrado")
                        Text(notice.dateIso.replace('T',' '),
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                        if(notice.amountCents>0L)
                            Text(money.format(notice.amountCents/100.0))
                        TextButton(onClick={
                            when(notice.kind) {
                                InsightKind.BILLING -> onBilling()
                                InsightKind.APPOINTMENT -> onAgenda()
                                InsightKind.GOAL -> onGoals()
                            }
                        }) { Text("Abrir "+notice.kind.label) }
                    }
                }
            }
        }
        item {
            Text("Os indicadores são informativos e dependem dos registros locais.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
