#!/usr/bin/env python3
"""Checks integration only; touch/scroll/render still require an Android device."""
from pathlib import Path
p=Path(__file__).resolve().parents[1]
src=p/'app/src/main/java/br/com/evolufisico/nativo'
grid=(src/'ui/screens/ReferenceDashboardGrid.kt').read_text()
components=(src/'ui/screens/Components.kt').read_text()
for marker in ['detectDragGestures(','onDragCancel={clearDrag()}','DashboardPacking.move(currentOrder,id,to)','putString("order",savedOrder)','DashboardPacking.pack(width,gap,spans','DashboardPacking.width(width,gap,spans[i])','viewport.scroll.scrollBy(speed)','CompositionLocalProvider(LocalAdvancedEffects provides (LocalAdvancedEffects.current&&!editing))']:
 assert marker in grid,marker
assert 'FlowRow(' not in grid
assert 'if(editing)' in grid and 'Text("Arrastar"' in grid
assert 'rememberUpdatedState(order)' in grid
assert 'LocalDashboardViewport provides viewport' in components
assert 'viewport.bounds=it.boundsInWindow()' in components
print('PASS: encaixe por alturas, arraste em edição, alvo visual, cancelamento, persistência e rolagem automática integrados. Não comprova gesto em aparelho.')
