// Executes the pure C equations from the immutable uploaded HTML, without network or DOM.
const fs=require('fs'),vm=require('vm');
const html=fs.readFileSync('reference/app.html','utf8');
const start=html.indexOf('var C={\n  navy:function');
const end=html.indexOf('/* ---------- Leitura de fontes',start);
if(start<0||end<0)throw Error('EF77 source not found');
const ctx={Math};vm.createContext(ctx);vm.runInContext('var log10=Math.log10;'+html.slice(start,end),ctx);
const C=ctx.C,cases=[];
for(const sex of ['M','F'])for(const height of [155,180])for(const weight of [60,95])for(const waist of [75,105]){
 const neck=37,hip=105,age=35,pollock=23,bia=24;
 const navy=C.navy(sex,height,neck,waist,hip),rfm=C.rfm(sex,height,waist);
 const methods={"Pollock 7 dobras":pollock};if(navy!=null)methods['US Navy']=navy;if(rfm!=null)methods.RFM=rfm;methods['Bioimpedância']=bia;
 const vals=Object.values(methods),mean=vals.reduce((a,b)=>a+b,0)/vals.length,amplitude=Math.max(...vals)-Math.min(...vals),reference=amplitude>=8?null:mean,ffmi=C.ffmi(height,weight,reference);
 cases.push({sex,height,weight,waist,neck,hip,age,pollock,bia,methods,mean,amplitude,reference,absi:C.absi(height,weight,waist),ffmi:ffmi?.ffmi??null,normalizedFfmi:ffmi?.norm??null,water:C.act(sex,weight,height,age),fcmax:C.fcmax(age)});
}
fs.writeFileSync('qa/html/ef77-golden.json',JSON.stringify(cases,null,2)+'\n');console.log(cases.length+' EF77 reference vectors');
