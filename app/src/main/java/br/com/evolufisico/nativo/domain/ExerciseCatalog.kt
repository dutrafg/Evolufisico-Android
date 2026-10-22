package br.com.evolufisico.nativo.domain

import br.com.evolufisico.nativo.data.*

object ExerciseCatalog {
    fun validate(item:ExerciseDefinition) {
        require(item.id.isNotBlank() && item.id.length<=128) { "Identificador de exercício inválido." }
        require(item.name.isNotBlank() && item.name.length<=120) { "Nome do exercício deve ter de 1 a 120 caracteres." }
        require(item.muscleGroup.isNotBlank() && item.muscleGroup.length<=60) { "Informe um grupo muscular de até 60 caracteres." }
        require(item.equipment.isNotBlank() && item.equipment.length<=80) { "Informe um equipamento de até 80 caracteres." }
        require(item.instructions.length<=4000) { "Instruções acima de 4000 caracteres." }
    }
    fun checkUnique(item:ExerciseDefinition,existing:List<ExerciseDefinition>) {
        validate(item)
        require(existing.none { it.id!=item.id && TrainingInsights.exerciseKey(it.name)==TrainingInsights.exerciseKey(item.name) }) {
            "Este nome já está no catálogo, inclusive nos arquivados. Edite ou restaure o exercício existente."
        }
    }
    fun filter(rows:List<ExerciseDefinition>,query:String,group:String="Todos",includeArchived:Boolean=false)=rows
        .filter { (includeArchived || !it.archived) && (group=="Todos" || it.muscleGroup==group) &&
            WorkspaceRules.search(it.name+" "+it.muscleGroup+" "+it.equipment+" "+it.instructions,query) }
        .sortedWith(compareBy<ExerciseDefinition>{TrainingInsights.exerciseKey(it.name)}.thenBy{it.id})
    fun csv(rows:List<ExerciseDefinition>):String {
        require(rows.size<=20000) { "Catálogo acima de 20.000 linhas." }
        return ProfessionalTools.csv(listOf("Exercício","Grupo muscular","Equipamento","Instruções","Arquivado"),
            rows.map{listOf(it.name,it.muscleGroup,it.equipment,it.instructions,if(it.archived)"Sim" else "Não")})
    }
}
