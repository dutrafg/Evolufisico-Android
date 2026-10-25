package br.com.evolufisico.nativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.evolufisico.nativo.data.Assessment
import br.com.evolufisico.nativo.data.ChargeRecord
import br.com.evolufisico.nativo.data.IntakeForm
import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.AssessmentMath
import br.com.evolufisico.nativo.data.LocalDatabase
import br.com.evolufisico.nativo.data.Student
import br.com.evolufisico.nativo.data.TaskRecord
import androidx.core.view.WindowCompat
import java.text.SimpleDateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Graphite = Color(0xFF1C1B1F)
private val Teal = Color(0xFF107454)
private val Fog = Color(0xFFF2F7F4)
private val Muted = Color(0xFF575A5F)
private val accents = listOf(
    "Verde-petróleo" to Color(0xFF107454),
    "Azul" to Color(0xFF2563EB),
    "Índigo" to Color(0xFF4F46E5),
    "Roxo" to Color(0xFF7C3AED),
    "Vinho" to Color(0xFF9F1239),
    "Vermelho" to Color(0xFFDC2626),
    "Laranja" to Color(0xFFC2410C),
    "Âmbar" to Color(0xFFB45309),
    "Oliva" to Color(0xFF4D7C0F),
    "Grafite" to Color(0xFF475569)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("appearance", MODE_PRIVATE)
        setContent {
            var dark by rememberSaveable { mutableStateOf(preferences.getBoolean("dark", false)) }
            var accentIndex by rememberSaveable { mutableIntStateOf(preferences.getInt("accent", 0).coerceIn(0, accents.lastIndex)) }
            SideEffect {
                val bars = WindowCompat.getInsetsController(window, window.decorView)
                bars.isAppearanceLightStatusBars = !dark
                bars.isAppearanceLightNavigationBars = !dark
            }
            EvoluFisicoTheme(dark = dark, accentIndex = accentIndex) {
                EvoluFisicoApp(
                    dark = dark,
                    accentIndex = accentIndex,
                    onDarkChange = {
                        dark = it
                        preferences.edit().putBoolean("dark", it).apply()
                    },
                    onAccentChange = {
                        accentIndex = it
                        preferences.edit().putInt("accent", it).apply()
                    }
                )
            }
        }
    }
}

@Composable
private fun EvoluFisicoTheme(dark: Boolean, accentIndex: Int, content: @Composable () -> Unit) {
    val colors = if (dark) {
        darkColorScheme(
            primary = accents[accentIndex].second.copy(alpha = 0.9f),
            secondary = accents[accentIndex].second,
            background = Color(0xFF07100D),
            surface = Color(0xFF142D31),
            surfaceVariant = Color(0xFF203A45)
        )
    } else {
        lightColorScheme(
            primary = accents[accentIndex].second,
            secondary = Graphite,
            background = Fog,
            surface = Color.White
        )
    }
    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp)
        ),
        content = content
    )
}

private data class Destination(val title: String, val icon: ImageVector)

private val destinations = listOf(
    Destination("Início", Icons.Default.Home),
    Destination("Alunos", Icons.Default.Groups),
    Destination("Agenda", Icons.Default.CalendarMonth),
    Destination("Módulos", Icons.Default.MoreHoriz)
)

private val moduleNames = listOf(
    "Início",
    "Perfil Unificado",
    "Avaliações",
    "Evolução",
    "Calculadora",
    "Anamnese",
    "Análises Completas",
    "Cobranças",
    "Agenda",
    "Tarefas",
    "Configurações"
)

private val primaryModuleNames = listOf(
    "Início",
    "Perfil Unificado",
    "Avaliações",
    "Evolução",
    "Calculadora",
    "Anamnese",
    "Análises Completas",
    "Cobranças",
    "Configurações"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EvoluFisicoApp(dark: Boolean, accentIndex: Int, onDarkChange: (Boolean) -> Unit, onAccentChange: (Int) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { LocalDatabase(context) }
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var showModulePicker by rememberSaveable { mutableStateOf(false) }
    var activeModule by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStudentId by rememberSaveable { mutableStateOf<Long?>(null) }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var profileAssessments by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    var allAssessments by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var tasks by remember { mutableStateOf<List<TaskRecord>>(emptyList()) }
    var forms by remember { mutableStateOf<List<IntakeForm>>(emptyList()) }
    var charges by remember { mutableStateOf<List<ChargeRecord>>(emptyList()) }
    var counts by remember { mutableStateOf(0 to 0) }
    var studentsWithoutAssessment by remember { mutableIntStateOf(0) }
    var reevaluationsDue by remember { mutableIntStateOf(0) }
    val preferences = remember(context) { context.getSharedPreferences("appearance", android.content.Context.MODE_PRIVATE) }
    var favoriteModules by remember {
        mutableStateOf(preferences.getStringSet("favorite_modules", emptySet()).orEmpty().toSet())
    }
    var maskFinancial by remember { mutableStateOf(preferences.getBoolean("mask_financial", false)) }
    var reevaluationIntervalDays by remember { mutableIntStateOf(preferences.getInt("reevaluation_interval_days", 90).coerceIn(30, 90)) }
    fun toggleFavorite(module: String) {
        val next = favoriteModules.toMutableSet()
        if (!next.add(module)) next.remove(module)
        favoriteModules = next
        preferences.edit().putStringSet("favorite_modules", next).apply()
    }
    var appointmentDialog by remember { mutableStateOf(false) }
    var editingAppointment by remember { mutableStateOf<Appointment?>(null) }
    var taskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskRecord?>(null) }
    var chargeDialog by remember { mutableStateOf(false) }
    var intakeDialog by remember { mutableStateOf(false) }
    var studentDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var assessmentDialog by remember { mutableStateOf(false) }
    var restoreUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val exportBackup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { it.write(database.exportBackupJson().toByteArray(Charsets.UTF_8)) }
                        ?: error("Não foi possível criar o arquivo.")
                }
            } catch (e: Exception) { errorMessage = e.message ?: "Falha ao exportar o backup." }
        }
    }
    val importBackup = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) restoreUri = uri
    }

    fun refresh() {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                listOf(database.students(), database.counts(), selectedStudentId?.let(database::assessments).orEmpty(), database.appointments(), database.studentsWithoutAssessment(), database.reevaluationsDue(reevaluationIntervalDays), database.tasks(), database.intakeForms(), database.charges(), database.allAssessments())
            }
            students = result[0] as List<Student>
            counts = result[1] as Pair<Int, Int>
            profileAssessments = result[2] as List<Assessment>
            appointments = result[3] as List<Appointment>
            studentsWithoutAssessment = result[4] as Int
            reevaluationsDue = result[5] as Int
            tasks = result[6] as List<TaskRecord>
            forms = result[7] as List<IntakeForm>
            charges = result[8] as List<ChargeRecord>
            allAssessments = result[9] as List<Assessment>
        }
    }

    LaunchedEffect(reevaluationIntervalDays) { refresh() }
    LaunchedEffect(selectedStudentId) {
        profileAssessments = if (selectedStudentId == null) emptyList()
        else withContext(Dispatchers.IO) { database.assessments(selectedStudentId!!) }
    }

    val selectedStudent = students.firstOrNull { it.id == selectedStudentId }
    val title = when {
        selectedStudent != null -> selectedStudent.name
        activeModule != null -> when (activeModule) { "Formulários" -> "Anamnese"; "Análises" -> "Análises Completas"; else -> activeModule.orEmpty() }
        else -> destinations[selected].title
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("EvoluFísico", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    if (selectedStudentId != null || activeModule != null) {
                        IconButton(onClick = {
                            if (selectedStudentId != null) selectedStudentId = null else { activeModule = null; selected = 0 }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    } else {
                        Surface(
                            modifier = Modifier.padding(horizontal = 12.dp).size(36.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("EF", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            )
        },
        // A navegação por abas foi substituída pelo seletor compacto de módulos.
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.widthIn(max = 600.dp).fillMaxSize()) {
                    Box(Modifier.fillMaxSize().padding(bottom = 70.dp)) {
                    when {
                selectedStudent != null -> ProfileScreen(
                    student = selectedStudent,
                    assessments = profileAssessments,
                    onAddAssessment = { assessmentDialog = true },
                    onDeleteStudent = {
                        scope.launch {
                            withContext(Dispatchers.IO) { database.deleteStudent(selectedStudent.id) }
                            selectedStudentId = null
                            refresh()
                        }
                    },
                    onDeleteAssessment = { id ->
                        scope.launch {
                            withContext(Dispatchers.IO) { database.deleteAssessment(id) }
                            refresh()
                        }
                    }
                )
                activeModule == "Configurações" -> SettingsScreen(
                    dark = dark,
                    accentIndex = accentIndex,
                    onAccentChange = onAccentChange,
                    onDarkChange = onDarkChange,
                    maskFinancial = maskFinancial,
                    onMaskFinancialChange = { maskFinancial = it; preferences.edit().putBoolean("mask_financial", it).apply() },
                    reevaluationIntervalDays = reevaluationIntervalDays,
                    onReevaluationIntervalChange = { days -> reevaluationIntervalDays = days; preferences.edit().putInt("reevaluation_interval_days", days).apply() },
                    onBackup = { exportBackup.launch("EvoluFisico-Backup.json") },
                    onRestore = { importBackup.launch(arrayOf("application/json", "text/plain")) }
                )
                activeModule == "Perfil Unificado" || selected == 1 -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { editingStudent = null; studentDialog = true },
                    onEdit = { editingStudent = it; studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null }
                )
                activeModule == "Avaliações" -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { editingStudent = null; studentDialog = true },
                    onEdit = { editingStudent = it; studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null },
                    subtitle = "Escolha um aluno para registrar uma avaliação."
                )
                activeModule == "Evolução" -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { editingStudent = null; studentDialog = true },
                    onEdit = { editingStudent = it; studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null },
                    subtitle = "Abra o perfil para consultar o histórico de avaliações."
                )
                activeModule == "Calculadora" -> CalculatorScreen()
                activeModule == "Formulários" || activeModule == "Anamnese" -> FormsScreen(forms, students, { intakeDialog = true }, { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteIntakeForm(id) }; refresh() } })
                activeModule == "Cobranças" -> ChargeScreen(charges, students, maskFinancial, { chargeDialog = true }, { id -> scope.launch { withContext(Dispatchers.IO) { database.markChargeReceived(id) }; refresh() } }, { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteCharge(id) }; refresh() } })
                activeModule == "Tarefas" -> TaskScreen(
                    tasks = tasks,
                    onAdd = { editingTask = null; taskDialog = true },
                    onEdit = { editingTask = it; taskDialog = true },
                    onToggle = { id, done -> scope.launch { withContext(Dispatchers.IO) { database.setTaskCompleted(id, done) }; refresh() } },
                    onDelete = { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteTask(id) }; refresh() } }
                )
                activeModule == "Análises" || activeModule == "Análises Completas" -> AnalyticsScreen(students, allAssessments, onOpenStudent = { selectedStudentId = it })
                activeModule == "Agenda" || selected == 2 -> AgendaScreen(
                    appointments = appointments,
                    onAdd = { editingAppointment = null; appointmentDialog = true },
                    onEdit = { editingAppointment = it; appointmentDialog = true },
                    onDelete = { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteAppointment(id) }; refresh() } }
                )
                activeModule != null -> ModulePlaceholder(activeModule.orEmpty())
                selected == 0 -> HomeScreen(
                    studentCount = counts.first,
                    studentsWithoutAssessment = studentsWithoutAssessment,
                    reevaluationsDue = reevaluationsDue,
                    reevaluationIntervalDays = reevaluationIntervalDays,
                    students = students,
                    assessments = allAssessments,
                    appointments = appointments,
                    tasks = tasks,
                    charges = charges,
                    maskFinancial = maskFinancial,
                    favoriteModules = favoriteModules.sorted(),
                    onOpen = { activeModule = it },
                    onAddStudent = { studentDialog = true },
                    onAddAppointment = { editingAppointment = null; appointmentDialog = true },
                    onEditAppointment = { editingAppointment = it; appointmentDialog = true },
                    onDeleteAppointment = { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteAppointment(id) }; refresh() } },
                    onAddTask = { editingTask = null; taskDialog = true },
                    onToggleTask = { id, done -> scope.launch { withContext(Dispatchers.IO) { database.setTaskCompleted(id, done) }; refresh() } },
                    onEditTask = { editingTask = it; taskDialog = true },
                    onDeleteTask = { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteTask(id) }; refresh() } },
                    onOpenStudent = { selectedStudentId = it }
                )
                else -> ModuleListScreen(
                    favorites = favoriteModules,
                    onOpen = { activeModule = it },
                    onToggleFavorite = ::toggleFavorite
                )
                    }
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 12.dp).clickable { showModulePicker = true },
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 6.dp
                    ) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                            Text("Módulos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (showModulePicker) {
        Dialog(onDismissRequest = { showModulePicker = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp).heightIn(max = 720.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Módulos do EvoluFísico", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Selecione onde deseja acessar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { showModulePicker = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Fechar")
                        }
                    }
                    Column(
                        modifier = Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        primaryModuleNames.chunked(2).forEach { rowModules ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowModules.forEach { module ->
                                    Card(
                                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                                        onClick = {
                                            showModulePicker = false
                                            selectedStudentId = null
                                            when (module) {
                                                "Início" -> { selected = 0; activeModule = null }
                                                "Perfil Unificado" -> { selected = 1; activeModule = null }
                                                "Anamnese" -> { selected = 3; activeModule = "Formulários" }
                                                "Análises Completas" -> { selected = 3; activeModule = "Análises" }
                                                else -> { selected = 3; activeModule = module }
                                            }
                                        },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(moduleIcon(module), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(21.dp))
                                            Text(module, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                                        }
                                    }
                                }
                                if (rowModules.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text("Acesso rápido aos módulos e ferramentas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    if (appointmentDialog) {
        AppointmentFormDialog(initial = editingAppointment, onDismiss = { appointmentDialog = false; editingAppointment = null }, onSave = { id, title, date, time, location, notes, kind ->
            scope.launch {
                try {
                    withContext(Dispatchers.IO) { database.saveAppointment(id, title, date, time, location, notes, kind) }
                    appointmentDialog = false
                    editingAppointment = null
                    errorMessage = null
                    refresh()
                } catch (exception: Exception) {
                    errorMessage = exception.message ?: "Não foi possível salvar o atendimento."
                }
            }
        })
    }

    if (taskDialog) TaskFormDialog(
        initial = editingTask,
        onDismiss = { taskDialog = false; editingTask = null },
        onSave = { id, title, dueDate -> scope.launch {
            try { withContext(Dispatchers.IO) { database.saveTask(id, title, dueDate, editingTask?.completed ?: false) }; taskDialog = false; editingTask = null; refresh() }
            catch (e: Exception) { errorMessage = e.message ?: "Não foi possível salvar a tarefa." }
        } }
    )

    if (chargeDialog) ChargeFormDialog(
        students = students,
        onDismiss = { chargeDialog = false },
        onSave = { studentId, title, amount, dueDate, notes -> scope.launch {
            try { withContext(Dispatchers.IO) { database.saveCharge(studentId, title, amount, dueDate, notes) }; chargeDialog = false; refresh() }
            catch (e: Exception) { errorMessage = e.message ?: "Não foi possível salvar a cobrança." }
        } }
    )

    if (intakeDialog) IntakeFormDialog(
        students = students,
        onDismiss = { intakeDialog = false },
        onSave = { studentId, template, answers -> scope.launch {
            try { withContext(Dispatchers.IO) { database.saveIntakeForm(studentId, template, todayIso(), answers) }; intakeDialog = false; refresh() }
            catch (e: Exception) { errorMessage = e.message ?: "Não foi possível salvar o formulário." }
        } }
    )

    restoreUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { restoreUri = null },
            title = { Text("Restaurar backup?") },
            text = { Text("A restauração substitui os dados locais atuais por alunos, avaliações, agenda, formulários, tarefas e cobranças presentes no arquivo.") },
            confirmButton = { TextButton(onClick = {
                restoreUri = null
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { database.restoreBackupJson(it.readText()) }
                                ?: error("Não foi possível ler o arquivo.")
                        }
                        refresh()
                    } catch (e: Exception) { errorMessage = e.message ?: "Falha ao restaurar o backup." }
                }
            }) { Text("Restaurar dados") } },
            dismissButton = { TextButton(onClick = { restoreUri = null }) { Text("Cancelar") } }
        )
    }

    if (studentDialog) {
        StudentFormDialog(
            initial = editingStudent,
            onDismiss = { studentDialog = false },
            onSave = { id, name, phone, email, birthDate ->
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            if (id == null) database.saveStudent(name, phone, email, birthDate)
                            else database.updateStudent(id, name, phone, email, birthDate)
                        }
                        studentDialog = false
                        editingStudent = null
                        errorMessage = null
                        refresh()
                    } catch (exception: Exception) {
                        errorMessage = exception.message ?: "Não foi possível salvar o aluno."
                    }
                }
            }
        )
    }

    if (assessmentDialog && selectedStudent != null) {
        AssessmentFormDialog(
            studentName = selectedStudent.name,
            onDismiss = { assessmentDialog = false },
            onSave = { date, weight, height, waist, hip, bodyFat, neck, chest, arm, thigh, notes ->
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            database.saveAssessment(selectedStudent.id, date, weight, height, waist, hip, notes, bodyFat, neck, chest, arm, thigh)
                        }
                        assessmentDialog = false
                        errorMessage = null
                        refresh()
                    } catch (exception: Exception) {
                        errorMessage = exception.message ?: "Não foi possível salvar a avaliação."
                    }
                }
            }
        )
    }

    errorMessage?.let { message ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Não foi possível concluir") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("Entendi") } }
        )
    }
}

@Composable
private fun HomeScreen(
    studentCount: Int,
    studentsWithoutAssessment: Int,
    reevaluationsDue: Int,
    reevaluationIntervalDays: Int,
    students: List<Student>,
    assessments: List<Assessment>,
    appointments: List<Appointment>,
    tasks: List<TaskRecord>,
    charges: List<ChargeRecord>,
    maskFinancial: Boolean,
    favoriteModules: List<String>,
    onOpen: (String) -> Unit,
    onAddStudent: () -> Unit,
    onAddAppointment: () -> Unit,
    onEditAppointment: (Appointment) -> Unit,
    onDeleteAppointment: (Long) -> Unit,
    onAddTask: () -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onEditTask: (TaskRecord) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onOpenStudent: (Long) -> Unit
) {
    val today = todayIso()
    var selectedDate by rememberSaveable { mutableStateOf(today) }
    var monthKey by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
    val selectedMonth = runCatching { YearMonth.parse(monthKey) }.getOrDefault(YearMonth.now())
    val pendingTasks = tasks.filterNot { it.completed }.sortedBy { it.dueDate }
    val pendingCharges = charges.filter { it.status == "Pendente" }
    val todayAppointments = appointments.filter { it.date == today }
    val scheduledEvaluations = todayAppointments.count { it.kind == "Avaliação" }
    val amountLabel = if (maskFinancial) "••••" else NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(pendingCharges.sumOf { it.amount })
    val latestAssessments = remember(students, assessments) {
        assessments.groupBy { it.studentId }.mapValues { (_, values) -> values.maxByOrNull { it.date } }
    }
    val overdueStudents = remember(students, latestAssessments, reevaluationIntervalDays) {
        students.mapNotNull { student ->
            val latest = latestAssessments[student.id] ?: return@mapNotNull null
            val days = runCatching { ChronoUnit.DAYS.between(LocalDate.parse(latest.date), LocalDate.now()) }.getOrNull() ?: return@mapNotNull null
            if (days >= reevaluationIntervalDays) Triple(student, latest, days) else null
        }.sortedByDescending { it.third }
    }
    val upcomingReturns = appointments.filter { appointment ->
        appointment.kind == "Retorno" && appointment.date >= today && appointment.date <= LocalDate.now().plusDays(14).toString()
    }.sortedWith(compareBy<Appointment> { it.date }.thenBy { it.time })
    val appointmentsOnSelectedDate = appointments.filter { it.date == selectedDate }.sortedBy { it.time }
    val nextAppointments = appointments.filter { it.date >= today }.sortedWith(compareBy<Appointment> { it.date }.thenBy { it.time }).take(3)
    val hasNoStudents = studentCount == 0

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Dashboard Secretária", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Sua rotina, alunos e próximos acompanhamentos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("Atendimentos", todayAppointments.size.toString(), "Hoje", Modifier.weight(1f))
                    MetricCard("Avaliações", scheduledEvaluations.toString(), "Previstas hoje", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("Reavaliações", reevaluationsDue.toString(), "Em atraso", Modifier.weight(1f))
                    MetricCard("Cobranças", pendingCharges.size.toString(), "Em aberto: $amountLabel", Modifier.weight(1f))
                }
            }
        }
        item {
            SectionCard("Calendário e agenda") {
                HomeCalendar(
                    month = selectedMonth,
                    selectedDate = selectedDate,
                    appointmentDates = appointments.mapTo(mutableSetOf()) { it.date },
                    onPreviousMonth = { monthKey = selectedMonth.minusMonths(1).toString() },
                    onNextMonth = { monthKey = selectedMonth.plusMonths(1).toString() },
                    onSelectDate = {
                        selectedDate = it
                        monthKey = runCatching { YearMonth.from(LocalDate.parse(it)).toString() }.getOrDefault(monthKey)
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    if (selectedDate == today) "Atendimentos de hoje" else "Atendimentos em ${displayDate(selectedDate)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (appointmentsOnSelectedDate.isEmpty()) {
                    Text("Nenhum atendimento agendado para esta data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    appointmentsOnSelectedDate.take(3).forEach { appointment ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("${appointment.time}  ·  ${appointment.title}", fontWeight = FontWeight.Medium)
                                Text(appointment.kind, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                if (appointment.location.isNotBlank()) Text(appointment.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { onEditAppointment(appointment) }) { Text("Editar") }
                            TextButton(onClick = { onDeleteAppointment(appointment.id) }) { Text("Excluir") }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onAddAppointment) { Text("Novo atendimento") }
                    TextButton(onClick = { onOpen("Agenda") }) { Text("Agenda completa") }
                }
            }
        }
        item {
            SectionCard("Próximos atendimentos") {
                if (nextAppointments.isEmpty()) {
                    Text("Nenhum próximo retorno agendado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    nextAppointments.forEach { appointment ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(appointment.title, fontWeight = FontWeight.Medium)
                                Text("${appointment.kind} · ${displayDate(appointment.date)} · ${appointment.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { onEditAppointment(appointment) }) { Text("Editar") }
                        }
                    }
                }
            }
        }
        item {
            SectionCard("Central de atenção") {
                when {
                    hasNoStudents -> {
                        Text("Cadastre o primeiro aluno para iniciar o acompanhamento.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(onClick = onAddStudent) { Text("Cadastrar aluno") }
                    }
                    overdueStudents.isEmpty() && upcomingReturns.isEmpty() && studentsWithoutAssessment == 0 && pendingTasks.isEmpty() && pendingCharges.isEmpty() ->
                        Text("Tudo em dia. Nenhum alerta precisa de atenção agora.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> {
                        if (studentsWithoutAssessment > 0) {
                            Text("$studentsWithoutAssessment aluno(s) ainda não têm avaliação registrada.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = { onOpen("Perfil Unificado") }) { Text("Ver alunos") }
                        }
                        if (upcomingReturns.isNotEmpty()) {
                            Text("Retornos nos próximos 14 dias", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            upcomingReturns.take(3).forEach { appointment ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(appointment.title, fontWeight = FontWeight.Medium)
                                        Text("${displayDate(appointment.date)} · ${appointment.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    TextButton(onClick = { onOpen("Agenda") }) { Text("Agenda") }
                                }
                            }
                        }
                        if (overdueStudents.isNotEmpty()) {
                            Text("Reavaliações a acompanhar · prazo de $reevaluationIntervalDays dias", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            overdueStudents.take(3).forEach { (student, last, days) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(student.name, fontWeight = FontWeight.Medium)
                                        Text("Última avaliação ${displayDate(last.date)} · há ${days} dias", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    TextButton(onClick = { onOpenStudent(student.id) }) { Text("Abrir") }
                                }
                            }
                        }
                        if (pendingCharges.isNotEmpty()) {
                            Text("Cobranças pendentes: ${pendingCharges.size} · $amountLabel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = { onOpen("Cobranças") }) { Text("Abrir cobranças") }
                        }
                    }
                }
            }
        }
        item {
            SectionCard("Tarefas") {
                val pending = pendingTasks.take(3)
                if (pending.isEmpty()) Text("Nenhuma tarefa pendente.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                pending.forEach { task ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = false, onCheckedChange = { onToggleTask(task.id, it) })
                        Column(Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.Medium)
                            Text("Prazo: ${displayDate(task.dueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = { onEditTask(task) }) { Text("Editar") }
                        TextButton(onClick = { onDeleteTask(task.id) }) { Text("Excluir") }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAddTask) { Text("Nova tarefa") }
                    TextButton(onClick = { onOpen("Tarefas") }) { Text("Ver todas") }
                }
            }
        }
        if (favoriteModules.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Favoritos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(favoriteModules) { module ->
                            QuickAction(module, moduleIcon(module), Modifier.width(155.dp)) { onOpen(module) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCalendar(
    month: YearMonth,
    selectedDate: String,
    appointmentDates: Set<String>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (String) -> Unit
) {
    val locale = Locale("pt", "BR")
    val monthLabel = month.atDay(1).format(DateTimeFormatter.ofPattern("MMMM yyyy", locale))
        .replaceFirstChar { it.uppercase() }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(monthLabel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        IconButton(onClick = onPreviousMonth) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mês anterior") }
        IconButton(onClick = onNextMonth) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Próximo mês") }
    }
    Row {
        listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { day ->
            Box(Modifier.weight(1f).height(24.dp), contentAlignment = Alignment.Center) {
                Text(day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    val offset = month.atDay(1).dayOfWeek.value % 7
    val dates: List<LocalDate?> = List(offset) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }
    dates.chunked(7).forEach { week ->
        Row {
            week.forEach { date ->
                if (date == null) {
                    Spacer(Modifier.weight(1f).height(40.dp))
                } else {
                    val iso = date.toString()
                    val selected = selectedDate == iso
                    val hasAppointment = iso in appointmentDates
                    Column(
                        modifier = Modifier.weight(1f).height(42.dp).clickable { onSelectDate(iso) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).background(
                                if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            Modifier.size(3.dp).background(
                                if (hasAppointment) MaterialTheme.colorScheme.primary else Color.Transparent,
                                CircleShape
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun moduleIcon(module: String): ImageVector = when (module) {
    "Início" -> Icons.Default.Home
    "Agenda" -> Icons.Default.CalendarMonth
    "Perfil Unificado" -> Icons.Default.Person
    "Alunos" -> Icons.Default.Groups
    "Avaliações" -> Icons.Default.Assignment
    "Evolução" -> Icons.Default.ShowChart
    "Calculadora" -> Icons.Default.Calculate
    "Formulários", "Anamnese" -> Icons.Default.Description
    "Análises", "Análises Completas" -> Icons.Default.Analytics
    "Cobranças" -> Icons.Default.ReceiptLong
    "Tarefas" -> Icons.Default.Checklist
    "Configurações" -> Icons.Default.Settings
    else -> Icons.Default.MoreHoriz
}

@Composable
private fun MetricCard(label: String, value: String, detail: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Muted)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
    }
}

@Composable
private fun QuickAction(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, onClick = onClick, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StudentsScreen(
    students: List<Student>,
    errorMessage: String?,
    onAdd: () -> Unit,
    onEdit: (Student) -> Unit,
    onOpen: (Long) -> Unit,
    onClearError: () -> Unit,
    subtitle: String = "Cadastre e acesse o Perfil Unificado de cada aluno."
) {
    var query by rememberSaveable { mutableStateOf("") }
    val visibleStudents = students.filter {
        query.isBlank() || it.name.contains(query, true) || it.phone.contains(query, true) || it.email.contains(query, true)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Alunos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (students.isNotEmpty()) {
                OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Buscar aluno") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
            }
            Button(onClick = onAdd) { Text("Adicionar aluno") }
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                TextButton(onClick = onClearError) { Text("Fechar aviso") }
            }
        }
        if (visibleStudents.isEmpty()) {
            item {
                EmptyCard(
                    title = if (students.isEmpty()) "Nenhum aluno cadastrado" else "Nenhum resultado",
                    body = if (students.isEmpty()) "Os dados ficam salvos neste aparelho. Cadastre um aluno para começar." else "Tente outro nome, telefone ou e-mail."
                )
            }
        }
        items(visibleStudents, key = { it.id }) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpen(student.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(student.name, fontWeight = FontWeight.SemiBold)
                        val contact = listOf(student.phone, student.email).filter { it.isNotBlank() }.joinToString(" · ")
                        Text(if (contact.isBlank()) "Perfil Unificado" else contact, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onEdit(student) }) { Icon(Icons.Default.Edit, contentDescription = "Editar aluno") }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Abrir perfil")
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    student: Student,
    assessments: List<Assessment>,
    onAddAssessment: () -> Unit,
    onDeleteStudent: () -> Unit,
    onDeleteAssessment: (Long) -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Perfil Unificado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(student.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (student.phone.isNotBlank()) Text("Telefone: " + student.phone)
                    if (student.email.isNotBlank()) Text("E-mail: " + student.email)
                    if (student.birthDate.isNotBlank()) Text("Nascimento: " + student.birthDate)
                    Text("ID do aluno: " + student.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onAddAssessment) { Text("Nova avaliação") }
                TextButton(onClick = { confirmDelete = true }) { Text("Excluir aluno") }
            }
        }
        item {
            Text("Histórico de avaliações", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        if (assessments.size >= 2) {
            val latest = assessments[0]
            val previous = assessments[1]
            item {
                SectionCard("Evolução entre as duas últimas avaliações") {
                    val weightDelta = latest.weightKg - previous.weightKg
                    Text("Peso: " + (if (weightDelta > 0) "+" else "") + formatNumber(weightDelta) + " kg")
                    Text("IMC: " + (previous.bmi?.let(::formatNumber) ?: "—") + " → " + (latest.bmi?.let(::formatNumber) ?: "—"))
                    if (latest.waistCm != null && previous.waistCm != null) {
                        val waistDelta = latest.waistCm - previous.waistCm
                        Text("Cintura: " + (if (waistDelta > 0) "+" else "") + formatNumber(waistDelta) + " cm")
                    }
                    Text("Comparação descritiva. Considere diferenças de método e condições de medição.", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            }
        }
        if (assessments.size >= 2) {
            item {
                SectionCard("Variação do peso") {
                    WeightTrendChart(assessments.take(6).reversed())
                    Text("Gráfico descritivo. Compare avaliações feitas em condições semelhantes.", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            }
        }
        if (assessments.isEmpty()) {
            item { EmptyCard("Sem avaliações", "Registre a primeira avaliação para iniciar o acompanhamento.") }
        }
        items(assessments, key = { it.id }) { item ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(item.date, fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = { onDeleteAssessment(item.id) }) { Text("Excluir") }
                    }
                    Text("Peso: " + formatNumber(item.weightKg) + " kg  ·  Altura: " + formatNumber(item.heightCm) + " cm")
                    Text("IMC: " + (item.bmi?.let(::formatNumber) ?: "—"), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    if (item.waistCm != null) Text("Cintura: " + formatNumber(item.waistCm) + " cm")
                    if (item.hipCm != null) Text("Quadril: " + formatNumber(item.hipCm) + " cm")
                    if (item.bodyFatPercent != null) Text("Gordura estimada: " + formatNumber(item.bodyFatPercent) + "%")
                    listOf("Pescoço" to item.neckCm, "Peitoral" to item.chestCm, "Braço" to item.armCm, "Coxa" to item.thighCm)
                        .forEach { (label, value) -> if (value != null) Text("$label: " + formatNumber(value) + " cm") }
                    if (item.notes.isNotBlank()) Text(item.notes, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Excluir aluno?") },
            text = { Text("O perfil e as avaliações vinculadas serão removidos deste aparelho.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDeleteStudent() }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun WeightTrendChart(history: List<Assessment>) {
    val values = history.map { it.weightKg.toFloat() }
    val lineColor = MaterialTheme.colorScheme.primary
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(Modifier.fillMaxWidth().height(150.dp)) {
            if (values.size < 2) return@Canvas
            val minValue = values.minOrNull() ?: return@Canvas
            val maxValue = values.maxOrNull() ?: return@Canvas
            val range = (maxValue - minValue).coerceAtLeast(0.5f)
            val inset = 12f
            val points = values.mapIndexed { index, value ->
                Offset(
                    x = inset + (size.width - inset * 2) * index / values.lastIndex.toFloat(),
                    y = size.height - inset - (value - minValue) / range * (size.height - inset * 2)
                )
            }
            points.zipWithNext().forEach { (start, end) -> drawLine(lineColor, start, end, strokeWidth = 5f) }
            points.forEach { drawCircle(lineColor, radius = 7f, center = it) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(displayDate(history.first().date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(displayDate(history.last().date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("Peso: " + formatNumber(values.first().toDouble()) + " → " + formatNumber(values.last().toDouble()) + " kg", fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StudentFormDialog(initial: Student?, onDismiss: () -> Unit, onSave: (Long?, String, String, String, String) -> Unit) {
    var name by rememberSaveable(initial?.id) { mutableStateOf(initial?.name.orEmpty()) }
    var phone by rememberSaveable(initial?.id) { mutableStateOf(initial?.phone.orEmpty()) }
    var email by rememberSaveable(initial?.id) { mutableStateOf(initial?.email.orEmpty()) }
    var birthDate by rememberSaveable(initial?.id) { mutableStateOf(initial?.birthDate.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Novo aluno" else "Editar aluno") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(name, { name = it; error = null }, label = { Text("Nome *") }, singleLine = true)
                OutlinedTextField(phone, { phone = it }, label = { Text("Telefone") }, singleLine = true)
                OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, singleLine = true)
                OutlinedTextField(birthDate, { birthDate = it }, label = { Text("Nascimento (DD/MM/AAAA)") }, singleLine = true)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) error = "Informe o nome do aluno."
                else onSave(initial?.id, name, phone, email, birthDate)
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun AssessmentFormDialog(
    studentName: String,
    onDismiss: () -> Unit,
    onSave: (String, Double, Double, Double?, Double?, Double?, Double?, Double?, Double?, Double?, String) -> Unit
) {
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var waist by rememberSaveable { mutableStateOf("") }
    var hip by rememberSaveable { mutableStateOf("") }
    var bodyFat by rememberSaveable { mutableStateOf("") }
    var neck by rememberSaveable { mutableStateOf("") }
    var chest by rememberSaveable { mutableStateOf("") }
    var arm by rememberSaveable { mutableStateOf("") }
    var thigh by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val date = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Avaliação de " + studentName) },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 500.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Data: " + date, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(weight, { weight = it; error = null }, label = { Text("Peso (kg) *") }, singleLine = true)
                OutlinedTextField(height, { height = it; error = null }, label = { Text("Altura (cm) *") }, singleLine = true)
                OutlinedTextField(waist, { waist = it }, label = { Text("Cintura (cm)") }, singleLine = true)
                OutlinedTextField(hip, { hip = it }, label = { Text("Quadril (cm)") }, singleLine = true)
                OutlinedTextField(bodyFat, { bodyFat = it; error = null }, label = { Text("Gordura corporal estimada (%)") }, singleLine = true)
                OutlinedTextField(neck, { neck = it; error = null }, label = { Text("Pescoço (cm)") }, singleLine = true)
                OutlinedTextField(chest, { chest = it; error = null }, label = { Text("Peitoral (cm)") }, singleLine = true)
                OutlinedTextField(arm, { arm = it; error = null }, label = { Text("Braço (cm)") }, singleLine = true)
                OutlinedTextField(thigh, { thigh = it; error = null }, label = { Text("Coxa (cm)") }, singleLine = true)
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, minLines = 2)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedWeight = AssessmentMath.parseNumber(weight)
                val parsedHeight = AssessmentMath.parseNumber(height)
                val parsedWaist = if (waist.isBlank()) null else AssessmentMath.parseNumber(waist)
                val parsedHip = if (hip.isBlank()) null else AssessmentMath.parseNumber(hip)
                val parsedBodyFat = if (bodyFat.isBlank()) null else AssessmentMath.parseNumber(bodyFat)
                val parsedNeck = if (neck.isBlank()) null else AssessmentMath.parseNumber(neck)
                val parsedChest = if (chest.isBlank()) null else AssessmentMath.parseNumber(chest)
                val parsedArm = if (arm.isBlank()) null else AssessmentMath.parseNumber(arm)
                val parsedThigh = if (thigh.isBlank()) null else AssessmentMath.parseNumber(thigh)
                val optionalInputs = listOf(waist to parsedWaist, hip to parsedHip, bodyFat to parsedBodyFat, neck to parsedNeck, chest to parsedChest, arm to parsedArm, thigh to parsedThigh)
                val invalidOptional = optionalInputs.any { (raw, parsed) -> raw.isNotBlank() && parsed == null } ||
                    (parsedBodyFat != null && parsedBodyFat !in 1.0..70.0) ||
                    listOf(parsedNeck, parsedChest, parsedArm, parsedThigh).any { it != null && it !in 1.0..300.0 }
                if (parsedWeight == null || parsedHeight == null || invalidOptional ||
                    AssessmentMath.bmi(parsedWeight ?: 0.0, parsedHeight ?: 0.0) == null
                ) {
                    error = "Confira os valores. Peso e altura são obrigatórios; use números positivos."
                } else {
                    onSave(date, parsedWeight, parsedHeight, parsedWaist, parsedHip, parsedBodyFat, parsedNeck, parsedChest, parsedArm, parsedThigh, notes)
                }
            }) { Text("Salvar avaliação") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun CalculatorScreen() {
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var waist by rememberSaveable { mutableStateOf("") }
    var hip by rememberSaveable { mutableStateOf("") }
    val weightValue = AssessmentMath.parseNumber(weight)
    val heightValue = AssessmentMath.parseNumber(height)
    val waistValue = AssessmentMath.parseNumber(waist)
    val hipValue = AssessmentMath.parseNumber(hip)
    val bmi = if (weightValue != null && heightValue != null) AssessmentMath.bmi(weightValue, heightValue) else null
    val waistHeightRatio = if (waistValue != null && heightValue != null && waistValue > 0 && heightValue > 0) waistValue / heightValue else null
    val waistHipRatio = if (waistValue != null && hipValue != null && waistValue > 0 && hipValue > 0) waistValue / hipValue else null
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Calculadora e laboratório", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Índices antropométricos derivados dos valores inseridos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            SectionCard("Medidas") {
                OutlinedTextField(weight, { weight = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Peso (kg)") }, singleLine = true)
                OutlinedTextField(height, { height = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Altura (cm)") }, singleLine = true)
                OutlinedTextField(waist, { waist = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Cintura (cm)") }, singleLine = true)
                OutlinedTextField(hip, { hip = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Quadril (cm)") }, singleLine = true)
            }
        }
        item {
            SectionCard("Resultados") {
                Text("IMC: " + (bmi?.let(::formatNumber) ?: "—"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Cintura/estatura: " + (waistHeightRatio?.let(::formatNumber) ?: "—"))
                Text("Cintura/quadril: " + (waistHipRatio?.let(::formatNumber) ?: "—"))
                Text("São índices matemáticos estimados e não constituem diagnóstico. Interprete com os métodos e limites próprios.", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
        }
    }
}

@Composable
private fun ModuleListScreen(favorites: Set<String>, onOpen: (String) -> Unit, onToggleFavorite: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Módulos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Acesso rápido às áreas do EvoluFísico.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(moduleNames) { module ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpen(module) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(moduleIcon(module), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(module, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    IconButton(onClick = { onToggleFavorite(module) }) {
                        Icon(
                            if (module in favorites) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (module in favorites) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (module in favorites) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ModulePlaceholder(title: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(moduleIcon(title), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Esta área está planejada para uma próxima etapa.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsScreen(
    dark: Boolean,
    accentIndex: Int,
    onAccentChange: (Int) -> Unit,
    onDarkChange: (Boolean) -> Unit,
    maskFinancial: Boolean,
    onMaskFinancialChange: (Boolean) -> Unit,
    reevaluationIntervalDays: Int,
    onReevaluationIntervalChange: (Int) -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        item {
            Text("Geral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (dark) "Modo escuro" else "Modo claro", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = dark, onCheckedChange = onDarkChange)
            }
            Spacer(Modifier.height(10.dp))
            Text("Cor de destaque", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            accents.chunked(5).forEachIndexed { rowIndex, row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEachIndexed { columnIndex, accent ->
                        val index = rowIndex * 5 + columnIndex
                        Box(
                            modifier = Modifier.weight(1f).size(42.dp).background(accent.second, CircleShape)
                                .clickable { onAccentChange(index) }
                                .semantics {
                                    contentDescription = "Cor de destaque: " + accent.first
                                    selected = index == accentIndex
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == accentIndex) {
                                Icon(Icons.Default.Check, contentDescription = "Selecionada: " + accent.first, tint = Color.White)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Text("Selecionada: " + accents[accentIndex].first, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
        item {
            Text("Acompanhamento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Prazo padrão para sinalizar reavaliações a acompanhar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(30, 60, 90).forEach { days ->
                    if (days == reevaluationIntervalDays) {
                        Button(onClick = { onReevaluationIntervalChange(days) }, modifier = Modifier.weight(1f)) { Text("$days dias") }
                    } else {
                        androidx.compose.material3.OutlinedButton(onClick = { onReevaluationIntervalChange(days) }, modifier = Modifier.weight(1f)) { Text("$days dias") }
                    }
                }
            }
        }
        item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
        item {
            Text("Dados e privacidade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            Text(
                "Os dados desta Beta ficam no armazenamento local do aplicativo. O backup é exportado manualmente; a restauração substitui os dados locais pelo conteúdo do arquivo.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ocultar valores financeiros", modifier = Modifier.weight(1f))
                Switch(checked = maskFinancial, onCheckedChange = onMaskFinancialChange)
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onBackup, modifier = Modifier.weight(1f)) { Text("Exportar backup") }
                androidx.compose.material3.OutlinedButton(onClick = onRestore, modifier = Modifier.weight(1f)) { Text("Restaurar") }
            }
        }
        item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
        item {
            Text("EvoluFísico Android — Beta 15", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Versão nativa baseada no EvoluFisico2 3.0.0.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyCard(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun AgendaScreen(appointments: List<Appointment>, onAdd: () -> Unit, onEdit: (Appointment) -> Unit, onDelete: (Long) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Agenda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Organize os próximos atendimentos.", color = Muted)
            Spacer(Modifier.height(6.dp))
            Button(onClick = onAdd) { Text("Novo atendimento") }
        }
        if (appointments.isEmpty()) item { EmptyCard("Agenda vazia", "Cadastre um atendimento para organizar sua rotina.") }
        items(appointments, key = { it.id }) { appointment ->
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(appointment.title, fontWeight = FontWeight.SemiBold)
                            Text(displayDate(appointment.date) + "  ·  " + appointment.time, color = Teal)
                        }
                        Row {
                            TextButton(onClick = { onEdit(appointment) }) { Text("Editar") }
                            TextButton(onClick = { onDelete(appointment.id) }) { Text("Excluir") }
                        }
                    }
                    if (appointment.location.isNotBlank()) Text(appointment.location, color = Muted)
                    if (appointment.notes.isNotBlank()) Text(appointment.notes, color = Muted)
                }
            }
        }
    }
}

@Composable
private fun AppointmentFormDialog(initial: Appointment?, onDismiss: () -> Unit, onSave: (Long?, String, String, String, String, String, String) -> Unit) {
    var title by rememberSaveable(initial?.id) { mutableStateOf(initial?.title.orEmpty()) }
    var date by rememberSaveable(initial?.id) { mutableStateOf(initial?.date ?: todayIso()) }
    var time by rememberSaveable(initial?.id) { mutableStateOf(initial?.time.orEmpty()) }
    var location by rememberSaveable(initial?.id) { mutableStateOf(initial?.location.orEmpty()) }
    var notes by rememberSaveable(initial?.id) { mutableStateOf(initial?.notes.orEmpty()) }
    var kind by rememberSaveable(initial?.id) { mutableStateOf(initial?.kind ?: "Atendimento") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Novo compromisso" else "Editar compromisso") },
        text = {
            Column(modifier = Modifier.heightIn(max = 500.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipo do compromisso", style = MaterialTheme.typography.labelLarge)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Atendimento", "Avaliação", "Retorno").forEach { option ->
                        if (kind == option) {
                            Button(onClick = { kind = option }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 3.dp, vertical = 8.dp)) { Text("✓ $option", style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                        } else {
                            androidx.compose.material3.OutlinedButton(onClick = { kind = option }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 3.dp, vertical = 8.dp)) { Text(option, style = MaterialTheme.typography.labelSmall, maxLines = 1) }
                        }
                    }
                }
                OutlinedTextField(title, { title = it; error = null }, label = { Text("Descrição *") }, singleLine = true)
                OutlinedTextField(date, { date = it; error = null }, label = { Text("Data (AAAA-MM-DD)") }, singleLine = true)
                OutlinedTextField(time, { time = it; error = null }, label = { Text("Horário (HH:MM)") }, singleLine = true)
                OutlinedTextField(location, { location = it }, label = { Text("Local") }, singleLine = true)
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, minLines = 2)
                if (error != null) Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val validDate = runCatching {
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
                    parser.parse(date)?.let { parser.format(it) == date } == true
                }.getOrDefault(false)
                val validTime = runCatching {
                    val parser = SimpleDateFormat("HH:mm", Locale.ROOT).apply { isLenient = false }
                    parser.parse(time)?.let { parser.format(it) == time } == true
                }.getOrDefault(false)
                if (title.isBlank()) error = "Informe a descrição do compromisso."
                else if (!validDate) error = "Informe uma data válida no formato AAAA-MM-DD."
                else if (!validTime) error = "Informe um horário válido no formato HH:MM."
                else onSave(initial?.id, title, date, time, location, notes, kind)
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun todayIso(): String = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date())

private fun displayDate(value: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(value)
    parsed?.let { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(it) } ?: value
}.getOrDefault(value)

private fun formatNumber(value: Double): String =
    String.format(Locale("pt", "BR"), "%.1f", value)
