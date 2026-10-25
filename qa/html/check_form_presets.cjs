const fs=require('fs'),path=require('path'),vm=require('vm');
const root=path.resolve(__dirname,'../..');const html=fs.readFileSync(path.join(root,'reference/app.html'),'utf8');
const source=[...html.matchAll(/<script\b[^>]*>([\s\S]*?)<\/script\s*>/gi)].map(m=>m[1]).find(s=>s.includes('const PRESETS=['));
if(!source)throw Error('Missing preset library in pinned HTML');
const start=source.indexOf('const PRESETS=')+'const PRESETS='.length,end=source.indexOf('const templates=',start);
const presets=vm.runInNewContext(source.slice(start,end).trim().replace(/;$/,''),{}, {timeout:2000});
const native=fs.readFileSync(path.join(root,'app/src/main/java/br/com/evolufisico/nativo/domain/HtmlFormLibrary.kt'),'utf8');
for(const t of presets)for(const text of [t.id,t.title,t.description,...t.questions.map(q=>q.label)]){
 if(!native.includes(JSON.stringify(text).replace(/\$/g,'\\$')))throw Error('Native text differs from pinned HTML: '+t.id);
}
console.log(`${presets.length} models and ${presets.reduce((n,t)=>n+t.questions.length,0)} question texts match pinned HTML.`);
