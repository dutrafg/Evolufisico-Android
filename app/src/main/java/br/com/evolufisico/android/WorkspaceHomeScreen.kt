package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.agenda.AgendaHome
import br.com.evolufisico.android.agenda.UpcomingAppointment
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

/** Apenas informações úteis para o próximo atendimento; módulos ficam fora da Home. */
data class WorkspaceMetrics(
    val clients:Int, val appointmentsToday:Int,
    val overdueAppointments:Int, val pendingCharges:Int,
    val next:List<UpcomingAppointment>
)

object WorkspaceSummary {
    fun calculate(snapshot:BackupSnapshot, now:LocalDateTime):WorkspaceMetrics {
        val names=snapshot.clients.associate { it.id to it.name }
        return WorkspaceMetrics(
            clients=snapshot.clients.size,
            appointmentsToday=snapshot.operations.appointments.count { a ->
                a.status=="Agendado" && a.startIso.substringBefore('T')==now.toLocalDate().toString()
            },
            overdueAppointments=AgendaHome.pendingCount(snapshot.operations.appointments,now),
            pendingCharges=snapshot.charges.count { !it.isReceived },
            next=AgendaHome.next(snapshot.operations.appointments,names,now,3)
        )
    }
}

@Composable
fun WorkspaceHomeScreen(
    clientCount:Int, backupManager:SecureBackupManager,
    onNavigate:(String)->Unit
) {
    var metrics by remember { mutableStateOf<WorkspaceMetrics?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(backupManager,refresh) {
        loading=true
        val data=withContext(Dispatchers.IO) {
            runCatching { WorkspaceSummary.calculate(backupManager.summary(),LocalDateTime.now()) }
        }
        data.onSuccess { metrics=it;error=null }.onFailure {
            error="Não foi possível carregar o resumo. Verifique os dados locais."
        }
        loading=false
    }
    LazyColumn(
        contentPadding=PaddingValues(horizontal=16.dp,vertical=18.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Column {
                    Text("Área de trabalho",style=MaterialTheme.typography.headlineSmall)
                    Text("Visão geral · "+LocalDate.now().toString(),
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick={refresh++},enabled=!loading) {
                    Text("Atualizar")
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                WorkspaceMetric("Clientes",(metrics?.clients?:clientCount).toString(),
                    Modifier.weight(1f),onClick={onNavigate("CLIENTS")})
                WorkspaceMetric("Hoje",(metrics?.appointmentsToday?:0).toString(),
                    Modifier.weight(1f),onClick={onNavigate("AGENDA")})
            }
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                WorkspaceMetric("Agenda pendente",(metrics?.overdueAppointments?:0).toString(),
                    Modifier.weight(1f),onClick={onNavigate("AGENDA")})
                WorkspaceMetric("A receber",(metrics?.pendingCharges?:0).toString(),
                    Modifier.weight(1f),onClick={onNavigate("BILLING")})
            }
        }
        if (loading) item { Text("Atualizando indicadores…") }
        error?.let { issue -> item {
            Text(issue,color=MaterialTheme.colorScheme.error)
        } }
        item {
            Card(shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("Próximos atendimentos",style=MaterialTheme.typography.titleMedium)
                    val next=metrics?.next.orEmpty()
                    if(next.isEmpty()) {
                        Text(if(loading) "Consultando agenda…" else "Nenhum atendimento futuro agendado.",
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    } else next.forEach { a ->
                        Text(a.startIso.replace('T',' ')+" · "+a.clientName)
                        Text(a.service,style=MaterialTheme.typography.bodySmall,
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick={onNavigate("AGENDA")}) { Text("Ver agenda completa") }
                }
            }
        }
        item {
            Text("Acesso rápido",style=MaterialTheme.typography.titleMedium)
        }
        item {
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                Button(modifier=Modifier.weight(1f),onClick={onNavigate("CLIENTS")}) {
                    Text("Clientes")
                }
                Button(modifier=Modifier.weight(1f),onClick={onNavigate("AGENDA")}) {
                    Text("Agenda")
                }
            }
        }
        item {
            Text("Avaliações, formulários, relatórios e demais funções permanecem nos "+
                "respectivos módulos.",style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WorkspaceMetric(
    label:String, value:String, modifier:Modifier=Modifier,
    onClick:()->Unit
) {
    Card(onClick=onClick,modifier=modifier,shape=RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(14.dp)) {
            Text(label,style=MaterialTheme.typography.labelMedium,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value,style=MaterialTheme.typography.headlineMedium,
                color=MaterialTheme.colorScheme.primary)
        }
    }
}
