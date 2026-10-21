package br.com.evolufisico.android

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.evolufisico.android.backup.SecureBackupManager
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.reporting.CsvReports
import br.com.evolufisico.android.reporting.ReportFilter
import br.com.evolufisico.android.reporting.ReportKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@Composable
fun ReportsScreen(
    clients: List<Client>,
    manager: SecureBackupManager,
    onDocumentFlow: (Boolean)->Unit
) {
    val context= LocalContext.current
    val scope=rememberCoroutineScope()
    var kind by remember { mutableStateOf(ReportKind.OVERVIEW) }
    var selectedClient by remember { mutableStateOf<String?>(null) }
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    var chooseKind by remember { mutableStateOf(false) }
    var chooseClient by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val launcher=rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        onDocumentFlow(false)
        if(uri != null && !processing) {
            processing=true
            scope.launch {
                val result=withContext(Dispatchers.IO) {
                    runCatching {
                        val filter=ReportFilter(
                            clientId=selectedClient,
                            from=from.takeIf{it.isNotBlank()}?.let(LocalDate::parse),
                            to=to.takeIf{it.isNotBlank()}?.let(LocalDate::parse)
                        )
                        val bytes=CsvReports.generate(manager.summary(),kind,filter)
                        context.contentResolver.openOutputStream(uri,"w")?.use {
                            it.write(bytes)
                            it.flush()
                        } ?: error("A pasta escolhida não permite gravação.")
                        bytes.size
                    }
                }
                message=result.fold(
                    onSuccess={ "Relatório CSV exportado com sucesso ("+it+" bytes)." },
                    onFailure={ "Falha ao exportar: "+(it.localizedMessage?:"Verifique filtros e destino.") }
                )
                processing=false
            }
        }
    }
    Box(Modifier.fillMaxSize(),contentAlignment=Alignment.TopCenter) {
    Column(Modifier.fillMaxWidth().widthIn(max=VisualTokens.contentMaxWidthDp.dp)
        .verticalScroll(rememberScrollState())
        .padding(VisualLayout.edgePaddingDp(LocalConfiguration.current.screenWidthDp).dp),
        verticalArrangement=Arrangement.spacedBy(13.dp)) {
        Text("Relatórios e exportações",style=MaterialTheme.typography.headlineSmall)
        Text("Exporte dados locais para planilhas CSV, com filtros opcionais. " +
            "Nenhuma informação é enviada para servidores.",
            color=MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Tipo de relatório",style=MaterialTheme.typography.titleMedium)
        Box {
            OutlinedButton(onClick={chooseKind=true},modifier=Modifier.fillMaxWidth(),
                enabled=!processing){Text(kind.title)}
            DropdownMenu(expanded=chooseKind,onDismissRequest={chooseKind=false}) {
                ReportKind.entries.forEach { option ->
                    DropdownMenuItem(text={Text(option.title)},onClick={
                        kind=option;chooseKind=false
                    })
                }
            }
        }
        Text("Filtrar por cliente",style=MaterialTheme.typography.titleMedium)
        Box {
            OutlinedButton(onClick={chooseClient=true},modifier=Modifier.fillMaxWidth(),
                enabled=!processing) {
                Text(clients.firstOrNull{it.id==selectedClient}?.name?:"Todos os clientes")
            }
            DropdownMenu(expanded=chooseClient,onDismissRequest={chooseClient=false}) {
                DropdownMenuItem(text={Text("Todos os clientes")},onClick={
                    selectedClient=null;chooseClient=false
                })
                clients.forEach { person ->
                    DropdownMenuItem(text={Text(person.name)},onClick={
                        selectedClient=person.id;chooseClient=false
                    })
                }
            }
        }
        Text("Período opcional",style=MaterialTheme.typography.titleMedium)
        OutlinedTextField(value=from,onValueChange={from=it.take(10)},
            label={Text("A partir de (AAAA-MM-DD)")},
            singleLine=true,modifier=Modifier.fillMaxWidth())
        OutlinedTextField(value=to,onValueChange={to=it.take(10)},
            label={Text("Até (AAAA-MM-DD)")},
            singleLine=true,modifier=Modifier.fillMaxWidth())
        Text("A filtragem temporal utiliza a data principal do registro. " +
            "O catálogo de serviços e a lista de clientes não dependem de datas.",
            color=MaterialTheme.colorScheme.onSurfaceVariant,
            style=MaterialTheme.typography.bodySmall)
        Button(enabled=!processing,modifier=Modifier.fillMaxWidth(),onClick={
            val check=runCatching{
                ReportFilter(selectedClient,
                    from.takeIf{it.isNotBlank()}?.let(LocalDate::parse),
                    to.takeIf{it.isNotBlank()}?.let(LocalDate::parse))
            }
            if(check.isFailure) {
                message="Período inválido. Utilize AAAA-MM-DD e início anterior ao fim."
            } else {
                message=null
                onDocumentFlow(true)
                launcher.launch("EvoluFisico_${kind.filename}_${LocalDate.now()}.csv")
            }
        }){Text(if(processing)"Exportando..." else "Exportar CSV")}
        message?.let { Text(it,color=MaterialTheme.colorScheme.primary) }
        HorizontalDivider()
        Text("Dados do protótipo",style=MaterialTheme.typography.titleMedium)
        Text("Arquivos CSV e JSON gerados nesta fase não possuem criptografia. " +
            "Use somente dados fictícios até a implantação das proteções finais.",
            color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
    }
}
