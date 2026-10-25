package br.com.evolufisico.nativo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.core.view.WindowCompat
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.*
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.IntegrityAuditor
import br.com.evolufisico.nativo.ui.screens.*

private data class PendingRoute(val index:Int,val create:Boolean,val client:String,val query:String,val date:String,val search:String="")
private data class Destination(val label: String, val icon: ImageVector, val group: String)
private val tabs = listOf(
    Destination("Início", Icons.Filled.Home,"Principal"),
    Destination("Avaliações", Icons.AutoMirrored.Filled.Assignment,"Acompanhamento"),
    Destination("Evolução", Icons.AutoMirrored.Filled.ShowChart,"Acompanhamento"),
    Destination("Calculadora", Icons.Filled.Calculate,"Ferramentas"),
    Destination("Formulários", Icons.Filled.Description,"Acompanhamento"),
    Destination("Análises", Icons.Filled.Psychology,"Acompanhamento"),
    Destination("Cobranças", Icons.Filled.Payments,"Gestão"),
    Destination("Configurações", Icons.Filled.Settings,"Ferramentas"),
    Destination("Organização", Icons.AutoMirrored.Filled.EventNote,"Principal"),
    Destination("Perfil Unificado", Icons.Filled.Person,"Acompanhamento"),

)
private val blues = workspacePalettes.map{Color(it.light)}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeApp(
    repository: AppRepository,
    message: String, onDismissMessage: () -> Unit,
    onCreateEvaluationPdf: (Evaluation) -> Unit,
    onCreateEvolutionPdf: (String, List<Evaluation>) -> Unit,
    onMonthlyBillingPdf:(List<Charge>,String)->Unit,
    onCreateBillingPdf: () -> Unit,
    onChooseFolder: () -> Unit, onBackup: (String) -> Unit, onRestore: (String) -> Unit,
    onImportLegacy: (String) -> Unit, onRestoreDevice: () -> Unit, onScheduleBackup: (String) -> Unit, onDisableBackup: () -> Unit,
    onEnableReminders: () -> Unit, onDisableReminders: () -> Unit, onExportCsv:(String,String)->Unit,onIntakePdf:(Intake)->Unit,onIndividualBackup:(String,String)->Unit,onOrganizationBackup:(String)->Unit,onAnalysisPdf:(String,List<String>,String)->Unit,onImportOrganizationJson:()->Unit,dataBusy:Boolean=false
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("appearance", android.content.Context.MODE_PRIVATE) }
    // A new app composition always starts at Início, including process restoration.
    var screen by remember { mutableIntStateOf(0) }
    var moduleMenuOpen by remember { mutableStateOf(false) }
    var createRequest by rememberSaveable { mutableIntStateOf(0) }
    var actionCounter by rememberSaveable { mutableIntStateOf(0) }
    var studentId by rememberSaveable { mutableStateOf("") }
    var routeQuery by rememberSaveable { mutableStateOf("") }
    var routeSearch by rememberSaveable {mutableStateOf("")}
    var searchOpen by remember {mutableStateOf(false)}
    var needsProfile by remember {mutableStateOf(context.getSharedPreferences("professional",android.content.Context.MODE_PRIVATE).getString("name","").orEmpty().isBlank())}
    var routeDate by rememberSaveable { mutableStateOf("") }
    val screenState=rememberSaveableStateHolder()
    val moduleOrder=br.com.evolufisico.nativo.domain.HtmlParity.navigation

    var editorActive by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<PendingRoute?>(null) }
    fun navigate(index:Int, create:Boolean=false, client:String="",query:String="",date:String="",search:String="") {
        if(index !in tabs.indices)return
        if(editorActive) {
            pendingRoute=PendingRoute(index,create,client,query,date,search)
            return
        }
        if(create || client.isNotBlank() || query.isNotBlank() || date.isNotBlank() || search.isNotBlank())screenState.removeState(index)
        screen=index;studentId=client;routeQuery=query;routeDate=date;routeSearch=search
        createRequest=if(create)++actionCounter else 0
    }
    var visualMode by remember {mutableStateOf(br.com.evolufisico.nativo.domain.VisualEffects.mode(prefs.getString("visual-mode",null)))}
    var compact by remember{mutableStateOf(prefs.getBoolean("compact",false))}
    var largeText by remember{mutableStateOf(prefs.getBoolean("large-text",false))}
    var dark by remember { mutableStateOf(prefs.getBoolean("dark", false)) }
    var tone by remember { mutableIntStateOf(prefs.getInt("web-tone", 0).coerceIn(0, blues.lastIndex)) }
    val notificationPrefs=context.getSharedPreferences("native-notifications",android.content.Context.MODE_PRIVATE)
    var reminderLead by remember { mutableIntStateOf(notificationPrefs.getInt("lead-minutes",60).takeIf{it in br.com.evolufisico.nativo.domain.ReminderPolicy.allowedLeadMinutes} ?: 60) }
    val colors=workspaceColors(tone,dark)
    SideEffect {
        (context as? android.app.Activity)?.let{activity->
            WindowCompat.getInsetsController(activity.window,activity.window.decorView).apply {
                isAppearanceLightStatusBars=!dark
                isAppearanceLightNavigationBars=!dark
            }
        }
    }
    CompositionLocalProvider(LocalEditorState provides {editorActive=it},LocalCompactWorkspace provides compact,LocalAdvancedEffects provides (visualMode=="advanced"),LocalLiquidEffects provides (visualMode=="liquid")) {
    MaterialTheme(colorScheme=colors,typography=workspaceTypography(largeText),shapes=Shapes(small=RoundedCornerShape(10.dp),medium=RoundedCornerShape(14.dp),large=RoundedCornerShape(22.dp))) {
        if(needsProfile) AlertDialog(onDismissRequest={},properties=androidx.compose.ui.window.DialogProperties(dismissOnBackPress=false,dismissOnClickOutside=false),
            title={Text("Seu perfil profissional")},text={Column(Modifier.heightIn(max=520.dp).verticalScroll(rememberScrollState())){ProfessionalSettings(onSaved={needsProfile=false},required=true)}},confirmButton={})
        if(searchOpen)WorkspaceSearchDialog(repository,onDismiss={searchOpen=false},onOpen={entry->searchOpen=false;navigate(entry.module,client=entry.client,query=entry.section,date=entry.date,search=entry.search)})
        if(dataBusy) AlertDialog(onDismissRequest={},
            properties=androidx.compose.ui.window.DialogProperties(dismissOnBackPress=false,dismissOnClickOutside=false),
            title={Text("Processando dados")},
            text={Column(verticalArrangement=Arrangement.spacedBy(16.dp)) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Text("Aguarde a conclusão da operação de dados.")
            }},confirmButton={})
        pendingRoute?.let { route -> AlertDialog(onDismissRequest={pendingRoute=null},title={Text("Sair da edição?")},
            text={Text("As alterações ainda não salvas serão perdidas ao trocar de módulo.")},
            confirmButton={TextButton(onClick={editorActive=false;pendingRoute=null;navigate(route.index,route.create,route.client,route.query,route.date,route.search)}){Text("Descartar e continuar")}},
            dismissButton={TextButton(onClick={pendingRoute=null}){Text("Continuar editando")}})
        }
        BackHandler(enabled=screen!=0 && !moduleMenuOpen) { navigate(0) }
        if(moduleMenuOpen) ModalBottomSheet(onDismissRequest={moduleMenuOpen=false}) {
            TextButton(onClick={moduleMenuOpen=false;searchOpen=true}){Icon(Icons.Filled.Search,null);Text("Pesquisar")}
            Text("Módulo",style=MaterialTheme.typography.titleLarge,modifier=Modifier.padding(horizontal=24.dp,vertical=8.dp))
            Column(Modifier.fillMaxWidth().heightIn(max=560.dp).verticalScroll(rememberScrollState()).padding(horizontal=12.dp).padding(bottom=24.dp)) {
                moduleOrder.forEach { index -> val destination=tabs[index]
                    NavigationDrawerItem(label={Text(destination.label)},selected=screen==index,
                        onClick={moduleMenuOpen=false;navigate(index)},icon={Icon(destination.icon,null)})
                }
            }
        }
        // Protect the whole shell,
        // including cutouts and landscape side bars, before measuring its contents.
        Box(Modifier.fillMaxSize().background(colors.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
            val imeVisible=WindowInsets.ime.getBottom(LocalDensity.current)>0
            Box {
                Scaffold(modifier=Modifier.imePadding(),contentWindowInsets=WindowInsets(0,0,0,0),
                    // Reserve space so the last content controls can scroll above the button.
                    floatingActionButtonPosition=FabPosition.Start,
                    floatingActionButton={if(!imeVisible)ExtendedFloatingActionButton(
                        onClick={moduleMenuOpen=true},modifier=Modifier.padding(bottom=24.dp),
                        containerColor=if(visualMode=="liquid")colors.surfaceContainerHigh else colors.primaryContainer,
                        contentColor=if(visualMode=="liquid")colors.onSurface else colors.onPrimaryContainer,
                        elevation=FloatingActionButtonDefaults.elevation(defaultElevation=if(visualMode=="liquid")12.dp else 6.dp),
                        icon={Icon(Icons.Filled.Menu,null)},text={Text("Módulo")})}
                ) {padding->
                    Row(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).padding(bottom=if(imeVisible)0.dp else 100.dp)) {
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            if(message.isNotBlank())Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=4.dp),verticalAlignment=Alignment.CenterVertically){
                                Text(message,Modifier.weight(1f),color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.bodySmall)
                                TextButton(onClick=onDismissMessage){Text("Fechar")}
                            }
                            key(screen) {screenState.SaveableStateProvider(screen) {when(screen) {
                                0->HomeScreen(repository,onOrganization={section,date->navigate(8,query=section,date=date)},onModule={index,client->navigate(index,client=client)},onSearch={searchOpen=true})
                                1->EvaluationsScreen(repository,onCreateEvaluationPdf,createRequest,studentId,onIndividualBackup)
                                2->EvolutionScreen(repository,onCreateEvolutionPdf,studentId)
                                3->CalculatorScreen()
                                4->IntakeScreen(repository,studentId,onIntakePdf)
                                5->AnalysisScreen(repository,studentId,onAnalysisPdf)
                                6->BillingScreen(repository,onCreateBillingPdf,studentId,onMonthlyBillingPdf)
                                8->OrganizationScreen(repository,onOrganizationBackup,onRestore,onImportOrganizationJson,initialView=routeQuery,initialDate=routeDate,initialSearch=routeSearch,onEvaluations={client->navigate(1,client=client)})
                                9->UnifiedProfileScreen(repository,studentId){index,client->navigate(index,client=client)}
                                7->SettingsScreen(initialSection=routeQuery,visualMode=visualMode,onVisualModeChange={visualMode=it;prefs.edit().putString("visual-mode",it).apply()},dark=dark,onDarkChange={dark=it;prefs.edit().putBoolean("dark",it).apply()},tone=tone,
                                    onToneChange={tone=it;prefs.edit().putInt("web-tone",it).apply()},palette=workspacePalettes.map{if(dark)Color(it.dark) else Color(it.light)},
                                    onChooseFolder,onBackup,onRestore,onImportLegacy,onRestoreDevice,onScheduleBackup,onDisableBackup,onEnableReminders,onDisableReminders,
                                    reminderLeadMinutes=reminderLead,onReminderLeadChange={lead->reminderLead=lead
                                        notificationPrefs.edit().putInt("lead-minutes",lead).apply()
                                        repository.rescheduleReminders()},
                                    onRunAudit={IntegrityAuditor.run(repository.evaluations.toList(),repository.charges.toList(),repository.intakes.toList(),repository.appointments.toList(),repository.tasks.toList()).description()},compact=compact,onCompactChange={compact=it;prefs.edit().putBoolean("compact",it).apply()},
                                    largeText=largeText,onLargeTextChange={largeText=it;prefs.edit().putBoolean("large-text",it).apply()})

                            }}}
                        }
                    }
                }
            }
        }
    }
}

}


