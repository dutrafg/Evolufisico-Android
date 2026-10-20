package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.agenda.AgendaHome
import br.com.evolufisico.android.agenda.UpcomingAppointment
import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.backup.SecureBackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Apenas informações úteis para o próximo atendimento; módulos ficam fora da Home. */
data class WorkspaceMetrics(
    val clients:Int, val appointmentsToday:Int,
    val overdueAppointments:Int, val pendingCharges:Int,
    val next:List<UpcomingAppointment>
)

data class WorkspaceAction(val label:String,val route:String)

object WorkspaceAttention {
    fun actions(metrics:WorkspaceMetrics):List<WorkspaceAction> = buildList {
        if(metrics.overdueAppointments>0) {
            add(WorkspaceAction("Revisar "+metrics.overdueAppointments+" atendimento(s) pendente(s)",
                "AGENDA"))
        }
        if(metrics.appointmentsToday>0) {
            add(WorkspaceAction("Conferir "+metrics.appointmentsToday+" atendimento(s) de hoje",
                "AGENDA"))
        }
        if(metrics.pendingCharges>0) {
            add(WorkspaceAction("Consultar "+metrics.pendingCharges+" cobrança(s) pendente(s)",
                "BILLING"))
        }
    }
}

object WorkspaceSummary {
    fun calculate(snapshot:BackupSnapshot, now:LocalDateTime):WorkspaceMetrics {
        val names=snapshot.clients.associate { it.id to it.name }
        return WorkspaceMetrics(
            clients=snapshot.clients.size,
            appointmentsToday=snapshot.operations.appointments.count { a ->
                a.status=="Agendado" && a.startIso.substringBefore('T')==now.toLocalDate().toString()
            },
            overdueAppointments=snapshot.operations.appointments.count { a ->
                a.status=="Agendado" && a.startIso.substringBefore('T')<now.toLocalDate().toString()
            },
            pendingCharges=snapshot.charges.count { !it.isReceived },
            next=AgendaHome.next(snapshot.operations.appointments,names,now,3)
        )
    }
}

object WorkspaceDisplay {
 fun initials(name:String):String = name.trim().split(Regex("\\s+"))
     .filter{it.isNotBlank()}.take(2).mapNotNull{it.firstOrNull()?.uppercaseChar()}
     .joinToString("").ifBlank{"?"}
 fun appointmentDate(raw:String,today:LocalDate):String {
  val d=runCatching{LocalDateTime.parse(raw)}.getOrNull()?:return raw
  val day=when(d.toLocalDate()) {
   today -> "Hoje"
   today.plusDays(1) -> "Amanhã"
   else -> d.format(DateTimeFormatter.ofPattern("dd/MM",Locale.forLanguageTag("pt-BR")))
  }
  return day+" · "+d.format(DateTimeFormatter.ofPattern("HH:mm"))
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
                    Text("Área de trabalho",style=MaterialTheme.typography.headlineSmall,
                        fontWeight=FontWeight.Bold)
                    Text("Aqui está o resumo do seu dia.",
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
                WorkspaceMetric("Cobranças pendentes",(metrics?.pendingCharges?:0).toString(),
                    Modifier.weight(1f),onClick={onNavigate("BILLING")})
            }
        }
        if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        metrics?.let { value ->
            val attention=WorkspaceAttention.actions(value)
            if(attention.isNotEmpty()) item {
                Card(shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Acompanhar hoje",style=MaterialTheme.typography.titleMedium)
                        attention.forEach { action ->
                            TextButton(onClick={onNavigate(action.route)}) {
                                Text(action.label)
                            }
                        }
                    }
                }
            }
        }
        error?.let { issue -> item {
            Text(issue,color=MaterialTheme.colorScheme.error)
        } }
        item {
            Card(shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                        Text("Próximos atendimentos",style=MaterialTheme.typography.titleMedium,
                            fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f))
                        TextButton(onClick={onNavigate("AGENDA")}){Text("Ver todos")}
                    }
                    val next=metrics?.next.orEmpty()
                    if(next.isEmpty()) {
                        Text(if(loading) "Consultando agenda…" else "Nenhum atendimento futuro agendado.",
                            color=MaterialTheme.colorScheme.onSurfaceVariant)
                    } else next.forEachIndexed { i,a ->
                        if(i>0) HorizontalDivider()
                        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
                            horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.size(38.dp).background(Color(0xFFE9F2FD),CircleShape),
                                contentAlignment=Alignment.Center) {
                                Text(WorkspaceDisplay.initials(a.clientName),
                                    color=Color(0xFF17589A),fontWeight=FontWeight.Bold)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(a.clientName,style=MaterialTheme.typography.titleSmall)
                                Text(a.service,style=MaterialTheme.typography.bodySmall,
                                    color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(WorkspaceDisplay.appointmentDate(a.startIso,LocalDate.now()),
                                style=MaterialTheme.typography.labelSmall,
                                color=MaterialTheme.colorScheme.primary)
                        }
                    }

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

    }
}

@Composable
private fun WorkspaceMetric(
    label:String, value:String, modifier:Modifier=Modifier,
    onClick:()->Unit
) {
    val palette=when(label) {
        "Clientes" -> Color(0xFFE9F2FF) to Color(0xFF174E91)
        "Hoje" -> Color(0xFFE5F5ED) to Color(0xFF156B46)
        "Agenda pendente" -> Color(0xFFFFF2DC) to Color(0xFF94600A)
        else -> Color(0xFFFCE9F0) to Color(0xFF9C2D4A)
    }
    Card(onClick=onClick,modifier=modifier.heightIn(min=112.dp),
        shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=palette.first)) {
        Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
            Text(label,style=MaterialTheme.typography.labelMedium,color=palette.second)
            Spacer(Modifier.height(3.dp))
            Text(value,style=MaterialTheme.typography.headlineMedium,
                fontWeight=FontWeight.Bold,color=palette.second)
        }
    }
}
