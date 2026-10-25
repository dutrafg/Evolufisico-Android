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

@Composable fun WorkspaceOverview(repo:AppRepository,onModule:(Int)->Unit,onClient:(String)->Unit={onModule(1)}) {
    val date=LocalDate.now();val today=date.toString()
    Text(date.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM",Locale.forLanguageTag("pt-BR"))),
        color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.labelLarge)
    val pending=repo.charges.filter{!it.paid && !it.exempt}
    val metrics=listOf(
        OverviewMetric("Alunos cadastrados",WorkspaceRules.latest(repo.evaluations.toList()).size.toString(),"Avaliações por aluno",Icons.Filled.Groups,1),
        OverviewMetric("Avaliações",repo.evaluations.size.toString(),"Histórico de registros",Icons.Filled.Assessment,1),
        OverviewMetric("A receber",money(pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}),"Líquido de descontos e cortesias",Icons.Filled.Payments,6),
        OverviewMetric("Análises disponíveis",repo.evaluations.groupBy{it.studentId}.values.count{HtmlParity.eligible(it)}.toString(),"Três datas e pelo menos 21 dias",Icons.Filled.Psychology,5)
    )
    OrganizationCards(metrics,{it.title},"home-stats"){metric->DashboardMetricCard(metric.title,metric.value,metric.detail,metric.icon,{onModule(metric.destination)})}
}
