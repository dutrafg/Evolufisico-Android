package br.com.evolufisico.android

/** Breakpoints calculados pela largura de conteúdo real após a barra lateral. */
object WorkspaceLayoutRules {
    fun contentWidthDp(screenWidthDp:Int):Int {
        val safe=screenWidthDp.coerceAtLeast(0)
        return (safe-if(safe>=840) 80 else 0).coerceAtLeast(0)
    }
    fun gridColumns(screenWidthDp:Int):Int = when(val width=contentWidthDp(screenWidthDp)) {
        in 0..359 -> 1
        in 360..699 -> 2
        in 700..999 -> 3
        else -> 4
    }
    fun metricColumns(screenWidthDp:Int):Int =
        if(contentWidthDp(screenWidthDp)>=1000) 4 else 2
}
