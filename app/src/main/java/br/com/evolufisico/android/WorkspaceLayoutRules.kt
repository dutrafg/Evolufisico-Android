package br.com.evolufisico.android

/** Breakpoints calculados pela largura de conteúdo real após a barra lateral. */
object WorkspaceLayoutRules {
    fun contentWidthDp(screenWidthDp:Int):Int {
        val safe=screenWidthDp.coerceAtLeast(0)
        return (safe-if(safe>=840) 80 else 0).coerceAtLeast(0)
    }
    /** Espaço real de conteúdo depois do rail e das margens. */
    fun usableWidthDp(screenWidthDp:Int):Int =
        (contentWidthDp(screenWidthDp) -
            2 * VisualLayout.edgePaddingDp(screenWidthDp)).coerceAtLeast(0)

    /** Largura mínima escalável para títulos com fonte ampliada. */
    fun minimumTileDp(fontScale:Float):Int {
        val safe=fontScale.takeIf { it.isFinite() }?.coerceIn(1f,2f) ?: 1f
        return (148f * (1f + (safe-1f)*0.75f)).toInt()
    }

    private fun fittingColumns(screenWidthDp:Int,fontScale:Float):Int {
        val available=usableWidthDp(screenWidthDp)
        val minimum=minimumTileDp(fontScale)
        // 10dp entre colunas; uma coluna sempre disponível em telas pequenas.
        return ((available+10)/(minimum+10)).coerceAtLeast(1)
    }

    fun gridColumns(screenWidthDp:Int,fontScale:Float=1f):Int {
        val breakpoint=when(val width=contentWidthDp(screenWidthDp)) {
            in 0..359 -> 1
            in 360..699 -> 2
            in 700..999 -> 3
            else -> 4
        }
        return minOf(breakpoint,fittingColumns(screenWidthDp,fontScale))
    }

    fun metricColumns(screenWidthDp:Int,fontScale:Float=1f):Int {
        val breakpoint=when {
            contentWidthDp(screenWidthDp)<360 -> 1
            contentWidthDp(screenWidthDp)>=1000 -> 4
            else -> 2
        }
        val fitting=fittingColumns(screenWidthDp,fontScale)
        return when {
            breakpoint==4 && fitting>=4 -> 4
            breakpoint>=2 && fitting>=2 -> 2
            else -> 1
        }
    }
}

/** Padding balanceado para a densidade de smartphone/tablet. */
object VisualLayout {
    fun edgePaddingDp(screenWidthDp:Int):Int = when {
        screenWidthDp<360 -> 16
        screenWidthDp>=840 -> 24
        else -> 20
    }
    fun maxContentWidthDp():Int = VisualTokens.contentMaxWidthDp
    fun moduleColumns(screenWidthDp:Int):Int=WorkspaceLayoutRules.gridColumns(screenWidthDp)
    fun isTablet(screenWidthDp:Int):Boolean=screenWidthDp>=840
}
