#!/usr/bin/env python3
"""Static product-scope and safety checks; never a replacement for Android runtime QA."""
from pathlib import Path
import re,json
p=Path(__file__).resolve().parents[1];src=p/'app/src/main/java/br/com/evolufisico/nativo'
files={f.name:f.read_text() for f in src.rglob('*.kt')}
app=files['NativeApp.kt'];upstream=json.loads((p/'qa/HTML_REFERENCE.json').read_text())
import hashlib
assert hashlib.sha256((p/upstream['projectPath']).read_bytes()).hexdigest()==upstream['sha256']
labels=re.findall(r'Destination\("([^"\n]+)"',app)
native_labels=[labels[i] for i in [0,9,1,2,3,4,5,6,7,8]]
assert native_labels==upstream['modules']
assert 'Destination("Perfil Unificado"' in app and '9->UnifiedProfileScreen' in app
assert 'Icons.Filled.Psychology' in app
for removed in ['StructuredTrainingScreen','HabitsScreen','NotebookScreen','ClientsScreen','GoalsScreen','ReportsScreen','ModuleHub']:
    assert not re.search(r'fun '+removed+r'\b','\n'.join(files.values())),removed
assert 'HtmlParity.navigation' in app
assert 'if(index !in tabs.indices)return' in app
assert 'HtmlParity.panels' in files['OrganizationWorkspaceScreen.kt']
assert 'CalendarScreen(repository)' in files['OrganizationWorkspaceScreen.kt']
assert 'Organizar painel' in files['OrganizationWorkspaceScreen.kt']
assert 'heightIn(min=height.dp)' in files['OrganizationWorkspaceScreen.kt']
assert 'putString("name"' in files['SettingsScreen.kt']
assert 'HtmlParity.eligible(chosen)' in files['AnalysisScreen.kt']
assert 'selectedClient' in files['EvaluationsScreen.kt'] and 'CardGrid(profiles,220.0' in files['EvaluationsScreen.kt']
assert 'Dispatchers.Default' in files['MainActivity.kt'] and 'private fun saveExport' in files['MainActivity.kt']
assert 'EF-NATIVE-BACKUP' in files['CryptoStore.kt'] and 'RestoreJsonRules.validate' in files['CryptoStore.kt']
assert 'HtmlParity.individualBackupKind(kind)' in files['CryptoStore.kt']
assert 'Snapshot.withMutableSnapshot' in files['AppRepository.kt']
assert 'android.webkit.WebView' not in '\n'.join(files.values())
assert 'android.permission.INTERNET' not in (p/'app/src/main/AndroidManifest.xml').read_text()
assert 'versionCode = 714' in (p/'app/build.gradle.kts').read_text()
assert 'runAttemptCount<3' in files['NativeBackupWorker.kt']
assert 'last-error' in files['NativeBackupWorker.kt'] and 'backupStatus.lastError' in files['SettingsScreen.kt']
assert 'manager.notify(id,0,notice)' in files['AppointmentNotifications.kt']
assert 'ReminderPolicy.shouldNotify' in files['AppointmentNotifications.kt']
assert 'FlowRow' in files['Components.kt']
print(f'Auditoria estática: {len(files)} arquivos Kotlin, destinos atuais mapeados à referência 3.0, Perfil Unificado integrado. PASSOU. Não valida aparência em aparelho.')
