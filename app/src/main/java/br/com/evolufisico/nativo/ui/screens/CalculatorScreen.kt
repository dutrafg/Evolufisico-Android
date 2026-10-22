package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.domain.AdvancedCalculations
import br.com.evolufisico.nativo.domain.Anthropometry
import br.com.evolufisico.nativo.domain.Metrics

@Composable
fun CalculatorScreen() = Screen {
    Heading("Calculadora", "Antropometria, composição corporal, equações metabólicas e frequência cardíaca em Kotlin.")
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hip by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Não informado") }
    val foldValues = remember { mutableStateMapOf<String, String>() }
    fun positive(s: String): Double? = s.trim().replace(',', '.').toDoubleOrNull()
        ?.takeIf { it.isFinite() && it > 0.0 }
    fun percent(s: String): Double? = s.trim().replace(',', '.').toDoubleOrNull()
        ?.takeIf { it.isFinite() && it in 0.0..100.0 }
    Field("Peso (kg)", weight, { weight = it })
    Field("Altura (cm)", height, { height = it })
    Field("Idade (anos)", age, { age = it })
    PillChoices(listOf("Não informado", "Feminino", "Masculino"), sex, { sex = it })
    Field("Cintura (cm)", waist, { waist = it })
    Field("Quadril (cm)", hip, { hip = it })
    Field("Gordura aferida por equipamento (%)", fat, { fat = it })
    val w = positive(weight); val h = positive(height); val f = percent(fat)
    val years = age.toIntOrNull()
    DataCard("IMC", Metrics.oneDecimal(Metrics.bmi(w, h)))
    DataCard("RCQ", Metrics.oneDecimal(Metrics.waistHipRatio(positive(waist), positive(hip))))
    DataCard("Relação cintura/estatura", Metrics.oneDecimal(AdvancedCalculations.waistHeightRatio(positive(waist), h)))
    DataCard("BRI · índice de arredondamento corporal", Metrics.oneDecimal(AdvancedCalculations.bri(positive(waist), h)))
    DataCard("Massa gorda (equipamento)", "${Metrics.oneDecimal(Metrics.fatMassKg(w, f))} kg")
    DataCard("Massa livre de gordura (equipamento)", "${Metrics.oneDecimal(w?.let { mass -> Metrics.fatMassKg(mass,f)?.let { mass - it } })} kg")
    HorizontalDivider()
    Heading("Dobras cutâneas", "Jackson & Pollock (7 locais) e Siri; fórmula estimativa para adultos de 18–80 anos.")
    Anthropometry.sites.forEach { (key,label) -> Field("$label (mm)", foldValues[key].orEmpty(), { foldValues[key] = it }) }
    val folds = foldValues.mapNotNull { (k,v) -> positive(v)?.let { k to it } }.toMap()
    val pollock = Anthropometry.pollock7(sex, years, w, folds)
    DataCard("Soma das dobras", "${Metrics.oneDecimal(pollock?.totalMm)} mm")
    DataCard("Densidade corporal", pollock?.density?.let { "%.4f".format(java.util.Locale.forLanguageTag("pt-BR"),it) } ?: "—")
    DataCard("Gordura estimada", "${Metrics.oneDecimal(pollock?.fatPercent)} %")
    DataCard("Massa magra estimada", "${Metrics.oneDecimal(pollock?.leanMassKg)} kg")
    if (pollock?.clamped == true) Text("Estimativa limitada a 3–60%: confira a coleta.",color=MaterialTheme.colorScheme.error)
    HorizontalDivider()
    Heading("Metabolismo estimado", "Equações alternativas não são diretamente intercambiáveis; a calculadora não prescreve dieta.")
    val formulas = listOf("Mifflin–St Jeor", "Harris-Benedict revisada", "Harris-Benedict clássica", "Katch-McArdle")
    var formula by remember { mutableStateOf(formulas.first()) }
    PillChoices(formulas,formula,{formula=it})
    val basal = when (formula) {
        formulas[0] -> Anthropometry.mifflinStJeor(sex,years,w,h)
        formulas[1] -> AdvancedCalculations.harrisBenedictRevised(sex,years,w,h)
        formulas[2] -> AdvancedCalculations.harrisBenedictClassic(sex,years,w,h)
        else -> AdvancedCalculations.katchMcArdle(w,f ?: pollock?.fatPercent)
    }
    DataCard("Taxa metabólica basal · $formula", "${Metrics.oneDecimal(basal)} kcal/dia")
    val levels = linkedMapOf("Sedentário (1,20)" to 1.2, "Leve (1,375)" to 1.375,
        "Moderado (1,55)" to 1.55, "Intenso (1,725)" to 1.725, "Muito intenso (1,90)" to 1.9)
    var level by remember { mutableStateOf(levels.keys.first()) }
    PillChoices(levels.keys.toList(), level, { level = it })
    val expenditure = AdvancedCalculations.dailyExpenditure(basal,levels[level])
    DataCard("Gasto energético diário estimado", "${Metrics.oneDecimal(expenditure)} kcal/dia")
    var strategy by remember { mutableStateOf("Manutenção") }
    PillChoices(listOf("Manutenção", "Déficit 20%", "Superávit 10%"),strategy,{strategy=it})
    val multiplier = when(strategy) { "Déficit 20%" -> .8; "Superávit 10%" -> 1.1; else -> 1.0 }
    DataCard("Exemplo matemático · $strategy", "${Metrics.oneDecimal(expenditure?.times(multiplier))} kcal/dia")
    Text("O ajuste mostrado é uma simulação, não uma indicação personalizada de ingestão energética.",style=MaterialTheme.typography.bodySmall)
    var desiredFat by remember { mutableStateOf("") }
    Field("Percentual de gordura alvo para simulação (%)",desiredFat,{desiredFat=it})
    DataCard("Peso teórico mantendo a massa magra", "${Metrics.oneDecimal(AdvancedCalculations.weightForTargetFat(w,f ?: pollock?.fatPercent,percent(desiredFat)))} kg")
    HorizontalDivider()
    Heading("Reserva cardíaca", "Exemplo de Karvonen. Informe a FC máxima medida ou definida por profissional e a FC de repouso.")
    var maximum by remember { mutableStateOf("") }
    var resting by remember { mutableStateOf("") }
    Field("Frequência cardíaca máxima (bpm)",maximum,{maximum=it})
    Field("Frequência cardíaca em repouso (bpm)",resting,{resting=it})
    for (intensity in listOf(50,60,70,80)) {
        DataCard("Exemplo a $intensity% da reserva", "${Metrics.oneDecimal(AdvancedCalculations.karvonen(maximum.toIntOrNull(),resting.toIntOrNull(),intensity))} bpm")
    }
    Text("Equações e índices são informativos e exigem interpretação técnica. Resultados não são diagnóstico nem prescrição clínica.",style=MaterialTheme.typography.bodySmall)
}
