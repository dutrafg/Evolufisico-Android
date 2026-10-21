package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.evolufisico.android.data.PreRegistration
import br.com.evolufisico.android.data.PreRegistrationRules
import br.com.evolufisico.android.data.PreRegistrationStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

/** Pré-cadastro Android: rascunho separado do cliente, sem criar avaliação clínica. */
@Composable
fun PreRegistrationPanel(onOpenClients:()->Unit) {
    val context=LocalContext.current
    val store=remember(context) { PreRegistrationStore(context.applicationContext) }
    val scope=rememberCoroutineScope()
    var items by remember { mutableStateOf<List<PreRegistration>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var problem by remember { mutableStateOf<String?>(null) }
    var fatal by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf<PreRegistration?>(null) }
    var openEditor by remember { mutableStateOf(false) }
    var confirmRemove by remember { mutableStateOf<PreRegistration?>(null) }

    LaunchedEffect(store) {
        withContext(Dispatchers.IO) { runCatching { store.load() } }
            .onSuccess { items=it;problem=null;fatal=false }
            .onFailure { fatal=true;problem="Falha ao ler os pré-cadastros. Os arquivos foram preservados." }
        loading=false
    }
    fun write(next:(List<PreRegistration>)->List<PreRegistration>,success:String) {
        if(busy || loading || fatal) return
        busy=true
        scope.launch {
            val result=withContext(Dispatchers.IO) {
                runCatching {
                    val current=store.load()
                    store.save(next(current))
                    store.load()
                }
            }
            result.onSuccess { items=it;info=success;problem=null }
                .onFailure { problem="Não foi possível gravar os pré-cadastros: ${it.message}" }
            busy=false
        }
    }
    Card(modifier=Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Pré-cadastros",style=MaterialTheme.typography.titleMedium)
                    Text("Identificação provisória. Nenhuma avaliação será criada.",
                        style=MaterialTheme.typography.bodySmall,
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick={editing=null;openEditor=true},
                    enabled=!busy && !loading && !fatal,
                    modifier=Modifier.heightIn(min=48.dp)) { Text("Novo") }
            }
            if(loading) Text("Consultando pré-cadastros…")
            problem?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            info?.let { Text(it,color=MaterialTheme.colorScheme.primary,
                style=MaterialTheme.typography.bodySmall) }
            if(!loading && !fatal && items.isEmpty()) {
                Text("Nenhum pré-cadastro aguardando atendimento.",
                    style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items.sortedByDescending { it.updatedAtIso }.forEach { item ->
                Column(Modifier.fillMaxWidth(),
                    verticalArrangement=Arrangement.spacedBy(3.dp)) {
                    Text(item.name,style=MaterialTheme.typography.titleSmall)
                    Text(item.email,style=MaterialTheme.typography.bodySmall)
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick={editing=item;openEditor=true},enabled=!busy) {
                            Text("Editar")
                        }
                        TextButton(onClick={confirmRemove=item},enabled=!busy) {
                            Text("Excluir")
                        }
                    }
                }
            }
            Text("Conclua o cadastro na área Clientes. O rascunho permanece aqui " +
                "até você confirmar sua exclusão.",
                style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick=onOpenClients,modifier=Modifier.fillMaxWidth()
                .heightIn(min=48.dp)) { Text("Abrir Clientes") }
        }
    }

    if(openEditor) {
        PreRegistrationEditor(
            current=editing,enabled=!busy,
            onDismiss={openEditor=false},
            onSave={ name,email,phone ->
                val now=Instant.now()
                val row=editing?.copy(name=name.trim(),email=email.trim(),
                    phone=phone.trim(),updatedAtIso=now.toString())
                    ?:PreRegistration.create(name,email,phone,now)
                val err=PreRegistrationRules.validate(row)
                if(err!=null) {
                    problem=err
                } else {
                    openEditor=false
                    problem=null
                    write({ existing ->
                        (existing.filterNot { it.id==row.id } + row).also(
                            PreRegistrationRules::validateAll)
                    },"Pré-cadastro salvo no dispositivo.")
                }
            }
        )
    }
    confirmRemove?.let { item ->
        AlertDialog(onDismissRequest={confirmRemove=null},
            title={Text("Excluir pré-cadastro?")},
            text={Text("Esta ação não altera avaliações nem cadastros já concluídos.")},
            confirmButton={
                Button(enabled=!busy,onClick={
                    confirmRemove=null
                    write({ list->list.filterNot { it.id==item.id } },
                        "Pré-cadastro excluído.")
                }) { Text("Excluir rascunho") }
            },
            dismissButton={TextButton(onClick={confirmRemove=null}){Text("Cancelar")})
    }
}

@Composable
private fun PreRegistrationEditor(
    current:PreRegistration?,enabled:Boolean,onDismiss:()->Unit,
    onSave:(String,String,String)->Unit
) {
    var name by remember(current?.id) { mutableStateOf(current?.name.orEmpty()) }
    var email by remember(current?.id) { mutableStateOf(current?.email.orEmpty()) }
    var phone by remember(current?.id) { mutableStateOf(current?.phone.orEmpty()) }
    var validation by remember(current?.id) { mutableStateOf<String?>(null) }
    Dialog(onDismissRequest=onDismiss) {
        Surface(shape=MaterialTheme.shapes.medium) {
            Column(Modifier.fillMaxWidth().heightIn(max=580.dp)
                .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp)) {
                Text(if(current==null)"Novo pré-cadastro" else "Editar pré-cadastro",
                    style=MaterialTheme.typography.titleLarge)
                OutlinedTextField(value=name,onValueChange={name=it.take(120)},
                    label={Text("Nome")},modifier=Modifier.fillMaxWidth(),singleLine=true)
                OutlinedTextField(value=email,onValueChange={email=it.take(200)},
                    label={Text("E-mail")},modifier=Modifier.fillMaxWidth(),singleLine=true)
                OutlinedTextField(value=phone,onValueChange={phone=it.take(40)},
                    label={Text("Telefone com DDD")},modifier=Modifier.fillMaxWidth(),
                    singleLine=true)
                validation?.let {Text(it,color=MaterialTheme.colorScheme.error)}
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick=onDismiss,modifier=Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(onClick={
                        val trial=current?.copy(name=name.trim(),email=email.trim(),phone=phone.trim())
                            ?:PreRegistration.create(name,email,phone)
                        val err=PreRegistrationRules.validate(trial)
                        if(err!=null) validation=err else onSave(name,email,phone)
                    },enabled=enabled,modifier=Modifier.weight(1f)) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}
