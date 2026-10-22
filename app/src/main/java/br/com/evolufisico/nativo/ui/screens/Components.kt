package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import br.com.evolufisico.nativo.ui.LocalCompactWorkspace
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable fun Screen(content: @Composable ColumnScope.() -> Unit) {
    val inset=if(LocalCompactWorkspace.current)12.dp else 18.dp
    val gap=if(LocalCompactWorkspace.current)8.dp else 12.dp
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
        Column(Modifier.widthIn(max=1120.dp).fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(start=inset,end=inset,top=inset,bottom=104.dp),
            verticalArrangement=Arrangement.spacedBy(gap),content=content)
    }
}
@Composable fun Heading(title: String, description: String = "") {
    var help by remember(title) { mutableStateOf(false) }
    if(help) AlertDialog(onDismissRequest={help=false},title={Text(title)},text={Text(description)},
        confirmButton={TextButton(onClick={help=false}){Text("Entendi")}})
    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
        if(description.isNotBlank()) IconButton(onClick={help=true},modifier=Modifier.size(44.dp)) {
            Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription="Ajuda sobre $title",modifier=Modifier.size(22.dp))
        }
    }
    if (description.isNotBlank()) Text(description, style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}
@Composable fun Field(label: String, value: String, onChange: (String) -> Unit,
                      modifier: Modifier = Modifier, lines: Int = 1) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) },
        singleLine=lines==1,
        modifier = modifier.fillMaxWidth(), minLines = lines, maxLines = if (lines > 1) 5 else 1)
}
@Composable fun DataCard(title: String, detail: String, actions: @Composable RowScope.() -> Unit = {}) {
    val inset=if(LocalCompactWorkspace.current)12.dp else 16.dp
    OutlinedCard(Modifier.fillMaxWidth(),colors=CardDefaults.outlinedCardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(inset), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(detail, style = MaterialTheme.typography.bodyMedium)
            // Actions scroll rather than stretching/compressing help and action controls on narrow phones.
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
        }
    }
}
@Composable fun PillChoices(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { item -> FilterChip(selected = item == selected, onClick = { onSelected(item) }, label = { Text(item) }) }
    }
}
fun money(cents: Long): String = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(cents / 100.0)
/** Disambiguates students with identical names without exposing full IDs. */
fun studentChoices(students: List<br.com.evolufisico.nativo.data.Evaluation>): List<Pair<String,String>> {
    val unique=students.distinctBy{it.studentId}
    val perName=unique.groupingBy { it.name.lowercase().trim() }.eachCount()
    return unique.map{student ->
        val name=student.name
        student.studentId to if((perName[name.lowercase().trim()]?:0)>1)
            "$name · ${student.studentId.take(6)}" else name
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun DateField(label: String, value: String, onChange: (String) -> Unit) {
    var visible by remember(label) { mutableStateOf(false) }
    val current = remember(value) { runCatching { LocalDate.parse(value) }.getOrNull()?.takeIf{it.year in 1..9999} ?: LocalDate.now() }
    val picker = rememberDatePickerState(
        initialSelectedDateMillis = current.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        yearRange = 1..9999
    )
    LaunchedEffect(current) {
        picker.selectedDateMillis = current.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { Field(label, value, onChange) }
        OutlinedButton(onClick = { visible = true }) { Text("Calendário") }
    }
    if (visible) DatePickerDialog(onDismissRequest = { visible = false },
        confirmButton = { TextButton(onClick = {
            picker.selectedDateMillis?.let { millis ->
                onChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
            }
            visible = false
        }) { Text("Selecionar") } },
        dismissButton = { TextButton(onClick = { visible = false }) { Text("Cancelar") } }) {
        DatePicker(state = picker, showModeToggle = false)
    }
}

/** Bound composition cost for large local databases; every page remains reachable. */
@Composable fun <T> PagedRecords(records:List<T>, resetKey:String="", itemKey:(T)->Any, content:@Composable (T)->Unit) {
    var page by androidx.compose.runtime.saveable.rememberSaveable(resetKey){mutableIntStateOf(0)}
    val last=((records.size-1).coerceAtLeast(0))/30
    val current=page.coerceIn(0,last)
    @Composable fun Controls() {
        if(records.size>30) Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) {
            TextButton(enabled=current>0,onClick={page=current-1}){Text("Anterior")}
            Text("${current+1} / ${last+1} · ${records.size} registros",Modifier.weight(1f),textAlign=androidx.compose.ui.text.style.TextAlign.Center,style=MaterialTheme.typography.labelMedium)
            TextButton(enabled=current<last,onClick={page=current+1}){Text("Próxima")}
        }
    }
    Controls()
    val bounds=br.com.evolufisico.nativo.domain.LayoutPolicy.pageBounds(records.size,current)
    bounds.forEach { index -> val record=records[index]; key(itemKey(record)){content(record)} }
    Controls()
}

@Composable fun NumericField(label:String,value:String,onChange:(String)->Unit,decimal:Boolean=false) {
    OutlinedTextField(value,onValueChange=onChange,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true,
        keyboardOptions=KeyboardOptions(keyboardType=if(decimal)KeyboardType.Decimal else KeyboardType.Number,imeAction=ImeAction.Next))
}
