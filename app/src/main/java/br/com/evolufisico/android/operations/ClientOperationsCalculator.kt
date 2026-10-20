package br.com.evolufisico.android.operations

import br.com.evolufisico.android.backup.BackupSnapshot
import java.time.LocalDate

/** Totais calculados de fontes existentes, sem duplicar bancos. */
data class ClientOperations(
    val clientId:String,
    val name:String,
    val scheduled:Int,
    val completed:Int,
    val missed:Int,
    val cancelled:Int,
    val activeGoals:Int,
    val completedGoals:Int,
    val pendingCents:Long,
    val overdueCents:Long,
    val receivedCents:Long
) {
    val attendancePercent:Int?
        get()=if(completed+missed==0) null else completed*100/(completed+missed)
}

/**
 * Resume todos os registros no momento da leitura, evitando cache desatualizado.
 * O valor vencido considera apenas cobranças não pagas.
 */
object ClientOperationsCalculator {
    fun calculate(
        data:BackupSnapshot,
        today:LocalDate=LocalDate.now()
    ):List<ClientOperations> {
        val operations=data.operations
        return data.clients.map { client ->
            val appointments=operations.appointments.filter { it.clientId==client.id }
            val goals=operations.goals.filter { it.clientId==client.id }
            val charges=data.charges.filter { it.clientId==client.id }
            val pending=charges.filterNot { it.isReceived }
            ClientOperations(client.id,client.name,
                appointments.count { it.status=="Agendado" },
                appointments.count { it.status=="Concluído" },
                appointments.count { it.status=="Faltou" },
                appointments.count { it.status=="Cancelado" },
                goals.count { !it.completed },
                goals.count { it.completed },
                pending.sumOf { it.amountCents },
                pending.filter {
                    runCatching { LocalDate.parse(it.dueDateIso).isBefore(today) }.getOrDefault(false)
                }.sumOf { it.amountCents },
                charges.filter { it.isReceived }.sumOf { it.amountCents }
            )
        }.sortedWith(compareByDescending<ClientOperations> { it.overdueCents }
            .thenBy { it.name.lowercase() })
    }
}
