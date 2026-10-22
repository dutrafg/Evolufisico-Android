package br.com.evolufisico.nativo.domain

import org.junit.Assert.*
import org.junit.Test

class FinancialTest {
    @Test fun dailyServicePriceIsTwentyFiveReais() = assertEquals(2500L,Financial.services["Diária"])
    @Test fun netCannotBecomeNegative() = assertEquals(0L,Financial.net(500,700))
    @Test fun courtesyNeverCountsAsReceivable() = assertEquals(0L,Financial.billable(10000,0,true))
    @Test fun centRemaindersGoToTheFirstInstallments() = assertEquals(listOf(334L,334L,333L),Financial.installments(1001,3))
    @Test fun installmentsSumExactlyToNetValue() {
        for(amount in listOf(0L,1L,99L,10001L,Long.MAX_VALUE)) for(count in listOf(1,2,3,7,24))
            assertEquals(amount,Financial.installments(amount,count).sum())
    }
    @Test fun percentageDiscountRoundsHalfUp() {
        assertEquals(101L,Financial.discountByPercent(201,50.0))
        assertEquals(1L,Financial.discountByPercent(1,50.0))
    }
    @Test fun invalidDiscountPercentIsRejected() {
        assertThrows(IllegalArgumentException::class.java){Financial.discountByPercent(100,-1.0)}
        assertThrows(IllegalArgumentException::class.java){Financial.discountByPercent(100,Double.NaN)}
    }
    @Test fun invalidInstallmentCountsAreRejected() {
        assertThrows(IllegalArgumentException::class.java){Financial.installments(10,0)}
        assertThrows(IllegalArgumentException::class.java){Financial.installments(-1,1)}
    }
}
