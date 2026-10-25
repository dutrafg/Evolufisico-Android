package br.com.evolufisico.nativo.domain
import org.junit.Test
import org.junit.Assert.*
import br.com.evolufisico.nativo.data.Charge
import java.time.YearMonth
class MonthlyLedgerTest {
 private val oct=YearMonth.of(2026,10)
 @Test fun paymentInAnotherMonthDoesNotFollowDueDate(){val c=Charge(amountCents=10000,dueDate="2026-09-20",paid=true,paidDate="2026-10-01");assertEquals(10000L,MonthlyLedger.summary(listOf(c),oct).receivedCents);assertEquals(0L,MonthlyLedger.summary(listOf(c),oct.minusMonths(1)).receivedCents)}
 @Test fun pendingUsesDueDateAndDiscount(){val c=Charge(amountCents=10000,discountCents=2500,dueDate="2026-10-01");assertEquals(7500L,MonthlyLedger.summary(listOf(c),oct).pendingCents)}
 @Test fun newMonthStartsEmptyWithoutErasingHistory(){val c=Charge(amountCents=8000,paid=true,paidDate="2026-09-30");assertEquals(0L,MonthlyLedger.summary(listOf(c),oct).receivedCents);assertEquals(8000L,MonthlyLedger.summary(listOf(c),oct.minusMonths(1)).receivedCents)}
 @Test fun courtesiesExcludedFromCashAndPending(){val rows=listOf(Charge(exempt=true,paid=true,paidDate="2026-10-01",amountCents=9000),Charge(exempt=true,dueDate="2026-10-01",amountCents=7000));val r=MonthlyLedger.summary(rows,oct);assertTrue(r.paid.isEmpty());assertTrue(r.pending.isEmpty())}
 @Test fun invalidDateIsNotConvertedToCurrentMonth(){assertTrue(MonthlyLedger.summary(listOf(Charge(dueDate="2026-02-30")),oct).pending.isEmpty())}
 @Test fun oldPaidRecordFallsBackOnlyWhenDateIsAbsent(){assertEquals(100L,MonthlyLedger.summary(listOf(Charge(amountCents=100,paid=true,dueDate="2026-10-05")),oct).receivedCents);assertEquals(0L,MonthlyLedger.summary(listOf(Charge(amountCents=100,paid=true,dueDate="2026-10-05",paidDate="invalid")),oct).receivedCents)}
 @Test fun monthHistoryKeepsCurrentAndSortsDescending(){val rows=listOf(Charge(dueDate="2026-08-01"),Charge(dueDate="2026-08-02"),Charge(paid=true,paidDate="2026-11-01"));assertEquals(listOf("2026-11","2026-10","2026-08"),MonthlyLedger.periods(rows,oct).map{it.toString()})}
}
