package br.com.evolufisico.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.ClientRules
import br.com.evolufisico.android.data.EncryptedClientStore
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val brandBlue = Color(0xFF235C91)
private val lightScheme = lightColorScheme(primary = brandBlue, secondary = Color(0xFF44749A),
    surface = Color(0xFFFAFCFF), surfaceVariant = Color(0xFFE7F0F8))
private val darkScheme = darkColorScheme(primary = Color(0xFF9CCBFF), secondary = Color(0xFFA9C8E6))

private enum class Section(val title: String, val icon: ImageVector) {
    HOME("Início", Icons.Outlined.Home),
    CLIENTS("Clientes", Icons.Outlined.People),
    ASSESSMENTS("Avaliações", Icons.Outlined.Assessment),
    EVOLUTION("Evolução", Icons.Outlined.TrendingUp),
    BILLING("Cobranças", Icons.Outlined.ReceiptLong),
    SETTINGS("Configurações", Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = EncryptedClientStore(applicationContext)
        val assessmentRepository = EncryptedAssessmentStore(applicationContext)
        val themePrefs = getSharedPreferences("ui_preferences", MODE_PRIVATE)
        setContent {
            var darkMode by remember { mutableStateOf(themePrefs.getBoolean("dark_mode", false)) }
            MaterialTheme(colorScheme = if (darkMode) darkScheme else lightScheme) {
                AppContent(
                    repository = repository,
                    assessmentRepository = assessmentRepository,
                    darkMode = darkMode,
                    onDarkModeChange = {
                        darkMode = it
                        themePrefs.edit().putBoolean("dark_mode", it).apply()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    repository: EncryptedClientStore,
    assessmentRepository: EncryptedAssessmentStore,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    var current by remember { mutableStateOf(Section.HOME) }
    val clients = remember { mutableStateListOf<Client>() }
    var loading by remember { mutableStateOf(true) }
    var storageError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(repository) {
        val result = withContext(Dispatchers.IO) { runCatching { repository.load() } }
        result.onSuccess { clients.addAll(it) }
            .onFailure { storageError = "Não foi possível abrir os dados protegidos: ${it.message}" }
        loading = false
    }

    fun persist(next: List<Client>) {
        if (saving || loading || storageError != null) return
        saving = true
        scope.launch {
            val removedIds = clients.map { it.id }.toSet() - next.map { it.id }.toSet()
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    if (removedIds.isNotEmpty() &&
                        assessmentRepository.load().any { it.clientId in removedIds }) {
                        throw IllegalArgumentException(
                            "Este cliente possui avaliações. Exclua as avaliações antes de excluir o cliente."
                        )
                    }
                    repository.save(next)
                }
            }
            result.onSuccess {
                clients.clear()
                clients.addAll(next)
                snackbar.showSnackbar("Dados salvos com segurança neste dispositivo.")
            }.onFailure {
                if (it is IllegalArgumentException) {
                    snackbar.showSnackbar(it.message ?: "Exclusão bloqueada.")
                } else {
                    storageError = "Falha ao salvar: ${it.message}"
                    snackbar.showSnackbar("Alterações não foram aplicadas. Verifique o armazenamento.")
                }
            }
            saving = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (current == Section.HOME) "EvoluFísico" else current.title,
                    fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { current = Section.SETTINGS }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Section.entries.filter { it != Section.SETTINGS }.forEach { section ->
                    NavigationBarItem(
                        selected = current == section,
                        onClick = { current = section },
                        icon = { Icon(section.icon, contentDescription = null) },
                        label = { Text(section.title, maxLines = 1) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { insets ->
        Surface(modifier = Modifier.fillMaxSize().padding(insets)) {
            if (loading) {
                CenterMessage("Abrindo dados locais protegidos...")
            } else if (storageError != null) {
                CenterMessage(storageError ?: "Erro ao acessar dados")
            } else {
                when (current) {
                    Section.HOME -> HomeScreen(clients.size, onNavigate = { current = it })
                    Section.CLIENTS -> ClientsScreen(clients.toList(), saving, onPersist = ::persist)
                    Section.ASSESSMENTS -> AssessmentsScreen(clients.toList(), assessmentRepository)
                    Section.EVOLUTION -> PlaceholderScreen("Evolução", "Gráficos e relatórios por período")
                    Section.BILLING -> PlaceholderScreen("Cobranças", "Recebimentos, pendências e histórico financeiro")
                    Section.SETTINGS -> SettingsScreen(darkMode, onDarkModeChange)
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(clientCount: Int, onNavigate: (Section) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Área de trabalho", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold)
            Text("Projeto Android nativo • etapa inicial", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Clientes cadastrados", style = MaterialTheme.typography.titleMedium)
                    Text("$clientCount", style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Button(onClick = { onNavigate(Section.CLIENTS) }) { Text("Gerenciar clientes") }
                }
            }
        }
        item { FeatureCard("Avaliações", "Módulo em desenvolvimento", { onNavigate(Section.ASSESSMENTS) }) }
        item { FeatureCard("Evolução", "Módulo em desenvolvimento", { onNavigate(Section.EVOLUTION) }) }
        item { FeatureCard("Cobranças", "Módulo em desenvolvimento", { onNavigate(Section.BILLING) }) }
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, action: () -> Unit) {
    Card(onClick = action, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ClientsScreen(clients: List<Client>, saving: Boolean, onPersist: (List<Client>) -> Unit) {
    var editorOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Client?>(null) }
    var deleting by remember { mutableStateOf<Client?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${clients.size} cadastro(s)", style = MaterialTheme.typography.titleMedium)
            Button(enabled = !saving, onClick = { editing = null; editorOpen = true }) { Text("Novo cliente") }
        }
        Spacer(Modifier.height(12.dp))
        if (clients.isEmpty()) {
            Text("Nenhum cliente cadastrado. Use Novo cliente para começar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(clients, key = { it.id }) { client ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(client.name, style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(enabled = !saving, onClick = { editing = client; editorOpen = true }) {
                                    Text("Editar")
                                }
                                TextButton(enabled = !saving, onClick = { deleting = client }) {
                                    Text("Excluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (editorOpen) {
        ClientEditorDialog(
            initialName = editing?.name ?: "",
            onDismiss = { editorOpen = false },
            onSave = { name ->
                val chosen = editing
                val updated = if (chosen == null) clients + Client.new(name)
                    else clients.map { if (it.id == chosen.id) it.copy(name = name.trim()) else it }
                onPersist(updated)
                editorOpen = false
            }
        )
    }
    deleting?.let { client ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Excluir cliente?") },
            text = { Text("Deseja excluir ${client.name}? Esta ação não poderá ser desfeita nesta etapa.") },
            confirmButton = {
                Button(onClick = { onPersist(clients.filterNot { it.id == client.id }); deleting = null }) {
                    Text("Excluir")
                }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ClientEditorDialog(initialName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    val error = ClientRules.validateName(name)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialName.isEmpty()) "Novo cliente" else "Editar cliente") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Nome") }, singleLine = true, isError = error != null,
                    modifier = Modifier.fillMaxWidth())
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { Button(enabled = error == null, onClick = { onSave(name.trim()) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun PlaceholderScreen(name: String, details: String) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(details, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Ainda não implementado nesta versão inicial.", color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun CenterMessage(message: String) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SettingsScreen(darkMode: Boolean, onDarkModeChange: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Modo escuro", style = MaterialTheme.typography.titleMedium)
                Text("Preferência salva no dispositivo", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
        }
        Text("Segurança: clientes e avaliações cifrados com Android Keystore (AES-GCM).",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Sem conexão com Google Drive. Sem backup/restauração nesta etapa.",
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Versão 0.2.0-alpha", style = MaterialTheme.typography.bodySmall)
    }
}
