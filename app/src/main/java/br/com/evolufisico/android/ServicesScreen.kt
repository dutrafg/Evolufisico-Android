package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.ops.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ServicesModule(all:List<ServiceItem>,busy:Boolean,message:String?,
    quickCreateId:Int=0,onQuickConsumed:()->Unit={},
    save:(List<ServiceItem>)->Unit) {
    var search by remember{mutableStateOf("")}
    var enabledOnly by remember{mutableStateOf(true)}
    var editing by remember{mutableStateOf<ServiceItem?>(null)}
    var showEditor by remember{mutableStateOf(false)}
    var removing by remember{mutableStateOf<ServiceItem?>(null)}
    LaunchedEffect(quickCreateId,busy) {
        if(quickCreateId>0 && !busy) {
            editing=null;showEditor=true
            onQuickConsumed()
        }
    }
    val money=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    val visible=all.filter{(!enabledOnly||it.enabled)&&it.name.contains(search,true)}
        .sortedBy{it.name.lowercase()}
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(16.dp),
        verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Text("Catálogo de serviços",style=MaterialTheme.typography.titleLarge)
            Text("Preços de referência locais; cobranças continuam no módulo financeiro.",
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            Button(enabled=!busy,modifier=Modifier.fillMaxWidth(),
                onClick={editing=null;showEditor=true}){Text("Novo serviço")}
            OutlinedTextField(search,{search=it.take(80)},label={Text("Buscar serviço")},
                modifier=Modifier.fillMaxWidth())
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                Text("Apenas serviços ativos")
                Spacer(Modifier.width(12.dp))
                Switch(checked=enabledOnly,onCheckedChange={enabledOnly=it})
            }
            message?.let{Text(it,color=MaterialTheme.colorScheme.primary)}
        }
        if(visible.isEmpty())item{Text("Nenhum serviço nesta seleção.")}
        items(visible,key={it.id}){s->
            Card(modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text(s.name,style=MaterialTheme.typography.titleMedium)
                    Text(money.format(s.priceCents/100.0)+" · "+s.durationMinutes+" min")
                    Text(if(s.enabled)"Ativo" else "Inativo",
                        color=MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                        TextButton(enabled=!busy,onClick={editing=s;showEditor=true}){Text("Editar")}
                        TextButton(enabled=!busy,onClick={
                            save(all.map{if(it.id==s.id)it.copy(enabled=!it.enabled) else it})
                        }){Text(if(s.enabled)"Desativar" else "Ativar")}
                        TextButton(enabled=!busy,onClick={removing=s}){Text("Excluir")}
                    }
                }
            }
        }
    }
    if(showEditor)ServiceEditor(editing,onDismiss={showEditor=false},onSave={s->
        save(if(editing==null)all+s else all.map{if(it.id==s.id)s else it})
        showEditor=false
    })
    removing?.let{s-> AlertDialog(onDismissRequest={removing=null},
        title={Text("Excluir serviço?")},
        text={Text("Isso não remove cobranças nem atendimentos já cadastrados.")},
        confirmButton={Button(onClick={save(all.filterNot{it.id==s.id});removing=null}){Text("Excluir")}},
        dismissButton={TextButton(onClick={removing=null}){Text("Cancelar")}})}
}

@Composable
private fun ServiceEditor(current:ServiceItem?,onDismiss:()->Unit,onSave:(ServiceItem)->Unit) {
    var name by remember(current?.id){mutableStateOf(current?.name?:"Avaliação física")}
    var price by remember(current?.id){mutableStateOf(
        current?.let{"${it.priceCents/100},${(it.priceCents%100).toString().padStart(2,'0')}"}?:"25,00")}
    var duration by remember(current?.id){mutableStateOf((current?.durationMinutes?:60).toString())}
    var error by remember{mutableStateOf<String?>(null)}
    AlertDialog(onDismissRequest=onDismiss,title={Text("Serviço")},
        text={Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
            OutlinedTextField(name,{name=it.take(100)},label={Text("Nome")})
            OutlinedTextField(price,{price=it.take(16)},label={Text("Preço R$ 25,00")})
            OutlinedTextField(duration,{duration=it.take(4)},label={Text("Duração em minutos")})
            error?.let{Text(it,color=MaterialTheme.colorScheme.error)}
        }},
        confirmButton={Button(onClick={
            val match=Regex("""^\d{1,7}(?:[,.]\d{1,2})?$""").matches(price.trim())
            val cent=if(match) {
                val pair=price.trim().replace(',', '.').split('.')
                (pair[0].toLongOrNull()?:0L)*100L+
                    (pair.getOrNull(1)?:"").padEnd(2,'0').take(2).toLongOrNull().let{it?:0L}
            } else -1L
            val s=ServiceItem(current?.id?:java.util.UUID.randomUUID().toString(),
                name.trim(),cent,duration.toIntOrNull()?:0,current?.enabled?:true)
            error=OperationalRules.service(s)
            if(error==null)onSave(s)
        }){Text("Salvar")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
    )
}
