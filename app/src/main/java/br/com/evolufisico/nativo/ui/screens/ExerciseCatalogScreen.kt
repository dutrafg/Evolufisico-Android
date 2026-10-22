package br.com.evolufisico.nativo.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.*

/** Embedded in Treinos so the sixteen top-level destinations remain stable. */
@Composable fun ExerciseCatalogContent(repo:AppRepository,onExportCsv:(String,String)->Unit) {
    var query by rememberSaveable{mutableStateOf("")}
    var group by rememberSaveable{mutableStateOf("Todos")}
    var archived by rememberSaveable{mutableStateOf(false)}
    var editing by remember{mutableStateOf<ExerciseDefinition?>(null)}
    var deleting by remember{mutableStateOf<ExerciseDefinition?>(null)}
    var error by remember{mutableStateOf("")}
    ActionError(error){error=""}
    deleting?.let{item->ConfirmDelete("exercício do catálogo",{deleting=null},{repo.delete(item)})}
    editing?.let{item->key(item.id){
        var name by remember{mutableStateOf(item.name)}
        var muscle by remember{mutableStateOf(item.muscleGroup)}
        var equipment by remember{mutableStateOf(item.equipment)}
        var instructions by remember{mutableStateOf(item.instructions)}
        WorkspaceEditor("Exercício do catálogo",{editing=null},{
            repo.save(item.copy(name=name.trim(),muscleGroup=muscle.trim(),equipment=equipment.trim(),instructions=instructions));editing=null
        }) {
            Field("Nome *",name,{name=it})
            Field("Grupo muscular *",muscle,{muscle=it})
            Field("Equipamento *",equipment,{equipment=it})
            Field("Instruções próprias",instructions,{instructions=it},lines=5)
            Text("Editar, arquivar ou excluir esta ficha não altera as séries já salvas nos treinos. Não há carga ou prescrição automática.")
        }
    }}
    Heading("Biblioteca de exercícios","Catálogo próprio, local e criptografado, com busca e organização por grupo muscular.")
    Button(onClick={editing=ExerciseDefinition()}){Text("+ Cadastrar exercício")}
    Field("Buscar nome, grupo, equipamento ou instruções",query,{query=it})
    val groups=listOf("Todos")+repo.exercises.map{it.muscleGroup}.distinct().sorted()
    PillChoices(groups,group,{group=it})
    PillChoices(listOf("Ativos","Incluir arquivados"),if(archived)"Incluir arquivados" else "Ativos",{archived=it=="Incluir arquivados"})
    val found=ExerciseCatalog.filter(repo.exercises.toList(),query,group,archived)
    Text("${found.size} exercícios neste filtro. O backup completo inclui o catálogo; o individual contém somente os registros do cliente.")
    OutlinedButton(enabled=found.isNotEmpty(),onClick={runCatching{onExportCsv("Catalogo_Exercicios.csv",ExerciseCatalog.csv(found))}
        .onFailure{error=it.message ?: "Falha ao exportar catálogo."}}){Text("Exportar catálogo filtrado CSV")}
    if(found.isEmpty())EmptyWorkspace("Nenhum exercício encontrado. Cadastre uma ficha ou limpe os filtros.")
    PagedRecords(found,query+group+archived,{it.id}){item->
        var expanded by rememberSaveable(item.id){mutableStateOf(false)}
        DataCard(item.name,"${item.muscleGroup} · ${item.equipment}${if(item.archived)" · Arquivado" else ""}\n"+
            if(expanded)item.instructions else item.instructions.take(200)) {
            TextButton(onClick={editing=item}){Text("Editar")}
            if(item.instructions.length>200)TextButton(onClick={expanded=!expanded}){Text(if(expanded)"Recolher" else "Ler instruções")}
            TextButton(onClick={runCatching{repo.save(item.copy(archived=!item.archived))}.onFailure{error=it.message.orEmpty()}}){Text(if(item.archived)"Restaurar" else "Arquivar")}
            TextButton(onClick={deleting=item}){Text("Excluir ficha")}
        }
    }
}
