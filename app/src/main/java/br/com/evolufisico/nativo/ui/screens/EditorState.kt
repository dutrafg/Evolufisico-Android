package br.com.evolufisico.nativo.ui.screens

import androidx.compose.runtime.*

/** Prevents module navigation from silently destroying an active editor. */
val LocalEditorState = staticCompositionLocalOf<(Boolean)->Unit> { {} }

@Composable fun TrackEditing() {
    val report=LocalEditorState.current
    DisposableEffect(Unit) {
        report(true)
        onDispose { report(false) }
    }
}
