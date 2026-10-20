package br.com.evolufisico.android.trends

import br.com.evolufisico.android.backup.BackupSnapshot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

data class MonthlyTrend(
    val month:YearMonth,
    val billedCents:Long,
    val receivedCents:Long,
    val pendingCents:Long,
    val scheduled:Int,
    val completed:Int,
    val cancelled:Int,
    val missed:Int=0
) {
    val attendancePercent:Int? get() {
        val attended=completed+missed
        return if(attended==0) null else completed*100/attended
    }
}

/** Recebimentos por data efetiva; valores faturados por vencimento. */
object MonthlyTrends {
    fun collect(
        snapshot:BackupSnapshot,
        reference:YearMonth=YearMonth.now(),
        months:Int=6,
        clientId:String?=null
    ):List<MonthlyTrend> {
        require(months in 1..24)
        require(clientId==null||snapshot.clients.any{it.id==clientId})
        return (months-1 downTo 0).map { offset ->
            val month=reference.minusMonths(offset.toLong())
            val charges=snapshot.charges.filter { clientId==null||it.clientId==clientId }
            val billed=charges.filter {
                runCatching{YearMonth.from(LocalDate.parse(it.dueDateIso))}.getOrNull()==month
            }
            val received=charges.filter {
                it.receivedDateIso!=null &&
                    runCatching{YearMonth.from(LocalDate.parse(it.receivedDateIso!!))}.getOrNull()==month
            }
            val appointments=snapshot.operations.appointments.filter {
                (clientId==null||it.clientId==clientId) &&
                    runCatching { YearMonth.from(LocalDateTime.parse(it.startIso)) }.getOrNull()==month
            }
            MonthlyTrend(
                month,billed.sumOf{it.amountCents},received.sumOf{it.amountCents},
                billed.filterNot{it.isReceived}.sumOf{it.amountCents},
                appointments.count{it.status=="Agendado"},
                appointments.count{it.status=="Concluído"},
                appointments.count{it.status=="Cancelado"},
                appointments.count{it.status=="Faltou"}
            )
        }
    }
}
