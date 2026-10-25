package br.com.evolufisico.nativo.domain

import kotlin.math.*

/** EF77 equations and operational limits from reference/app.html. No diagnostic classifications. */
object HtmlLaboratory {
    data class Input(val sex:String,val age:Int?,val height:Double?,val weight:Double?,val neck:Double?,val waist:Double?,val hip:Double?,val pollock:Double?,val bia:Double?)
    data class Result(val methods:Map<String,Double>,val mean:Double?,val amplitude:Double?,val reference:Double?,val absi:Double?,val ffmi:Double?,val normalizedFfmi:Double?,val water:Double?,val fcmax:Double?,val minor:Boolean)
    private fun within(v:Double?,lo:Double,hi:Double)=v?.takeIf{it.isFinite()&&it in lo..hi}
    fun calculate(i:Input):Result {
        val h=within(i.height,100.0,230.0);val w=within(i.weight,20.0,400.0)
        val n=within(i.neck,20.0,70.0);val c=within(i.waist,40.0,200.0);val q=within(i.hip,50.0,200.0)
        val age=i.age?.takeIf{it in 5..110};val minor=age!=null&&age<18
        val sex=when(i.sex){"F","Feminino"->"F";"M","Masculino"->"M";else->null}
        val methods=linkedMapOf<String,Double>()
        if(!minor)within(i.pollock,2.0,60.0)?.let{methods["Pollock 7 dobras"]=it}
        if(!minor&&sex!=null&&h!=null&&n!=null&&c!=null){
            val d=if(sex=="F")q?.let{c+it-n} else c-n
            if(d!=null&&d>0){val density=if(sex=="F")1.29579-.35004*log10(d)+.221*log10(h) else 1.0324-.19077*log10(d)+.15456*log10(h)
                within(495/density-450,2.0,60.0)?.let{methods["US Navy"]=it}}
        }
        if(!minor&&sex!=null&&h!=null&&c!=null)within((if(sex=="F")76 else 64)-20*h/c,2.0,60.0)?.let{methods["RFM"]=it}
        within(i.bia,2.0,60.0)?.let{methods["Bioimpedância"]=it}
        val mean=methods.values.takeIf{it.isNotEmpty()}?.average()
        val amplitude=methods.values.takeIf{it.isNotEmpty()}?.let{it.max()-it.min()}
        val ref=mean?.takeIf{amplitude!=null&&amplitude<8}
        val hm=h?.div(100)
        val ffmi=if(hm!=null&&w!=null&&ref!=null)w*(1-ref/100)/(hm*hm) else null
        val absi=if(hm!=null&&w!=null&&c!=null)(c/100)/(w/(hm*hm)).pow(2.0/3)/sqrt(hm) else null
        val water=if(!minor&&sex!=null&&h!=null&&w!=null&&age!=null){val v=if(sex=="F")-2.097+.1069*h+.2466*w else 2.447-.09516*age+.1074*h+.3362*w;v.takeIf{it.isFinite()&&it>0}} else null
        return Result(methods,mean,amplitude,ref,absi,ffmi,if(ffmi!=null&&hm!=null)ffmi+6.1*(1.8-hm) else null,water,age?.let{208-.7*it},minor)
    }
}
