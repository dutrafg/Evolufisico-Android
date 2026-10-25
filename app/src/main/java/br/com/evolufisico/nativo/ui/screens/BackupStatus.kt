package br.com.evolufisico.nativo.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

/** Reacts to settings and WorkManager preference updates; contains no password. */
data class BackupStatus(val hasFolder:Boolean=false,val enabled:Boolean=false,val time:String="22:00",val lastSuccess:String="",val lastError:String="")

@Composable fun rememberBackupStatus():BackupStatus {
    val context=LocalContext.current
    val prefs=remember(context){context.getSharedPreferences("native-backup",Context.MODE_PRIVATE)}
    fun read()=BackupStatus(prefs.getString("folder",null)!=null,prefs.getBoolean("enabled",false),
        prefs.getString("time","22:00") ?: "22:00",prefs.getString("last-success","").orEmpty(),prefs.getString("last-error","").orEmpty())
    var status by remember(prefs){mutableStateOf(read())}
    DisposableEffect(prefs) {
        val listener=SharedPreferences.OnSharedPreferenceChangeListener { _,_->status=read() }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        status=read()
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return status
}
