package br.com.evolufisico.android

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import br.com.evolufisico.android.evolution.EvolutionAnalytics
import br.com.evolufisico.android.evolution.EvolutionMetric
import br.com.evolufisico.android.evolution.EvolutionPeriod
import br.com.evolufisico.android.evolution.EvolutionSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private data class PendingExport(val clientName: String, val series: EvolutionSeries)

@Composable
fun EvolutionScreen(clients: List<Client>, store: EncryptedAssessmentStore, onDocumentFlow: (Boolean) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var assessments by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var chosenClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
    var metric by remember { mutableStateOf(EvolutionMetric.WEIGHT) }
    var period by remember { mutableStateOf(EvolutionPeriod.ALL) }
    var pending by remember { mutableStateOf<PendingExport?>(null) }
    var exporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(store) {
        val result = withContext(Dispatchers.IO) { runCatching { store.load() } }
        result.onSuccess { assessments = it }
            .onFailure { loadError = "Não foi possível ler as avaliações criptografadas. Os arquivos foram preservados." }
        loading = false
    }
    LaunchedEffect(clients) {
        if (clients.isNotEmpty() && clients.none { it.id == chosenClientId }) {
            chosenClientId = clients.first().id
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        onDocumentFlow(false)
        val snapshot = pending
        pending = null
        if (uri != null && snapshot != null && !exporting) {
            exporting = true
            exportMessage = null
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        val stream = context.contentResolver.openOutputStream(uri, "w")
                            ?: throw IllegalStateException("Não foi possível abrir o destino escolhido.")
                        stream.use { EvolutionPdfReport.write(it, snapshot.clientName, snapshot.series) }
                    }
                }
                exportMessage = result.fold(
                    onSuccess = { "PDF salvo no local escolhido." },
                    onFailure = { "Falha ao exportar PDF: ${it.localizedMessage ?: "erro de gravação"}" }
                )
                exporting = false
            }
        }
    }

    if (loading) {
        EvolutionNotice("Carregando avaliações locais protegidas...")
        return
    }
    if (loadError != null) {
        EvolutionNotice(loadError!!)
        return
    }
    if (clients.isEmpty()) {
        EvolutionNotice("Cadastre um cliente e uma avaliação antes de consultar a evolução.")
        return
    }

    val selectedName = clients.firstOrNull { it.id == chosenClientId }?.name ?: "Cliente"
    val series = remember(assessments, chosenClientId, metric, period) {
        EvolutionAnalytics.build(assessments, chosenClientId, metric, period)
    }
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")) }

    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal=VisualLayout.edgePaddingDp(
                androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp).dp,
            vertical=20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Evolução e resultados", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold)
            Text("Comparação descritiva de medidas, vinculada ao cliente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            DropdownSelector(
                label = "Cliente",
                value = selectedName,
                choices = clients.map { it.id to it.name },
                onSelect = { chosenClientId = it; exportMessage = null }
            )
        }
        item {
            DropdownSelector(
                label = "Indicador",
                value = metric.title,
                choices = EvolutionMetric.entries.map { it.name to it.title },
                onSelect = { metric = EvolutionMetric.valueOf(it); exportMessage = null }
            )
        }
        item {
            Text("Período", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()) {
                EvolutionPeriod.entries.forEach { choice ->
                    FilterChip(
                        selected = period == choice,
                        onClick = { period = choice; exportMessage = null },
                        label = { Text(choice.title, maxLines = 1) }
                    )
                }
            }
        }
        item {
            Card(shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
                modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(metric.title, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary)
                    if (series.points.isEmpty()) {
                        Text("Sem medições válidas para este indicador e período.")
                    } else {
                        Text(
                            "Primeira: ${fmt(series.first!!)} ${metric.unit}   •   Última: ${fmt(series.last!!)} ${metric.unit}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        series.absoluteChange?.let { change ->
                            Text(
                                "Variação: ${signed(change)} ${metric.unit} (${signed(series.percentChange!!)}%)",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TrendGraph(series)
                        Text("${series.points.size} ponto(s) • Sem estimativas ou diagnósticos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (series.ignoredInvalid > 0) {
            item {
                Text("${series.ignoredInvalid} avaliação(ões) não possuem valor ou data utilizável para este indicador.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Button(
                enabled = !exporting && series.points.isNotEmpty(),
                onClick = {
                    pending = PendingExport(selectedName, series.copy(points = series.points.toList()))
                    onDocumentFlow(true)
                    launcher.launch("EvoluFisico_Evolucao_${java.time.LocalDate.now()}.pdf")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (exporting) "Gerando PDF..." else "Exportar relatório PDF")
            }
            Text(
                "Ao exportar, o PDF fica sem criptografia. Escolha um local seguro para dados pessoais.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            exportMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Text("Histórico do indicador", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
        }
        items(series.points.asReversed().take(100), key = { it.assessmentId }) { point ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(point.date.format(formatter))
                    Text("${fmt(point.value)} ${metric.unit}", fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (series.points.size > 100) {
            item { Text("Exibidos os 100 registros mais recentes. O relatório contém todos os pontos.",
                style = MaterialTheme.typography.bodySmall) }
        }
    }
    }
}

@Composable
private fun TrendGraph(series: EvolutionSeries) {
    val primary = MaterialTheme.colorScheme.primary
    val subtle = MaterialTheme.colorScheme.outlineVariant
    val values = series.points.map { it.value }
    val label = "Gráfico ${series.metric.title}: " +
        series.points.joinToString("; ") { "${it.date}: ${fmt(it.value)}" }.take(800)
    Canvas(
        modifier = Modifier.fillMaxWidth().height(190.dp).semantics { contentDescription = label }
    ) {
        val left = 12.dp.toPx()
        val top = 12.dp.toPx()
        val right = size.width - 12.dp.toPx()
        val bottom = size.height - 12.dp.toPx()
        if (values.isEmpty()) return@Canvas
        val lo = values.minOrNull()!!
        val hi = values.maxOrNull()!!
        val spread = (hi - lo).takeIf { it > 0.00001 } ?: maxOf(1.0, abs(hi) * 0.1)
        val floor = lo - spread * 0.15
        val ceil = hi + spread * 0.15
        fun x(index: Int): Float = if (values.size == 1) (left + right) / 2f
            else left + (right - left) * index / (values.size - 1).toFloat()
        fun y(value: Double): Float =
            bottom - ((value - floor) / (ceil - floor) * (bottom - top)).toFloat()
        drawLine(subtle, Offset(left, bottom), Offset(right, bottom), strokeWidth = 1.dp.toPx())
        drawLine(subtle, Offset(left, top), Offset(left, bottom), strokeWidth = 1.dp.toPx())
        values.forEachIndexed { index, value ->
            if (index > 0) {
                drawLine(primary, Offset(x(index - 1), y(values[index - 1])),
                    Offset(x(index), y(value)), strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round)
            }
            drawCircle(primary, radius = 4.dp.toPx(), center = Offset(x(index), y(value)))
        }
    }
}

@Composable
private fun DropdownSelector(
    label: String,
    value: String,
    choices: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(value, maxLines = 1)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                choices.forEach { (id, text) ->
                    DropdownMenuItem(text = { Text(text) }, onClick = {
                        onSelect(id)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun EvolutionNotice(message: String) {
    Column(Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
    }
}

private fun fmt(number: Double): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%.2f", number)

private fun signed(number: Double): String =
    String.format(Locale.forLanguageTag("pt-BR"), "%+.2f", number)
