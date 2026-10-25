package br.com.evolufisico.nativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.util.Date
import java.util.Locale
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
            background = Color(0xFF111719),
            surface = Color(0xFF1B2427)
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
            small = RoundedCornerShape(24.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(24.dp)
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
private fun EvoluFisicoApp(dark: Boolean, accentIndex: Int, onDarkChange: (Boolean) -> Unit, onAccentChange: (Int) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { LocalDatabase(context) }
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableIntStateOf(0) }
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
    fun toggleFavorite(module: String) {
        val next = favoriteModules.toMutableSet()
        if (!next.add(module)) next.remove(module)
        favoriteModules = next
        preferences.edit().putStringSet("favorite_modules", next).apply()
    }
    var appointmentDialog by remember { mutableStateOf(false) }
    var taskDialog by remember { mutableStateOf(false) }
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
                listOf(database.students(), database.counts(), selectedStudentId?.let(database::assessments).orEmpty(), database.appointments(), database.studentsWithoutAssessment(), database.reevaluationsDue(), database.tasks(), database.intakeForms(), database.charges(), database.allAssessments())
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

    LaunchedEffect(Unit) { refresh() }
    LaunchedEffect(selectedStudentId) {
        profileAssessments = if (selectedStudentId == null) emptyList()
        else withContext(Dispatchers.IO) { database.assessments(selectedStudentId!!) }
    }

    val selectedStudent = students.firstOrNull { it.id == selectedStudentId }
    val title = when {
        selectedStudent != null -> selectedStudent.name
        activeModule != null -> activeModule.orEmpty()
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
                            if (selectedStudentId != null) selectedStudentId = null else activeModule = null
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    } else {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.padding(horizontal = 16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = Graphite, tonalElevation = 0.dp) {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selected == index && selectedStudentId == null && activeModule == null,
                        onClick = {
                            selected = index
                            selectedStudentId = null
                            activeModule = null
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.title) },
                        label = { Text(destination.title) },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f), selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, unselectedIconColor = Muted, unselectedTextColor = Muted)
                    )
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
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
                activeModule == "Formulários" -> FormsScreen(forms, students, { intakeDialog = true }, { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteIntakeForm(id) }; refresh() } })
                activeModule == "Cobranças" -> ChargeScreen(charges, students, maskFinancial, { chargeDialog = true }, { id -> scope.launch { withContext(Dispatchers.IO) { database.markChargeReceived(id) }; refresh() } }, { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteCharge(id) }; refresh() } })
                activeModule == "Tarefas" -> TaskScreen(tasks, { taskDialog = true }, { id, done -> scope.launch { withContext(Dispatchers.IO) { database.setTaskCompleted(id, done) }; refresh() } }, { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteTask(id) }; refresh() } })
                activeModule == "Análises" -> AnalyticsScreen(students, allAssessments, onOpenStudent = { selectedStudentId = it })
                activeModule == "Agenda" || selected == 2 -> AgendaScreen(appointments = appointments, onAdd = { appointmentDialog = true }, onDelete = { id -> scope.launch { withContext(Dispatchers.IO) { database.deleteAppointment(id) }; refresh() } })
                activeModule != null -> ModulePlaceholder(activeModule.orEmpty())
                selected == 0 -> HomeScreen(
                    studentCount = counts.first,
                    assessmentCount = counts.second,
                    todayAppointments = appointments.filter { it.date == todayIso() },
                    studentsWithoutAssessment = studentsWithoutAssessment,
                    reevaluationsDue = reevaluationsDue,
                    favoriteModules = favoriteModules.sorted(),
                    onOpen = { activeModule = it },
                    onAddStudent = { studentDialog = true }
                )
                else -> ModuleListScreen(
                    favorites = favoriteModules,
                    onOpen = { activeModule = it },
                    onToggleFavorite = ::toggleFavorite
                )
            }
        }
    }

    if (appointmentDialog) {
        AppointmentFormDialog(onDismiss = { appointmentDialog = false }, onSave = { title, date, time, location, notes ->
            scope.launch {
                try {
                    withContext(Dispatchers.IO) { database.saveAppointment(title, date, time, location, notes) }
                    appointmentDialog = false
                    errorMessage = null
                    refresh()
                } catch (exception: Exception) {
                    errorMessage = exception.message ?: "Não foi possível salvar o atendimento."
                }
            }
        })
    }

    if (taskDialog) TaskFormDialog(
        onDismiss = { taskDialog = false },
        onSave = { title, dueDate -> scope.launch {
            try { withContext(Dispatchers.IO) { database.saveTask(null, title, dueDate) }; taskDialog = false; refresh() }
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
            onSave = { date, weight, height, waist, hip, notes ->
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            database.saveAssessment(selectedStudent.id, date, weight, height, waist, hip, notes)
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
    assessmentCount: Int,
    todayAppointments: List<Appointment>,
    studentsWithoutAssessment: Int,
    reevaluationsDue: Int,
    favoriteModules: List<String>,
    onOpen: (String) -> Unit,
    onAddStudent: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Dashboard Secretária", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Acompanhe alunos, avaliações e próximos atendimentos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                if (maxWidth >= 600.dp) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCard("Alunos", studentCount.toString(), "Cadastros locais", Modifier.weight(1f))
                        MetricCard("Avaliações", assessmentCount.toString(), "Histórico salvo", Modifier.weight(1f))
                        MetricCard("Sem avaliação", studentsWithoutAssessment.toString(), "Perfis sem histórico", Modifier.weight(1f))
                        MetricCard("Reavaliações", reevaluationsDue.toString(), "Há 90 dias ou mais", Modifier.weight(1f))
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Alunos", studentCount.toString(), "Cadastros locais", Modifier.weight(1f))
                            MetricCard("Avaliações", assessmentCount.toString(), "Histórico salvo", Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        item {
            SectionCard("Agenda de hoje") {
                if (todayAppointments.isEmpty()) Text("A agenda ainda está vazia.", color = Muted)
                else todayAppointments.take(3).forEach { Text(it.time + "  ·  " + it.title, color = MaterialTheme.colorScheme.onSurface) }
                Button(onClick = { onOpen("Agenda") }) { Text("Abrir agenda") }
            }
        }
        item {
            SectionCard("Central de atenção") {
                val message = when {
                    studentCount == 0 -> "Cadastre o primeiro aluno para iniciar o acompanhamento."
                    reevaluationsDue > 0 -> "$reevaluationsDue aluno(s) estão há 90 dias ou mais sem reavaliação."
                    studentsWithoutAssessment > 0 -> "$studentsWithoutAssessment aluno(s) ainda não têm avaliação registrada."
                    else -> "Todos os alunos têm avaliações recentes registradas."
                }
                Text(message, color = Muted)
                Button(onClick = if (studentCount == 0) onAddStudent else ({ onOpen("Perfil Unificado") })) {
                    Text(if (studentCount == 0) "Cadastrar aluno" else "Ver alunos")
                }
            }
        }
        item {
            if (favoriteModules.isEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAction("Perfil Unificado", Icons.Default.Person, Modifier.weight(1f)) { onOpen("Perfil Unificado") }
                    QuickAction("Avaliações", Icons.Default.FitnessCenter, Modifier.weight(1f)) { onOpen("Avaliações") }
                }
            } else {
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

private fun moduleIcon(module: String): ImageVector = when (module) {
    "Agenda" -> Icons.Default.CalendarMonth
    "Perfil Unificado" -> Icons.Default.Person
    "Alunos" -> Icons.Default.Groups
    "Configurações" -> Icons.Default.MoreHoriz
    else -> Icons.Default.FitnessCenter
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
    onSave: (String, Double, Double, Double?, Double?, String) -> Unit
) {
    var weight by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var waist by rememberSaveable { mutableStateOf("") }
    var hip by rememberSaveable { mutableStateOf("") }
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
                val invalidOptional = (waist.isNotBlank() && parsedWaist == null) || (hip.isNotBlank() && parsedHip == null)
                if (parsedWeight == null || parsedHeight == null || invalidOptional ||
                    AssessmentMath.bmi(parsedWeight ?: 0.0, parsedHeight ?: 0.0) == null
                ) {
                    error = "Confira os valores. Peso e altura são obrigatórios; use números positivos."
                } else {
                    onSave(date, parsedWeight, parsedHeight, parsedWaist, parsedHip, notes)
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
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
            Text("EvoluFísico Android — Beta 4", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun AgendaScreen(appointments: List<Appointment>, onAdd: () -> Unit, onDelete: (Long) -> Unit) {
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
                        TextButton(onClick = { onDelete(appointment.id) }) { Text("Excluir") }
                    }
                    if (appointment.location.isNotBlank()) Text(appointment.location, color = Muted)
                    if (appointment.notes.isNotBlank()) Text(appointment.notes, color = Muted)
                }
            }
        }
    }
}

@Composable
private fun AppointmentFormDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(todayIso()) }
    var time by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo atendimento") },
        text = {
            Column(modifier = Modifier.heightIn(max = 500.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it; error = null }, label = { Text("Motivo *") }, singleLine = true)
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
                if (title.isBlank()) error = "Informe o motivo do atendimento."
                else if (!validDate) error = "Informe uma data válida no formato AAAA-MM-DD."
                else if (!validTime) error = "Informe um horário válido no formato HH:MM."
                else onSave(title, date, time, location, notes)
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
