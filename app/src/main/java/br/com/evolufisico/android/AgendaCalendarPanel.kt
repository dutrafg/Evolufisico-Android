package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.agenda.AgendaCalendar
import br.com.evolufisico.android.ops.Appointment
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaCalendarPanel(
    appointments: List<Appointment>,
    selected: LocalDate?,
    onSelect: (LocalDate?) -> Unit
) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    val weeks = remember(month, appointments) { AgendaCalendar.weeks(month, appointments) }
    val locale = Locale.forLanguageTag("pt-BR")
    val caption = month.atDay(1).format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", locale))
    Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(VisualTokens.cardRadiusDp.dp),
        colors=CardDefaults.cardColors(
            containerColor=VisualTokens.panel(VisualTokens.dark(MaterialTheme.colorScheme))),
        elevation=CardDefaults.cardElevation(defaultElevation=1.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Calendário de atendimentos",style=MaterialTheme.typography.titleMedium,
                color=MaterialTheme.colorScheme.onSurface)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                IconButton(onClick={month=month.minusMonths(1)}) {
                    Icon(Icons.Outlined.ChevronLeft,contentDescription="Mês anterior",
                        tint=MaterialTheme.colorScheme.onSurface)
                }
                Text(caption,modifier=Modifier.weight(1f).padding(top=10.dp),
                    textAlign=TextAlign.Center,style=MaterialTheme.typography.titleSmall,
                    color=MaterialTheme.colorScheme.onSurface)
                IconButton(onClick={month=month.plusMonths(1)}) {
                    Icon(Icons.Outlined.ChevronRight,contentDescription="Próximo mês",
                        tint=MaterialTheme.colorScheme.onSurface)
                }
            }
            // A largura definida evita linhas de calendario sem restricoes
            // dentro da rolagem horizontal. Cada coluna tem ao menos 48 dp.
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val gridWidth = maxOf(maxWidth, 336.dp)
                Column(Modifier.fillMaxWidth(),
                    verticalArrangement=Arrangement.spacedBy(4.dp)) {
                Box(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    Column(Modifier.width(gridWidth),
                        verticalArrangement=Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom").forEach { day ->
                            Text(day,modifier=Modifier.weight(1f),
                                textAlign=TextAlign.Center,
                                style=MaterialTheme.typography.labelSmall,
                                color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    weeks.forEach { week ->
                        Row(Modifier.fillMaxWidth()) {
                            week.forEach { cell ->
                                if(cell==null) {
                                    Spacer(Modifier.weight(1f).height(48.dp))
                                } else {
                                    val chosen=selected==cell.date
                                    val today=cell.date==LocalDate.now()
                                    val spokenDate=cell.date.format(
                                        DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",locale))
                                    val accessibleDate=buildString {
                                        append(spokenDate)
                                        if(today) append(", hoje")
                                        if(chosen) append(", selecionado")
                                        if(cell.scheduled>0) {
                                            append(", ")
                                            append(cell.scheduled)
                                            append(" atendimento(s)")
                                        }
                                    }
                                    TextButton(
                                        onClick={onSelect(if(chosen) null else cell.date)},
                                        modifier=Modifier.weight(1f).heightIn(min=48.dp)
                                            .semantics { contentDescription=accessibleDate },
                                        shape=RoundedCornerShape(10.dp),
                                        colors=ButtonDefaults.textButtonColors(
                                            containerColor=when {
                                                chosen -> MaterialTheme.colorScheme.primaryContainer
                                                today -> MaterialTheme.colorScheme.surfaceVariant
                                                else -> Color.Transparent
                                            }),
                                        contentPadding=PaddingValues(0.dp)
                                    ) {
                                        Text(cell.date.dayOfMonth.toString()+
                                            (if(cell.scheduled>0)" •" else ""),
                                            color=when {
                                                chosen -> MaterialTheme.colorScheme.onPrimaryContainer
                                                today -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            style=if(chosen||today)MaterialTheme.typography.labelLarge
                                                else MaterialTheme.typography.labelSmall,
                                            textAlign=TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                    }
                }
                if(maxWidth < gridWidth) {
                    Text("Deslize para os lados para ver os sete dias",
                        style=MaterialTheme.typography.bodySmall,
                        color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
                }
            }
            Text("• = atendimento agendado · "+AgendaCalendar.active(month,appointments)+
                " ativos no mês",style=MaterialTheme.typography.bodySmall,
                color=MaterialTheme.colorScheme.onSurfaceVariant)
            if(selected!=null) {
                val selectedLabel="Dia selecionado: "+selected.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy",locale))
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val narrow=maxWidth<340.dp || LocalDensity.current.fontScale>=1.5f
                    if(narrow) {
                        Column(Modifier.fillMaxWidth(),
                            verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text(selectedLabel,style=MaterialTheme.typography.bodySmall)
                            OutlinedButton(onClick={onSelect(null)},
                                modifier=Modifier.fillMaxWidth().heightIn(min=48.dp)) {
                                Text("Limpar dia")
                            }
                        }
                    } else {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement=Arrangement.spacedBy(8.dp),
                            verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                            Text(selectedLabel,modifier=Modifier.weight(1f),
                                style=MaterialTheme.typography.bodySmall)
                            OutlinedButton(onClick={onSelect(null)}){Text("Limpar dia")}
                        }
                    }
                }
            }
            OutlinedButton(
                onClick={month=YearMonth.now();onSelect(LocalDate.now())},
                modifier=Modifier.fillMaxWidth()
            ) { Text("Ir para hoje") }
        }
    }
}
