#!/usr/bin/env python3
"""Non-runtime audit. It CANNOT prove Android UI compilation or runtime correctness."""
from pathlib import Path
root=Path(__file__).resolve().parents[1]
src=root/'app/src/main/java/br/com/evolufisico/nativo'
files={p.name:p.read_text('utf-8') for p in src.rglob('*.kt')}
expected=['HomeScreen.kt','EvaluationsScreen.kt','EvolutionScreen.kt','CalculatorScreen.kt',
          'IntakeScreen.kt','AnalysisScreen.kt','BillingScreen.kt','SettingsScreen.kt']
assert all(s in files for s in expected), 'Missing one of eight modules'
assert len(files)>=19, f'Only {len(files)} Kotlin files'
assert 'android.webkit.WebView' not in '\n'.join(files.values()), 'A WebView dependency has slipped in'
assert '<uses-permission android:name="android.permission.INTERNET"' not in (root/'app/src/main/AndroidManifest.xml').read_text(), 'Unexpected network permission'
assert 'data/CryptoStore.kt' and 'CryptoStore.kt' in files
assert 'EF-NATIVE-BACKUP' in files['CryptoStore.kt']
assert 'IntegrityAuditor' in files['SettingsScreen.kt'] or 'onRunAudit' in files['SettingsScreen.kt']
assert 'LegacyImporter' in files['MainActivity.kt']
assert 'PdfDocument' in files['Reports.kt']
assert 'NavigationBarItem' in files['NativeApp.kt']
assert 'validateTarget' in files['GoalTracking.kt']
assert 'fun progress' in files['GoalTracking.kt']
assert 'DatePicker' in files['Components.kt']
assert 'goalWeightKg' in files['EvaluationsScreen.kt']
assert 'goalWeightBaselineKg' in files['IntegrityAuditor.kt']
assert 'Agenda.overlaps' in files['IntegrityAuditor.kt']
assert 'bytes.fill(0)' in (src/'MainActivity.kt').read_text()
assert 'file.delete()' in (src/'system'/'NativeBackupWorker.kt').read_text()
assert 'versionCode = 620' in (root/'app/build.gradle.kts').read_text()
assert '0.1.0-native-beta.20' in (root/'app/build.gradle.kts').read_text()
assert 'ReminderPolicy.shouldNotify' in files['AppointmentNotifications.kt']
assert 'ReminderPolicy.shouldSchedule' in files['AppointmentNotifications.kt']
assert '1440' in files['SettingsScreen.kt']
assert 'listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom")' in files['ProfessionalScreens.kt']
assert 'selectedItems.count' in files['ProfessionalScreens.kt']
assert 'Metas registradas' in files['Reports.kt']
assert (root/'app/src/test/java/br/com/evolufisico/nativo/domain/Beta3MatrixTest.kt').exists()
assert 'forLanguageTag' in files['Metrics.kt']
assert 'AutoMirrored' in files['NativeApp.kt']
assert 'not applicable' in files['Metrics.kt'].lower() or 'penalize' in files['Metrics.kt'].lower()
import re
matrix=(root/'app/src/test/java/br/com/evolufisico/nativo/domain/Alpha600MatrixTest.kt').read_text()
stages=[int(n) for n in re.findall(r'@Test fun alphaA(\d+)_', matrix)]
assert stages == list(range(151,601)), f'Expected exactly A151-A600, got {len(stages)} stage vectors'
assert all(name in files for name in ['WorkspaceEntities.kt','WorkspaceRules.kt','WorkspaceScreens.kt','ProfessionalScreens.kt','EditorState.kt'])
assert len(re.findall(r'^    Destination\(',files['NativeApp.kt'],re.M)) == 16
for kind in ['training','habit','note']:
    assert f'"{kind}"' in files['CryptoStore.kt']
    assert f'"{kind}" -> WorkspaceRules.validate' in files['CryptoStore.kt']
assert 'Snapshot.withMutableSnapshot' in files['AppRepository.kt']
assert 'TrackEditing()' in files['EditorState.kt']
assert 'PagedRecords' in files['Components.kt']
assert 'until 10000' in (root/'app/src/test/java/br/com/evolufisico/nativo/domain/WorkspaceMatrixTest.kt').read_text()
assert 'StructuredTrainingScreen' in files['NativeApp.kt']
assert 'fun csvCell' in files['ProfessionalTools.kt']
assert 'WorkoutSet.fromJson' in files['WorkspaceEntities.kt']
assert 'goalWeight,goalFat' in files['EvaluationsScreen.kt']
assert 'csvBytes?.fill(0)' in files['MainActivity.kt']
assert 'workspaceColors(tone,dark)' in files['NativeApp.kt']
assert 'ModalNavigationDrawer' not in files['NativeApp.kt']
assert 'smallestScreenWidthDp>=600' in files['NativeApp.kt']
assert 'ModuleHub(moduleOrder' in files['NativeApp.kt']
assert 'moduleOrder.take(4)' in files['NativeApp.kt']
assert 'putString("module-order"' in files['NativeApp.kt']
assert 'ReminderPolicy.shouldSchedule(Duration.between(LocalDateTime.now(),date).toMillis(),lead)' in files['AppointmentNotifications.kt']
assert 'DashboardMetricCard' in files['WorkspaceOverview.kt']
assert 'CardGrid(found' in files['NativeApp.kt']
assert 'CompactModuleCard' in files['NativeApp.kt']
assert 'dueReassessments' in files['DashboardInsights.kt']
assert 'PagedRecords(visibleTasks' in files['HomeScreen.kt']
assert 'TrainingProgression(repo,onExportCsv)' in files['StructuredTraining.kt']
assert 'TrainingInsights.batch' in files['StructuredTraining.kt']
assert 'onCreateTrainingPdf' in files['NativeApp.kt']
assert 'Reports.training(item)' in files['MainActivity.kt']
assert 'fun training(item:TrainingSession)' in files['Reports.kt']
assert 'it.studentId==clientId' in files['TrainingInsights.kt']
assert 'enabled=!item.completed' in files['StructuredTraining.kt']
print(f'Auditoria estática: {len(files)} arquivos Kotlin; 16 destinos; Beta 20, evolução de cargas, PDF de treino e filtros integrados. PASSOU.')
