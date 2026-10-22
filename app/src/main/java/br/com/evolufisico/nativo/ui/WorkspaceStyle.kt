package br.com.evolufisico.nativo.ui

import androidx.compose.material3.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight

val LocalCompactWorkspace=staticCompositionLocalOf{false}

data class WorkspacePalette(val label:String,val light:Long,val dark:Long)
val workspacePalettes=listOf(
    WorkspacePalette("Azul profundo",0xFF163654,0xFFADCBE8),WorkspacePalette("Azul clássico",0xFF204C74,0xFFA5CDF4),
    WorkspacePalette("Azul intenso",0xFF255A87,0xFFACD1F0),WorkspacePalette("Azul aço",0xFF315F7D,0xFFB5D5EB),
    WorkspacePalette("Azul petróleo",0xFF234861,0xFFA5CEE7),WorkspacePalette("Petróleo escuro",0xFF123E47,0xFF9AD4DE),
    WorkspacePalette("Verde floresta",0xFF244C3C,0xFFA7D9BB),WorkspacePalette("Ameixa",0xFF4E3D68,0xFFD1BDEA),
    WorkspacePalette("Vinho",0xFF583849,0xFFE4BACA),WorkspacePalette("Grafite",0xFF3E464F,0xFFC6CFD9))

fun workspaceColors(tone:Int,dark:Boolean):ColorScheme {
    val p=workspacePalettes[tone.coerceIn(workspacePalettes.indices)]
    val base=Color(p.light);val accent=Color(p.dark)
    return if(dark)darkColorScheme(primary=accent,onPrimary=Color(0xFF10212C),
        primaryContainer=lerp(base,Color.Black,0.3f),onPrimaryContainer=accent,
        secondary=accent,onSecondary=Color(0xFF10212C),secondaryContainer=Color(0xFF26333D),onSecondaryContainer=Color(0xFFDCE7F0),
        surface=Color(0xFF141B23),background=Color(0xFF0E141C),onSurface=Color(0xFFE1E7EF),onBackground=Color(0xFFE1E7EF),
        surfaceContainer=Color(0xFF1C2530),surfaceContainerLow=Color(0xFF18212B),surfaceContainerHigh=Color(0xFF26313E),
        onSurfaceVariant=Color(0xFFBDC9D6),outline=Color(0xFF8C9AA9))
    else lightColorScheme(primary=base,onPrimary=Color.White,primaryContainer=lerp(base,Color.White,0.88f),onPrimaryContainer=base,
        secondary=base,onSecondary=Color.White,secondaryContainer=lerp(base,Color.White,0.91f),onSecondaryContainer=base,
        surface=Color(0xFFFAFBFD),background=Color(0xFFF0F4F8),onSurface=Color(0xFF182838),onBackground=Color(0xFF182838),
        surfaceContainer=Color(0xFFEAF0F6),surfaceContainerLow=Color(0xFFF2F6FA),surfaceContainerHigh=Color(0xFFE1E9F1),
        onSurfaceVariant=Color(0xFF47596C),outline=Color(0xFF687B8D))
}

fun workspaceTypography(large:Boolean):Typography {
    val scale=if(large)1.12f else 1f
    val t=Typography()
    return t.copy(headlineSmall=t.headlineSmall.copy(fontWeight=FontWeight.SemiBold,fontSize=t.headlineSmall.fontSize*scale,lineHeight=t.headlineSmall.lineHeight*scale),
        titleLarge=t.titleLarge.copy(fontWeight=FontWeight.SemiBold,fontSize=t.titleLarge.fontSize*scale,lineHeight=t.titleLarge.lineHeight*scale),
        titleMedium=t.titleMedium.copy(fontWeight=FontWeight.SemiBold,fontSize=t.titleMedium.fontSize*scale,lineHeight=t.titleMedium.lineHeight*scale),
        titleSmall=t.titleSmall.copy(fontWeight=FontWeight.SemiBold,fontSize=t.titleSmall.fontSize*scale,lineHeight=t.titleSmall.lineHeight*scale),
        bodyLarge=t.bodyLarge.copy(fontSize=t.bodyLarge.fontSize*scale,lineHeight=t.bodyLarge.lineHeight*scale),
        bodyMedium=t.bodyMedium.copy(fontSize=t.bodyMedium.fontSize*scale,lineHeight=t.bodyMedium.lineHeight*scale),
        bodySmall=t.bodySmall.copy(fontSize=t.bodySmall.fontSize*scale,lineHeight=t.bodySmall.lineHeight*scale),
        labelLarge=t.labelLarge.copy(fontSize=t.labelLarge.fontSize*scale,lineHeight=t.labelLarge.lineHeight*scale),
        labelMedium=t.labelMedium.copy(fontSize=t.labelMedium.fontSize*scale,lineHeight=t.labelMedium.lineHeight*scale),
        labelSmall=t.labelSmall.copy(fontSize=t.labelSmall.fontSize*scale,lineHeight=t.labelSmall.lineHeight*scale))
}
