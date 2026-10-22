package br.com.evolufisico.nativo.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.domain.RestCountdown
import kotlinx.coroutines.delay

class RestTimerState(initial:RestCountdown=RestCountdown()) {
    var value by mutableStateOf(initial)
    companion object {
        val Saver=listSaver<RestTimerState,Long>(
            save={listOf(it.value.remainingMillis,it.value.deadlineElapsed ?: -1L)},
            restore={RestTimerState(RestCountdown(it[0],it[1].takeIf{deadline->deadline>=0}))})
    }
}

@Composable fun rememberRestTimerState()=rememberSaveable(saver=RestTimerState.Saver){RestTimerState()}

/** No alarm or background notification. The deadline survives module navigation and rotation. */
@Composable fun RestTimer(state:RestTimerState=rememberRestTimerState()) {
    var manual by rememberSaveable{mutableStateOf("60")}
    var error by remember{mutableStateOf("")}
    LaunchedEffect(state.value.running,state.value.deadlineElapsed) {
        while(state.value.running) {
            state.value=state.value.tick(SystemClock.elapsedRealtime())
            delay(200)
        }
    }
    val remaining=((state.value.remainingMillis+999)/1000).toInt()
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text("Descanso · ${remaining/60}:${(remaining%60).toString().padStart(2,'0')}",style=MaterialTheme.typography.titleMedium)
            Text(if(remaining==0)"Descanso encerrado." else "Mantém a contagem ao trocar de módulo. Sem aviso em segundo plano.",style=MaterialTheme.typography.bodySmall)
            PillChoices(listOf("30 s","60 s","90 s","120 s"),if(!state.value.running)"$remaining s" else "") {
                state.value=RestCountdown.seconds(it.substringBefore(' ').toInt());error=""
            }
            ActionButtons {
                Button(enabled=state.value.running || remaining>0,onClick={
                    state.value=if(state.value.running)state.value.pause(SystemClock.elapsedRealtime()) else state.value.start(SystemClock.elapsedRealtime())
                }){Text(if(state.value.running)"Pausar" else "Iniciar")}
                TextButton(onClick={state.value=state.value.addSeconds(30,SystemClock.elapsedRealtime())}){Text("+30 s")}
                TextButton(onClick={state.value=RestCountdown()}){Text("Reiniciar")}
            }
            NumericField("Duração manual (segundos)",manual,{manual=it})
            OutlinedButton(onClick={runCatching{state.value=RestCountdown.seconds(manual.toIntOrNull() ?: 0);error=""}
                .onFailure{error=it.message ?: "Duração inválida."}}){Text("Definir duração")}
            if(error.isNotBlank())Text(error,color=MaterialTheme.colorScheme.error)
        }
    }
}
