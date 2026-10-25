package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.role
import br.com.evolufisico.nativo.ui.workspacePalettes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    initialSection:String="",
    visualMode:String="advanced",onVisualModeChange:(String)->Unit={},
    dark: Boolean, onDarkChange: (Boolean) -> Unit, tone: Int,
    onToneChange: (Int) -> Unit, palette: List<Color>,
    onChooseFolder: () -> Unit, onBackup: (String) -> Unit, onRestore: (String) -> Unit,
    onImportLegacy: (String) -> Unit, onRestoreDevice: () -> Unit,
    onScheduleBackup: (String) -> Unit, onDisableBackup: () -> Unit,
    onEnableReminders: () -> Unit, onDisableReminders: () -> Unit,
    reminderLeadMinutes:Int=60,onReminderLeadChange:(Int)->Unit={},
    onRunAudit: () -> String,
    compact:Boolean=false,onCompactChange:(Boolean)->Unit={},largeText:Boolean=false,onLargeTextChange:(Boolean)->Unit={}
) = Screen {
    Heading("Configurações", "Aparência, dados e privacidade do aplicativo nativo.")
    var section by rememberSaveable(initialSection) { mutableStateOf(when(initialSection){"Dados e privacidade"->"Geral";"Notificações","Personalização","Manutenção"->initialSection;else->"Geral"}) }
    ReferenceSectionTabs(listOf("Geral","Personalização","Notificações","Manutenção"),section,{section=it})
    when(section) {
        "Personalização" -> {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cardWidth=if(maxWidth>=760.dp)(maxWidth-18.dp)/2 else maxWidth
        FlowRow(horizontalArrangement=Arrangement.spacedBy(18.dp),verticalArrangement=Arrangement.spacedBy(18.dp)) {
            EffectsCard(Modifier.width(cardWidth),shape=RoundedCornerShape(14.dp)) {Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                Text("Aparência",style=MaterialTheme.typography.titleLarge)
                Text("Alterne entre os modos claro e escuro e escolha entre dez paletas profissionais.",style=MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment=Alignment.CenterVertically){Text("Tema escuro",Modifier.weight(1f));Switch(dark,onCheckedChange=onDarkChange,modifier=Modifier.semantics{contentDescription="Tema escuro"})}
                Text("Cor principal do aplicativo",style=MaterialTheme.typography.titleSmall)
                Text("Escolha entre cinco cores originais e cinco tons sóbrios para interfaces profissionais. A preferência fica salva neste dispositivo.",style=MaterialTheme.typography.bodySmall)
                PaletteChoices(tone,palette,onToneChange)
                Text("Efeitos visuais",style=MaterialTheme.typography.titleSmall)
                PillChoices(listOf("Avançada","Essencial","Liquid Glass"),when(visualMode){"liquid"->"Liquid Glass";"essential"->"Essencial";else->"Avançada"},{onVisualModeChange(when(it){"Liquid Glass"->"liquid";"Essencial"->"essential";else->"advanced"})})
                Text(if(visualMode=="liquid")"Superfícies tonais com profundidade e reflexo suave; conteúdo e formulários preservam o contraste." else if(visualMode=="advanced")"Iluminação difusa, contornos e sombras com intensidade 20% maior que no HTML. Profundidade 3D reforçada ao tocar ou passar o cursor." else "Sem sombras, brilho, desfoque decorativo ou inclinação nos cartões.",style=MaterialTheme.typography.bodySmall)
                Text("A escolha vale para todos os módulos e não altera dados ou relatórios. As animações respeitam a configuração do Android.",style=MaterialTheme.typography.bodySmall)
                HorizontalDivider()
                Row(verticalAlignment=Alignment.CenterVertically){Text("Layout compacto",Modifier.weight(1f));Switch(compact,onCheckedChange=onCompactChange,modifier=Modifier.semantics{contentDescription="Layout compacto"})}
                Row(verticalAlignment=Alignment.CenterVertically){Text("Texto ampliado",Modifier.weight(1f));Switch(largeText,onCheckedChange=onLargeTextChange,modifier=Modifier.semantics{contentDescription="Texto ampliado"})}
            }}
        }
    }
    HorizontalDivider()
        }
        "Geral" -> {
    EffectsCard(Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){ProfessionalSettings()}}
    val backupStatus=rememberBackupStatus()
    Text("Dados e privacidade", style = MaterialTheme.typography.titleMedium)
    Text(if(backupStatus.hasFolder)"Pasta de backup selecionada. Se o acesso foi revogado pelo Android, selecione novamente." else "Nenhuma pasta de backup selecionada.")
    Text("Os registros ficam no banco local criptografado por chave protegida no Android Keystore. O aplicativo não solicita acesso irrestrito aos arquivos do celular.")
    OutlinedButton(onClick = onChooseFolder) { Text("Escolher pasta local para backups") }
    Text("A pasta deve permitir gravação pelo seletor nativo. O Android 11+ restringe algumas pastas, incluindo a raiz e Android/data.",
        style = MaterialTheme.typography.bodySmall)
    HorizontalDivider()
    var backupPassword by remember { mutableStateOf("") }
    var importPassword by remember { mutableStateOf("") }
    Text("Exportar backup criptografado", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(backupPassword, onValueChange = { backupPassword = it },
        label = { Text("Senha do backup (mínimo 8 caracteres)") },
        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    Button(enabled = backupPassword.length >= 8, onClick = {
        onBackup(backupPassword); backupPassword = ""
    }) { Text("Criar backup protegido") }
    HorizontalDivider()
    Text("Restaurar backup nativo", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(importPassword, onValueChange = { importPassword = it },
        label = { Text("Senha utilizada na exportação") },
        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    Button(enabled = importPassword.isNotBlank(), onClick = {
        onRestore(importPassword); importPassword = ""
    }) { Text("Selecionar arquivo e restaurar") }
    Text("Aceita arquivos .efbackup nativos compatíveis. O importador verifica o formato e não substitui registros existentes.",
        style = MaterialTheme.typography.bodySmall)
    HorizontalDivider()
    Text("Importação do EvoluFísico anterior", style=MaterialTheme.typography.titleMedium)
    Text("Aceita backup JSON legado e pacotes .efx com senha mestra. A importação é aditiva e cancela integralmente em caso de ID repetido ou checksum incompatível.")
    var legacyPassword by remember { mutableStateOf("") }
    OutlinedTextField(legacyPassword,{legacyPassword=it},label={Text("Senha mestra do .efx (opcional para JSON)")},
        visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth())
    Button(onClick={ onImportLegacy(legacyPassword);legacyPassword="" }) { Text("Importar backup antigo JSON / .efx") }
    HorizontalDivider()
    Text("Backup automático diário",style=MaterialTheme.typography.titleMedium)
    Text("Protegido pelo Android Keystore, sem salvar senha. Só pode ser restaurado nesta instalação; não substitui o backup portátil protegido por senha.")
    Text(if(backupStatus.enabled)"Ativo · horário preferencial ${backupStatus.time}" else "Backup automático desativado.")
    Text("Último backup automático concluído: "+backupStatus.lastSuccess.ifBlank{"nenhum registrado"},style=MaterialTheme.typography.bodySmall)
    if(backupStatus.lastError.isNotBlank())Text(backupStatus.lastError,color=MaterialTheme.colorScheme.error)
    var dailyTime by remember(backupStatus.time) { mutableStateOf(backupStatus.time) }
    Field("Horário preferencial (HH:MM)",dailyTime,{dailyTime=it})
    ActionButtons {
        Button(onClick={onScheduleBackup(dailyTime)}){Text("Ativar diário")}
        OutlinedButton(onClick=onDisableBackup){Text("Desativar")}
    }
    OutlinedButton(onClick=onRestoreDevice){Text("Restaurar backup desta instalação (.efdevice)")}
    Text("O Android poderá executar a rotina após o horário programado por economia de bateria.",style=MaterialTheme.typography.bodySmall)
    HorizontalDivider()
        }
        "Manutenção" -> {
    Text("Autoverificação dos dados locais", style=MaterialTheme.typography.titleMedium)
    Text("Confere datas, vínculos, histórico, medidas e cobranças. Não envia informações e não corrige registros automaticamente.")
    var auditResult by remember { mutableStateOf("") }
    Button(onClick={ auditResult = runCatching { onRunAudit() }.getOrElse {
        "Falha ao verificar os dados: ${it.localizedMessage ?: "erro desconhecido"}" } }) { Text("Verificar integridade dos registros") }
    if (auditResult.isNotBlank()) {
        Text(auditResult, style=MaterialTheme.typography.bodySmall)
        TextButton(onClick={auditResult=""}) { Text("Ocultar resultado") }
    }
    HorizontalDivider()
        }
        "Notificações" -> {
    Text("Notificações e lembretes",style=MaterialTheme.typography.titleMedium)
    Text("Escolha quando quer receber um lembrete dos atendimentos futuros. O Android pode atrasar avisos por economia de bateria.")
    PillChoices(listOf(0,5,10,15,30,60,1440).map{when(it){0->"No horário";5->"5 min";10->"10 min";15->"15 min";30->"30 min";60->"1 hora";else->"24 horas"}},
        when(reminderLeadMinutes){0->"No horário";5->"5 min";10->"10 min";15->"15 min";30->"30 min";1440->"24 horas";else->"1 hora"},{label->
            onReminderLeadChange(when(label){"No horário"->0;"5 min"->5;"10 min"->10;"15 min"->15;"30 min"->30;"24 horas"->1440;else->60})
        })
    NativeNotificationControls()
    ActionButtons {
        Button(onClick=onEnableReminders){Text("Ativar lembretes")}
        OutlinedButton(onClick=onDisableReminders){Text("Desativar")}
    }
    HorizontalDivider()
        }
    }
    HorizontalDivider()
    Text("EvoluFísico Nativo · Beta 100 · revisão 14",style=MaterialTheme.typography.labelMedium)
    Text("Baseado exclusivamente no HTML fornecido. Dados antigos são preservados nos backups completos.",style=MaterialTheme.typography.bodySmall)
}

/** Compact wrapped swatches correspond to ef-colors-list in the immutable HTML. */
@Composable private fun PaletteChoices(tone:Int,palette:List<Color>,onSelect:(Int)->Unit) {
    FlowRow(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(9.dp),verticalArrangement=Arrangement.spacedBy(9.dp)) {
        palette.forEachIndexed{i,color->
            val label=(workspacePalettes.getOrNull(i)?.label ?: "Cor ${i+1}").split('-',' ').joinToString(" "){it.replaceFirstChar{c->c.uppercaseChar()}}
            val active=tone==i
            Surface(onClick={onSelect(i)},shape=RoundedCornerShape(10.dp),color=MaterialTheme.colorScheme.surface,
                border=androidx.compose.foundation.BorderStroke(if(active)2.dp else 1.dp,if(active)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                modifier=Modifier.heightIn(min=48.dp).semantics{this.selected=active;role=androidx.compose.ui.semantics.Role.RadioButton;contentDescription="Cor $label"}) {
                Row(Modifier.padding(horizontal=10.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                    Box(Modifier.size(18.dp).background(color,RoundedCornerShape(6.dp)))
                    Text(label,style=MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
@Composable internal fun ProfessionalSettings(onSaved:()->Unit={},required:Boolean=false) {
    val context=androidx.compose.ui.platform.LocalContext.current
    val prefs=remember{context.getSharedPreferences("professional",android.content.Context.MODE_PRIVATE)}
    var name by rememberSaveable{mutableStateOf(prefs.getString("name","").orEmpty())}
    var cref by rememberSaveable{mutableStateOf(prefs.getString("cref","").orEmpty())}
    var nickname by rememberSaveable{mutableStateOf(prefs.getString("nickname","").orEmpty())}
    var workplace by rememberSaveable{mutableStateOf(prefs.getString("workplace","").orEmpty())}
    var saved by remember{mutableStateOf(false)}
    Text("Dados profissionais",style=MaterialTheme.typography.titleLarge)
    Text("Configure nome e CREF utilizados em avaliações e relatórios.")
    Field("Nome do profissional",name,{name=it.take(200);saved=false})
    Field("Apelido profissional",nickname,{nickname=it.take(100);saved=false})
    Field("Local de trabalho / clínica",workplace,{workplace=it.take(200);saved=false})
    if(required)Text("Informe seu nome para continuar. Os demais campos são opcionais.")
    Field("CREF",cref,{cref=it.take(80);saved=false})
    Button(enabled=name.isNotBlank(),onClick={prefs.edit().putString("name",name.trim()).putString("nickname",nickname.trim()).putString("workplace",workplace.trim()).putString("cref",cref.trim()).apply();saved=true;onSaved()}){Text("Salvar dados profissionais")}
    if(saved)Text("Dados profissionais salvos.")
}
