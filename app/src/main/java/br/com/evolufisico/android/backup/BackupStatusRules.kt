package br.com.evolufisico.android.backup

/** Sem diagnósticos clínicos ou dados do cliente; somente metadados de execução. */
object BackupStatusRules {
    private const val HOUR = 3_600_000L
    /** WorkManager: no maximo duas novas tentativas antes da proxima janela diaria. */
    fun retryAfterFailure(runAttemptCount:Int):Boolean=runAttemptCount in 0..1
    fun isLate(
        active: Boolean,
        nowMillis: Long,
        enabledAtMillis: Long,
        lastSuccessMillis: Long
    ): Boolean {
        if (!active || nowMillis < 0) return false
        val since = if (lastSuccessMillis > 0) lastSuccessMillis else enabledAtMillis
        return since > 0 && nowMillis > since && nowMillis - since > 36 * HOUR
    }
}
