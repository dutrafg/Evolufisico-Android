package br.com.evolufisico.nativo.domain

data class IntakeQuestion(val id:String,val text:String)
object IntakeQuestionnaire {
    val common = listOf(
    IntakeQuestion("objective", "Principal objetivo e expectativas"),
    IntakeQuestion("history", "Doenças diagnosticadas relatadas"),
    IntakeQuestion("medication", "Medicamentos e tratamento em andamento (relato)"),
    IntakeQuestion("pain", "Dor, lesões, cirurgias e restrições"),
    IntakeQuestion("medical_clearance", "Recebeu alguma recomendação ou restrição médica para atividades físicas?"),
    IntakeQuestion("symptoms", "Já apresentou dor no peito, desmaios ou falta de ar incomum ao esforço?"),
    IntakeQuestion("sleep", "Média de horas de sono, qualidade e horários"),
    IntakeQuestion("work", "Profissão, rotina e tempo sentado"),
    IntakeQuestion("nutrition", "Rotina alimentar e acompanhamento nutricional"),
    IntakeQuestion("hydration", "Hidratação habitual"),
    IntakeQuestion("stress", "Estresse, descanso e recuperação"),
    IntakeQuestion("time", "Dias e horários disponíveis para atividade"),
    IntakeQuestion("limitations", "Limitações de mobilidade e equilíbrio"))
/** Original PAR-Q inspired yes/no screening, collected as a self-report, not a clearance. */
    val parq = listOf(
    IntakeQuestion("parq_1", "Profissional de saúde já recomendou que faça atividade física apenas com orientação?"),
    IntakeQuestion("parq_2", "Sente dor no peito durante esforço físico?"),
    IntakeQuestion("parq_3", "Sentiu dor no peito em repouso no último mês?"),
    IntakeQuestion("parq_4", "Apresenta tontura que cause perda de equilíbrio ou episódios de desmaio?"),
    IntakeQuestion("parq_5", "Tem condição óssea/articular que possa piorar com exercício?"),
    IntakeQuestion("parq_6", "Usa medicamentos para pressão arterial ou condição cardíaca?"),
    IntakeQuestion("parq_7", "Conhece outro motivo de saúde para não iniciar exercícios sem orientação?")
)
    val starter = listOf(
    IntakeQuestion("before_exercise", "Já praticou exercícios anteriormente?"),
    IntakeQuestion("activity", "Como é seu nível atual de atividade?"),
    IntakeQuestion("barriers", "Quais dificuldades impedem começar?"),
    IntakeQuestion("preferences", "Atividades que gostaria de experimentar"),
    IntakeQuestion("confidence", "Grau de confiança em iniciar uma rotina (0–10)"),
    IntakeQuestion("support", "Apoio familiar e condições de acesso ao local"),
    IntakeQuestion("starting_goal", "Primeira meta pequena e viável"))
    val intermediate = listOf(
    IntakeQuestion("experience", "Há quanto tempo pratica exercícios?"),
    IntakeQuestion("frequency", "Frequência e duração dos treinos atuais"),
    IntakeQuestion("split", "Divisão atual e grupos musculares"),
    IntakeQuestion("intensity", "Intensidade percebida e progressão de cargas"),
    IntakeQuestion("plateau", "Dificuldades, platôs e faltas recorrentes"),
    IntakeQuestion("exercise_history", "Lesões ou exercícios que causam desconforto"),
    IntakeQuestion("recovery", "Recuperação entre as sessões"),
    IntakeQuestion("short_term", "Metas para as próximas 8–12 semanas"))
    val advanced = listOf(
    IntakeQuestion("modality", "Modalidade esportiva e tempo de prática"),
    IntakeQuestion("periodization", "Periodização, macrociclo e mesociclo atual"),
    IntakeQuestion("volume", "Volume semanal de treino por grupo muscular"),
    IntakeQuestion("load", "Carga, RPE/RIR e progressões recentes"),
    IntakeQuestion("test", "Testes de desempenho, referências e datas"),
    IntakeQuestion("competition", "Calendário competitivo e metas de performance"),
    IntakeQuestion("fatigue", "Monitoramento de fadiga, sono e dor muscular"),
    IntakeQuestion("injury", "Histórico de afastamento e reabilitação"),
    IntakeQuestion("resources", "Equipamentos, recursos e restrições de agenda"),
    IntakeQuestion("strategy", "Estratégia de recuperação e ajustes planejados"))

    val labels:Map<String,String> = (common+parq+starter+intermediate+advanced).associate{it.id to it.text}
}
