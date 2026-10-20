package br.com.evolufisico.android.reporting

import br.com.evolufisico.android.backup.BackupSnapshot
import br.com.evolufisico.android.ops.OperationalRules
import java.time.LocalDate

enum class ReportKind(val title: String, val filename: String) {
    OVERVIEW("Resumo geral", "resumo"),
    CLIENTS("Clientes", "clientes"),
    ASSESSMENTS("Avaliações", "avaliacoes"),
    FORMS("Formulários", "formularios"),
    APPOINTMENTS("Agenda", "agenda"),
    GOALS("Metas", "metas"),
    CHARGES("Cobranças", "cobrancas"),
    SERVICES("Serviços", "servicos")
}

data class ReportFilter(
    val clientId: String? = null,
    val from: LocalDate? = null,
    val to: LocalDate? = null
) {
    init { require(from == null || to == null || !from.isAfter(to)) }
    fun matches(id: String?, date: String? = null): Boolean {
        if (clientId != null && id != clientId) return false
        if (date == null || (from == null && to == null)) return true
        val day = runCatching { LocalDate.parse(date.take(10)) }.getOrNull() ?: return false
        return (from == null || !day.isBefore(from)) && (to == null || !day.isAfter(to))
    }
}

object CsvReports {
    /** CSV UTF-8 com BOM e separador ; para planilhas em português-BR. */
    fun generate(snapshot: BackupSnapshot, kind: ReportKind, f: ReportFilter = ReportFilter()): ByteArray {
        val clients = snapshot.clients.associateBy { it.id }
        require(f.clientId == null || f.clientId in clients) { "Cliente não encontrado." }
        fun name(id: String): String = clients[id]?.name ?: "(desconhecido)"
        val records: List<List<String>> = when (kind) {
            ReportKind.CLIENTS -> listOf(listOf("ID","Cliente")) +
                snapshot.clients.filter { f.matches(it.id) }.sortedBy { it.name.lowercase() }
                    .map { listOf(it.id,it.name) }
            ReportKind.ASSESSMENTS -> listOf(listOf("Cliente","Data","Nível","Objetivo","Peso kg",
                "Altura cm","Frequência","Limitações","Histórico","Hábitos","Experiência")) +
                snapshot.assessments.filter { f.matches(it.clientId,it.dateIso) }
                    .sortedBy{it.dateIso}.map {
                        listOf(name(it.clientId),it.dateIso,it.trainingLevel,it.goal,
                            it.weightKg.toString(),it.heightCm.toString(),
                            it.weeklyFrequency.toString(),it.limitations,
                            it.healthHistory,it.dailyHabits,it.trainingExperience)
                    }
            ReportKind.FORMS -> listOf(listOf("Cliente","Data","Modelo","Respostas")) +
                snapshot.forms.filter { f.matches(it.clientId,it.dateIso) }
                    .sortedBy{it.dateIso}.map {
                        listOf(name(it.clientId),it.dateIso,it.templateId,
                            it.answers.toSortedMap().entries.joinToString(" | ") { a ->
                                a.key + ": " + a.value
                            })
                    }
            ReportKind.APPOINTMENTS -> listOf(listOf("Cliente","Data e hora","Minutos",
                "Serviço","Situação","Observações")) +
                snapshot.operations.appointments.filter { f.matches(it.clientId,it.startIso) }
                    .sortedBy{it.startIso}.map {
                        listOf(name(it.clientId),it.startIso,it.durationMinutes.toString(),
                            it.service,it.status,it.notes)
                    }
            ReportKind.GOALS -> listOf(listOf("Cliente","Meta","Unidade","Inicial","Atual",
                "Alvo","Progresso %","Prazo","Concluída")) +
                snapshot.operations.goals.filter { f.matches(it.clientId,it.deadlineIso) }
                    .sortedBy{it.deadlineIso}.map {
                        listOf(name(it.clientId),it.title,it.unit,it.starting.toString(),
                            it.current.toString(),it.target.toString(),OperationalRules.progress(it).toString(),
                            it.deadlineIso,if(it.completed)"Sim" else "Não")
                    }
            ReportKind.CHARGES -> listOf(listOf("Cliente","Serviço","Valor (centavos)",
                "Vencimento","Recebimento","Situação")) +
                snapshot.charges.filter { f.matches(it.clientId,it.dueDateIso) }
                    .sortedBy{it.dueDateIso}.map {
                        listOf(name(it.clientId),it.description,it.amountCents.toString(),
                            it.dueDateIso,it.receivedDateIso.orEmpty(),
                            if(it.isReceived)"Recebido" else "Pendente")
                    }
            ReportKind.SERVICES -> listOf(listOf("Serviço","Valor (centavos)",
                "Duração (min)","Ativo")) +
                snapshot.operations.services.sortedBy{it.name.lowercase()}.map {
                    listOf(it.name,it.priceCents.toString(),it.durationMinutes.toString(),
                        if(it.enabled)"Sim" else "Não")
                }
            ReportKind.OVERVIEW -> {
                val appointments=snapshot.operations.appointments.filter { f.matches(it.clientId,it.startIso) }
                val goals=snapshot.operations.goals.filter { f.matches(it.clientId,it.deadlineIso) }
                val charges=snapshot.charges.filter { f.matches(it.clientId,it.dueDateIso) }
                listOf(listOf("Indicador","Valor"),
                    listOf("Clientes",snapshot.clients.count { f.matches(it.id) }.toString()),
                    listOf("Avaliações",snapshot.assessments.count { f.matches(it.clientId,it.dateIso) }.toString()),
                    listOf("Formulários",snapshot.forms.count { f.matches(it.clientId,it.dateIso) }.toString()),
                    listOf("Atendimentos",appointments.size.toString()),
                    listOf("Agendados",appointments.count { it.status=="Agendado" }.toString()),
                    listOf("Concluídos",appointments.count { it.status=="Concluído" }.toString()),
                    listOf("Metas",goals.size.toString()),
                    listOf("Metas concluídas",goals.count { it.completed }.toString()),
                    listOf("Cobranças",charges.size.toString()),
                    listOf("Recebido (centavos)",charges.filter { it.isReceived }.sumOf { it.amountCents }.toString()),
                    listOf("Pendente (centavos)",charges.filterNot { it.isReceived }.sumOf { it.amountCents }.toString()),
                    listOf("Serviços ativos",snapshot.operations.services.count { it.enabled }.toString()))
            }
        }
        require(records.size <= 100_000) { "Relatório excede limite de registros." }
        return ("\uFEFF" + records.joinToString("\r\n") { row ->
            row.joinToString(";") { escape(it) }
        } + "\r\n").toByteArray(Charsets.UTF_8)
    }

    /** Protege fórmulas em Excel e delimitadores CSV. */
    fun escape(value: String): String {
        val sanitized=value.replace("\u0000","").replace("\r\n","\n")
            .replace("\r","\n").trimStart('\uFEFF')
        val first=sanitized.trimStart().firstOrNull()
        val safe=if(first == '=' || first == '+' || first == '-' ||
            first == '@' || first == '\t') "'"+sanitized else sanitized
        return if(safe.contains(';') || safe.contains('"') || safe.contains('\n') ||
            safe.startsWith(' ') || safe.endsWith(' ')) {
            "\"" + safe.replace("\"","\"\"") + "\""
        } else safe
    }
}
