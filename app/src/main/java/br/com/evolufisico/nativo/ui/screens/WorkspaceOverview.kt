package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class OverviewMetric(val title:String,val value:String,val detail:String,val icon:ImageVector,val destination:Int)

@Composable fun WorkspaceOverview(repo:AppRepository,onModule:(Int)->Unit,onClient:(String)->Unit={onModule(8)}) {
    val date=LocalDate.now();val today=date.toString()
    Text(date.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM",Locale.forLanguageTag("pt-BR"))),
        color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.labelLarge)
    val pending=repo.charges.filter{!it.paid && !it.exempt}
    val metrics=listOf(
        OverviewMetric("Clientes",WorkspaceRules.latest(repo.evaluations.toList()).size.toString(),"Perfis cadastrados",Icons.Filled.Groups,8),
        OverviewMetric("Atendimentos hoje",repo.appointments.count{it.date==today && it.status !in listOf("Cancelado","Faltou")}.toString(),"Inclui atendimentos concluídos",Icons.Filled.CalendarMonth,9),
        OverviewMetric("Tarefas pendentes",repo.tasks.count{!it.done}.toString(),"Ações ainda não concluídas",Icons.Filled.TaskAlt,10),
        OverviewMetric("Avaliações",repo.evaluations.size.toString(),"Histórico de registros",Icons.Filled.Assessment,1),
        OverviewMetric("A receber",money(pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}),"Líquido de descontos e cortesias",Icons.Filled.Payments,6),
        OverviewMetric("Treinos hoje",repo.trainingSessions.count{!it.isTemplate && it.date==today}.toString(),"Sessões; modelos não entram na contagem",Icons.Filled.FitnessCenter,12)
    )
    CardGrid(metrics){metric->DashboardMetricCard(metric.title,metric.value,metric.detail,metric.icon,{onModule(metric.destination)})}
    Text("Acesso rápido",style=MaterialTheme.typography.titleMedium)
    val shortcuts=listOf(8 to "Clientes",11 to "Metas",12 to "Treinos",13 to "Hábitos",15 to "Relatórios")
    CardGrid(shortcuts,270.0,3){(index,title)->CompactModuleCard(title,"Abrir módulo",when(index){8->Icons.Filled.Groups;11->Icons.Filled.Flag;12->Icons.Filled.FitnessCenter;13->Icons.Filled.WaterDrop;else->Icons.Filled.Assessment},{onModule(index)})}
    var period by rememberSaveable{mutableStateOf("30 dias")}
    Heading("Resumo do período","Contagens de registros locais no intervalo selecionado, incluindo hoje. Datas futuras e modelos de treino são excluídos.")
    PillChoices(listOf("7 dias","30 dias","90 dias"),period,{period=it})
    val summary=DashboardInsights.period(repo.evaluations.toList(),repo.trainingSessions.toList(),repo.habits.toList(),date,period.substringBefore(' ').toInt())
    DataCard("Atividade registrada · $period","${summary.evaluations} avaliações · ${summary.completedTraining} treinos concluídos\n${summary.achievedHabits} de ${summary.habitEntries} registros de hábitos com meta atingida") {
        TextButton(onClick={onModule(15)}){Text("Abrir relatórios")}
    }
    val overdue=repo.tasks.count{!it.done && it.dueDate<today}
    val chargesOverdue=pending.count{it.dueDate<today}
    val returns=DashboardInsights.dueReassessments(repo.evaluations.toList(),date)
    var expanded by rememberSaveable{mutableStateOf(true)}
    ElevatedCard(colors=CardDefaults.elevatedCardColors(containerColor=MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text("Acompanhamento pendente",style=MaterialTheme.typography.titleMedium)
            Text("$overdue tarefas vencidas · $chargesOverdue cobranças vencidas · ${returns.size} reavaliações previstas até hoje")
            TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Recolher pendências" else "Mostrar pendências")}
            if(expanded) {
                if(overdue+chargesOverdue+returns.size==0)Text("Nenhuma pendência nesses critérios.")
                if(overdue>0)CompactModuleCard("Tarefas vencidas","$overdue pendentes",Icons.Filled.TaskAlt,{onModule(10)})
                if(chargesOverdue>0)CompactModuleCard("Cobranças vencidas","$chargesOverdue pendentes",Icons.Filled.Payments,{onModule(6)})
                returns.take(5).forEach{client->CompactModuleCard(client.name,"Reavaliar · ${client.nextAssessment}",Icons.Filled.Person,{onClient(client.studentId)})}
                if(returns.size>5)Text("Mais ${returns.size-5} perfis com reavaliação prevista. Consulte Clientes.")
            }
        }
    }
}
