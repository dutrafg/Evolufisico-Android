package br.com.evolufisico.nativo.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import br.com.evolufisico.nativo.data.FormTemplate

@Composable fun TemplateEditor(original:FormTemplate,onClose:()->Unit,onSave:(FormTemplate)->Unit) {
    var title by remember(original.id){mutableStateOf(original.title)}
    var level by remember(original.id){mutableStateOf(original.level)}
    var description by remember(original.id){mutableStateOf(original.description)}
    var questions by remember(original.id){mutableStateOf(original.questions.joinToString("\n"))}
    DialogScreen { WorkspaceEditor("Modelo de anamnese",onClose,{
        val result=original.copy(title=title.trim(),level=level,description=description,questions=questions.lines().map{it.trim()}.filter{it.isNotBlank()})
        result.validate();onSave(result)
    }) {
        Field("Título *",title,{title=it});Field("Descrição",description,{description=it},lines=2)
        PillChoices(listOf("Iniciante","Intermediário","Avançado"),level,{level=it})
        Field("Perguntas: uma por linha (1–60)",questions,{questions=it},lines=5)
        Text("Respostas já salvas preservam os textos originais, mesmo após editar ou excluir o modelo.")
    }}
}
