package br.com.evolufisico.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.ops.ClientNote
import br.com.evolufisico.android.ops.OperationalRules
import java.time.LocalDate
import java.util.UUID

@Composable
fun ClientNotesModule(
    clients:List<Client>, all:List<ClientNote>, busy:Boolean,
    message:String?,quickCreateId:Int=0,onQuickConsumed:()->Unit={},
    save:(List<ClientNote>)->Unit
) {
    var clientId by remember { mutableStateOf(clients.firstOrNull()?.id.orEmpty()) }
    var chooseClient by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Todas") }
    var editing by remember { mutableStateOf<ClientNote?>(null) }
    var showingEditor by remember { mutableStateOf(false) }
    var removing by remember { mutableStateOf<ClientNote?>(null) }
    LaunchedEffect(quickCreateId,busy,clients.size) {
        if(quickCreateId>0 && !busy) {
            if(clients.isNotEmpty()) { editing=null;showingEditor=true }
            onQuickConsumed()
        }
    }

    LaunchedEffect(clients) {
        if(clients.isNotEmpty() && clients.none { it.id==clientId }) clientId=clients.first().id
    }
    val visible=all.filter { note ->
        note.clientId==clientId &&
            (category=="Todas" || note.category==category) &&
            (query.isBlank() || note.title.contains(query.trim(),true) ||
                note.body.contains(query.trim(),true) || note.dateIso.contains(query.trim()))
    }.sortedWith(compareByDescending<ClientNote> { it.pinned }
        .thenByDescending { it.dateIso }.thenBy { it.title })

    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Anotações por cliente",style=MaterialTheme.typography.headlineSmall)
            Text("Registre acompanhamentos e observações sem alterar as avaliações.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Box {
                OutlinedButton(onClick={chooseClient=true},modifier=Modifier.fillMaxWidth()) {
                    Text("Cliente: "+(clients.firstOrNull{it.id==clientId}?.name?:"Cadastre um cliente"))
                }
                DropdownMenu(expanded=chooseClient,onDismissRequest={chooseClient=false}) {
                    clients.forEach { c ->
                        DropdownMenuItem(text={Text(c.name)},onClick={
                            clientId=c.id;chooseClient=false
                        })
                    }
                }
            }
            Button(enabled=!busy&&clients.isNotEmpty(),modifier=Modifier.fillMaxWidth(),
                onClick={editing=null;showingEditor=true}) {Text("Nova anotação")}
            OutlinedTextField(value=query,onValueChange={query=it.take(120)},
                modifier=Modifier.fillMaxWidth(),singleLine=true,
                label={Text("Buscar nas anotações")})
            Row(Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                (listOf("Todas")+OperationalRules.noteCategories).forEach { choice ->
                    FilterChip(selected=category==choice,onClick={category=choice},
                        label={Text(choice)})
                }
            }
            Text("${visible.size} anotação(ões)",color=MaterialTheme.colorScheme.onSurfaceVariant)
            message?.let { Text(it,color=MaterialTheme.colorScheme.primary) }
        }
        if(visible.isEmpty()) item { Text("Nenhuma anotação corresponde à seleção.") }
        items(visible,key={it.id}) { note ->
            Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                    Text((if(note.pinned)"★ " else "")+note.title,
                        style=MaterialTheme.typography.titleMedium)
                    Text(note.dateIso+" · "+note.category,
                        color=MaterialTheme.colorScheme.primary,
                        style=MaterialTheme.typography.bodySmall)
                    Text(note.body)
                    Row(Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                        TextButton(enabled=!busy,onClick={editing=note;showingEditor=true}) {
                            Text("Editar")
                        }
                        TextButton(enabled=!busy,onClick={
                            save(all.map { if(it.id==note.id) it.copy(pinned=!it.pinned) else it })
                        }) { Text(if(note.pinned)"Desafixar" else "Fixar") }
                        TextButton(enabled=!busy,onClick={removing=note}) {
                            Text("Excluir")
                        }
                    }
                }
            }
        }
    }
    if(showingEditor && clients.isNotEmpty()) {
        ClientNoteEditor(
            clientId=clientId,
            allowedClients=clients.map{it.id}.toSet(),
            previous=editing,
            onDismiss={showingEditor=false},
            onSave={new ->
                save(if(editing==null) all+new else all.map { if(it.id==new.id)new else it })
                showingEditor=false
            }
        )
    }
    removing?.let{ item ->
        AlertDialog(onDismissRequest={removing=null},title={Text("Excluir anotação?")},
            text={Text("Este registro será excluído do histórico local.")},
            confirmButton={Button(onClick={
                save(all.filterNot{it.id==item.id}); removing=null
            }){Text("Excluir")}},
            dismissButton={TextButton(onClick={removing=null}){Text("Cancelar")}})
    }
}

@Composable
private fun ClientNoteEditor(
    clientId:String, allowedClients:Set<String>, previous:ClientNote?,
    onDismiss:()->Unit, onSave:(ClientNote)->Unit
) {
    var date by remember(previous?.id) {
        mutableStateOf(previous?.dateIso?:LocalDate.now().toString())
    }
    var category by remember(previous?.id) { mutableStateOf(previous?.category?:"Geral") }
    var title by remember(previous?.id) { mutableStateOf(previous?.title.orEmpty()) }
    var body by remember(previous?.id) { mutableStateOf(previous?.body.orEmpty()) }
    var pinned by remember(previous?.id) { mutableStateOf(previous?.pinned?:false) }
    var openCategory by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest=onDismiss,
        title={Text(if(previous==null)"Nova anotação" else "Editar anotação")},
        text={
            Column(Modifier.heightIn(max=460.dp).verticalScroll(rememberScrollState()),
                verticalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(date,{date=it.take(10)},singleLine=true,
                    label={Text("Data AAAA-MM-DD")},modifier=Modifier.fillMaxWidth())
                Box {
                    OutlinedButton(onClick={openCategory=true},modifier=Modifier.fillMaxWidth()) {
                        Text("Categoria: "+category)
                    }
                    DropdownMenu(expanded=openCategory,onDismissRequest={openCategory=false}) {
                        OperationalRules.noteCategories.forEach { option ->
                            DropdownMenuItem(text={Text(option)},onClick={
                                category=option;openCategory=false
                            })
                        }
                    }
                }
                OutlinedTextField(title,{title=it.take(120)},singleLine=true,
                    label={Text("Título")},modifier=Modifier.fillMaxWidth())
                OutlinedTextField(body,{body=it.take(3000)},minLines=3,maxLines=7,
                    label={Text("Observação")},modifier=Modifier.fillMaxWidth())
                Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                    Switch(checked=pinned,onCheckedChange={pinned=it})
                    Spacer(Modifier.width(9.dp))
                    Text("Fixar no topo")
                }
                error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
            }
        },
        confirmButton={Button(onClick={
            val candidate=ClientNote(previous?.id?:UUID.randomUUID().toString(),
                previous?.clientId?:clientId,date,category,title.trim(),body.trim(),pinned)
            error=OperationalRules.note(candidate,allowedClients)
            if(error==null)onSave(candidate)
        }) { Text("Salvar") }},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
    )
}
