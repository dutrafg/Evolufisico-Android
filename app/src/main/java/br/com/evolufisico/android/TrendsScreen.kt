package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.trends.MonthlyTrends
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TrendsScreen(clients:List<Client>,manager:SecureBackupManager) {
    var snapshot by remember { mutableStateOf<BackupSnapshot?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var revision by remember { mutableIntStateOf(0) }
    var selectedClient by remember { mutableStateOf<String?>(null) }
    var months by remember { mutableIntStateOf(6) }
    var clientMenu by remember { mutableStateOf(false) }
    var period by remember { mutableStateOf(YearMonth.now()) }
    LaunchedEffect(manager,revision) {
        loading=true
        val result=withContext(Dispatchers.IO) { runCatching{manager.summary()} }
        result.onSuccess{snapshot=it;error=null}
            .onFailure{snapshot=null;error=it.localizedMessage?:"Falha ao ler os dados."}
        loading=false
    }
    val money=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val formatter=DateTimeFormatter.ofPattern("MMM/yyyy",Locale.forLanguageTag("pt-BR"))
    val history=snapshot?.let{
        runCatching{MonthlyTrends.collect(it,period,months,selectedClient)}.getOrDefault(emptyList())
    }.orEmpty()
    val maxValue=history.maxOfOrNull{maxOf(it.billedCents,it.receivedCents)}?.coerceAtLeast(1L)?:1L
    LazyColumn(
        modifier=Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Comparativos mensais",style=MaterialTheme.typography.titleLarge)
            Text("Atendimentos e cobranças por competência. " +
                "Recebidos são registrados no mês do pagamento.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick={revision++},enabled=!loading,
                modifier=Modifier.fillMaxWidth()) {
                Text(if(loading)"Carregando..." else "Atualizar dados")
            }
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            Box {
                OutlinedButton(onClick={clientMenu=true},modifier=Modifier.fillMaxWidth()) {
                    Text("Cliente: "+(clients.firstOrNull{it.id==selectedClient}?.name?:"Todos"))
                }
                DropdownMenu(expanded=clientMenu,onDismissRequest={clientMenu=false}) {
                    DropdownMenuItem(text={Text("Todos os clientes")},onClick={
                        selectedClient=null;clientMenu=false
                    })
                    clients.forEach{c->
                        DropdownMenuItem(text={Text(c.name)},onClick={
                            selectedClient=c.id;clientMenu=false
                        })
                    }
                }
            }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                FilterChip(selected=months==6,onClick={months=6},label={Text("6 meses")})
                FilterChip(selected=months==12,onClick={months=12},label={Text("12 meses")})
            }
            Row(horizontalArrangement=Arrangement.SpaceBetween,modifier=Modifier.fillMaxWidth()) {
                TextButton(onClick={period=period.minusMonths(1)}){Text("← Anterior")}
                Text(period.format(formatter),style=MaterialTheme.typography.titleMedium)
                TextButton(enabled=period.isBefore(YearMonth.now()),
                    onClick={period=period.plusMonths(1)}){Text("Próximo →")}
            }
        }
        if(snapshot!=null && history.isEmpty())item {
            Text("Nenhum dado disponível.")
        }
        items(history.asReversed()) { value ->
            Card(modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(15.dp),
                    verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Text(value.month.format(formatter).replaceFirstChar{it.uppercase()},
                        style=MaterialTheme.typography.titleMedium)
                    Text("Vencimentos: "+money.format(value.billedCents/100.0))
                    LinearProgressIndicator(
                        progress={(value.billedCents.toFloat()/maxValue.toFloat()).coerceIn(0f,1f)},
                        modifier=Modifier.fillMaxWidth())
                    Text("Recebido: "+money.format(value.receivedCents/100.0))
                    LinearProgressIndicator(
                        progress={(value.receivedCents.toFloat()/maxValue.toFloat()).coerceIn(0f,1f)},
                        modifier=Modifier.fillMaxWidth())
                    Text("Ainda pendente neste mês: "+money.format(value.pendingCents/100.0))
                    Text("Atendimentos: "+value.completed+" concluídos · "+
                        value.scheduled+" agendados · "+value.cancelled+" cancelados",
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Text("Valores representam os registros cadastrados. " +
                "Não há conciliação bancária automática.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
