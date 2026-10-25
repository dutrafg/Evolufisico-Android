package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.domain.HtmlHome
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(repository:AppRepository,onOrganization:(String,String)->Unit,onModule:(Int,String)->Unit,onSearch:()->Unit)=Screen(bottomInset=24.dp) {
    val context=LocalContext.current
    val today=LocalDate.now()
    val summary=HtmlHome.summarize(repository.appointments.toList(),repository.tasks.toList(),repository.notes.toList(),repository.organizationGoals.toList(),today)
    val professional=context.getSharedPreferences("professional",android.content.Context.MODE_PRIVATE)
    val name=professional.getString("nickname","").orEmpty().ifBlank{professional.getString("name","").orEmpty()}.trim().take(100)
    val greeting=when(LocalTime.now().hour){in 0..11->"Bom dia";in 12..17->"Boa tarde";else->"Boa noite"}
    var editing by rememberSaveable{mutableStateOf(false)}
    var resetEpoch by rememberSaveable{mutableIntStateOf(0)}
    Surface(color=MaterialTheme.colorScheme.primaryContainer,shape=MaterialTheme.shapes.large) {
        Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(today.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM",Locale.forLanguageTag("pt-BR"))),style=MaterialTheme.typography.labelLarge)
            Text(greeting+(if(name.isBlank())"" else ", $name")+"! Veja o que precisa da sua atenção.",style=MaterialTheme.typography.headlineSmall)
            Text("Seu panorama profissional em um só lugar: insights, avisos e próximos passos.")
            ActionButtons {OutlinedButton(onClick=onSearch){Icon(Icons.Filled.Search,null);Text("Pesquisar")};Button(onClick={onModule(9,"")}){Icon(Icons.Filled.Person,null);Text("Perfil Unificado")};OutlinedButton(onClick={onOrganization("agenda","")}){Text("Abrir Organização")}}
        }
    }
    ActionButtons {
        OutlinedButton(onClick={editing=!editing}){Text(if(editing)"Concluir edição" else "Editar layout")}
        if(editing)TextButton(onClick={listOf("homeStats","homeCards").forEach{group->context.getSharedPreferences("html-home-layout-$group",android.content.Context.MODE_PRIVATE).edit().clear().apply()};resetEpoch++}){Text("Restaurar layout padrão")}
    }
    if(editing)Text("Arraste pela alça Arrastar e solte sobre outro cartão para mudar a posição, inclusive entre colunas. A tela rola ao aproximar o dedo das bordas. Indicadores e painéis são organizados em seus respectivos grupos.",style=MaterialTheme.typography.bodySmall)
    val metrics=listOf(Triple("Eventos de hoje",summary.todayEvents,"agenda"),Triple("Prazos vencidos",summary.overdue,"tasks"),Triple("Tarefas pendentes",summary.pendingTasks,"tasks"),Triple("Metas ativas",summary.activeGoals,"goals"))
    ReferenceDashboardGrid("homeStats",metrics.indices.map{"kpi-$it"},editing,resetEpoch){key->val (label,count,section)=metrics[key.removePrefix("kpi-").toInt()];HomeKpi(label,count,section,{onOrganization(section,"")})}
    ReferenceDashboardGrid("homeCards",listOf("avisos","acontecimentos","insights","semana","acoes","avaliacoes","financeiro","evolucao"),editing,resetEpoch){id->when(id){
        "avisos"->HomeSection("PRIORIDADES","Avisos que exigem atenção") {summary.priorities.forEach{HomeInsight(it,onOrganization)}}
        "acontecimentos"->HomeSection("AGENDA UNIFICADA","Próximos acontecimentos") {
            if(summary.upcoming.isEmpty())Text("Nenhum compromisso ou lembrete futuro cadastrado. A agenda permanece disponível em Organização.")
            summary.upcoming.forEach{e->DataCard(e.title,"${e.date} ${e.time}\n${e.detail.take(85)}") {TextButton(onClick={onOrganization("agenda",e.date)}){Text("Abrir no calendário")}}}
            TextButton(onClick={onOrganization("agenda","")}){Text("Calendário")}
        }
        "insights"->HomeSection("INTELIGÊNCIA OPERACIONAL","Insights da sua rotina") {Text("Orientações baseadas em regras, não em diagnósticos automáticos.",style=MaterialTheme.typography.bodySmall);summary.insights.forEach{HomeInsight(it,onOrganization)}}
        "semana"->HomeSection("TENDÊNCIA","Próximos 7 dias") {HomeWeek(summary.week)}
        "acoes"->HomeSection("ACESSO RÁPIDO","Ações do dia") {
            ActionButtons {listOf("Agenda" to "agenda","Tarefas" to "tasks","Notas" to "notes","Metas" to "goals").forEach{(title,section)->TextButton(onClick={onOrganization(section,"")}){Text(title)}}}
            Text("Cadastros e gerenciamento ficam em Organização. Este painel apresenta somente resumos e alertas.",style=MaterialTheme.typography.bodySmall)
        }
        "avaliacoes"->HomeSection("ACOMPANHAMENTO","Avaliações e retornos") {
            val d=br.com.evolufisico.nativo.domain.DecisionCenter.summarize(repository.evaluations.toList(),today)
            Text("${d.clients} alunos acompanhados · ${d.records} avaliações · ${d.overdue.size} retornos vencidos · ${d.upcoming.size} em até 14 dias · ${d.missing} sem retorno informado")
            (d.overdue+d.upcoming).take(5).forEach{e->DataCard(e.name,"Retorno: ${e.nextAssessment}"){TextButton(onClick={onModule(1,e.studentId)}){Text("Ver avaliações")}}}
            TextButton(onClick={onModule(1,"")}){Text("Abrir Avaliações")}
        }
        "financeiro"->HomeSection("REGISTROS LOCAIS","Panorama financeiro") {
            val rows=repository.charges.toList()
            val period=br.com.evolufisico.nativo.domain.MonthlyLedger.summary(rows,java.time.YearMonth.from(today))
            val pending=rows.filter{!it.paid&&!it.exempt}
            Text("Recebido neste mês: ${money(period.receivedCents)}")
            Text("A receber em todos os períodos: ${money(pending.sumOf{br.com.evolufisico.nativo.domain.Financial.billable(it.amountCents,it.discountCents,it.exempt)})}")
            pending.sortedBy{it.dueDate}.take(3).forEach{c->DataCard(c.student,"${c.dueDate} · ${c.service}"){TextButton(onClick={onModule(6,c.studentId)}){Text("Ver cobrança")}}}
            TextButton(onClick={onModule(6,"")}){Text("Abrir Cobranças")}
        }
        "evolucao"->HomeSection("HISTÓRICO","Evolução registrada") {
            val d=br.com.evolufisico.nativo.domain.DecisionCenter.summarize(repository.evaluations.toList(),today)
            Text("${d.withHistory} alunos com avaliações em datas distintas. Diferenças de peso não representam diagnóstico.")
            if(d.changes.isEmpty())Text("Sem pares com peso válido registrado nas duas últimas datas.")
            d.changes.forEach{c->DataCard(c.evaluation.name,"${c.evaluation.date} · ${String.format(Locale.forLanguageTag("pt-BR"),"%+.1f",c.delta)} kg"){TextButton(onClick={onModule(2,c.evaluation.studentId)}){Text("Ver evolução")}}}
        }
    }}
}
@Composable private fun HomeSection(eyebrow:String,title:String,content:@Composable ColumnScope.()->Unit) {
    EffectsCard(Modifier.fillMaxWidth()) {Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {Text(eyebrow,style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.primary);Text(title,style=MaterialTheme.typography.titleLarge);content()}}
}
@Composable private fun HomeInsight(insight:HtmlHome.Insight,onOpen:(String,String)->Unit) {DataCard(insight.title,insight.text) {TextButton(onClick={onOpen(insight.section,"")}){Text("Consultar")}}}
@Composable private fun HomeKpi(label:String,count:Int,section:String,onOpen:()->Unit) {
    EffectsCard(onClick=onOpen,modifier=Modifier.fillMaxWidth().heightIn(min=96.dp),shape=RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {Icon(when(section){"tasks"->Icons.Filled.TaskAlt;"goals"->Icons.Filled.Flag;else->Icons.Filled.CalendarMonth},null,Modifier.size(22.dp),tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.weight(1f));Text(count.toString(),style=MaterialTheme.typography.headlineSmall)}
            Text(label,style=MaterialTheme.typography.labelLarge)
        }
    }
}
@Composable private fun HomeWeek(week:List<Pair<String,Int>>) {
    val peak=week.maxOf{it.second}.coerceAtLeast(1)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)) {week.forEach{(date,count)->
        Column(Modifier.weight(1f).clearAndSetSemantics{contentDescription="$date: $count ${if(count==1)"item" else "itens"}"},horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text(LocalDate.parse(date).format(DateTimeFormatter.ofPattern("EEE",Locale.forLanguageTag("pt-BR"))).removeSuffix("."),style=MaterialTheme.typography.labelSmall)
            Box(Modifier.width(16.dp).height(72.dp).background(MaterialTheme.colorScheme.surfaceVariant,RoundedCornerShape(4.dp)),contentAlignment=Alignment.BottomCenter) {Box(Modifier.fillMaxWidth().fillMaxHeight((count.toFloat()/peak).coerceAtLeast(.02f)).background(MaterialTheme.colorScheme.primary,RoundedCornerShape(4.dp)))}
            Text(count.toString(),style=MaterialTheme.typography.labelLarge)
        }
    }}
}
