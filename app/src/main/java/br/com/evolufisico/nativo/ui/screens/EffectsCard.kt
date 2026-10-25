package br.com.evolufisico.nativo.ui.screens

import android.animation.ValueAnimator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import br.com.evolufisico.nativo.domain.VisualEffects

internal val LocalAdvancedEffects=staticCompositionLocalOf {true}
internal val LocalLiquidEffects=staticCompositionLocalOf {false}

/** Non-consuming touch observation preserves nested buttons, scrolling and selection. */
@Composable internal fun EffectsCard(
    modifier:Modifier=Modifier,
    shape:Shape=CardDefaults.outlinedShape,
    colors:CardColors=CardDefaults.outlinedCardColors(),
    onClick:(()->Unit)?=null,
    content:@Composable ColumnScope.()->Unit
) {
    val advanced=LocalAdvancedEffects.current
    val liquid=LocalLiquidEffects.current
    val interaction=remember{MutableInteractionSource()}
    val hovered by interaction.collectIsHoveredAsState()
    var touched by remember{mutableStateOf(false)}
    val motion=advanced && ValueAnimator.areAnimatorsEnabled()
    val fraction by animateFloatAsState(if(motion&&(hovered||touched))1f else 0f,tween(170),label="Profundidade do cartão")
    val density=LocalDensity.current.density
    val decorated=modifier.hoverable(interaction).pointerInput(advanced){
        if(advanced)awaitEachGesture {
            awaitFirstDown(requireUnconsumed=false,pass=PointerEventPass.Initial)
            touched=true
            try { do {val event=awaitPointerEvent(PointerEventPass.Initial)}while(event.changes.any{it.pressed}) }
            finally {touched=false}
        }
    }.graphicsLayer {
        rotationX=if(motion)VisualEffects.rotationDegrees*fraction else if(liquid)0.35f else 0f
        translationY=if(motion)-VisualEffects.liftDp*density*fraction else if(liquid)-2f*density else 0f
        cameraDistance=18f*density
        shadowElevation=if(advanced)(VisualEffects.restingDepthDp+(VisualEffects.activeDepthDp-VisualEffects.restingDepthDp)*fraction)*density else if(liquid)8f*density else 0f
        this.shape=shape
        clip=false
    }
    val border=BorderStroke(1.dp,if(advanced)MaterialTheme.colorScheme.primary.copy(alpha=.24f) else MaterialTheme.colorScheme.outlineVariant)
    @Composable fun Interior() {
        Box {
            if(liquid)Box(Modifier.matchParentSize().background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha=.92f),MaterialTheme.colorScheme.surface.copy(alpha=.97f),MaterialTheme.colorScheme.surfaceContainer.copy(alpha=.95f)),start=androidx.compose.ui.geometry.Offset.Zero,end=androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY))))
            if(advanced)Box(Modifier.matchParentSize().blur(VisualEffects.glowRadiusDp.dp)
                .background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha=.055f*VisualEffects.multiplier),MaterialTheme.colorScheme.surface.copy(alpha=0f)))))
            Column(content=content)
        }
    }
    if(onClick==null)OutlinedCard(decorated,shape=shape,colors=colors,border=border){Interior()}
    else OutlinedCard(onClick=onClick,modifier=decorated,shape=shape,colors=colors,border=border){Interior()}
}
