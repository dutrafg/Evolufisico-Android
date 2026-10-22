package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.domain.*
import java.time.LocalDate
import java.util.Locale

@Composable fun TrainingProgression(repo:AppRepository,onExportCsv:(String,String)->Unit) {
    var clientId by rememberSaveable{mutableStateOf("")}
    var clientName by rememberSaveable{mutableStateOf("")}
    var start by rememberSaveable{mutableStateOf(LocalDate.now().minusDays(89).toString())}
    var end by rememberSaveable{mutableStateOf(LocalDate.now().toString())}
    var preset by rememberSaveable{mutableStateOf("90 dias")}
    var query by rememberSaveable{mutableStateOf("")}
    var error by remember{mutableStateOf("")}
    var selected by rememberSaveable{mutableStateOf("")}
    Heading("Evolução de cargas","Comparação descritiva por cliente e exercício. Somente sessões concluídas e séries marcadas entram nos valores. Carga zero não estima peso corporal; nomes diferentes não são combinados automaticamente.")
    ClientPicker(repo,clientId,clientName,emptyLabel="Selecionar cliente",clearLabel="Limpar seleção"){id,name->clientId=id;clientName=name;selected=""}
    PillChoices(listOf("30 dias","90 dias","1 ano"),preset,{period->
        preset=period;selected=""
        end=LocalDate.now().toString();start=LocalDate.now().minusDays(when(period){"30 dias"->29L;"90 dias"->89L;else->364L}).toString()
    })
    DateField("De",start,{start=it;selected="";preset=""});DateField("Até",end,{end=it;selected="";preset=""})
    Field("Buscar exercício",query,{query=it})
    val result=runCatching{TrainingInsights.history(repo.trainingSessions.toList(),clientId,WorkspaceRules.date(start),WorkspaceRules.date(end))}
    if(result.isFailure)Text(result.exceptionOrNull()?.message ?: "Período inválido.",color=MaterialTheme.colorScheme.error)
    val points=result.getOrDefault(emptyList()).filter{WorkspaceRules.search(it.exercise,query)}
    val trends=TrainingInsights.trends(points)
    if(clientId.isBlank())Text("Selecione um cliente para evitar misturar históricos.")
    else if(points.isEmpty() && result.isSuccess)EmptyWorkspace("Nenhuma série executada neste cliente, período e busca.")
    else if(result.isSuccess) {
        Text("${trends.size} exercícios · ${points.sumOf{it.sets.toLong()}} séries marcadas")
        OutlinedButton(onClick={runCatching{onExportCsv("Evolucao_Cargas_${LocalDate.now()}.csv",TrainingInsights.csv(points))}.onFailure{error=it.message ?: "Falha ao exportar."}}){Text("Exportar CSV deste filtro")}
        Text("O CSV contém exercícios e medidas; não inclui nome ou ID do cliente.",style=MaterialTheme.typography.bodySmall)
        PagedRecords(trends,clientId+start+end+query,{it.exercise}){trend->
            fun fmt(value:Double)=String.format(Locale.forLanguageTag("pt-BR"),"%.1f",value)
            DataCard(trend.exercise,"${trend.current.date} · carga máxima externa ${fmt(trend.current.maximumKg)} kg\n"+
                (trend.previous?.let{"Anterior: ${it.date} · ${fmt(it.maximumKg)} kg · variação ${fmt(trend.differenceKg!!)} kg"} ?: "Sem outro dia registrado para comparar.")) {
                TextButton(onClick={selected=if(selected==trend.exercise)"" else trend.exercise}){Text(if(selected==trend.exercise)"Recolher histórico" else "Ver histórico diário")}
            }
            if(selected==trend.exercise) {
                val history=points.filter{TrainingInsights.exerciseKey(it.exercise)==TrainingInsights.exerciseKey(trend.exercise)}.sortedByDescending{it.date}
                PagedRecords(history,clientId+start+end+selected,{it.date}){point->
                    DataCard(point.date,"${point.sets} séries · ${point.repetitions} rep · máxima ${fmt(point.maximumKg)} kg\nVolume externo: ${fmt(point.volume)} kg·rep")
                }
            }
        }
    }
    ActionError(error){error=""}
}
