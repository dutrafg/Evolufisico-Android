const fs=require('fs'),vm=require('vm');
const html=fs.readFileSync('reference/app.html','utf8');
const engine=[...html.matchAll(/<script\b[^>]*>([\s\S]*?)<\/script\s*>/gi)].map(x=>x[1]).find(x=>x.includes("var VERSION='6.8'"));
if(!engine)throw Error('EF68 source missing');
const ctx={window:{__EF63_STATE__:{version:1,getAvaliacoes:()=>[]},addEventListener:()=>{}},EFU:{num:x=>x==null||x===''?null:Number.isFinite(Number(x))?Number(x):null,norm:x=>String(x)},EFIdentity:{key:x=>x.cliente_id},document:{getElementById:()=>null},setTimeout:()=>{}};
vm.createContext(ctx);vm.runInContext(engine,ctx,{timeout:2000});
const cases=[];
for(let n=0;n<36;n++){
 const days=n%6===0?[0]:[0,14,n%5===0?19:35,n%4===0?140:70];
 const records=days.map((d,i)=>{const bia=n%7===0&&i>0,f=30-(n%3===0&&i===3?-1:i)*(n%5)*.7;return{cliente_id:'c',id:`${n}-${i}`,nome:'Caso sintético',data_avaliacao:new Date(Date.UTC(2026,0,1+d)).toISOString().slice(0,10),peso:90-i*.5,perimetria:{cintura:90-i*(n%4)},dobras_resultado:{percGordura:bia?null:f,massaMagra:60+i*.7},bioimpedancia:{pgordura:bia?f:null}}});
 const a=ctx.window.EF68.analyzeRecords('c',records);
 cases.push({rows:a.validTimeline.map(r=>({day:Math.floor(r.date/86400000),fat:r.gordura,lean:r.massaMagra,waist:r.cintura,weight:r.peso,method:r.method})),events:a.events.map(e=>e.type).sort(),deltas:a.transitions.map(t=>t.delta)});
}
fs.writeFileSync('qa/html/ef68-golden.json',JSON.stringify(cases,null,2)+'\n');console.log(cases.length+' EF68 reference vectors');
