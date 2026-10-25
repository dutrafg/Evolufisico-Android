package br.com.evolufisico.nativo.system

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.util.Base64
import br.com.evolufisico.nativo.data.Charge
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.data.TrainingSession
import br.com.evolufisico.nativo.domain.WorkspaceRules
import br.com.evolufisico.nativo.domain.ProfessionalTools
import br.com.evolufisico.nativo.domain.ClientWorkspace
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.AssessmentChecks
import br.com.evolufisico.nativo.domain.Financial
import br.com.evolufisico.nativo.domain.Metrics
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.Locale

/** Android-native PDFs: no embedded HTML, printer dialogs or WebView screenshots. */
object Reports {
    private val blue=Color.rgb(29,66,104)
    private class Writer(private val doc:PdfDocument) {
        private var pageNo=0
        private lateinit var page:PdfDocument.Page
        private var y=0f
        private val pen=Paint(Paint.ANTI_ALIAS_FLAG)
        private fun newPage(){
            if(::page.isInitialized)doc.finishPage(page)
            pageNo++
            page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNo).create())
            page.canvas.drawColor(Color.WHITE)
            pen.color=blue;pen.textSize=23f;pen.isFakeBoldText=true
            page.canvas.drawText("EvoluFísico",44f,57f,pen)
            pen.textSize=10f;pen.isFakeBoldText=false
            page.canvas.drawText("Relatório nativo • ${LocalDate.now()} • Página $pageNo",44f,77f,pen)
            pen.strokeWidth=1f;page.canvas.drawLine(44f,91f,551f,91f,pen)
            y=120f
        }
        private fun space(height:Float){if(y+height>785f)newPage()}
        fun section(title:String){ space(48f);y+=10f
            pen.color=blue;pen.textSize=15f;pen.isFakeBoldText=true
            page.canvas.drawText(title.take(65),44f,y,pen);pen.isFakeBoldText=false;y+=26f
        }
        fun line(label:String,value:String=""){
            val raw=if(value.isBlank())label else "$label: $value"
            val paragraphs=raw.split('\n')
            paragraphs.forEach { part ->
                var remain=part.trim()
                if(remain.isEmpty()){space(16f);y+=16f;return@forEach}
                while(remain.isNotEmpty()){
                    space(21f)
                    pen.color=Color.rgb(36,45,55);pen.textSize=11f;pen.isFakeBoldText=false
                    val size=pen.breakText(remain,true,507f,null).coerceAtLeast(1)
                    page.canvas.drawText(remain.take(size),44f,y,pen)
                    remain=remain.drop(size).trimStart();y+=19f
                }
            }
        }
        fun image(label:String,encoded:String){
            val value=encoded.substringAfter(";base64,",encoded)
            val decoded=runCatching{Base64.decode(value,Base64.DEFAULT)}.getOrNull()?:return
            if(decoded.size>3_000_000)return
            val opts=BitmapFactory.Options().apply{inJustDecodeBounds=true}
            BitmapFactory.decodeByteArray(decoded,0,decoded.size,opts)
            if(opts.outWidth !in 1..5000||opts.outHeight !in 1..5000)return
            val sample=(maxOf(opts.outHeight,opts.outWidth)/1000).coerceAtLeast(1)
            val bitmap=BitmapFactory.decodeByteArray(decoded,0,decoded.size,
                BitmapFactory.Options().apply{inSampleSize=sample})?:return
            val targetHeight=(bitmap.height*400f/bitmap.width).coerceAtMost(260f)
            space(targetHeight+45f)
            line(label)
            val rect=RectF(44f,y,444f,y+targetHeight)
            page.canvas.drawBitmap(bitmap,null,rect,pen)
            y+=targetHeight+15f
            bitmap.recycle()
        }
        fun finish():ByteArray {
            doc.finishPage(page)
            return ByteArrayOutputStream().use{stream->doc.writeTo(stream);stream.toByteArray()}
        }
        init{newPage()}
    }
    private inline fun render(block:Writer.()->Unit):ByteArray {
        val doc=PdfDocument()
        return try{Writer(doc).run{block();finish()}}finally{doc.close()}
    }
    fun evaluation(e:Evaluation)=render{
        section("Avaliação física • ${e.name}")
        line("Data",e.date);line("Próxima",e.nextAssessment.ifBlank{"Não informada"})
        if(e.goalWeightKg!=null || e.goalBodyFatPercent!=null) {
            section("Metas registradas")
            e.goalWeightKg?.let { line("Meta de peso","${Metrics.oneDecimal(it)} kg") }
            e.goalBodyFatPercent?.let { line("Meta de gordura","${Metrics.oneDecimal(it)}%") }
            line("Prazo pretendido",e.goalDate.ifBlank { "Não informado" })
            if(e.goalNotes.isNotBlank()) line("Observação da meta",e.goalNotes)
            line("Nota","Acompanhamento do objetivo registrado; não constitui prescrição ou diagnóstico.")
        }
        line("Nome",e.name);line("Sexo",e.sex);line("Idade",e.age?.toString()?:"—")
        line("Contato",listOf(e.phone,e.email).filter{it.isNotBlank()}.joinToString(" • "))
        line("Profissional",e.professional);line("Registro",e.registration);line("Unidade",e.clinic)
        section("Antropometria")
        line("Peso","${Metrics.oneDecimal(e.weightKg)} kg")
        line("Altura","${Metrics.oneDecimal(e.heightCm)} cm")
        line("IMC",Metrics.oneDecimal(Metrics.bmi(e.weightKg,e.heightCm)))
        line("Cintura","${Metrics.oneDecimal(e.waistCm)} cm")
        line("Quadril","${Metrics.oneDecimal(e.hipCm)} cm")
        line("RCQ",Metrics.oneDecimal(Metrics.waistHipRatio(e.waistCm,e.hipCm)))
        if(e.perimeters.isNotEmpty()){
            section("Perimetria")
            e.perimeters.toSortedMap().forEach{(k,v)->line(Anthropometry.perimeters[k]?:k,"${Metrics.oneDecimal(v)} cm")}
        }
        if(e.skinfolds.isNotEmpty()){
            section("Dobras cutâneas")
            e.skinfolds.toSortedMap().forEach{(k,v)->line(Anthropometry.sites[k]?:k,"${Metrics.oneDecimal(v)} mm")}
            val result=Anthropometry.pollock7(e.sex,e.age,e.weightKg,e.skinfolds)
            line("Soma das dobras", "${Metrics.oneDecimal(result?.totalMm)} mm")
            line("Gordura estimada (Pollock 7)","${Metrics.oneDecimal(result?.fatPercent)} %")
            line("Massa gorda estimada","${Metrics.oneDecimal(result?.fatMassKg)} kg")
            line("Massa magra estimada","${Metrics.oneDecimal(result?.leanMassKg)} kg")
        }
        if(e.bioimpedance.isNotEmpty()||e.bodyFatPercent!=null){
            section("Bioimpedância aferida")
            line("Gordura aferida","${Metrics.oneDecimal(e.bodyFatPercent)} %")
            line("Massa gorda aferida","${Metrics.oneDecimal(Metrics.fatMassKg(e.weightKg,e.bodyFatPercent))} kg")
            e.bioimpedance.toSortedMap().forEach{(k,v)->line(Anthropometry.bioFields[k]?:k,Metrics.oneDecimal(v))}
        }
        section("Aderência e contexto")
        line("Treino",if(e.trainingExperienced) e.trainingAdherence?.let{"$it %"}?:"Não medido" else "Não se aplica — nunca treinou")
        line("Dieta",if(e.dietExperienced)e.dietAdherence?.let{"$it %"}?:"Não medido" else "Não se aplica — nunca fez dieta")
        line("Média aplicável",Metrics.adherence(e.trainingExperienced,e.trainingAdherence,e.dietExperienced,e.dietAdherence)?.let{"${Metrics.oneDecimal(it)} %"}?:"Não avaliável")
        line("Energia",e.energy?.toString()?:"—");line("Fase",e.phase);line("Semana",e.week)
        section("Parecer do profissional")
        line(e.notes.ifBlank{"Não informado"})
        val more=linkedMapOf(
            "parecer_status" to "Status informado", "parecer_tags" to "Marcadores",
            "parecer_sono" to "Sono", "parecer_estresse" to "Estresse",
            "parecer_dor" to "Dor / desconforto", "meta_peso" to "Meta de peso",
            "meta_pgordura" to "Meta de gordura", "meta_prazo" to "Prazo",
            "pontos_fortes" to "Pontos fortes", "pontos_atencao" to "Pontos de atenção",
            "ajuste_treino" to "Orientações de treino", "ajuste_dieta" to "Orientações alimentares",
            "proximos_passos" to "Próximos passos", "bia_risco" to "Classificação do aparelho",
            "bia_analise_texto" to "Notas da bioimpedância")
        more.forEach{(key,label)->e.extendedAnswers[key]?.takeIf{it.isNotBlank()}?.let{line(label,it)}}
        if(e.photos.isNotEmpty()){
            section("Registro fotográfico")
            e.photos.forEach{(key,value)->image(key.replace('_',' '),value)}
        }
        section("Conferência de consistência")
        val alerts=AssessmentChecks.review(e)
        if(alerts.isEmpty())line("Nenhum alerta automático nos dados disponíveis.")
        else alerts.forEach { line(it.explanation) }
        section("Nota técnica")
        line("Dados e estimativas dependem de protocolo, calibração e interpretação por profissional habilitado. Não constitui diagnóstico.")
    }
    fun evolution(name:String,items:List<Evaluation>)=render{
        section("Evolução • $name")
        line("Total de avaliações",items.size.toString())
        val ordered=br.com.evolufisico.nativo.domain.EvaluationTimeline.ordered(items)
        ordered.forEach{e->
            section("Avaliação de ${e.date}")
            line("Peso","${Metrics.oneDecimal(e.weightKg)} kg")
            line("IMC",Metrics.oneDecimal(Metrics.bmi(e.weightKg,e.heightCm)))
            line("Gordura aferida","${Metrics.oneDecimal(e.bodyFatPercent)} %")
            line("Gordura estimada (Pollock)","${Metrics.oneDecimal(Anthropometry.pollock7(e.sex,e.age,e.weightKg,e.skinfolds)?.fatPercent)} %")
            line("Cintura","${Metrics.oneDecimal(e.waistCm)} cm")
            line("Aderência","${Metrics.oneDecimal(Metrics.adherence(e.trainingExperienced,e.trainingAdherence,e.dietExperienced,e.dietAdherence))} %")
        }
        if(ordered.size>=2){
            section("Variações entre primeira e última")
            val first=ordered.first();val last=ordered.last()
            fun delta(a:Double?,b:Double?)=if(a!=null&&b!=null)Metrics.oneDecimal(b-a)else"—"
            line("Peso","${delta(first.weightKg,last.weightKg)} kg")
            line("Cintura","${delta(first.waistCm,last.waistCm)} cm")
            line("Gordura aferida","${delta(first.bodyFatPercent,last.bodyFatPercent)} ponto(s) percentual(is)")
        }
        section("Nota técnica")
        line("Comparações refletem medidas registradas; diferenças de protocolo ou equipamentos podem influenciar resultados.")
    }
    fun monthlyBilling(items:List<Charge>,period:String,professional:String="")=render {
        section(if(period.isBlank())"Histórico financeiro completo" else "Caixa mensal — $period")
        if(professional.isNotBlank())line("Profissional",professional)
        val periods=if(period.isBlank())br.com.evolufisico.nativo.domain.MonthlyLedger.periods(items) else listOf(java.time.YearMonth.parse(period))
        periods.forEach{month->
            val report=br.com.evolufisico.nativo.domain.MonthlyLedger.summary(items,month)
            section(month.toString());line("Recebido",currency(report.receivedCents));line("A receber",currency(report.pendingCents))
            listOf("Recebimentos" to report.paid,"Pendências" to report.pending).forEach{(title,rows)->
                section(title)
                if(rows.isEmpty())line("Nenhum lançamento neste período.")
                rows.forEach{c->line("${br.com.evolufisico.nativo.domain.MonthlyLedger.date(c)} • ${c.student} ${c.secondStudent}");line(c.service,"${currency(Financial.billable(c.amountCents,c.discountCents,c.exempt))} • ${c.paymentMethod}")}
            }
        }
        line("Não substitui documentos fiscais ou comprovantes bancários.")
    }
    fun billing(items:List<Charge>)=render{
        section("Relatório financeiro")
        val charged=items.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
        val received=items.filter{it.paid}.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}
        line("Lançamentos",items.size.toString())
        line("Recebido",currency(received));line("A receber",currency(charged-received))
        line("Total líquido",currency(charged))
        section("Extrato detalhado")
        items.sortedBy{it.dueDate}.forEach{charge->
            line("${charge.dueDate} • ${charge.student} • ${charge.secondStudent}")
            line("  Serviço",charge.service)
            line("  Valor original",currency(charge.amountCents))
            line("  Desconto",currency(charge.discountCents))
            line("  Total",currency(Financial.billable(charge.amountCents,charge.discountCents,charge.exempt)))
            line("  Pagamento","${if(charge.exempt)"Cortesia"else if(charge.paid)"Pago"else"Pendente"} • ${charge.paymentMethod} • ${charge.installments} parcela(s)")
        }
        section("Resumo")
        line("Não substitui documentos fiscais ou comprovantes bancários.")
    }
    fun training(item:TrainingSession):ByteArray {
        WorkspaceRules.validate(item)
        return render {
            section(if(item.isTemplate)"Modelo de treino" else "Sessão de treino")
            line("Título",item.title)
            if(!item.isTemplate){line("Cliente",item.student.ifBlank{"Sem vínculo"});line("Data",item.date)}
            line("Duração informada","${item.durationMinutes} min")
            line("Situação",if(item.isTemplate)"Modelo reutilizável" else if(item.completed)"Concluído" else "Planejado")
            if(item.sets.isNotEmpty()){
                section("Séries estruturadas")
                item.sets.forEachIndexed{index,row->
                    line("${index+1}. ${row.exercise}")
                    line("Repetições / carga externa","${row.repetitions} rep · ${Metrics.oneDecimal(row.loadKg)} kg · ${if(item.isTemplate)"Planejada" else if(row.done)"Marcada" else "Não marcada"}")
                }
                if(!item.isTemplate)line("Volume externo das séries marcadas","${Metrics.oneDecimal(ProfessionalTools.completedVolume(item))} kg·rep")
            }
            if(item.exercises.isNotBlank()){section("Instruções complementares");line(item.exercises)}
            if(item.notes.isNotBlank()){section("Observações");line(item.notes)}
            if(!item.isTemplate)item.effort?.let{line("Esforço percebido","$it / 10")}
            section("Leitura do registro")
            line("Reproduz dados registrados. Carga zero significa sem carga externa informada; o volume não estima peso corporal ou resistência de máquina.")
        }
    }
    fun clientDossier(data:ClientWorkspace.Snapshot):ByteArray = render {
        val profile=data.profile
        section("Resumo do dossiê")
        line("Cliente",profile.name);line("Contato",listOf(profile.phone,profile.email).filter{it.isNotBlank()}.joinToString(" · "))
        line("Avaliação mais recente",profile.date)
        line("Peso / gordura","${Metrics.oneDecimal(profile.weightKg)} kg · ${Metrics.oneDecimal(profile.bodyFatPercent)}%")
        section("Acompanhamento")
        line("Avaliações",data.evaluations.size.toString())
        line("Atendimentos",data.appointments.size.toString())
        line("Treinos concluídos",data.training.count{it.completed}.toString())
        line("Registros de hábitos",data.habits.size.toString())
        line("Notas ativas",data.notes.size.toString())
        val pending=data.charges.filter{!it.paid && !it.exempt}
        line("Cobranças vinculadas pendentes",currency(pending.sumOf{Financial.billable(it.amountCents,it.discountCents,it.exempt)}))
        line("Critério financeiro","Cobranças conjuntas entram pelo valor integral, sem rateio.")
        section("Últimas avaliações (até 10)")
        data.evaluations.take(10).forEach{line(it.date,"${Metrics.oneDecimal(it.weightKg)} kg · ${Metrics.oneDecimal(it.bodyFatPercent)}% gordura")}
        section("Atendimentos por data (até 10)")
        data.appointments.take(10).forEach{line("${it.date} ${it.time}","${it.type} · ${it.status}")}
        section("Tarefas pendentes por prioridade (até 20)")
        val tasks=data.tasks.filterNot{it.done}
        if(tasks.isEmpty())line("Nenhuma tarefa pendente vinculada.")
        tasks.take(20).forEach{line(it.dueDate,it.title)}
        section("Escopo do resumo")
        line("Resumo de registros vinculados por ID, não inclui fotos nem conteúdo das notas. Não substitui o backup criptografado nem os relatórios detalhados.")
    }
    fun intake(item:br.com.evolufisico.nativo.data.Intake):ByteArray {
        if(item.answers.containsKey("_template"))return br.com.evolufisico.nativo.domain.FillableIntakePdf.generate(item)
        return render {
        section("Anamnese · ${item.level}")
        line("Cliente",item.student);line("Data",item.date)
        line("Identificador",item.studentId.ifBlank{"Sem vínculo"})
        line("Registro autodeclarado. Este documento não concede aptidão para atividade física.")
        section("Respostas registradas")
        if(item.answers["objective"].isNullOrBlank() && item.objective.isNotBlank())line("Objetivo",item.objective)
        if(item.answers["history"].isNullOrBlank() && item.healthHistory.isNotBlank())line("Histórico relatado",item.healthHistory)
        if(item.answers["work"].isNullOrBlank() && item.routine.isNotBlank())line("Rotina",item.routine)
        if(item.answers.containsKey("_template")) {
            line("Modelo",item.answers.getValue("_template"))
            item.answers.keys.filter{it.startsWith("_label_")}.sortedBy{it.substringAfterLast('_').toIntOrNull() ?: 0}.forEach{key->
                val response=item.answers["response_"+key.substringAfterLast('_')].orEmpty()
                line(item.answers.getValue(key),response.ifBlank{"Não respondido: __________________________________"})
            }
        } else {
            item.answers.filterValues{it.isNotBlank()}.forEach{(key,value)->line(br.com.evolufisico.nativo.domain.IntakeQuestionnaire.labels[key] ?: key,value)}
            val unanswered=(1..7).count{item.answers["parq_$it"].isNullOrBlank()}
            line("Perguntas de triagem sem resposta",unanswered.toString())
        }
        section("Observações e encaminhamentos");line(item.observations.ifBlank{"Sem observações."})
    }
    }
    fun analysis(name:String,dates:List<String>,text:String):ByteArray=render {
        require(dates.distinct().size in 3..4 && text.length<=12000)
        section("Relatório de análises")
        line("Aluno",name);line("Avaliações selecionadas",dates.joinToString(", "))
        line("Síntese do profissional",text)
        line("Limites","Índices matemáticos exploratórios. Não são diagnóstico nem percentual de precisão clínica. A edição desta síntese não altera as avaliações originais.")
    }
    private fun currency(value:Long)=java.text.NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value/100.0)
}
