package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.billing.ChargeRules
import br.com.evolufisico.android.billing.EncryptedChargeStore
import br.com.evolufisico.android.data.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Locale

@Composable
fun BillingScreen(clients: List<Client>, store: EncryptedChargeStore) {
    var charges by remember { mutableStateOf<List<Charge>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("Pendentes") }
    var search by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<Charge?>(null) }
    var deleting by remember { mutableStateOf<Charge?>(null) }
    var reversing by remember { mutableStateOf<Charge?>(null) }
    var editor by remember { mutableStateOf(false) }
    var confirmation by remember { mutableStateOf<Charge?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(store) {
        val result = withContext(Dispatchers.IO) { runCatching { store.load() } }
        result.onSuccess { charges = it }
            .onFailure { error = "Não foi possível abrir as cobranças protegidas. Dados preservados." }
        loading = false
    }
    fun persist(next: List<Charge>) {
        if (busy || loading || error != null) return
        busy = true
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { store.save(next, clients.map { it.id }.toSet()) }
            }
            result.onSuccess { charges = next; message = "Cobranças salvas no dispositivo." }
                .onFailure { error = "Não foi possível salvar as cobranças. Os dados não foram substituídos." }
            busy = false
        }
    }
    if (loading) { BillingNotice("Abrindo cobranças..."); return }
    if (error != null) { BillingNotice(error!!); return }

    val clientNames = clients.associate { it.id to it.name }
    val visible = charges.filter { charge ->
        val statusMatches = when (status) {
            "Pendentes" -> !charge.isReceived
            "Atrasadas" -> ChargeRules.isOverdue(charge)
            "Recebidas" -> charge.isReceived
            else -> true
        }
        statusMatches && (search.isBlank() ||
            charge.description.contains(search.trim(),ignoreCase = true) ||
            (clientNames[charge.clientId] ?: "").contains(search.trim(),ignoreCase = true))
    }.sortedWith(compareBy<Charge> { it.dueDateIso }.thenBy { it.id })
    LazyColumn(modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Cobranças", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold)
            Text("Recebimentos vinculados ao cliente", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("A receber", style = MaterialTheme.typography.labelLarge)
                    Text(reais(ChargeRules.total(charges,false)),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text("Recebido: ${reais(ChargeRules.total(charges,true))}")
                    val month = java.time.YearMonth.now()
                    Text("No mês atual: " + month.monthValue.toString().padStart(2,'0') + "/" + month.year)
                    Text("Vence no mês: " + reais(ChargeRules.monthlyTotal(charges,false,month)))
                    Text("Recebido no mês: " + reais(ChargeRules.monthlyTotal(charges,true,month)))
                    val month = java.time.YearMonth.now()
                    Text("Mês atual (${month.monthValue.toString().padStart(2,'0')}/${month.year}):",
                        style = MaterialTheme.typography.labelLarge)
                    Text("Vence no mês: ${reais(ChargeRules.monthlyTotal(charges,false,month))}")
                    Text("Recebido no mês: ${reais(ChargeRules.monthlyTotal(charges,true,month))}")
                }
            }
        }
        item {
            Button(enabled = !busy && clients.isNotEmpty(),
                onClick = { editing = null; editor = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Nova cobrança")
            }
            if (clients.isEmpty()) Text("Cadastre um cliente antes de criar cobranças.")
        }
        item {
            OutlinedTextField(
                value = search, onValueChange = { search = it.take(80) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                label = { Text("Buscar cliente ou serviço") }
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Pendentes", "Atrasadas", "Recebidas", "Todas").forEach { choice ->
                    FilterChip(selected = status == choice, onClick = { status = choice },
                        label = { Text(choice) })
                }
            }
            message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
        if (visible.isEmpty()) item { Text("Nenhuma cobrança nesta categoria.") }
        items(visible, key = { it.id }) { charge ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(clientNames[charge.clientId] ?: "Cliente não encontrado",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(charge.description)
                    Text(reais(charge.amountCents), color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium)
                    Text("Vencimento: ${displayDate(charge.dueDateIso)}")
                    Text(when {
                        charge.isReceived -> "Recebido em " + displayDate(charge.receivedDateIso!!)
                        ChargeRules.isOverdue(charge) -> "Atrasada"
                        else -> "Pendente"
                    }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        if (!charge.isReceived) {
                            TextButton(enabled = !busy, onClick = {
                                editing = charge; editor = true
                            }) { Text("Editar") }
                            TextButton(enabled = !busy, onClick = { deleting = charge }) {
                                Text("Excluir")
                            }
                            Button(enabled = !busy, onClick = { confirmation = charge }) {
                                Text("Receber")
                            }
                        } else {
                            OutlinedButton(enabled = !busy, onClick = { reversing = charge }) {
                                Text("Estornar recebimento")
                            }
                        }
                    }
                }
            }
        }
    }
    if (editor) ChargeEditor(clients, editing, onDismiss = { editor = false }, onSave = {
        val next = if (editing == null) charges + it else
            charges.map { old -> if (old.id == editing?.id) it else old }
        persist(next)
        editor = false
    })
    deleting?.let { charge ->
        AlertDialog(onDismissRequest = { deleting = null },
            title = { Text("Excluir cobrança pendente?") },
            text = { Text("Excluir o lançamento: " + charge.description + "?") },
            confirmButton = {
                Button(onClick = {
                    if (!charge.isReceived) persist(charges.filterNot { it.id == charge.id })
                    deleting = null
                }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } }
        )
    }
    reversing?.let { charge ->
        AlertDialog(onDismissRequest = { reversing = null },
            title = { Text("Confirmar estorno?") },
            text = { Text("O lançamento voltará ao estado pendente. Este estorno não é bancário.") },
            confirmButton = {
                Button(onClick = {
                    if (charge.isReceived) persist(charges.map {
                        if (it.id == charge.id) it.copy(receivedDateIso = null) else it
                    })
                    reversing = null
                }) { Text("Confirmar estorno") }
            },
            dismissButton = { TextButton(onClick = { reversing = null }) { Text("Cancelar") } }
        )
    }
    confirmation?.let { charge ->
        AlertDialog(onDismissRequest = { confirmation = null },
            title = { Text("Confirmar recebimento?") },
            text = { Text("Marcar como recebido: ${reais(charge.amountCents)} — ${charge.description}?") },
            confirmButton = {
                Button(onClick = {
                    persist(charges.map { item ->
                        if (item.id == charge.id) item.copy(receivedDateIso = LocalDate.now().toString()) else item
                    })
                    confirmation = null
                }) { Text("Confirmar recebimento") }
            },
            dismissButton = { TextButton(onClick = { confirmation = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ChargeEditor(
    clients: List<Client>, existing: Charge?, onDismiss: () -> Unit, onSave: (Charge) -> Unit
) {
    var clientId by remember(existing?.id) {
        mutableStateOf(existing?.clientId ?: clients.first().id)
    }
    var description by remember(existing?.id) {
        mutableStateOf(existing?.description ?: "Avaliação física")
    }
    var amount by remember(existing?.id) {
        mutableStateOf(existing?.let { ChargeRules.formatInput(it.amountCents) } ?: "25,00")
    }
    var due by remember(existing?.id) {
        mutableStateOf(existing?.dueDateIso ?: LocalDate.now().toString())
    }
    var issue by remember { mutableStateOf<String?>(null) }
    var choosing by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Nova cobrança" else "Editar cobrança pendente") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(9.dp)) {
                TextButton(onClick = { choosing = true }) {
                    Text("Cliente: ${clients.firstOrNull { it.id == clientId }?.name ?: "Selecione"}")
                }
                androidx.compose.material3.DropdownMenu(
                    expanded = choosing, onDismissRequest = { choosing = false }
                ) {
                    clients.forEach { client ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(client.name) }, onClick = {
                                clientId = client.id; choosing = false
                            }
                        )
                    }
                }
                OutlinedTextField(value = description,
                    onValueChange = { description = it.take(120) },
                    label = { Text("Serviço") }, singleLine = true)
                OutlinedTextField(value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Valor em R$ (ex.: 25,00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true)
                OutlinedTextField(value = due,
                    onValueChange = { due = it },
                    label = { Text("Vencimento (AAAA-MM-DD)") }, singleLine = true)
                issue?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Button(onClick = {
                val cents = ChargeRules.parseReais(amount)
                if (cents == null) { issue = "Informe um valor válido."; return@Button }
                val charge = if (existing == null)
                    Charge.new(clientId, description, cents, due.trim())
                else existing.copy(clientId = clientId, description = description.trim(),
                    amountCents = cents, dueDateIso = due.trim())
                val validation = ChargeRules.validate(charge, clients.map { it.id }.toSet())
                if (validation != null) issue = validation else onSave(charge)
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun BillingNotice(message: String) {
    Column(Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center) { Text(message) }
}

private fun reais(cents: Long): String =
    "R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", cents / 100.0)

private fun displayDate(iso: String): String =
    runCatching { LocalDate.parse(iso).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
        .getOrElse { iso }
