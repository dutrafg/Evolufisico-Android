#!/usr/bin/env python3
"""Static integration regression guard. Does not measure pixels or run Android."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
src=root/'app/src/main/java/br/com/evolufisico/nativo'
app=(src/'ui/NativeApp.kt').read_text()
activity=(src/'MainActivity.kt').read_text()
checks={
    'edge_to_edge_initialized':'enableEdgeToEdge()' in activity,
    'shell_safe_area':'BoxWithConstraints(Modifier.fillMaxSize().background(colors.background).windowInsetsPadding(WindowInsets.safeDrawing))' in app,
    'scaffold_no_second_system_padding':'contentWindowInsets=WindowInsets(0,0,0,0)' in app,
    'content_consumes_scaffold_padding':'.padding(padding).consumeWindowInsets(padding)' in app,
    'system_icon_theme':'isAppearanceLightStatusBars=!dark' in app and 'isAppearanceLightNavigationBars=!dark' in app,
    'module_panel_safe_area':'Surface(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing))' in app,
    'editor_safe_area':'.windowInsetsPadding(WindowInsets.safeDrawing).imePadding()' in (src/'ui/screens/WorkspaceScreens.kt').read_text(),
    'full_screen_editor_safe_area':'.windowInsetsPadding(WindowInsets.safeDrawing).imePadding()' in (src/'ui/screens/ProfessionalScreens.kt').read_text(),
}
assert all(checks.values()),checks
print(f'Área segura: {len(checks)} contratos estáticos presentes. Não comprova layout em aparelho.')
