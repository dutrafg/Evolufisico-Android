package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
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
    var section by rememberSaveable { mutableStateOf("Aparência") }
    PillChoices(listOf("Aparência","Dados e privacidade","Avisos","Sobre"),section,{section=it})
    when(section) {
        "Aparência" -> {
    Text("Aparência", style = MaterialTheme.typography.titleMedium)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Tema escuro", Modifier.weight(1f))
        Switch(checked = dark, onCheckedChange = onDarkChange)
    }
    Row(verticalAlignment=Alignment.CenterVertically){Text("Layout compacto",Modifier.weight(1f));Switch(compact,onCheckedChange=onCompactChange)}
    Row(verticalAlignment=Alignment.CenterVertically){Text("Texto ampliado",Modifier.weight(1f));Switch(largeText,onCheckedChange=onLargeTextChange)}
    Text("Os alvos de toque mantêm o tamanho; o modo compacto reduz os espaços dos cartões.",style=MaterialTheme.typography.bodySmall)
    Text("Cor principal")
    palette.forEachIndexed { i, color ->
        OutlinedCard(onClick={onToneChange(i)},modifier=Modifier.fillMaxWidth()) { Row(Modifier.padding(horizontal=12.dp),verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp).background(color, RoundedCornerShape(5.dp)))
            RadioButton(selected = tone == i, onClick = { onToneChange(i) })
            Text(listOf("Azul profundo","Azul clássico","Azul intenso","Azul aço","Azul petróleo","Petróleo escuro","Verde floresta","Ameixa","Vinho","Grafite").getOrElse(i){"Cor ${i+1}"},Modifier.weight(1f))
        }}
    }
    HorizontalDivider()
        }
        "Dados e privacidade" -> {
    Text("Dados e privacidade", style = MaterialTheme.typography.titleMedium)
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
    Text("Somente arquivos .efbackup gerados por esta versão são aceitos. O importador verifica o formato e não substitui registros existentes.",
        style = MaterialTheme.typography.bodySmall)
    HorizontalDivider()
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
    var dailyTime by remember { mutableStateOf("22:00") }
    Field("Horário preferencial (HH:MM)",dailyTime,{dailyTime=it})
    ActionButtons {
        Button(onClick={onScheduleBackup(dailyTime)}){Text("Ativar diário")}
        OutlinedButton(onClick=onDisableBackup){Text("Desativar")}
    }
    OutlinedButton(onClick=onRestoreDevice){Text("Restaurar backup desta instalação (.efdevice)")}
    Text("O Android poderá executar a rotina após o horário programado por economia de bateria.",style=MaterialTheme.typography.bodySmall)
    HorizontalDivider()
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
        "Avisos" -> {
    Text("Notificações e lembretes",style=MaterialTheme.typography.titleMedium)
    Text("Escolha quando quer receber um lembrete dos atendimentos futuros. O Android pode atrasar avisos por economia de bateria.")
    PillChoices(listOf(15,30,60,1440).map{when(it){15->"15 min";30->"30 min";60->"1 hora";else->"24 horas"}},
        when(reminderLeadMinutes){15->"15 min";30->"30 min";1440->"24 horas";else->"1 hora"},{label->
            onReminderLeadChange(when(label){"15 min"->15;"30 min"->30;"24 horas"->1440;else->60})
        })
    ActionButtons {
        Button(onClick=onEnableReminders){Text("Ativar lembretes")}
        OutlinedButton(onClick=onDisableReminders){Text("Desativar")}
    }
    HorizontalDivider()
        }
        "Sobre" -> {
    Text("EvoluFísico Nativo · 0.1.0-beta.20.1", style = MaterialTheme.typography.labelMedium)
    Text("Importador de backup legado implementado, mas ainda sem homologação em dispositivo. Preserve os arquivos e o aplicativo anterior.",
        style = MaterialTheme.typography.bodySmall)
    Text("16 módulos nativos · funcionamento local · sem permissão de internet.",style=MaterialTheme.typography.bodySmall)
    Text("Novos backups incluem treinos, hábitos e notas. Use esta versão ou posterior para restaurar esses módulos; backups antigos continuam aceitos.",style=MaterialTheme.typography.bodySmall)
        }
    }
}
