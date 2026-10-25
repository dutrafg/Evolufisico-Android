package br.com.evolufisico.nativo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.com.evolufisico.nativo.domain.HtmlParity
import br.com.evolufisico.nativo.domain.DashboardPacking
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal class DashboardViewport(val scroll:ScrollState) { var bounds=Rect.Zero }
internal val LocalDashboardViewport=staticCompositionLocalOf<DashboardViewport?>{null}

/** Reorder by dragging an explicit handle. Masonry packs variable-height cards without row holes. */
@Composable internal fun ReferenceDashboardGrid(group:String,ids:List<String>,editing:Boolean,resetEpoch:Int,body:@Composable (String)->Unit) {
    val context=LocalContext.current
    val prefs=remember(group){context.getSharedPreferences("html-home-layout-$group",android.content.Context.MODE_PRIVATE)}
    var savedOrder by remember(group,resetEpoch){mutableStateOf(prefs.getString("order",null))}
    val order=HtmlParity.recordOrder(ids,savedOrder)
    val currentOrder by rememberUpdatedState(order)
    var originBounds by remember{mutableStateOf(Rect.Zero)}
    val sizes=if(group=="homeStats")listOf("small","medium","large") else listOf("small","medium","large","full")
    val fallback=if(group=="homeStats")"small" else "medium"
    val selectedSizes=remember(group,resetEpoch){mutableStateMapOf<String,String>().apply{ids.forEach{id->put(id,prefs.getString("size-$id",fallback)?.takeIf{it in sizes} ?: fallback)}}}
    val positions=remember{mutableMapOf<String,Rect>()}
    var dragging by remember(group,resetEpoch){mutableStateOf<String?>(null)}
    var target by remember{mutableStateOf<String?>(null)}
    var dragOffset by remember{mutableStateOf(Offset.Zero)}
    var finger by remember{mutableStateOf(Offset.Zero)}
    val viewport=LocalDashboardViewport.current
    val density=LocalDensity.current
    fun findTarget():String? {
        val others=positions.filterKeys{it!=dragging}
        return others.entries.firstOrNull{it.value.contains(finger)}?.key
            ?: if(originBounds.contains(finger))null else others.minByOrNull{(_,r)->(r.center-finger).getDistanceSquared()}?.key
    }
    fun clearDrag(){dragging=null;target=null;dragOffset=Offset.Zero}
    fun store(value:List<String>){savedOrder=value.joinToString(",");prefs.edit().putString("order",savedOrder).apply()}
    LaunchedEffect(editing,resetEpoch){if(!editing)clearDrag()}
    LaunchedEffect(dragging) {
        if(dragging!=null && viewport!=null)while(isActive) {
            val edge=with(density){56.dp.toPx()}
            val bounds=viewport.bounds
            val speed=when {
                finger.y<bounds.top+edge -> -((bounds.top+edge-finger.y)/edge).coerceIn(0f,1f)*18f*density.density
                finger.y>bounds.bottom-edge -> ((finger.y-bounds.bottom+edge)/edge).coerceIn(0f,1f)*18f*density.density
                else->0f
            }
            if(speed!=0f){val consumed=viewport.scroll.scrollBy(speed);dragOffset+=Offset(0f,consumed);target=findTarget()}
            delay(16)
        }
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val spans=order.map{DashboardPacking.span(group,selectedSizes[it] ?: fallback,maxWidth.value,density.fontScale)}
        val gap=with(density){12.dp.roundToPx()}
        Layout(content={
            order.forEachIndexed{index,id->key(id){
                var handleBounds by remember{mutableStateOf(Rect.Zero)}
                DisposableEffect(id){onDispose{positions.remove(id)}}
                val active=dragging==id
                val destination=target==id && dragging!=null
                val outline=if(destination)Modifier.border(BorderStroke(2.dp,MaterialTheme.colorScheme.primary),MaterialTheme.shapes.medium) else Modifier
                Column(Modifier.zIndex(if(active)2f else 0f).graphicsLayer {
                    translationX=if(active)dragOffset.x else 0f
                    translationY=if(active)dragOffset.y else 0f
                    alpha=if(active).88f else 1f
                }.onGloballyPositioned{positions[id]=it.boundsInWindow()}.then(outline)) {
                    if(editing) {
                        Surface(color=if(active)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,shape=MaterialTheme.shapes.small,
                            modifier=Modifier.fillMaxWidth().heightIn(min=48.dp).onGloballyPositioned{handleBounds=it.boundsInWindow()}
                                .semantics{contentDescription="Arrastar cartão ${index+1}"}
                                .pointerInput(id,editing,resetEpoch){detectDragGestures(
                                    onDragStart={point->originBounds=positions[id] ?: Rect.Zero;dragging=id;dragOffset=Offset.Zero;finger=handleBounds.topLeft+point;target=null},
                                    onDragCancel={clearDrag()},
                                    onDragEnd={val to=target;if(to!=null)store(DashboardPacking.move(currentOrder,id,to));clearDrag()},
                                    onDrag={change,amount->change.consume();dragOffset+=amount;finger+=amount;target=findTarget()}
                                )}) {
                            Row(Modifier.padding(10.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {Icon(Icons.Filled.DragIndicator,null);Text("Arrastar",style=MaterialTheme.typography.labelLarge)}
                        }
                        ActionButtons {
                            TextButton(enabled=index>0,onClick={store(HtmlParity.moveRecords(ids,savedOrder,id,-1))}){Text("Subir")}
                            TextButton(enabled=index<order.lastIndex,onClick={store(HtmlParity.moveRecords(ids,savedOrder,id,1))}){Text("Descer")}
                            TextButton(onClick={val size=sizes[(sizes.indexOf(selectedSizes[id] ?: fallback)+1)%sizes.size];selectedSizes[id]=size;prefs.edit().putString("size-$id",size).apply()}){Text(when(selectedSizes[id]){"small"->"Pequeno";"medium"->"Médio";"large"->"Grande";else->"Largura total"})}
                        }
                    }
                    CompositionLocalProvider(LocalAdvancedEffects provides (LocalAdvancedEffects.current&&!editing)){body(id)}
                }
            }}
        },modifier=Modifier.fillMaxWidth()) {measurables,constraints->
            val width=constraints.maxWidth
            val placeables=measurables.mapIndexed{i,item->item.measure(Constraints.fixedWidth(DashboardPacking.width(width,gap,spans[i])))}
            val cells=DashboardPacking.pack(width,gap,spans,placeables.map{it.height})
            val height=cells.maxOfOrNull{it.y+it.height} ?: 0
            layout(width,constraints.constrainHeight(height)){placeables.forEachIndexed{i,item->item.placeRelative(cells[i].x,cells[i].y)}}
        }
    }
}
