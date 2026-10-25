package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.WorkspaceRules
import br.com.evolufisico.nativo.domain.ProfessionalTools
import java.time.LocalDate
import java.util.UUID

@Composable
fun WorkspaceEditor(title: String, onClose: () -> Unit, onSave: () -> Unit,
                    content: @Composable ColumnScope.() -> Unit) {
    TrackEditing()
    var error by remember { mutableStateOf("") }
    var discard by remember { mutableStateOf(false) }
    Dialog(onDismissRequest={discard=true},properties=DialogProperties(usePlatformDefaultWidth=false)) {
        BackHandler { discard=true }
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing).imePadding()) {
                Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween) {
                    TextButton(onClick={discard=true}) { Text("Cancelar") }
                    Button(onClick={runCatching(onSave).onFailure {error=it.message ?: "Falha ao salvar. Tente novamente."}}) { Text("Salvar") }
                }
                Screen(bottomInset=24.dp) {
                    Heading(title)
                    if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
                    content()
                }
            }
        }
        if(discard) AlertDialog(onDismissRequest={discard=false},title={Text("Descartar alterações?")},
            text={Text("As alterações ainda não salvas serão perdidas.")},
            confirmButton={TextButton(onClick=onClose){Text("Descartar")}},
            dismissButton={TextButton(onClick={discard=false}){Text("Continuar editando")}})
    }
}

@Composable
fun ClientPicker(repo: AppRepository, studentId: String, student: String, emptyLabel:String="Vincular cliente (opcional)",clearLabel:String="Sem vínculo",onChange: (String,String)->Unit) {
    var open by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val selectedLabel=studentChoices(WorkspaceRules.latest(repo.evaluations.toList())).toMap()[studentId] ?: student
    OutlinedButton(onClick={open=true},modifier=Modifier.fillMaxWidth()) {
        Text(if(student.isBlank()) emptyLabel else "Cliente: $selectedLabel")
    }
    if(open) AlertDialog(onDismissRequest={open=false},title={Text("Selecionar cliente")},text={
        Column {
            Field("Buscar cliente",query,{query=it})
            LazyColumn(Modifier.heightIn(max=320.dp)) {
                val found=WorkspaceRules.latest(repo.evaluations.toList()).filter {WorkspaceRules.search(it.name,query)}
                items(studentChoices(found),key={it.first}) { (id,label) ->
                    TextButton(onClick={onChange(id,found.first{it.studentId==id}.name);open=false}) {
                        Text((if(id==studentId) "✓ " else "")+label)
                    }
                }
                if(found.isEmpty()) item { Text("Nenhum perfil encontrado. Cadastre uma avaliação para criar um perfil.") }
            }
        }
    },confirmButton={TextButton(onClick={open=false}){Text("Fechar")}},
        dismissButton={TextButton(onClick={onChange("","");open=false}){Text(clearLabel)}})
}

@Composable
fun ConfirmDelete(label: String, onDismiss: ()->Unit, onDelete: ()->Unit) {
    var error by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest=onDismiss,title={Text("Excluir $label?")},
        text={Text(error.ifBlank {"Esta ação remove apenas este registro e não pode ser desfeita."})},
        confirmButton={TextButton(onClick={runCatching {onDelete();onDismiss()}.onFailure {error=it.message ?: "Falha ao excluir."}}){Text("Excluir")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})
}

@Composable
fun ActionError(message: String, onDismiss: ()->Unit) {
    if(message.isNotBlank()) AlertDialog(onDismissRequest=onDismiss,title={Text("Não foi possível concluir")},
        text={Text(message)},confirmButton={TextButton(onClick=onDismiss){Text("Entendi")}})
}

@Composable fun EmptyWorkspace(text: String) {
    EffectsCard(Modifier.fillMaxWidth()) { Text(text,Modifier.padding(24.dp),style=MaterialTheme.typography.bodyLarge) }
}

