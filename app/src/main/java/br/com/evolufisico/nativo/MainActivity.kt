package br.com.evolufisico.nativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val Graphite = Color(0xFF263238)
private val Teal = Color(0xFF168A7A)
private val Mint = Color(0xFFDDF3ED)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dark by rememberSaveable { mutableStateOf(false) }
            EvoluFisicoTheme(dark = dark) {
                EvoluFisicoApp(
                    dark = dark,
                    onDarkChange = { dark = it }
                )
            }
        }
    }
}

@Composable
private fun EvoluFisicoTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) {
        darkColorScheme(
            primary = Color(0xFF79D2BE),
            secondary = Color(0xFF9CCFC4),
            background = Color(0xFF111719),
            surface = Color(0xFF1B2427)
        )
    } else {
        lightColorScheme(
            primary = Teal,
            secondary = Graphite,
            background = Color(0xFFF5F7F6),
            surface = Color.White
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}

private data class Destination(
    val title: String,
    val icon: ImageVector
)

private val mainDestinations = listOf(
    Destination("Início", Icons.Default.Home),
    Destination("Alunos", Icons.Default.Groups),
    Destination("Agenda", Icons.Default.CalendarMonth),
    Destination("Mais", Icons.Default.MoreHoriz)
)

private val modules = listOf(
    "Perfil Unificado",
    "Avaliações",
    "Evolução",
    "Calculadora",
    "Formulários",
    "Análises",
    "Cobranças",
    "Tarefas",
    "Configurações"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvoluFisicoApp(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var currentModule by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("EvoluFísico", fontWeight = FontWeight.SemiBold)
                        Text(
                            currentModule ?: mainDestinations[selected].title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = if (selected == 3) Icons.Default.Menu else Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                mainDestinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selected == index && currentModule == null,
                        onClick = {
                            selected = index
                            currentModule = null
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.title) },
                        label = { Text(destination.title) }
                    )
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                currentModule == "Configurações" ->
                    SettingsScreen(dark = dark, onDarkChange = onDarkChange)
                currentModule != null ->
                    ModulePlaceholder(
                        title = currentModule.orEmpty(),
                        onBack = { currentModule = null }
                    )
                selected == 0 ->
                    HomeScreen(onOpen = { currentModule = it })
                selected == 1 ->
                    ModuleListScreen(title = "Alunos", onOpen = { currentModule = "Perfil Unificado" })
                selected == 2 ->
                    ModulePlaceholder(title = "Agenda", onBack = { selected = 0 })
                else ->
                    ModuleListScreen(title = "Módulos", onOpen = { currentModule = it })
            }
        }
    }
}

@Composable
private fun HomeScreen(onOpen: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Olá, profissional", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Sua rotina de atendimentos em um só lugar.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Atendimentos", "—", "Hoje", Modifier.weight(1f))
                MetricCard("Reavaliações", "—", "Acompanhar", Modifier.weight(1f))
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Agenda de hoje", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Seus próximos atendimentos aparecerão aqui.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { onOpen("Agenda") }) {
                        Text("Abrir agenda")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Central de atenção", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()
                    Text("Nenhuma pendência para mostrar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Text("Acesso rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction("Perfil Unificado", Icons.Default.Person, Modifier.weight(1f), onOpen)
                QuickAction("Cobranças", Icons.Default.FitnessCenter, Modifier.weight(1f), onOpen)
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, detail: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickAction(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onOpen: (String) -> Unit
) {
    Card(
        modifier = modifier,
        onClick = { onOpen(title) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ModuleListScreen(title: String, onOpen: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Acesse as áreas de trabalho do EvoluFísico 3.0.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(modules) { module ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpen(module) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(module, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ModulePlaceholder(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Esta área será portada do EvoluFisico2 para a versão nativa em etapas.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onBack) { Text("Voltar") }
    }
}

@Composable
private fun SettingsScreen(dark: Boolean, onDarkChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Aparência", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (dark) "Modo escuro" else "Modo claro")
            androidx.compose.material3.Switch(checked = dark, onCheckedChange = onDarkChange)
        }
        HorizontalDivider()
        Text("Dados e privacidade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            "Backup, restauração e armazenamento local serão implementados na etapa de dados.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
