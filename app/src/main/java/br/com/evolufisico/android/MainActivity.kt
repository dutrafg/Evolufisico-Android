package br.com.evolufisico.android

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.ClientSearch
import br.com.evolufisico.android.data.ClientRules
import br.com.evolufisico.android.data.EncryptedClientStore
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import br.com.evolufisico.android.billing.EncryptedChargeStore
import br.com.evolufisico.android.forms.EncryptedFormStore
import br.com.evolufisico.android.ops.OperationalStore
import br.com.evolufisico.android.agenda.AgendaHome
import br.com.evolufisico.android.agenda.UpcomingAppointment
import br.com.evolufisico.android.backup.SecureBackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

private val corporateShapes=Shapes(
    extraSmall=RoundedCornerShape(6.dp),
    small=RoundedCornerShape(10.dp),
    medium=RoundedCornerShape(12.dp),
    large=RoundedCornerShape(16.dp),
    extraLarge=RoundedCornerShape(18.dp)
)

private enum class Section(val title: String, val icon: ImageVector) {
    HOME("Início", Icons.Outlined.Home),
    MODULES("Módulos", Icons.Outlined.Apps),
    CLIENTS("Clientes", Icons.Outlined.People),
    ASSESSMENTS("Avaliações", Icons.Outlined.Assessment),
    FORMS("Formulários", Icons.Outlined.Description),
    AGENDA("Agenda", Icons.Outlined.CalendarMonth),
    GOALS("Metas", Icons.Outlined.Flag),
    SERVICES("Serviços", Icons.Outlined.Storefront),
    REPORTS("Relatórios", Icons.Outlined.Description),
    INSIGHTS("Indicadores", Icons.Outlined.TrendingUp),
    CLIENT_SUMMARY("Resumo por cliente", Icons.Outlined.People),
    TRENDS("Comparativos", Icons.Outlined.Assessment),
    SEARCH("Busca global", Icons.Outlined.Search),
    NOTES("Anotações", Icons.Outlined.Description),
    EVOLUTION("Evolução", Icons.Outlined.TrendingUp),
    BILLING("Cobranças", Icons.Outlined.ReceiptLong),
    SETTINGS("Configurações", Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private lateinit var session: DeviceSessionGate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        val currentVersion = packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
        if (ScreenshotPolicy.shouldBlock(currentVersion)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        session = DeviceSessionGate(this)
        val repository = EncryptedClientStore(applicationContext)
        val assessmentRepository = EncryptedAssessmentStore(applicationContext)
        val chargeRepository = EncryptedChargeStore(applicationContext)
        val formRepository = EncryptedFormStore(applicationContext)
        val operationalRepository = OperationalStore(applicationContext)
        val backupManager = SecureBackupManager(applicationContext)
        val themePrefs = getSharedPreferences("ui_preferences", MODE_PRIVATE)
        setContent {
            var darkMode by remember { mutableStateOf(themePrefs.getBoolean("dark_mode", false)) }
            var paletteId by remember {
                mutableStateOf(themePrefs.getString("palette_color", "azul") ?: "azul")
            }
            MaterialTheme(
                colorScheme=CorporatePalettes.scheme(paletteId,darkMode),
                shapes=corporateShapes,
                typography=CorporateTypography
            ) {
                if (!session.unlocked) {
                    DeviceGateScreen(session.message,
                        onUnlock = { session.requestUnlock() },
                        onSettings = { session.openSecuritySettings() })
                } else {
                AppContent(
                    repository = repository,
                    assessmentRepository = assessmentRepository,
                    chargeRepository = chargeRepository,
                    formRepository = formRepository,
                    operationalRepository = operationalRepository,
                    backupManager = backupManager,
                    onDocumentFlow = { session.externalDocumentFlow(it) },
                    onLock = { session.lock() },
                    darkMode = darkMode,
                    onDarkModeChange = {
                        darkMode = it
                        themePrefs.edit().putBoolean("dark_mode", it).apply()
                    },
                    paletteId=paletteId,
                    onPaletteChange={
                        paletteId=it
                        themePrefs.edit().putString("palette_color",it).apply()
                    }
                )
                }
            }
        }
    }
    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) session.backgrounded()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(
    repository: EncryptedClientStore,
    assessmentRepository: EncryptedAssessmentStore,
    chargeRepository: EncryptedChargeStore,
    formRepository: EncryptedFormStore,
    operationalRepository: OperationalStore,
    backupManager: SecureBackupManager,
    onDocumentFlow: (Boolean) -> Unit,
    onLock: () -> Unit,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    paletteId:String,
    onPaletteChange:(String)->Unit
) {
    var current by remember { mutableStateOf(Section.HOME) }
    var quickLaunch by remember { mutableStateOf<QuickLaunch?>(null) }
    var quickSequence by remember { mutableStateOf(0) }
    fun launchFavorite(route:String) {
        val destination=Section.entries.firstOrNull { it.name==route } ?: return
        quickSequence++
        quickLaunch=if(QuickLaunchRules.supportsCreate(route))
            QuickLaunch(route,quickSequence) else null
        current=destination
    }
    fun clearQuickLaunch() { quickLaunch=null }
    val appContext=LocalContext.current
    val shortcutPrefs=remember(appContext) {
        appContext.getSharedPreferences("module_shortcuts",android.content.Context.MODE_PRIVATE)
    }
    var favorites by remember(shortcutPrefs) {
        mutableStateOf(WorkspaceFavorites.parse(shortcutPrefs.getString("routes","").orEmpty()))
    }
    fun updateFavorites(next:Set<String>) {
        favorites=next
        shortcutPrefs.edit().putString("routes",WorkspaceFavorites.serialize(next)).apply()
    }
    val clients = remember { mutableStateListOf<Client>() }
    var loading by remember { mutableStateOf(true) }
    var storageError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val useRail = LocalConfiguration.current.screenWidthDp >= 840
    fun goBack() {
        clearQuickLaunch()
        current=Section.valueOf(WorkspaceNavigation.backTarget(current.name))
    }
    BackHandler(enabled=current!=Section.HOME) { goBack() }

    LaunchedEffect(repository) {
        val result = withContext(Dispatchers.IO) { runCatching {
            backupManager.recoverInterruptedRestore()
            backupManager.summary()
            repository.load()
        } }
        result.onSuccess { clients.addAll(it) }
            .onFailure { storageError = "Não foi possível abrir os dados locais: ${it.message}" }
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
                        (
                            assessmentRepository.load().any { it.clientId in removedIds } ||
                            chargeRepository.load().any { it.clientId in removedIds } ||
                            formRepository.load().any { it.clientId in removedIds } ||
                            operationalRepository.load().appointments.any { it.clientId in removedIds } ||
                            operationalRepository.load().goals.any { it.clientId in removedIds } ||
                            operationalRepository.load().notes.any { it.clientId in removedIds }
                        )) {
                        throw IllegalArgumentException(
                            "Cliente possui registros vinculados em avaliações, formulários, agenda, metas ou cobranças. Exclua-os antes de remover o cadastro."
                        )
                    }
                    repository.save(next)
                }
            }
            result.onSuccess {
                clients.clear()
                clients.addAll(next)
                snackbar.showSnackbar("Dados salvos neste dispositivo.")
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
        containerColor=MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (current == Section.HOME) "EvoluFísico" else current.title,
                    fontWeight = FontWeight.Bold,
                    color=MaterialTheme.colorScheme.primary,
                    maxLines=1,overflow=TextOverflow.Ellipsis) },
                colors=TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor=MaterialTheme.colorScheme.surface),
                navigationIcon = {
                    if(current!=Section.HOME) IconButton(onClick=::goBack) {
                        Icon(Icons.Outlined.ArrowBack,contentDescription="Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        clearQuickLaunch()
                        current = Section.SETTINGS
                    }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        bottomBar = {
            if (!useRail) NavigationBar(
                containerColor=MaterialTheme.colorScheme.surface,
                tonalElevation=2.dp
            ) {
                listOf(Section.HOME, Section.CLIENTS, Section.AGENDA,
                    Section.BILLING, Section.MODULES).forEach { section ->
                    NavigationBarItem(
                        selected = WorkspaceNavigation.bottomSelection(current.name)==section.name,
                        onClick = { clearQuickLaunch(); current = section },
                        icon = { Icon(section.icon, contentDescription = null) },
                        label = { Text(section.title, maxLines = 1) },
                        colors=NavigationBarItemDefaults.colors(
                            indicatorColor=MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { insets ->
        Row(modifier = Modifier.fillMaxSize().padding(insets)) {
            if (useRail) {
                NavigationRail(containerColor=MaterialTheme.colorScheme.surface) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                    listOf(Section.HOME, Section.CLIENTS, Section.AGENDA,
                        Section.BILLING, Section.MODULES, Section.SETTINGS).forEach { section ->
                        NavigationRailItem(
                            selected = current == section,
                            onClick = { clearQuickLaunch(); current = section },
                            icon = { Icon(section.icon, contentDescription = section.title) },
                            label = { Text(section.title, maxLines = 1) },
                            colors=NavigationRailItemDefaults.colors(
                                indicatorColor=MaterialTheme.colorScheme.primaryContainer)
                        )
                    }
                    }
                }
            }
            Surface(modifier = Modifier.weight(1f).fillMaxSize(),
                color=MaterialTheme.colorScheme.background) {
            if (loading) {
                CenterMessage("Abrindo dados locais...")
            } else if (storageError != null) {
                CenterMessage(storageError ?: "Erro ao acessar dados")
            } else {
                when (current) {
                    Section.HOME -> HomeScreen(clients.size, backupManager,
                        favorites,::updateFavorites,onNavigate = { launchFavorite(it.name) })
                    Section.MODULES -> ModuleDirectoryScreen(favorites,::updateFavorites) { key ->
                        clearQuickLaunch()
                        Section.entries.firstOrNull { it.name == key }?.let { current = it }
                    }
                    Section.CLIENTS -> ClientsScreen(clients.toList(), saving, backupManager,
                        onDocumentFlow, onPersist = ::persist)
                    Section.ASSESSMENTS -> AssessmentsScreen(clients.toList(),assessmentRepository,
                        quickCreateId=quickLaunch?.takeIf { it.route=="ASSESSMENTS" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.FORMS -> FormsScreen(clients.toList(),formRepository,onDocumentFlow,
                        quickCreateId=quickLaunch?.takeIf { it.route=="FORMS" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.AGENDA -> OperationsScreen(clients.toList(),operationalRepository,
                        OperationsTab.AGENDA,onDocumentFlow,
                        quickCreateId=quickLaunch?.takeIf { it.route=="AGENDA" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.GOALS -> OperationsScreen(clients.toList(),operationalRepository,OperationsTab.GOALS,
                        quickCreateId=quickLaunch?.takeIf { it.route=="GOALS" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.SERVICES -> OperationsScreen(clients.toList(),operationalRepository,OperationsTab.SERVICES,
                        quickCreateId=quickLaunch?.takeIf { it.route=="SERVICES" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.REPORTS -> ReportsScreen(clients.toList(), backupManager, onDocumentFlow)
                    Section.INSIGHTS -> InsightsScreen(backupManager,
                        onAgenda = { current = Section.AGENDA },
                        onBilling = { current = Section.BILLING },
                        onGoals = { current = Section.GOALS })
                    Section.CLIENT_SUMMARY -> ClientOperationsScreen(backupManager,
                        onClients = { current = Section.CLIENTS },
                        onDocumentFlow = onDocumentFlow,
                        onAgenda = { current = Section.AGENDA },
                        onBilling = { current = Section.BILLING },
                        onGoals = { current = Section.GOALS })
                    Section.TRENDS -> TrendsScreen(clients.toList(), backupManager)
                    Section.SEARCH -> GlobalSearchScreen(backupManager, onOpen = { key ->
                        Section.entries.firstOrNull { it.name == key }?.let {
                            clearQuickLaunch()
                            current = it
                        }
                    })
                    Section.NOTES -> OperationsScreen(clients.toList(),operationalRepository,OperationsTab.NOTES,
                        quickCreateId=quickLaunch?.takeIf { it.route=="NOTES" }?.id?:0,
                        onQuickConsumed=::clearQuickLaunch)
                    Section.EVOLUTION -> EvolutionScreen(clients.toList(), assessmentRepository, onDocumentFlow)
                    Section.BILLING -> BillingScreen(clients.toList(), chargeRepository, operationalRepository, onDocumentFlow)
                    Section.SETTINGS -> DataSettingsScreen(
                        darkMode, onDarkModeChange, paletteId,onPaletteChange,
                        backupManager,onDocumentFlow,onLock
                    ) { snapshot ->
                        clients.clear()
                        clients.addAll(snapshot.clients)
                        current = Section.HOME
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun HomeScreen(clientCount: Int,backupManager:SecureBackupManager,
    favoriteRoutes:Set<String>,onFavoritesChange:(Set<String>)->Unit,
    onNavigate:(Section)->Unit) {
    WorkspaceHomeScreen(clientCount,backupManager,favoriteRoutes,onFavoritesChange) { key ->
        Section.entries.firstOrNull { it.name == key }?.let(onNavigate)
    }
}

@Composable
private fun ClientsScreen(
    clients: List<Client>, saving: Boolean, backupManager: SecureBackupManager,
    onDocumentFlow: (Boolean) -> Unit, onPersist: (List<Client>) -> Unit
) {
    var editorOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Client?>(null) }
    var deleting by remember { mutableStateOf<Client?>(null) }
    var overview by remember { mutableStateOf<Client?>(null) }
    var query by remember { mutableStateOf("") }
    var descending by remember { mutableStateOf(false) }
    val visibleClients = clients.filter {
        ClientSearch.matches(it,query)
    }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
        .let { if (descending) it.reversed() else it }
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    Column(Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp)
        .padding(horizontal=VisualLayout.edgePaddingDp(LocalConfiguration.current.screenWidthDp).dp,vertical=24.dp),
        verticalArrangement=Arrangement.spacedBy(14.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (maxWidth < 420.dp) {
                Column(Modifier.fillMaxWidth(),
                    verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("${clients.size} cadastro(s)", style=MaterialTheme.typography.titleMedium)
                    Button(enabled=!saving,onClick={editing=null;editorOpen=true},
                        modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
                        Text("+ Novo cliente")
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
                    horizontalArrangement=Arrangement.SpaceBetween) {
                    Text("${clients.size} cadastro(s)",style=MaterialTheme.typography.titleMedium)
                    Button(enabled=!saving,onClick={editing=null;editorOpen=true},
                        modifier=Modifier.heightIn(min=48.dp)) { Text("+ Novo cliente") }
                }
            }
        }
        OutlinedTextField(value = query, onValueChange = { query = it.take(100) },
            label = { Text("Buscar por nome, telefone ou e-mail") },
            modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !descending, onClick = { descending = false },
                label = { Text("A–Z") })
            FilterChip(selected = descending, onClick = { descending = true },
                label = { Text("Z–A") })
            Text("${visibleClients.size} encontrados",
                modifier = Modifier.padding(top = 14.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (clients.isEmpty()) {
            Text("Nenhum cliente cadastrado. Use Novo cliente para começar.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else if (visibleClients.isEmpty()) {
            Text("Nenhum cliente corresponde à busca.")
        } else {
            LazyColumn(modifier=Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(visibleClients, key = { it.id }) { client ->
                    Card(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
                        elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,
                                horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                                Box(Modifier.heightIn(min=44.dp).widthIn(min=44.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape),contentAlignment=Alignment.Center) {
                                    Text(WorkspaceDisplay.initials(client.name),
                                        color=MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight=FontWeight.Bold)
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(client.name,style=MaterialTheme.typography.titleMedium)
                                    Text("Cadastro individual",style=MaterialTheme.typography.bodySmall,
                                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if(client.phone.isNotBlank()) {
                                Text("Telefone: "+client.phone,
                                    style=MaterialTheme.typography.bodySmall,
                                    color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if(client.email.isNotBlank()) {
                                Text("E-mail: "+client.email,
                                    style=MaterialTheme.typography.bodySmall,
                                    color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { overview = client }) {
                                    Text("Ficha")
                                }
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
    }
    if (editorOpen) {
        ClientEditorDialog(
            initial=editing,
            onDismiss={ editorOpen=false },
            onSave={ item ->
                val updated=if(editing==null) clients+item else
                    clients.map { if(it.id==item.id) item else it }
                onPersist(updated)
                editorOpen=false
            }
        )
    }
    overview?.let { client ->
        ClientOverviewDialog(client, backupManager, onDocumentFlow, onDismiss = { overview = null })
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
private fun ClientEditorDialog(
    initial:Client?,onDismiss:()->Unit,onSave:(Client)->Unit
) {
    var name by remember(initial?.id) { mutableStateOf(initial?.name.orEmpty()) }
    var phone by remember(initial?.id) { mutableStateOf(initial?.phone.orEmpty()) }
    var email by remember(initial?.id) { mutableStateOf(initial?.email.orEmpty()) }
    var birthDate by remember(initial?.id) { mutableStateOf(initial?.birthDateIso.orEmpty()) }
    val candidate=if(initial==null)
        Client("__temporary__",name.trim(),phone.trim(),email.trim(),birthDate.trim())
        else initial.copy(name=name.trim(),phone=phone.trim(),
            email=email.trim(),birthDateIso=birthDate.trim())
    val error=ClientRules.validateContact(candidate)
    AlertDialog(
        onDismissRequest=onDismiss,
        title={Text(if(initial==null) "Novo cliente" else "Editar cliente")},
        text={
            Column(Modifier.heightIn(max=420.dp).verticalScroll(rememberScrollState()),
                verticalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value=name,onValueChange={name=it.take(90)},
                    label={Text("Nome completo")},singleLine=true,
                    isError=error!=null && name.isBlank(),
                    modifier=Modifier.fillMaxWidth())
                OutlinedTextField(value=phone,onValueChange={phone=it.take(32)},
                    label={Text("Telefone (opcional)")},singleLine=true,
                    modifier=Modifier.fillMaxWidth())
                OutlinedTextField(value=email,onValueChange={email=it.take(120)},
                    label={Text("E-mail (opcional)")},singleLine=true,
                    modifier=Modifier.fillMaxWidth())
                OutlinedTextField(value=birthDate,
                    onValueChange={birthDate=it.take(10)},
                    label={Text("Nascimento (AAAA-MM-DD, opcional)")},
                    singleLine=true,modifier=Modifier.fillMaxWidth())
                if(error!=null) Text(error,color=MaterialTheme.colorScheme.error,
                    style=MaterialTheme.typography.bodySmall)
                Text("Os dados permanecem vinculados ao ID permanente do cliente.",
                    style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton={
            Button(enabled=error==null,onClick={
                val item=if(initial==null)
                    Client.new(candidate.name,candidate.phone,candidate.email,candidate.birthDateIso)
                    else candidate
                onSave(item)
            }){Text("Salvar")}
        },
        dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
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

