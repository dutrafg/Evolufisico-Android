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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.Assessment
import br.com.evolufisico.nativo.data.AssessmentMath
import br.com.evolufisico.nativo.data.LocalDatabase
import br.com.evolufisico.nativo.data.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Graphite = Color(0xFF263238)
private val Teal = Color(0xFF168A7A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("appearance", MODE_PRIVATE)
        setContent {
            var dark by rememberSaveable { mutableStateOf(preferences.getBoolean("dark", false)) }
            EvoluFisicoTheme(dark = dark) {
                EvoluFisicoApp(
                    dark = dark,
                    onDarkChange = {
                        dark = it
                        preferences.edit().putBoolean("dark", it).apply()
                    }
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
private fun EvoluFisicoApp(dark: Boolean, onDarkChange: (Boolean) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { LocalDatabase(context) }
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var activeModule by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStudentId by rememberSaveable { mutableStateOf<Long?>(null) }
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var profileAssessments by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    var counts by remember { mutableStateOf(0 to 0) }
    var studentDialog by remember { mutableStateOf(false) }
    var assessmentDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                Triple(database.students(), database.counts(), selectedStudentId?.let(database::assessments).orEmpty())
            }
            students = result.first
            counts = result.second
            profileAssessments = result.third
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
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("EvoluFísico", fontWeight = FontWeight.SemiBold)
                        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selected == index && selectedStudentId == null && activeModule == null,
                        onClick = {
                            selected = index
                            selectedStudentId = null
                            activeModule = null
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.title) },
                        label = { Text(destination.title) }
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
                activeModule == "Configurações" -> SettingsScreen(dark, onDarkChange)
                activeModule == "Perfil Unificado" || selected == 1 -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null }
                )
                activeModule == "Avaliações" -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null },
                    subtitle = "Escolha um aluno para registrar uma avaliação."
                )
                activeModule == "Evolução" -> StudentsScreen(
                    students = students,
                    errorMessage = errorMessage,
                    onAdd = { studentDialog = true },
                    onOpen = { selectedStudentId = it },
                    onClearError = { errorMessage = null },
                    subtitle = "Abra o perfil para consultar o histórico de avaliações."
                )
                activeModule == "Calculadora" -> CalculatorScreen()
                activeModule != null -> ModulePlaceholder(activeModule.orEmpty())
                selected == 0 -> HomeScreen(
                    studentCount = counts.first,
                    assessmentCount = counts.second,
                    onOpen = { activeModule = it },
                    onAddStudent = { studentDialog = true }
                )
                selected == 2 -> ModulePlaceholder("Agenda")
                else -> ModuleListScreen(onOpen = { activeModule = it })
            }
        }
    }

    if (studentDialog) {
        StudentFormDialog(
            onDismiss = { studentDialog = false },
            onSave = { name, phone, email, birthDate ->
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) { database.saveStudent(name, phone, email, birthDate) }
                        studentDialog = false
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
    onOpen: (String) -> Unit,
    onAddStudent: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Dashboard Secretária", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Acompanhe alunos, avaliações e próximos atendimentos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard("Alunos", studentCount.toString(), "Cadastros locais", Modifier.weight(1f))
                MetricCard("Avaliações", assessmentCount.toString(), "Histórico salvo", Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Agenda de hoje", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("A agenda ainda está vazia.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { onOpen("Agenda") }) { Text("Abrir agenda") }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Central de atenção", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider()
                    Text(
                        if (studentCount == 0) "Cadastre o primeiro aluno para iniciar o acompanhamento."
                        else "As pendências serão calculadas a partir dos dados cadastrados.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = onAddStudent) { Text("Cadastrar aluno") }
                }
            }
        }
        item { Text("Acesso rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction("Perfil Unificado", Icons.Default.Person, Modifier.weight(1f)) { onOpen("Perfil Unificado") }
                QuickAction("Avaliações", Icons.Default.FitnessCenter, Modifier.weight(1f)) { onOpen("Avaliações") }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, detail: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onOpen: (Long) -> Unit,
    onClearError: () -> Unit,
    subtitle: String = "Cadastre e acesse o Perfil Unificado de cada aluno."
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Alunos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onAdd) { Text("Adicionar aluno") }
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                TextButton(onClick = onClearError) { Text("Fechar aviso") }
            }
        }
        if (students.isEmpty()) {
            item {
                EmptyCard(
                    title = "Nenhum aluno cadastrado",
                    body = "Os dados ficam salvos neste aparelho. Cadastre um aluno para começar."
                )
            }
        }
        items(students, key = { it.id }) { student ->
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
private fun StudentFormDialog(onDismiss: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo aluno") },
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
                else onSave(name, phone, email, birthDate)
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
    val bmi = AssessmentMath.parseNumber(weight)?.let { w ->
        AssessmentMath.parseNumber(height)?.let { h -> AssessmentMath.bmi(w, h) }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Calculadora", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("IMC — estimativa calculada com peso e altura.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(weight, { weight = it }, label = { Text("Peso (kg)") }, singleLine = true)
        OutlinedTextField(height, { height = it }, label = { Text("Altura (cm)") }, singleLine = true)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Resultado", style = MaterialTheme.typography.titleMedium)
                Text(bmi?.let(::formatNumber) ?: "Preencha valores válidos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("O resultado é um índice matemático e não constitui diagnóstico.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ModuleListScreen(onOpen: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Módulos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Recursos que serão ativados em etapas da Beta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(moduleNames) { module ->
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
        Text("Este módulo está planejado para uma próxima etapa da Beta.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsScreen(dark: Boolean, onDarkChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Configurações", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Geral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(if (dark) "Modo escuro" else "Modo claro")
            Switch(checked = dark, onCheckedChange = onDarkChange)
        }
        HorizontalDivider()
        Text("Dados e privacidade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text("Os dados desta Beta ficam no armazenamento local do aplicativo. Backup e restauração serão adicionados antes da versão estável.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider()
        Text("EvoluFísico Android — Beta 1", color = MaterialTheme.colorScheme.onSurfaceVariant)
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

private fun formatNumber(value: Double): String =
    String.format(Locale("pt", "BR"), "%.1f", value)
