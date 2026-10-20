package br.com.evolufisico.android

/**
 * Exemplos de perguntas, não respostas predefinidas. Não inferem diagnósticos
 * nem devem ser preenchidos sem declaração do cliente.
 */
data class AnamnesisGuide(
    val introduction: String, val healthExample: String,
    val habitsExample: String, val experienceExample: String
)
object AnamnesisGuides {
    private val beginner = AnamnesisGuide(
        "Iniciante: priorize familiaridade com exercícios, rotina e segurança.",
        "Ex.: Informe condições de saúde conhecidas, limitações ou orientação médica recebida.",
        "Ex.: Quantas horas costuma dormir? Quanto tempo passa sentado por dia?",
        "Ex.: Já frequentou academia? Quais exercícios conhece ou evita?"
    )
    private val intermediate = AnamnesisGuide(
        "Intermediário: explore consistência, tolerância ao esforço e recuperação.",
        "Ex.: Houve lesões ou afastamento recente das atividades?",
        "Ex.: Como concilia frequência semanal, trabalho e recuperação?",
        "Ex.: Quais programas já seguiu? O que funcionou ou não?"
    )
    private val advanced = AnamnesisGuide(
        "Avançado: registre histórico de volume, intensidade e periodização.",
        "Ex.: Houve lesões de repetição, dores no treino ou restrições atuais?",
        "Ex.: Como estão sono, estresse e recuperação entre sessões intensas?",
        "Ex.: Que divisão, volume semanal e métodos utiliza atualmente?"
    )
    fun forLevel(level: String): AnamnesisGuide = when (level) {
        "Intermediário" -> intermediate
        "Avançado" -> advanced
        else -> beginner
    }
}
