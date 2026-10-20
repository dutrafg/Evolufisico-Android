package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.operations.ClientOperations
import br.com.evolufisico.android.operations.ClientOperationsCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

/** Resumo operacional somente leitura, sem registros ou preferências duplicados. */
@Composable
fun ClientOperationsScreen(
    backupManager:SecureBackupManager,
    onClients:()->Unit,
    onAgenda:()->Unit,
    onBilling:()->Unit,
    onGoals:()->Unit
) {
    var query by remember {mutableStateOf("")}
    var filter by remember {mutableStateOf("Todos")}
    var rows by remember {mutableStateOf<List<ClientOperations>>(emptyList())}
    var busy by remember {mutableStateOf(false)}
    var message by remember {mutableStateOf<String?>(null)}
    var revision by remember {mutableIntStateOf(0)}
    LaunchedEffect(backupManager,revision) {
        busy=true
        val result=withContext(Dispatchers.IO) {
            runCatching { ClientOperationsCalculator.calculate(backupManager.summary()) }
        }
        result.onSuccess { rows=it;message=null }
            .onFailure { message="Não foi possível ler os registros: "+
                (it.localizedMessage?:"Erro.") }
        busy=false
    }
    val money=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val filtered=rows.filter {
        (query.isBlank()||it.name.contains(query,true))&&when(filter) {
            "Com pendências"->it.pendingCents>0L
            "Em atraso"->it.overdueCents>0L
            "Com faltas"->it.missed>0
            "Com metas"->it.activeGoals>0
            else->true
        }
    }
    LazyColumn(
        modifier=Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Resumo por cliente",style=MaterialTheme.typography.titleLarge)
            Text("Compare atendimentos, metas e cobranças sem alterar dados.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(
                modifier=Modifier.fillMaxWidth(),enabled=!busy,
                onClick={revision++}
            ) {Text(if(busy)"Carregando..." else "Atualizar dados")}
            OutlinedTextField(value=query,onValueChange={query=it.take(80)},
                modifier=Modifier.fillMaxWidth(),singleLine=true,
                label={Text("Buscar cliente")})
            val choices=listOf("Todos","Com pendências","Em atraso","Com faltas","Com metas")
            Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
                choices.chunked(3).forEach { group ->
                    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                        group.forEach {item ->
                            FilterChip(
                                selected=filter==item,onClick={filter=item},
                                label={Text(item)})
                        }
                    }
                }
            }
            Text("Clientes nesta seleção: "+filtered.size,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            message?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }
        if(!busy&&filtered.isEmpty())item{Text("Nenhum cliente encontrado.")}
        items(filtered,key={it.clientId}) { client ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(15.dp),
                    verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Text(client.name,style=MaterialTheme.typography.titleMedium)
                    Text("Atendimentos: "+client.completed+" concluídos · "+
                        client.scheduled+" agendados · "+client.missed+" faltas · "+
                        client.cancelled+" cancelados")
                    client.attendancePercent?.let {
                        Text("Comparecimento: "+it+"%")
                        LinearProgressIndicator(progress={it/100f},modifier=Modifier.fillMaxWidth())
                    }
                    Text("Metas: "+client.activeGoals+" ativas · "+
                        client.completedGoals+" concluídas")
                    Text("A receber: "+money.format(client.pendingCents/100.0))
                    if(client.overdueCents>0)
                        Text("Em atraso: "+money.format(client.overdueCents/100.0),
                            color=MaterialTheme.colorScheme.error)
                    Text("Recebido: "+money.format(client.receivedCents/100.0))
                    Row(horizontalArrangement=Arrangement.spacedBy(3.dp)) {
                        TextButton(onClick=onAgenda){Text("Agenda")}
                        TextButton(onClick=onGoals){Text("Metas")}
                        TextButton(onClick=onBilling){Text("Cobranças")}
                    }
                }
            }
        }
        item {
            TextButton(onClick=onClients){Text("Abrir lista de clientes")}
            Text("Dados locais de teste, sem sincronização de rede.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
