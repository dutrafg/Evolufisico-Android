package br.com.evolufisico.nativo.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.system.AppointmentNotifications

/** Sound and vibration are governed by Android notification channel settings. */
@Composable internal fun NativeNotificationControls() {
    val context=LocalContext.current
    val prefs=remember {context.getSharedPreferences("native-notifications",Context.MODE_PRIVATE)}
    var sound by remember {mutableStateOf(prefs.getBoolean("sound",true))}
    var vibration by remember {mutableStateOf(prefs.getBoolean("vibration",true))}
    var state by remember {mutableStateOf("")}
    DisposableEffect(context) {
        val life=(context as? androidx.lifecycle.LifecycleOwner)?.lifecycle
        val obs=androidx.lifecycle.LifecycleEventObserver {_,event->if(event==androidx.lifecycle.Lifecycle.Event.ON_RESUME){sound=prefs.getBoolean("sound",true);vibration=prefs.getBoolean("vibration",true)}}
        life?.addObserver(obs);onDispose {life?.removeObserver(obs)}
    }
    EffectsCard(Modifier.fillMaxWidth()) {Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Text("Som e vibração",style=MaterialTheme.typography.titleMedium)
        Row(verticalAlignment=Alignment.CenterVertically){Text("Som dos lembretes",Modifier.weight(1f));Switch(sound,{sound=it;prefs.edit().putBoolean("sound",it).apply();AppointmentNotifications.channel(context)})}
        Row(verticalAlignment=Alignment.CenterVertically){Text("Vibrar nos lembretes",Modifier.weight(1f));Switch(vibration,{vibration=it;prefs.edit().putBoolean("vibration",it).apply();AppointmentNotifications.channel(context)})}
        Text("O volume e as permissões de cada canal são controlados pelo Android. Ajustes feitos no sistema prevalecem.",style=MaterialTheme.typography.bodySmall)
        ActionButtons {
            OutlinedButton(onClick={state=AppointmentNotifications.test(context)}){Text("Testar notificação")}
            OutlinedButton(onClick={runCatching {AppointmentNotifications.channel(context);context.startActivity(Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName).putExtra(Settings.EXTRA_CHANNEL_ID,AppointmentNotifications.channelId(context)))}.onFailure{state="Não foi possível abrir as configurações do Android."}}){Text("Ajustar no Android")}
        }
        if(state.isNotBlank())Text(state,style=MaterialTheme.typography.bodySmall)
    }}
}
