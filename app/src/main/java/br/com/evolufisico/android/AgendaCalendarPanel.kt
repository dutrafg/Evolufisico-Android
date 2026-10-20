package br.com.evolufisico.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
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
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Calendário de atendimentos",style=MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                TextButton(onClick={month=month.minusMonths(1)}) { Text("‹ Anterior") }
                Text(caption,modifier=Modifier.weight(1f).padding(top=12.dp),
                    textAlign=TextAlign.Center,style=MaterialTheme.typography.labelMedium)
                TextButton(onClick={month=month.plusMonths(1)}) { Text("Próximo ›") }
            }
            Row(Modifier.fillMaxWidth()) {
                listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom").forEach { day ->
                    Text(day,modifier=Modifier.weight(1f),textAlign=TextAlign.Center,
                        style=MaterialTheme.typography.labelSmall)
                }
            }
            weeks.forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { cell ->
                        if (cell == null) {
                            Text("",modifier=Modifier.weight(1f))
                        } else {
                            val chosen=selected==cell.date
                            val today=cell.date==LocalDate.now()
                            TextButton(
                                onClick={onSelect(if(chosen) null else cell.date)},
                                modifier=Modifier.weight(1f).padding(0.dp),
                                contentPadding=androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text(
                                    cell.date.dayOfMonth.toString()+
                                        (if(cell.scheduled>0)" •" else ""),
                                    color=if(chosen) MaterialTheme.colorScheme.primary
                                        else if(today) MaterialTheme.colorScheme.tertiary
                                        else MaterialTheme.colorScheme.onSurface,
                                    style=if(chosen)MaterialTheme.typography.labelLarge
                                        else MaterialTheme.typography.labelSmall,
                                    textAlign=TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Text("• = atendimento agendado · "+AgendaCalendar.active(month,appointments)+
                " ativos no mês",style=MaterialTheme.typography.bodySmall)
            if(selected!=null) {
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    Text("Selecionado: "+selected,
                        modifier=Modifier.weight(1f),style=MaterialTheme.typography.bodySmall)
                    OutlinedButton(onClick={onSelect(null)}){Text("Limpar dia")}
                }
            }
            OutlinedButton(
                onClick={month=YearMonth.now();onSelect(LocalDate.now())},
                modifier=Modifier.fillMaxWidth()
            ) { Text("Ir para hoje") }
        }
    }
}
