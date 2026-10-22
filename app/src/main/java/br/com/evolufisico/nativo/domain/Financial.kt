package br.com.evolufisico.nativo.domain

object Financial {
    val services: Map<String, Long> = linkedMapOf(
        "Avaliação física completa" to 15_000L,
        "Reavaliação" to 10_000L,
        "Bioimpedância" to 8_000L,
        "Treino / consultoria" to 25_000L,
        "Acompanhamento trimestral" to 35_000L,
        "Acompanhamento semestral" to 60_000L,
        "Diária" to 2_500L,
        "Personalizado" to 15_000L
    )
    fun net(base:Long,discount:Long):Long {
        require(base>=0 && discount>=0){"Valor e desconto não podem ser negativos."}
        return (base-discount).coerceAtLeast(0)
    }
    /** Courtesy is not a receivable nor a payment and must not inflate revenue. */
    fun billable(base:Long,discount:Long,exempt:Boolean):Long = if(exempt)0 else net(base,discount)
    /** Distributes cents exactly, largest remainder to the first installments. */
    fun installments(netCents:Long,n:Int):List<Long> {
        require(netCents>=0 && n in 1..24)
        return List(n){ netCents/n + if(it < netCents % n) 1 else 0 }
    }
    fun discountByPercent(amount:Long,percent:Double):Long {
        require(amount>=0 && percent.isFinite() && percent in 0.0..100.0)
        return java.math.BigDecimal.valueOf(amount).multiply(java.math.BigDecimal.valueOf(percent))
            .divide(java.math.BigDecimal.valueOf(100),0,java.math.RoundingMode.HALF_UP).longValueExact()
    }
}
