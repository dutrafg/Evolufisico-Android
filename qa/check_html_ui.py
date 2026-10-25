#!/usr/bin/env python3
"""Reference-derived UI contracts. Static checks only; no Android render claims."""
from pathlib import Path
import hashlib,json,re
root=Path(__file__).resolve().parents[1]
source=root/'app/src/main/java/br/com/evolufisico/nativo'
html=(root/'reference/app.html').read_text()
identity=json.loads((root/'qa/HTML_REFERENCE.json').read_text())
assert 'Dashboard Secretária' in html
assert 'Perfil Unificado' in html and 'efa-shell' in html
assert hashlib.sha256((root/'reference/app.html').read_bytes()).hexdigest()==identity['sha256']
app=(source/'ui/NativeApp.kt').read_text()
labels=re.findall(r'Destination\("([^"\n]+)"',app)
assert [labels[i] for i in [0,9,1,2,3,4,5,6,7,8]]==identity['modules']
for unsupported in ['modulesOpen','ModuleHub','Text("Módulos")','Icons.Filled.Apps','NavigationBarItem']:
    assert unsupported not in app,unsupported
assert 'ReferenceBottomNavigation' not in app
assert 'floatingActionButtonPosition=FabPosition.Start' in app
assert 'Modifier.padding(bottom=24.dp)' in app and 'Text("Módulo")' in app
assert 'moduleOrder.forEach' in app and 'ModalBottomSheet' in app
assert 'var screen by remember { mutableIntStateOf(0) }' in app
assert 'moduleMenuOpen=false;navigate(index)' in app
home=(source/'ui/screens/HomeScreen.kt').read_text()
profile=(source/'ui/screens/UnifiedProfileScreen.kt').read_text()
grid=(source/'ui/screens/ReferenceDashboardGrid.kt').read_text()
assert 'Perfil Unificado' in home and 'onModule(9' in home
assert 'Nova avaliação' not in home and 'openEvaluations' not in home
assert 'Avaliações' in profile and 'Evolução' in profile and 'Financeiro' in profile
assert 'repository.charges' in profile and 'repository.intakes' in profile
assert 'onOpenModule(2, selectedId)' in profile and 'onOpenModule(6, selectedId)' in profile
assert 'HomeKpi' in home and 'DashboardPacking.span(group' in grid
assert 'ReferenceDashboardGrid("homeStats"' in home and 'ReferenceDashboardGrid("homeCards"' in home
assert 'Restaurar layout padrão' in home and 'HtmlHome.cardSpan(group,size)' in (source/'domain/DashboardPacking.kt').read_text()
assert 'repository.save(' not in home
organization=(source/'ui/screens/OrganizationScreen.kt').read_text()
assert 'OrganizationWorkspaceScreen' in organization
assert 'enabled=!workspaceEditing' in organization and 'if(!workspaceEditing)workspaceOpen=false' in organization
assert 'onEvaluations(it)' in organization and 'onEvaluations={client->navigate(1,client=client)}' in app
assert '9->UnifiedProfileScreen' in app
for unsupported in ['Destination("Treinos"','Destination("Hábitos"','Destination("Clientes"','Destination("Módulos"']:
    assert unsupported not in app
print('PASS: dez destinos mapeados à referência EvoluFísico2 3.0, incluindo Perfil Unificado; menu flutuante preservado. Não comprova renderização Android.')

settings=(source/'ui/screens/SettingsScreen.kt').read_text()
assert 'topBar=' not in app and 'Icons.Filled.MonitorHeart' not in app
assert 'ef-colors-list' in html and 'gap:9px' in html
assert 'PaletteChoices(tone,palette,onToneChange)' in settings
assert 'RadioButton(selected' not in settings
assert 'Modifier.size(18.dp)' in settings and 'RoundedCornerShape(10.dp)' in settings
assert 'Modifier.fillMaxWidth()) { Row' not in settings
print('PASS: cabeçalho extra ausente no celular; paletas compactas em linhas, com rótulo, amostra e seleção acessível.')
