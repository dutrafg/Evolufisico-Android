package br.com.evolufisico.nativo.ui.screens

import androidx.activity.compose.BackHandler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.data.AppRepository
import br.com.evolufisico.nativo.data.Evaluation
import br.com.evolufisico.nativo.data.readLimited
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.AssessmentChecks
import br.com.evolufisico.nativo.domain.Metrics
import br.com.evolufisico.nativo.domain.GoalTracking
import java.io.ByteArrayOutputStream
import java.time.LocalDate

private val photoLabels = linkedMapOf("frente" to "Frente", "perfil_direito" to "Perfil direito",
    "perfil_esquerdo" to "Perfil esquerdo", "costas" to "Costas", "extra" to "Extra")

@Composable
fun EvaluationsScreen(repo: AppRepository, onPdf: (Evaluation) -> Unit, createRequest: Int=0, initialStudentId: String="") {
    var editing by remember { mutableStateOf<Evaluation?>(null) }
    var editorOpen by remember { mutableStateOf(false) }
    LaunchedEffect(createRequest) {
        if(createRequest>0) {
            editing=repo.evaluations.firstOrNull{it.studentId==initialStudentId}?.newCheckIn()
            editorOpen=true
        }
    }
    var deletePending by remember { mutableStateOf<Evaluation?>(null) }
    var query by remember { mutableStateOf("") }
    if (deletePending != null) AlertDialog(
        onDismissRequest = { deletePending = null }, title = { Text("Excluir avaliação?") },
        text = { Text("O registro será excluído. O histórico das demais avaliações não será afetado.") },
        confirmButton = { TextButton(onClick = { deletePending?.let(repo::delete); deletePending = null }) { Text("Excluir") } },
        dismissButton = { TextButton(onClick = { deletePending = null }) { Text("Cancelar") } }
    )
    if (editorOpen) EvaluationEditor(editing ?: remember(editorOpen) { Evaluation() }, repo.evaluations.distinctBy { it.studentId },
        onCancel = { editorOpen = false }, onSave = { repo.save(it); editorOpen = false })
    else Screen {
        Heading("Avaliações", "Antropometria, Pollock 7, bioimpedância, fotos e reavaliações.")
        Button(onClick = { editing = null; editorOpen = true }) { Text("+ Nova avaliação") }
        Field("Buscar por aluno", query, { query = it })
        val list = repo.evaluations.filter { br.com.evolufisico.nativo.domain.WorkspaceRules.search(it.name,query) }
        if (list.isEmpty()) Text("Nenhuma avaliação encontrada.")
        PagedRecords(list,query,{it.id}) { e ->
            val pg = Anthropometry.pollock7(e.sex,e.age,e.weightKg,e.skinfolds)?.fatPercent
            val earlier = repo.evaluations.filter { it.studentId == e.studentId && it.id != e.id && it.date < e.date }.maxWithOrNull(compareBy<Evaluation>{it.date}.thenBy{it.id})
            val alerts = AssessmentChecks.review(e,earlier)
            DataCard(e.name, "${e.date}  •  ${Metrics.oneDecimal(e.weightKg)} kg  •  IMC ${Metrics.oneDecimal(Metrics.bmi(e.weightKg,e.heightCm))}  •  Pollock ${Metrics.oneDecimal(pg)}%" +
                if(alerts.isEmpty()) "" else "  •  ${alerts.size} item(ns) para conferir") {
                TextButton(onClick = { editing = e.newCheckIn(); editorOpen = true }) { Text("Reavaliar") }
                TextButton(onClick = { editing = e; editorOpen = true }) { Text("Editar") }
            }
            if (alerts.isNotEmpty()) {
                var expanded by remember(e.id) { mutableStateOf(false) }
                TextButton(onClick={expanded=!expanded}) { Text(if(expanded) "Ocultar conferência" else "Conferir dados (${alerts.size})") }
                if(expanded) alerts.forEach { alert -> Text("• ${alert.explanation}",style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.error) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = { onPdf(e) }) { Text("PDF") }
                TextButton(onClick = { deletePending = e }) { Text("Excluir") }
            }
        }
    }
}

@Composable
private fun EvaluationEditor(initial: Evaluation, students: List<Evaluation>, onCancel: () -> Unit, onSave: (Evaluation) -> Unit) {
    TrackEditing()
    var discardRequested by remember(initial.id) { mutableStateOf(false) }
    BackHandler { discardRequested = true }
    if (discardRequested) AlertDialog(
        onDismissRequest = { discardRequested = false },
        title = { Text("Descartar alterações?") },
        text = { Text("Os dados desta edição que não foram salvos serão perdidos.") },
        confirmButton = { TextButton(onClick = onCancel) { Text("Descartar") } },
        dismissButton = { TextButton(onClick = { discardRequested = false }) { Text("Continuar editando") } }
    )
    val context = LocalContext.current
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var studentId by remember(initial.id) { mutableStateOf(initial.studentId) }
    var sex by remember(initial.id) { mutableStateOf(initial.sex) }
    var age by remember(initial.id) { mutableStateOf(initial.age?.toString() ?: "") }
    var date by remember(initial.id) { mutableStateOf(initial.date) }
    var email by remember(initial.id) { mutableStateOf(initial.email) }
    var phone by remember(initial.id) { mutableStateOf(initial.phone) }
    var next by remember(initial.id) { mutableStateOf(initial.nextAssessment) }
    var weight by remember(initial.id) { mutableStateOf(initial.weightKg?.toString() ?: "") }
    var height by remember(initial.id) { mutableStateOf(initial.heightCm?.toString() ?: "") }
    var waist by remember(initial.id) { mutableStateOf(initial.waistCm?.toString() ?: "") }
    var hip by remember(initial.id) { mutableStateOf(initial.hipCm?.toString() ?: "") }
    var fat by remember(initial.id) { mutableStateOf(initial.bodyFatPercent?.toString() ?: "") }
    var goalWeight by remember(initial.id) { mutableStateOf(initial.goalWeightKg?.toString() ?: initial.extendedAnswers["meta_peso"].orEmpty()) }
    var goalFat by remember(initial.id) { mutableStateOf(initial.goalBodyFatPercent?.toString() ?: initial.extendedAnswers["meta_pgordura"].orEmpty()) }
    var goalDate by remember(initial.id) { mutableStateOf(initial.goalDate.ifBlank { initial.extendedAnswers["meta_prazo"].orEmpty() }) }
    var goalNotes by remember(initial.id) { mutableStateOf(initial.goalNotes) }
    var trained by remember(initial.id) { mutableStateOf(initial.trainingExperienced) }
    var dieted by remember(initial.id) { mutableStateOf(initial.dietExperienced) }
    var trainAdherence by remember(initial.id) { mutableStateOf(initial.trainingAdherence?.toString() ?: "") }
    var dietAdherence by remember(initial.id) { mutableStateOf(initial.dietAdherence?.toString() ?: "") }
    var notes by remember(initial.id) { mutableStateOf(initial.notes) }
    var phase by remember(initial.id) { mutableStateOf(initial.phase) }
    var week by remember(initial.id) { mutableStateOf(initial.week) }
    var energy by remember(initial.id) { mutableStateOf(initial.energy?.toString() ?: "") }
    var professional by remember(initial.id) { mutableStateOf(initial.professional) }
    var registration by remember(initial.id) { mutableStateOf(initial.registration) }
    var clinic by remember(initial.id) { mutableStateOf(initial.clinic) }
    val per = remember(initial.id) { mutableStateMapOf<String, String>().also { m -> initial.perimeters.forEach { (k,v) -> m[k] = v.toString() } } }
    val folds = remember(initial.id) { mutableStateMapOf<String, String>().also { m -> initial.skinfolds.forEach { (k,v) -> m[k] = v.toString() } } }
    val bio = remember(initial.id) { mutableStateMapOf<String, String>().also { m -> initial.bioimpedance.forEach { (k,v) -> m[k] = v.toString() } } }
    val photos = remember(initial.id) { mutableStateMapOf<String, String>().also { m -> m.putAll(initial.photos) } }
    val extended = remember(initial.id) { mutableStateMapOf<String,String>().also { m -> m.putAll(initial.extendedAnswers) } }
    var photoToImport by remember { mutableStateOf("frente") }
    var photoError by remember { mutableStateOf("") }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readLimited(9_000_000) } ?: error("Imagem indisponível")
            require(bytes.size <= 9_000_000) { "Imagem acima de 9 MB" }
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes,0,bytes.size,options)
            require(options.outWidth > 0 && options.outHeight > 0) { "Imagem inválida" }
            val sample = (maxOf(options.outWidth, options.outHeight) / 1200).coerceAtLeast(1)
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
                ?: error("Não foi possível ler a foto")
            val scaled = if (maxOf(bitmap.width,bitmap.height) > 900) {
                val factor = 900.0 / maxOf(bitmap.width, bitmap.height)
                Bitmap.createScaledBitmap(bitmap,(bitmap.width*factor).toInt().coerceAtLeast(1),
                    (bitmap.height*factor).toInt().coerceAtLeast(1),true)
            } else bitmap
            val output = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 75, output)
            require(output.size() <= 650_000) { "Foto muito grande após compressão" }
            photos[photoToImport] = Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
            photoError = ""
        }.onFailure { photoError = it.localizedMessage ?: "Falha na foto" }
    }
    var section by remember(initial.id) { mutableStateOf("Cadastro") }
    var error by remember(initial.id) { mutableStateOf("") }
    fun n(s: String): Double? = s.trim().replace(',','.').takeIf(String::isNotBlank)?.toDoubleOrNull()
    fun parseMap(values: Map<String,String>): Map<String,Double> = values.mapNotNull { (k,v) -> n(v)?.let { k to it } }.toMap()
    Screen {
        Heading(if (initial.name.isBlank()) "Nova avaliação" else "Avaliação · ${initial.name}",
            "As informações cadastrais permanecem vinculadas pelo ID do aluno.")
        PillChoices(listOf("Cadastro", "Perímetros", "Dobras", "Bioimpedância", "Fotos", "Aderência", "Parecer"), section) { section = it }
        when (section) {
            "Cadastro" -> {
                if(initial.name.isBlank() && students.isNotEmpty()) {
                    Text("Continuar histórico de aluno existente (opcional)",style=MaterialTheme.typography.titleSmall)
                    val choices=studentChoices(students)
                    PillChoices(choices.map { it.second },choices.firstOrNull { it.first==studentId }?.second.orEmpty()) { label ->
                        students.firstOrNull { st -> choices.any { it.first==st.studentId && it.second==label } }?.let { st ->
                            studentId=st.studentId; name=st.name;sex=st.sex;age=st.age?.toString().orEmpty()
                            email=st.email; phone=st.phone; height=st.heightCm?.toString().orEmpty()
                            professional=st.professional;registration=st.registration;clinic=st.clinic
                        }
                    }
                }
                Field("Nome *",name,{name=it}); Field("E-mail",email,{email=it}); Field("Telefone/WhatsApp",phone,{phone=it})
                Text("Sexo (para fórmula das dobras)")
                PillChoices(listOf("Não informado","Feminino","Masculino"),sex) { sex=it }
                Field("Idade (anos)",age,{age=it}); DateField("Avaliação",date,{date=it})
                DateField("Próxima avaliação",next,{next=it})
                Field("Peso (kg)",weight,{weight=it}); Field("Altura (cm)",height,{height=it})
                Field("Fase",phase,{phase=it}); Field("Semana/check-in",week,{week=it})
                Field("Profissional",professional,{professional=it}); Field("Registro profissional",registration,{registration=it})
                Field("Clínica/unidade",clinic,{clinic=it})
            }
            "Perímetros" -> {
                Field("Cintura (cm)",waist,{waist=it}); Field("Quadril (cm)",hip,{hip=it})
                Anthropometry.perimeters.forEach { (key,label) -> Field("$label (cm)",per[key].orEmpty(),{per[key]=it}) }
                DataCard("RCQ",Metrics.oneDecimal(Metrics.waistHipRatio(n(waist),n(hip))))
            }
            "Dobras" -> {
                Text("Jackson & Pollock · 7 dobras em milímetros; adultos 18–80 anos, protocolo original do APK.")
                Anthropometry.sites.forEach { (key,label) -> Field("$label (mm)",folds[key].orEmpty(),{folds[key]=it}) }
                val result = Anthropometry.pollock7(sex,age.toIntOrNull(),n(weight),parseMap(folds))
                DataCard("Resultado Pollock 7", if(result == null) "Preencha sexo, idade, peso e as 7 dobras para calcular." else
                    "Soma ${Metrics.oneDecimal(result.totalMm)} mm · Gordura ${Metrics.oneDecimal(result.fatPercent)}% · Massa magra ${Metrics.oneDecimal(result.leanMassKg)} kg")
                if(result?.clamped == true) Text("Atenção: resultado limitado a 3–60%. Revise as medidas.", color=MaterialTheme.colorScheme.error)
                if((result?.totalMm ?: 0.0) > 280) Text("Soma acima de 280 mm: interpretação exige cautela.")
            }
            "Bioimpedância" -> {
                Field("Gordura aferida (%)",fat,{fat=it})
                Anthropometry.bioFields.forEach { (key,label) -> Field(label,bio[key].orEmpty(),{bio[key]=it}) }
                Field("Classificação apresentada pelo aparelho (texto original)",extended["bia_risco"].orEmpty(),{extended["bia_risco"]=it})
                Field("Observações específicas da bioimpedância",extended["bia_analise_texto"].orEmpty(),{extended["bia_analise_texto"]=it},lines=3)
                DataCard("Massa gorda aferida", "${Metrics.oneDecimal(Metrics.fatMassKg(n(weight),n(fat)))} kg")
            }
            "Fotos" -> {
                Text("Fotos guardadas no banco criptografado. Imagens são redimensionadas para reduzir o tamanho do backup.")
                photoLabels.forEach { (key,label) ->
                    Text(label, style=MaterialTheme.typography.titleSmall)
                    photos[key]?.takeIf { it.isNotEmpty() }?.let { encoded ->
                        val bitmap = remember(encoded) { runCatching { val bytes=Base64.decode(encoded.substringAfter(";base64,",encoded),Base64.DEFAULT)
                            if(bytes.size>4_000_000) null else BitmapFactory.decodeByteArray(bytes,0,bytes.size) }.getOrNull() }
                        if(bitmap != null) Image(bitmap.asImageBitmap(), label, modifier=Modifier.fillMaxWidth().heightIn(max=220.dp))
                    }
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick={photoToImport=key;imagePicker.launch("image/*")}) { Text("Escolher foto") }
                        if(photos.containsKey(key)) TextButton(onClick={photos.remove(key)}) { Text("Remover") }
                    }
                }
                if(photoError.isNotBlank()) Text(photoError,color=MaterialTheme.colorScheme.error)
            }
            "Aderência" -> {
                Field("Energia percebida (1–5)",energy,{energy=it})
                Text("Já treinou regularmente?"); Row { Switch(checked=trained,onCheckedChange={trained=it}); Spacer(Modifier.width(10.dp)); Text(if(trained) "Sim" else "Nunca treinou · não se aplica") }
                if(trained) Field("Aderência treino (0–100%)",trainAdherence,{trainAdherence=it})
                Text("Já seguiu dieta/plano alimentar?"); Row { Switch(checked=dieted,onCheckedChange={dieted=it}); Spacer(Modifier.width(10.dp)); Text(if(dieted) "Sim" else "Nunca fez dieta · não se aplica") }
                if(dieted) Field("Aderência dieta (0–100%)",dietAdherence,{dietAdherence=it})
                val adherence = Metrics.adherence(trained,trainAdherence.toIntOrNull(),dieted,dietAdherence.toIntOrNull())
                DataCard("Aderência aplicável",adherence?.let { "${Metrics.oneDecimal(it)}%" } ?: "Não avaliável (sem itens aplicáveis)")
            }
            "Parecer" -> {
                Field("Observações e parecer do profissional",notes,{notes=it},lines=4)
                Text("Metas definidas com o cliente",style=MaterialTheme.typography.titleMedium)
                Text("Acompanhamento matemático do objetivo registrado. Não representa prescrição nem avaliação clínica.",
                    style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Field("Meta de peso (kg)",goalWeight,{goalWeight=it})
                Field("Meta de gordura corporal (%)",goalFat,{goalFat=it})
                DateField("Prazo pretendido",goalDate,{goalDate=it})
                Field("Observação sobre a meta",goalNotes,{goalNotes=it},lines=2)
                val goalDateParsed=goalDate.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                if(goalWeight.isNotBlank()) DataCard("Percurso da meta de peso",
                    GoalTracking.summary("Peso",initial.goalWeightBaselineKg ?: n(weight),n(weight),n(goalWeight),"kg",goalDateParsed))
                if(goalFat.isNotBlank()) DataCard("Percurso da meta de gordura",
                    GoalTracking.summary("Gordura corporal",initial.goalBodyFatBaselinePercent ?: n(fat),n(fat),n(goalFat),"p.p.",goalDateParsed))
                val questions=linkedMapOf(
                    "parecer_status" to "Status informado pelo profissional",
                    "parecer_tags" to "Marcadores / pontos de acompanhamento",
                    "parecer_sono" to "Sono (observações)",
                    "parecer_estresse" to "Estresse (observações)",
                    "parecer_dor" to "Dor e desconforto (relato)",
                    "meta_peso" to "Meta de peso (kg)",
                    "meta_pgordura" to "Meta de gordura (%)",
                    "meta_prazo" to "Prazo pretendido (AAAA-MM-DD)",
                    "pontos_fortes" to "Pontos fortes",
                    "pontos_atencao" to "Pontos de atenção",
                    "ajuste_treino" to "Orientações de treino",
                    "ajuste_dieta" to "Orientações alimentares (profissional competente)",
                    "proximos_passos" to "Próximos passos")
                questions.forEach{(key,label)->Field(label,extended[key].orEmpty(),{extended[key]=it},lines=if(key in listOf("pontos_fortes","pontos_atencao","proximos_passos")) 2 else 1)}
            }
        }
        if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
        HorizontalDivider()
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick={discardRequested=true}) { Text("Cancelar") }
            Button(onClick={
                val values = listOf(weight,height,waist,hip,fat,goalWeight,goalFat)+per.values+folds.values+bio.values
                val tr=trainAdherence.toIntOrNull(); val di=dietAdherence.toIntOrNull()
                error=when {
                    name.trim().isBlank() -> "Informe o nome do aluno."
                    runCatching { LocalDate.parse(date) }.isFailure || (next.isNotBlank() && runCatching { LocalDate.parse(next) }.isFailure) || (goalDate.isNotBlank() && runCatching { LocalDate.parse(goalDate) }.isFailure) -> "Data inválida (AAAA-MM-DD)."
                    goalDate.isNotBlank() && runCatching { LocalDate.parse(goalDate).isBefore(LocalDate.parse(date)) }.getOrDefault(false) -> "O prazo da meta não pode ser anterior à avaliação atual."
                    age.isNotBlank() && age.toIntOrNull() !in 1..120 -> "Idade inválida."
                    energy.isNotBlank() && energy.toIntOrNull() !in 1..5 -> "Energia deve estar entre 1 e 5."
                    values.any { it.isNotBlank() && (n(it) == null || !n(it)!!.isFinite()) } -> "Verifique os valores numéricos."
                    listOf(weight,height,waist,hip).any { it.isNotBlank() && (n(it) ?: 0.0) <= 0 } -> "Peso e medidas preenchidas devem ser positivas."
                    per.values.any { it.isNotBlank() && (n(it) ?: 0.0) <= 0 } || folds.values.any { it.isNotBlank() && (n(it) ?: 0.0) <= 0 } -> "Perímetros e dobras devem ser maiores que zero."
                    fat.isNotBlank() && (n(fat) ?: -1.0) !in 0.0..100.0 -> "Percentual de gordura inválido."
                    goalWeight.isNotBlank() && !GoalTracking.validateTarget(n(weight),n(goalWeight),20.0..400.0) -> "Informe um peso atual e uma meta diferente, entre 20 e 400 kg."
                    goalFat.isNotBlank() && !GoalTracking.validateTarget(n(fat),n(goalFat),0.0..100.0) -> "Informe um percentual atual e uma meta diferente, entre 0 e 100%."
                    goalNotes.length > 500 -> "Observação da meta limitada a 500 caracteres."
                    weight.isNotBlank() && (n(weight) ?: -1.0) !in 20.0..400.0 -> "Peso fora da faixa permitida (20–400 kg)."
                    height.isNotBlank() && (n(height) ?: -1.0) !in 100.0..250.0 -> "Altura fora da faixa permitida (100–250 cm)."
                    bio["pgordura"]?.isNotBlank()==true && (n(bio["pgordura"].orEmpty()) ?: -1.0) !in 0.0..100.0 -> "Gordura BIA fora de 0–100 %."
                    goalDate.isNotBlank() && (runCatching { LocalDate.parse(goalDate) }.isFailure || runCatching { LocalDate.parse(goalDate) }.getOrNull()?.isBefore(LocalDate.parse(date)) == true) -> "Prazo da meta inválido ou anterior à avaliação."
                    trained && trainAdherence.isNotBlank() && tr !in 0..100 -> "Aderência do treino deve estar entre 0 e 100."
                    dieted && dietAdherence.isNotBlank() && di !in 0..100 -> "Aderência alimentar deve estar entre 0 e 100."
                    else -> ""
                }
                if(error.isEmpty()) runCatching { onSave(initial.copy(studentId=studentId,name=name.trim(),sex=sex,age=age.toIntOrNull(),date=date,
                    email=email.trim(),phone=phone.trim(),nextAssessment=next.trim(),weightKg=n(weight),heightCm=n(height),
                    waistCm=n(waist),hipCm=n(hip),bodyFatPercent=n(fat),trainingExperienced=trained,dietExperienced=dieted,
                    trainingAdherence=if(trained) tr else null,dietAdherence=if(dieted) di else null,energy=energy.toIntOrNull(),
                    goalWeightKg=n(goalWeight),
                    goalWeightBaselineKg=if(goalWeight.isBlank()) null else if(initial.goalWeightKg!=n(goalWeight)) n(weight) else initial.goalWeightBaselineKg ?: n(weight),
                    goalBodyFatPercent=n(goalFat),
                    goalBodyFatBaselinePercent=if(goalFat.isBlank()) null else if(initial.goalBodyFatPercent!=n(goalFat)) n(fat) else initial.goalBodyFatBaselinePercent ?: n(fat),
                    goalDate=goalDate.trim(),goalNotes=goalNotes.trim(),
                    notes=notes,phase=phase,week=week,professional=professional,registration=registration,clinic=clinic,
                    perimeters=parseMap(per),skinfolds=parseMap(folds),bioimpedance=parseMap(bio),photos=photos.toMap(),extendedAnswers=extended.toMap() + mapOf("meta_peso" to goalWeight, "meta_pgordura" to goalFat, "meta_prazo" to goalDate))) }.onFailure { error=it.message ?: "Não foi possível salvar. Tente novamente." }
            }) { Text("Salvar avaliação") }
        }
    }
}
