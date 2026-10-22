#!/usr/bin/env python3
"""Checks fixed sRGB text pairs declared in source, not rendered UI or interpolated containers."""
import json, re
from pathlib import Path
source=Path(__file__).resolve().parents[1]/'app/src/main/java/br/com/evolufisico/nativo/ui/WorkspaceStyle.kt'
text=source.read_text()
palettes=re.findall(r'WorkspacePalette\("([^\"]+)",0x([0-9A-Fa-f]+),0x([0-9A-Fa-f]+)\)',text)
assert len(palettes)==10

def literals(key):
    return [int(value,16) for value in re.findall(r'\b'+key+r'=Color\(0x([0-9A-Fa-f]+)\)',text)]
surface=literals('surface');on_surface=literals('onSurface');variant=literals('onSurfaceVariant')
assert len(surface)==len(on_surface)==len(variant)==2
assert 'onPrimary=Color.White' in text
on_primary=literals('onPrimary')[0]
def luminance(value):
    def channel(shift):
        c=((value>>shift)&255)/255
        return c/12.92 if c<=0.04045 else ((c+0.055)/1.055)**2.4
    return sum(weight*channel(shift) for weight,shift in [(0.2126,16),(0.7152,8),(0.0722,0)])
def contrast(a,b):
    x,y=sorted([luminance(a),luminance(b)])
    return (y+0.05)/(x+0.05)
results=[]
for name,light,dark in palettes:
    pairs=[('light-primary',int(light,16),0xffffffff),('dark-primary',int(dark,16),on_primary),
           ('dark-surface',surface[0],on_surface[0]),('dark-variant',surface[0],variant[0]),
           ('light-surface',surface[1],on_surface[1]),('light-variant',surface[1],variant[1])]
    for role,a,b in pairs:
        ratio=contrast(a,b)
        results.append({'palette':name,'pair':role,'contrast':round(ratio,4),'pass':ratio>=4.5})
report={'checks':len(results),'threshold':4.5,'pass':all(row['pass'] for row in results),
        'minimum':min(row['contrast'] for row in results),'scope':'Fixed source text pairs only; no interpolation, disabled states, screenshots or device QA.','results':results}
Path(__file__).with_name('PALETA_BETA100.json').write_text(json.dumps(report,ensure_ascii=False,indent=2))
assert report['pass'],report
print(f"Paleta: {report['checks']} pares fixos aprovados; menor contraste {report['minimum']}:1. Sem validação visual Android.")
