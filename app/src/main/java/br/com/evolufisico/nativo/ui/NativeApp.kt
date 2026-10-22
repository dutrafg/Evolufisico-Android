package br.com.evolufisico.nativo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.*
import br.com.evolufisico.nativo.domain.IntegrityAuditor
import br.com.evolufisico.nativo.domain.WorkspaceRules
import br.com.evolufisico.nativo.ui.screens.*
import androidx.compose.ui.platform.LocalConfiguration
import br.com.evolufisico.nativo.domain.ModuleOrder

private data class PendingRoute(val index:Int,val create:Boolean,val client:String,val query:String,val date:String)
private data class Destination(val label: String, val icon: ImageVector, val group: String)
private val tabs = listOf(
    Destination("Início", Icons.Filled.Home,"Principal"),
    Destination("Avaliações", Icons.AutoMirrored.Filled.Assignment,"Acompanhamento"),
    Destination("Evolução", Icons.AutoMirrored.Filled.ShowChart,"Acompanhamento"),
    Destination("Calculadora", Icons.Filled.Calculate,"Ferramentas"),
    Destination("Formulários", Icons.Filled.Description,"Acompanhamento"),
    Destination("Análises", Icons.Filled.Analytics,"Acompanhamento"),
    Destination("Cobrança", Icons.Filled.Payments,"Gestão"),
    Destination("Configurações", Icons.Filled.Settings,"Ferramentas"),
    Destination("Clientes", Icons.Filled.Groups,"Principal"),
    Destination("Agenda", Icons.Filled.CalendarMonth,"Principal"),
    Destination("Tarefas", Icons.Filled.TaskAlt,"Gestão"),
    Destination("Metas", Icons.Filled.Flag,"Acompanhamento"),
    Destination("Treinos", Icons.Filled.FitnessCenter,"Acompanhamento"),
    Destination("Hábitos", Icons.Filled.WaterDrop,"Acompanhamento"),
    Destination("Notas", Icons.AutoMirrored.Filled.StickyNote2,"Gestão"),
    Destination("Relatórios", Icons.Filled.Assessment,"Gestão")
)
private val blues = workspacePalettes.map{Color(it.light)}

@Composable
private fun ModuleMenu(screen: Int, query: String, onQuery:(String)->Unit, onSelect:(Int)->Unit, modifier:Modifier=Modifier,favorites:Set<Int> = emptySet(),onFavorite:(Int)->Unit={},order:List<Int> = ModuleOrder.defaults,onOrganize:()->Unit={}) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(12.dp),verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text("EvoluFísico",Modifier.padding(12.dp),style=MaterialTheme.typography.titleLarge)
        TextButton(onClick=onOrganize){Icon(Icons.Filled.Edit,null);Spacer(Modifier.width(8.dp));Text("Organizar módulos")}
        OutlinedTextField(query,onQuery,label={Text("Buscar módulo")},singleLine=true,modifier=Modifier.fillMaxWidth())
        if(favorites.isNotEmpty()) {
            Text("Favoritos",Modifier.padding(start=12.dp,top=12.dp),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
            order.filter{it in favorites}.filter{it in tabs.indices && WorkspaceRules.search(tabs[it].label,query)}.forEach{index->
                NavigationDrawerItem(label={Text(tabs[index].label)},selected=screen==index,onClick={onSelect(index)},
                    icon={Icon(tabs[index].icon,null)},shape=MaterialTheme.shapes.small)
            }
        }
        if(tabs.none{WorkspaceRules.search(it.label,query)})Text("Nenhum módulo encontrado.",Modifier.padding(12.dp))
        order.filter{WorkspaceRules.search(tabs[it].label,query)}.forEach{index->
            val dest=tabs[index]
            NavigationDrawerItem(label={Text(dest.label)},selected=screen==index,
                onClick={onSelect(index)},icon={Icon(dest.icon,null)},
                badge={IconButton(onClick={onFavorite(index)}){Icon(if(index in favorites)Icons.Filled.Star else Icons.Filled.StarBorder,
                    if(index in favorites)"Remover ${dest.label} dos favoritos" else "Favoritar ${dest.label}",Modifier.size(20.dp))}},shape=MaterialTheme.shapes.small)
        }
    }
}

@Composable
fun NativeApp(
    repository: AppRepository,
    message: String, onDismissMessage: () -> Unit,
    onCreateEvaluationPdf: (Evaluation) -> Unit,
    onCreateEvolutionPdf: (String, List<Evaluation>) -> Unit,
    onCreateBillingPdf: () -> Unit,
    onChooseFolder: () -> Unit, onBackup: (String) -> Unit, onRestore: (String) -> Unit,
    onImportLegacy: (String) -> Unit, onRestoreDevice: () -> Unit, onScheduleBackup: (String) -> Unit, onDisableBackup: () -> Unit,
    onEnableReminders: () -> Unit, onDisableReminders: () -> Unit, onExportCsv:(String,String)->Unit, onCreateTrainingPdf:(TrainingSession)->Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("appearance", android.content.Context.MODE_PRIVATE) }
    var screen by rememberSaveable { mutableIntStateOf(0) }
    var createRequest by rememberSaveable { mutableIntStateOf(0) }
    var actionCounter by rememberSaveable { mutableIntStateOf(0) }
    var studentId by rememberSaveable { mutableStateOf("") }
    var routeQuery by rememberSaveable { mutableStateOf("") }
    var routeDate by rememberSaveable { mutableStateOf("") }
    var fabOpen by rememberSaveable { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var globalQuery by rememberSaveable { mutableStateOf("") }
    var moduleQuery by rememberSaveable { mutableStateOf("") }
    val screenState=rememberSaveableStateHolder()
    var modulesOpen by rememberSaveable { mutableStateOf(false) }
    var moduleOrder by remember { mutableStateOf(ModuleOrder.parse(prefs.getString("module-order",null))) }
    fun saveOrder(value:List<Int>){moduleOrder=ModuleOrder.normalize(value);prefs.edit().putString("module-order",moduleOrder.joinToString(",")).apply()}
    var editorActive by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<PendingRoute?>(null) }
    fun navigate(index:Int, create:Boolean=false, client:String="",query:String="",date:String="") {
        if(editorActive) {
            pendingRoute=PendingRoute(index,create,client,query,date)
            searchOpen=false;fabOpen=false;modulesOpen=false
            return
        }
        if(create || client.isNotBlank() || query.isNotBlank() || date.isNotBlank())screenState.removeState(index)
        screen=index;studentId=client;routeQuery=query;routeDate=date
        createRequest=if(create)++actionCounter else 0
        fabOpen=false;searchOpen=false
        modulesOpen=false
    }
    var favorites by remember {mutableStateOf(prefs.getStringSet("favorites",setOf("1","12"))!!.mapNotNull{it.toIntOrNull()}.filter{it in tabs.indices}.toSet())}
    fun toggleFavorite(index:Int){favorites=if(index in favorites)favorites-index else favorites+index;prefs.edit().putStringSet("favorites",favorites.map{it.toString()}.toSet()).apply()}
    var compact by remember{mutableStateOf(prefs.getBoolean("compact",false))}
    var largeText by remember{mutableStateOf(prefs.getBoolean("large-text",false))}
    var dark by remember { mutableStateOf(prefs.getBoolean("dark", false)) }
    var tone by remember { mutableIntStateOf(prefs.getInt("tone", 1).coerceIn(0, blues.lastIndex)) }
    val notificationPrefs=context.getSharedPreferences("native-notifications",android.content.Context.MODE_PRIVATE)
    var reminderLead by remember { mutableIntStateOf(notificationPrefs.getInt("lead-minutes",60).takeIf{it in listOf(15,30,60,1440)} ?: 60) }
    val colors=workspaceColors(tone,dark)
    CompositionLocalProvider(LocalEditorState provides {editorActive=it},LocalCompactWorkspace provides compact) {
    MaterialTheme(colorScheme=colors,typography=workspaceTypography(largeText),shapes=Shapes(small=RoundedCornerShape(6.dp),medium=RoundedCornerShape(8.dp),large=RoundedCornerShape(12.dp))) {
        pendingRoute?.let { route -> AlertDialog(onDismissRequest={pendingRoute=null},title={Text("Sair da edição?")},
            text={Text("As alterações ainda não salvas serão perdidas ao trocar de módulo.")},
            confirmButton={TextButton(onClick={editorActive=false;pendingRoute=null;navigate(route.index,route.create,route.client,route.query,route.date)}){Text("Descartar e continuar")}},
            dismissButton={TextButton(onClick={pendingRoute=null}){Text("Continuar editando")}})
        }
        BackHandler(enabled=fabOpen || modulesOpen || screen!=0) {
            when {fabOpen->fabOpen=false;modulesOpen->modulesOpen=false;else->navigate(0)}
        }
        if(searchOpen) AlertDialog(onDismissRequest={searchOpen=false},title={Text("Busca global")},text={
            Column(Modifier.heightIn(max=440.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                Field("Módulo, cliente, nota ou atendimento",globalQuery,{globalQuery=it})
                if(globalQuery.isBlank())Text("Digite para pesquisar nos registros locais.")
                else {
                    tabs.forEachIndexed{index,dest->if(WorkspaceRules.search(dest.label,globalQuery))TextButton(onClick={navigate(index)}){Text("Módulo · ${dest.label}")}}
                    WorkspaceRules.latest(repository.evaluations.toList()).filter{WorkspaceRules.search(it.name+" "+it.email+" "+it.phone,globalQuery)}.take(8).forEach{client->
                        TextButton(onClick={navigate(8,client=client.studentId,query=client.name)}){Text("Cliente · ${client.name} · ${client.studentId.take(6)}")}
                    }
                    repository.notes.filter{!it.archived && WorkspaceRules.search(it.title+" "+it.body+" "+it.student,globalQuery)}.take(5).forEach{note->
                        TextButton(onClick={navigate(14,query=globalQuery)}){Text("Nota · ${note.title}")}
                    }
                    repository.appointments.filter{WorkspaceRules.search(it.student+" "+it.type+" "+it.date,globalQuery)}.take(5).forEach{item->
                        TextButton(onClick={navigate(9,date=item.date)}){Text("Agenda · ${item.date} ${item.time} · ${item.student}")}
                    }
                }
            }
        },confirmButton={TextButton(onClick={searchOpen=false}){Text("Fechar")}})
        if(modulesOpen) ModuleHub(moduleOrder,{modulesOpen=false},{saveOrder(it)},{navigate(it)},favorites,{toggleFavorite(it)})
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide=LocalConfiguration.current.smallestScreenWidthDp>=600 && maxWidth>=900.dp
            Box {
                Scaffold(
                    topBar={Surface(shadowElevation=2.dp){Row(Modifier.fillMaxWidth().heightIn(min=64.dp).padding(horizontal=12.dp),verticalAlignment=Alignment.CenterVertically){
                        if(!wide)IconButton(onClick={modulesOpen=true}){Icon(Icons.Filled.Apps,"Abrir painel de módulos")}
                        Icon(Icons.Filled.MonitorHeart,null,tint=MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f).padding(horizontal=12.dp)) {
                            Text("EvoluFísico",style=MaterialTheme.typography.titleMedium)
                            Text(tabs[screen].label,style=MaterialTheme.typography.labelMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick={globalQuery="";searchOpen=true}){Icon(Icons.Filled.Search,"Buscar no aplicativo")}
                    }}},
                    bottomBar={if(!wide)NavigationBar(tonalElevation=3.dp){
                        moduleOrder.take(4).forEach{index->val dest=tabs[index]
                            NavigationBarItem(selected=screen==index,onClick={navigate(index)},icon={Icon(dest.icon,dest.label,Modifier.size(22.dp))},label={Text(dest.label,maxLines=1,overflow=androidx.compose.ui.text.style.TextOverflow.Ellipsis)})}
                        NavigationBarItem(selected=screen !in moduleOrder.take(4),onClick={modulesOpen=true},icon={Icon(Icons.Filled.Apps,"Todos os módulos")},label={Text("Módulos")})
                    }},
                    floatingActionButton={if(!editorActive)Column(horizontalAlignment=Alignment.End,verticalArrangement=Arrangement.spacedBy(8.dp)){
                        if(fabOpen)Card{Column(Modifier.padding(8.dp).heightIn(max=330.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(4.dp)){
                            listOf(1 to "Nova avaliação",9 to "Agendar atendimento",10 to "Nova tarefa",12 to "Novo treino",13 to "Registrar hábito",14 to "Nova nota").forEach{(index,label)->
                                TextButton(onClick={navigate(index,true)}){Icon(tabs[index].icon,null,Modifier.size(20.dp));Spacer(Modifier.width(8.dp));Text(label)}
                            }
                        }}
                        ExtendedFloatingActionButton(onClick={fabOpen=!fabOpen},icon={Icon(if(fabOpen)Icons.Filled.Close else Icons.Filled.Add,if(fabOpen)"Fechar atalhos" else "Abrir atalhos")},text={Text(if(fabOpen)"Fechar" else "Criar")})
                    }}
                ) {padding->
                    Row(Modifier.fillMaxSize().padding(padding)) {
                        if(wide)Surface(Modifier.width(240.dp).fillMaxHeight(),tonalElevation=1.dp){ModuleMenu(screen,moduleQuery,{moduleQuery=it},{navigate(it)},Modifier.fillMaxSize(),favorites,{toggleFavorite(it)},moduleOrder,{modulesOpen=true})}
                        Column(Modifier.weight(1f).fillMaxHeight()) {
                            if(message.isNotBlank())Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=4.dp),verticalAlignment=Alignment.CenterVertically){
                                Text(message,Modifier.weight(1f),color=MaterialTheme.colorScheme.primary,style=MaterialTheme.typography.bodySmall)
                                TextButton(onClick=onDismissMessage){Text("Fechar")}
                            }
                            key(screen) {screenState.SaveableStateProvider(screen) {when(screen) {
                                0->HomeScreen(repository,{navigate(1)},{navigate(6)},{navigate(it)},{navigate(8,client=it)})
                                1->EvaluationsScreen(repository,onCreateEvaluationPdf,createRequest,studentId)
                                2->EvolutionScreen(repository,onCreateEvolutionPdf)
                                3->CalculatorScreen()
                                4->IntakeScreen(repository)
                                5->AnalysisScreen(repository)
                                6->BillingScreen(repository,onCreateBillingPdf)
                                7->SettingsScreen(dark=dark,onDarkChange={dark=it;prefs.edit().putBoolean("dark",it).apply()},tone=tone,
                                    onToneChange={tone=it;prefs.edit().putInt("tone",it).apply()},palette=workspacePalettes.map{if(dark)Color(it.dark) else Color(it.light)},
                                    onChooseFolder,onBackup,onRestore,onImportLegacy,onRestoreDevice,onScheduleBackup,onDisableBackup,onEnableReminders,onDisableReminders,
                                    reminderLeadMinutes=reminderLead,onReminderLeadChange={lead->reminderLead=lead
                                        notificationPrefs.edit().putInt("lead-minutes",lead).apply()
                                        repository.rescheduleReminders()},
                                    onRunAudit={IntegrityAuditor.run(repository.evaluations.toList(),repository.charges.toList(),repository.intakes.toList(),repository.appointments.toList(),repository.tasks.toList()).description()+
                                        "\n"+WorkspaceRules.audit(repository.trainingSessions.toList(),repository.habits.toList(),repository.notes.toList(),repository.evaluations.map{it.studentId}.toSet()).let { issues ->
                                            if(issues.isEmpty())"Treinos, hábitos e notas: nenhum problema detectado." else issues.joinToString("\n")
                                        }},compact=compact,onCompactChange={compact=it;prefs.edit().putBoolean("compact",it).apply()},
                                    largeText=largeText,onLargeTextChange={largeText=it;prefs.edit().putBoolean("large-text",it).apply()})
                                8->ClientsScreen(repository,routeQuery,studentId,onReevaluate={navigate(1,true,it)},onModule={navigate(it)})
                                9->CalendarScreen(repository,createRequest,routeDate)
                                10->TasksScreen(repository,createRequest)
                                11->GoalsScreen(repository)
                                12->StructuredTrainingScreen(repository,createRequest,onCreateTrainingPdf,onExportCsv)
                                13->HabitsScreen(repository,createRequest)
                                14->NotebookScreen(repository,createRequest,routeQuery)
                                15->ReportsScreen(repository,onCreateBillingPdf,onExportCsv)
                            }}}
                        }
                    }
                }
            }
        }
    }
}

}

/** Full-screen module panel replaces the side drawer on phones. Editing is explicit and reversible. */
@Composable
private fun ModuleHub(order:List<Int>,onClose:()->Unit,onSave:(List<Int>)->Unit,onSelect:(Int)->Unit,
    favorites:Set<Int>,onFavorite:(Int)->Unit) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var draft by rememberSaveable(order) { mutableStateOf(order) }
    var query by rememberSaveable { mutableStateOf("") }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    var discard by remember { mutableStateOf(false) }
    fun close(){if(editing && draft!=order)discard=true else onClose()}
    androidx.compose.ui.window.Dialog(onDismissRequest={close()},properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
        BackHandler { close() }
        Surface(Modifier.fillMaxSize().systemBarsPadding()) {
            Screen {
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                    Text(if(editing)"Organizar módulos" else "Módulos",Modifier.weight(1f),style=MaterialTheme.typography.headlineSmall)
                    TextButton(onClick={close()}){Text(if(editing)"Cancelar" else "Fechar")}
                }
                if(editing) {
                    Text("Use as setas para mover. Os quatro primeiros ficam na barra inferior do smartphone.")
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick={draft=ModuleOrder.defaults}){Text("Restaurar padrão")}
                        Button(onClick={onSave(draft);editing=false}){Text("Salvar ordem")}
                    }
                    draft.forEachIndexed{position,index->key(index){
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically) {
                                Icon(tabs[index].icon,null,Modifier.size(24.dp))
                                Text("${position+1}. ${tabs[index].label}",Modifier.weight(1f).padding(horizontal=8.dp))
                                IconButton(enabled=position>0,onClick={draft=ModuleOrder.move(draft,index,-1)}) {
                                    Icon(Icons.Filled.ArrowUpward,"Mover ${tabs[index].label} para cima")
                                }
                                IconButton(enabled=position<draft.lastIndex,onClick={draft=ModuleOrder.move(draft,index,1)}) {
                                    Icon(Icons.Filled.ArrowDownward,"Mover ${tabs[index].label} para baixo")
                                }
                            }
                        }
                    }}
                } else {
                    OutlinedButton(onClick={draft=order;editing=true;query=""}){Icon(Icons.Filled.Edit,null);Spacer(Modifier.width(8.dp));Text("Editar ordem dos módulos")}
                    Field("Buscar módulo",query,{query=it})
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        FilterChip(selected=onlyFavorites,onClick={onlyFavorites=!onlyFavorites},label={Text("Só favoritos")})
                        Spacer(Modifier.weight(1f))
                        if(query.isNotBlank())TextButton(onClick={query=""}){Text("Limpar busca")}
                    }
                    val found=order.filter{(!onlyFavorites || it in favorites) && WorkspaceRules.search(tabs[it].label,query)}
                    Text("${found.size} de ${order.size} módulos",style=MaterialTheme.typography.bodySmall)
                    if(found.isEmpty())Text(if(onlyFavorites)"Nenhum favorito encontrado. Desative o filtro para ver todos os módulos." else "Nenhum módulo encontrado.")
                    CardGrid(found,280.0,3){index->
                        CompactModuleCard(tabs[index].label,tabs[index].group,tabs[index].icon,{onSelect(index)},trailing={
                            IconButton(onClick={onFavorite(index)}) {
                                Icon(if(index in favorites)Icons.Filled.Star else Icons.Filled.StarBorder,
                                    if(index in favorites)"Remover ${tabs[index].label} dos favoritos" else "Favoritar ${tabs[index].label}")
                            }
                        })
                    }
                }
            }
        }
        if(discard)AlertDialog(onDismissRequest={discard=false},title={Text("Descartar a nova ordem?")},
            text={Text("A ordem salva dos módulos será mantida.")},
            confirmButton={TextButton(onClick={discard=false;onClose()}){Text("Descartar")}},
            dismissButton={TextButton(onClick={discard=false}){Text("Continuar editando")}})
    }
}
