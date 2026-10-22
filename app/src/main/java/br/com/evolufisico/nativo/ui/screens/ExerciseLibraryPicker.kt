package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.TrainingSession
import br.com.evolufisico.nativo.data.WorkoutSet
import br.com.evolufisico.nativo.data.ExerciseDefinition
import br.com.evolufisico.nativo.domain.*

@Composable fun ExerciseLibraryPicker(sessions:List<TrainingSession>,onClose:()->Unit,catalog:List<ExerciseDefinition> = emptyList(),onSelect:(WorkoutSet)->Unit) {
    var query by remember{mutableStateOf("")}
    var source by remember{mutableStateOf(if(catalog.any{!it.archived})"Catálogo" else "Histórico")}
    val library=remember(sessions,catalog,source){
        if(source=="Catálogo") ExerciseCatalog.filter(catalog,"").distinctBy{TrainingInsights.exerciseKey(it.name)}.map{WorkoutSet(exercise=it.name)}
        else TrainingInsights.library(sessions)
    }
    AlertDialog(onDismissRequest=onClose,title={Text("Reutilizar exercício")},text={Column {
        Text("No catálogo, a série começa com 10 repetições e carga zero para edição. O histórico reutiliza os valores registrados. Revise os valores antes de salvar.")
        PillChoices(listOf("Catálogo","Histórico"),source,{source=it})
        Field("Buscar exercício",query,{query=it})
        val found=library.filter{WorkspaceRules.search(it.exercise,query)}
        if(found.isEmpty())Text("Nenhum exercício encontrado. Você pode preencher uma série manualmente.")
        LazyColumn(Modifier.heightIn(max=340.dp)) {
            items(found,key={TrainingInsights.exerciseKey(it.exercise)}){row->
                TextButton(onClick={onSelect(row)}){Text("${row.exercise} · ${row.repetitions} rep · ${row.loadKg} kg")}
            }
        }
    }},confirmButton={TextButton(onClick=onClose){Text("Fechar")}})
}
