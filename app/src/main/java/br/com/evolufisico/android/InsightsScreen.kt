package br.com.evolufisico.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.insights.InsightKind
import br.com.evolufisico.android.insights.InsightSummary
import br.com.evolufisico.android.insights.InsightsOrder
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
    val context=LocalContext.current
    val preferences=remember(context) {
        context.getSharedPreferences("insights_order",android.content.Context.MODE_PRIVATE)
    }
    var order by remember(preferences) {
        mutableStateOf(InsightsOrder.parse(preferences.getString("kind_order",null)))
    }
    var organizing by remember { mutableStateOf(false) }
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
    val visible=InsightsOrder.sort(
        summary?.alerts?.filter { selected==null || it.kind==selected }.orEmpty(),order)
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    LazyColumn(Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp),
        contentPadding=PaddingValues(horizontal=VisualLayout.edgePaddingDp(
            LocalConfiguration.current.screenWidthDp).dp,vertical=20.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Pendências e indicadores",style=MaterialTheme.typography.titleLarge)
            Text("Resumo de agenda, prazos e cobranças sem alterar registros.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick={revision++},enabled=!loading,
                modifier=Modifier.fillMaxWidth()) {
                Text(if(loading)"Atualizando..." else "Atualizar indicadores")
            }
            OutlinedButton(onClick={organizing=!organizing},
                modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
                Text(if(organizing)"Concluir organização" else "Organizar análises")
            }
            if(organizing) {
                Text("Prioridade dos alertas — a ordem fica salva neste aparelho.",
                    style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
                order.forEachIndexed { index,kind ->
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
                        horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text(kind.label,modifier=Modifier.weight(1f),
                            style=MaterialTheme.typography.titleSmall)
                        TextButton(enabled=index>0,onClick={
                            val updated=InsightsOrder.move(order,kind,-1)
                            order=updated
                            preferences.edit()
                                .putString("kind_order",InsightsOrder.serialize(updated)).apply()
                        }) { Text("Subir") }
                        TextButton(enabled=index<order.lastIndex,onClick={
                            val updated=InsightsOrder.move(order,kind,1)
                            order=updated
                            preferences.edit()
                                .putString("kind_order",InsightsOrder.serialize(updated)).apply()
                        }) { Text("Descer") }
                    }
                }
                TextButton(onClick={
                    order=InsightsOrder.standard
                    preferences.edit().remove("kind_order").apply()
                }) { Text("Restaurar ordem padrão") }
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
}
