// Evaluates only the pinned mathematical engine in a sandbox, without browser/storage/network.
const fs=require('fs'),path=require('path'),vm=require('vm');const root=path.resolve(__dirname,'../..');
const html=fs.readFileSync(path.join(root,'reference/app.html'),'utf8');const engine=[...html.matchAll(/<script\b[^>]*>([\s\S]*?)<\/script\s*>/gi)].map(x=>x[1]).find(x=>x.includes("var VERSION='7.0', cache=new Map()"));
if(!engine)throw Error('Pinned EF70 engine missing');
const ctx={window:{__EF63_STATE__:{version:1,getAvaliacoes:()=>[]},addEventListener:()=>{}},EFU:{num:x=>x==null||x===''?null:Number.isFinite(Number(x))?Number(x):null},EFIdentity:{key:x=>x.cliente_id},EFMeasures:{lean:a=>a.dobras_resultado?.massaMagra??null},document:{getElementById:()=>null},setTimeout:()=>{}};vm.createContext(ctx);vm.runInContext(engine,ctx,{timeout:2000});
const cases=[];
for(let n=0;n<48;n++){
 const rows=[0,14,n%7===0?19:28,n%5===0?63:42].slice(0,n%3===0?4:3).map((d,i)=>{
  const bia=n%4===0&&i>0,g=25-i*(n%5)*.6,m=60+i*.7,c=90-i*(n%4),w=90-i*.5;
  return {cliente_id:'c',id:`${n}-${i}`,nome:'Caso sintético',data_avaliacao:new Date(Date.UTC(2026,0,1+d)).toISOString().slice(0,10),peso:w,perimetria:{cintura:c},dobras_resultado:{percGordura:bia?null:g,massaMagra:m},bioimpedancia:{pgordura:bia?g:(n%6===0?g+9:null)}};
 });
 const a=ctx.window.EF70.analyzeRecords('c',rows);cases.push({rows:a.list,score:a.score.value,quality:a.confidence,slopes:Object.fromEntries(Object.entries(a.trends).map(([k,v])=>[k,v?.per30??null]))});
}
const num=v=>v==null?'null':Number.isInteger(v)?String(v)+'.0':String(v);let out='package br.com.evolufisico.nativo.domain\nimport org.junit.Assert.*\nimport org.junit.Test\n/** Expected values executed from EF70 in reference/app.html; regenerate with qa/html/generate_ef70_cases.cjs. */\nclass HtmlEngineGoldenTest { @Test fun fortyEightVectorsMatchPinnedJavascript(){\n';
for(const [i,c] of cases.entries()){
 out+='run { val result=HtmlAnalysis.analyze(listOf('+c.rows.map(r=>`HtmlAnalysis.Point(${Math.floor(r.date/86400000)}L,${num(r.g)},${num(r.m)},${num(r.c)},${num(r.p)},${r.method?JSON.stringify(r.method):'null'},${num(r.pairedDelta)})`).join(',')+'));\n';
 out+=`assertEquals("score case ${i}",${c.score==null?'null':`Integer.valueOf(${c.score})`},result.trajectory);assertEquals("quality case ${i}",${c.quality==null?'null':`Integer.valueOf(${c.quality})`},result.sufficiency);\n`;
 for(const [key,label] of Object.entries({gordura:'Gordura',massaMagra:'Massa magra',cintura:'Cintura',peso:'Peso'})){const value=c.slopes[key];out+=value==null?`assertNull(result.per30["${label}"]);\n`:`assertEquals(${num(value)},result.per30["${label}"]!!,1e-8);\n`;}
 out+='}\n';
}
out+='}}\n';fs.writeFileSync(path.join(root,'app/src/test/java/br/com/evolufisico/nativo/domain/HtmlEngineGoldenTest.kt'),out);fs.writeFileSync(path.join(__dirname,'ef70-golden.json'),JSON.stringify(cases,null,2));console.log('48 independent JavaScript golden vectors generated.');
