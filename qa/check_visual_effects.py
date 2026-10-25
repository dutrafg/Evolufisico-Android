#!/usr/bin/env python3
"""Static integration contracts only; does not validate rendering or GPU cost."""
from pathlib import Path
p=Path(__file__).resolve().parents[1]
src=p/'app/src/main/java/br/com/evolufisico/nativo'
html=(p/'reference/app.html').read_text()
policy=(src/'domain/VisualEffects.kt').read_text()
card=(src/'ui/screens/EffectsCard.kt').read_text()
app=(src/'ui/NativeApp.kt').read_text()
for marker in ['efx-prebeta-visual-script','ef-billing-monthly-script','efa-shell','rotateX(.48deg)','translate3d(0,-4px,7px)']:
 assert marker in html,marker
assert 'multiplier=1.2f' in policy and 'rotationDegrees=.48f*multiplier*2f' in policy
assert 'ValueAnimator.areAnimatorsEnabled()' in card and 'if(advanced)' in card
assert '.consume(' not in card and 'requireUnconsumed=false' in card
assert 'LocalAdvancedEffects provides (visualMode=="advanced")' in app
assert 'putString("visual-mode",it)' in app
assert 'FabPosition.Start' in app and 'Text("Módulo")' in app
assert 'initialView=routeQuery,initialDate=routeDate,initialSearch=routeSearch' in app
assert 'WorkspaceSearchDialog' in app and 'onModule={index,client->navigate(index,client=client)}' in app
assert 'onMonthlyBillingPdf' in app
print('PASS: novo HTML, efeitos +20% e 3D reforçado, modo Essencial, gesto não consumidor, busca, menu e rotas contextuais. Sem validação visual Android.')
