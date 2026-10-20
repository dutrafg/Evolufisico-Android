package br.com.evolufisico.android.forms

import java.time.LocalDate
import java.util.UUID

enum class AnswerKind { TEXT, YES_NO }
data class FormQuestion(val id: String, val title: String, val example: String,
    val required: Boolean = true, val kind: AnswerKind = AnswerKind.TEXT)
data class FormTemplate(val id: String, val title: String, val subtitle: String,
    val questions: List<FormQuestion>)
data class FormResponse(
    val id: String, val clientId: String, val templateId: String,
    val dateIso: String, val answers: Map<String,String>
) {
    companion object {
        fun new(clientId: String, templateId: String, dateIso: String,
            answers: Map<String,String>) = FormResponse(UUID.randomUUID().toString(),
            clientId, templateId, dateIso, answers.mapValues { it.value.trim() })
    }
}

/** Modelos estáveis. IDs são preservados nos registros e no backup. */
object FormCatalog {
    private fun q(id: String, title: String, example: String,
        required: Boolean = true, kind: AnswerKind = AnswerKind.TEXT) =
        FormQuestion(id, title, example, required, kind)

    val templates: List<FormTemplate> = listOf(
        FormTemplate("anamnese_iniciante", "Anamnese · Iniciante",
            "Primeiro contato, hábitos e limitações", listOf(
                q("motivo", "Qual é seu objetivo principal?", "Ex.: melhorar saúde ou iniciar musculação."),
                q("atividade", "Pratica atividade física atualmente?", "Ex.: caminhada duas vezes na semana."),
                q("saude", "Tem algum cuidado de saúde ou orientação profissional relevante?",
                    "Registrar apenas o que o cliente declarar."),
                q("sono", "Como é seu sono e rotina?", "Ex.: horário de sono e trabalho."),
                q("experiencia", "Quais exercícios conhece ou evita?", "Ex.: nunca treinou com pesos.", false)
            )),
        FormTemplate("anamnese_intermediario", "Anamnese · Intermediário",
            "Regularidade, progressão e recuperação", listOf(
                q("objetivo", "Qual é seu objetivo nesta etapa?", "Ex.: aumentar força e regularidade."),
                q("treinos", "Qual foi sua frequência recente?", "Ex.: três ou quatro sessões semanais."),
                q("progresso", "O que mudou desde o último programa?", "Ex.: conseguiu manter a rotina.", false),
                q("limitacoes", "Existe dor, limitação ou recomendação de saúde?", "Registro autodeclarado."),
                q("recuperacao", "Como é a recuperação entre sessões?", "Ex.: sono e descanso.")
            )),
        FormTemplate("anamnese_avancado", "Anamnese · Avançado",
            "Carga, volume, periodização e recuperação", listOf(
                q("metas", "Quais são suas metas e prazos?", "Ex.: força em levantamento específico."),
                q("volume", "Como distribui volume e intensidade?", "Ex.: divisão semanal e séries."),
                q("periodizacao", "Qual metodologia ou periodização já utilizou?",
                    "Ex.: ciclos de acúmulo e intensidade."),
                q("recuperacao", "Como acompanha fadiga e recuperação?", "Ex.: sono e esforço percebido."),
                q("saude", "Há restrições, dores ou histórico relevante?", "Evite inferir diagnóstico.")
            )),
        FormTemplate("prontidao", "Prontidão para atividade física",
            "Triagem informativa; não substitui avaliação médica", listOf(
                q("recomendacao", "Possui restrição médica conhecida para exercício?",
                    "Sim ou Não", kind = AnswerKind.YES_NO),
                q("sintomas", "Sentiu sintomas incomuns durante esforço recentemente?",
                    "Sim ou Não", kind = AnswerKind.YES_NO),
                q("medicamentos", "Usa medicamentos que devem ser considerados no treino?",
                    "Se desejar, descreva orientação relevante.", false),
                q("orientacao", "Há alguma recomendação profissional a seguir?",
                    "Transcreva orientação declarada, se houver.", false)
            )),
        FormTemplate("habitos", "Hábitos e rotina",
            "Sono, alimentação e disponibilidade", listOf(
                q("sono", "Quantas horas costuma dormir?", "Ex.: sete horas em dias úteis."),
                q("trabalho", "Como é sua rotina de trabalho e deslocamento?", "Ex.: trabalho sentado."),
                q("disponibilidade", "Quais dias e horários são viáveis para treinar?",
                    "Ex.: seg/qua/sex às 18h."),
                q("alimentacao", "Há algum cuidado alimentar relevante?", "Não substituir orientação nutricional.", false),
                q("estresse", "Como avalia estresse e recuperação?", "Relato voluntário.", false)
            ))
    )
    fun find(id: String): FormTemplate? = templates.firstOrNull { it.id == id }
}

object FormRules {
    fun validate(item: FormResponse, clients: Set<String>, today: LocalDate = LocalDate.now()): String? {
        if (item.id.isBlank() || item.clientId !in clients) return "Cliente inválido."
        val template = FormCatalog.find(item.templateId) ?: return "Modelo de formulário desconhecido."
        val day = runCatching { LocalDate.parse(item.dateIso) }.getOrNull()
            ?: return "Data inválida. Use AAAA-MM-DD."
        if (day.isAfter(today) || day.isBefore(today.minusYears(120))) return "Data fora do período permitido."
        if (item.answers.size > 30) return "Quantidade de respostas inválida."
        if (item.answers.keys.any { key -> template.questions.none { it.id == key } })
            return "Este formulário contém respostas desconhecidas."
        for (q in template.questions) {
            val answer = item.answers[q.id]?.trim().orEmpty()
            if (q.required && answer.isEmpty()) return "Responda: " + q.title
            if (answer.length > 700) return "Resposta muito longa: " + q.title
            if (q.kind == AnswerKind.YES_NO && answer.isNotEmpty() &&
                answer !in setOf("Sim", "Não")) return "Resposta de seleção inválida."
        }
        return null
    }
}
