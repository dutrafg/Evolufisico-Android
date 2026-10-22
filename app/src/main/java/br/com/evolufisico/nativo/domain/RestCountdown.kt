package br.com.evolufisico.nativo.domain

/** Millisecond state machine driven by an injected monotonic clock; no Android dependencies. */
data class RestCountdown(val remainingMillis:Long=60_000L,val deadlineElapsed:Long?=null) {
    init { require(remainingMillis in 0..MAX_MILLIS && (deadlineElapsed==null || deadlineElapsed>=0)) }
    val running:Boolean get()=deadlineElapsed!=null
    fun remainingAt(now:Long):Long = deadlineElapsed?.let{(it-now).coerceIn(0,MAX_MILLIS)} ?: remainingMillis
    fun start(now:Long):RestCountdown {
        require(now>=0 && now<=Long.MAX_VALUE-MAX_MILLIS)
        return if(running || remainingMillis==0L)this else copy(deadlineElapsed=now+remainingMillis)
    }
    fun pause(now:Long)=RestCountdown(remainingAt(now))
    fun tick(now:Long):RestCountdown {
        val value=remainingAt(now)
        return if(value==0L)RestCountdown(0) else copy(remainingMillis=value)
    }
    fun addSeconds(seconds:Int,now:Long):RestCountdown {
        require(seconds in 1..3600 && now>=0 && now<=Long.MAX_VALUE-MAX_MILLIS)
        val remaining=(remainingAt(now)+seconds*1000L).coerceAtMost(MAX_MILLIS)
        return RestCountdown(remaining,if(running && remainingAt(now)>0)now+remaining else null)
    }
    companion object {
        const val MAX_MILLIS=3_600_000L
        fun seconds(value:Int):RestCountdown {
            require(value in 1..3600) { "Informe de 1 a 3600 segundos." }
            return RestCountdown(value*1000L)
        }
    }
}
