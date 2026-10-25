package br.com.evolufisico.nativo.ui

import androidx.compose.material3.*
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight

val LocalCompactWorkspace=staticCompositionLocalOf{false}

data class WorkspacePalette(val label:String,val light:Long,val dark:Long)
val workspacePalettes=listOf(
    WorkspacePalette("Verde",0xFF086849,0xFF75D7A9),WorkspacePalette("Azul",0xFF235CBD,0xFF73AEF9),
    WorkspacePalette("Roxo",0xFF6244BA,0xFFAD95FF),WorkspacePalette("Laranja",0xFFA85517,0xFFF9B276),
    WorkspacePalette("Rosa",0xFFA53C71,0xFFF3A5C5),WorkspacePalette("Azul-marinho",0xFF233F68,0xFF85A9E4),
    WorkspacePalette("Petróleo",0xFF145C68,0xFF71C1C9),WorkspacePalette("Esmeralda",0xFF17624E,0xFF76C8A3),
    WorkspacePalette("Índigo",0xFF343F79,0xFF9FAAF0),WorkspacePalette("Ameixa",0xFF653A65,0xFFD2A4D0))

fun workspaceColors(tone:Int,dark:Boolean):ColorScheme {
    val p=workspacePalettes[tone.coerceIn(workspacePalettes.indices)]
    val base=Color(p.light);val accent=Color(p.dark)
    return if(dark)darkColorScheme(primary=accent,onPrimary=Color(0xFF04150D),
        primaryContainer=lerp(base,Color.Black,0.3f),onPrimaryContainer=accent,
        secondary=accent,onSecondary=Color(0xFF04150D),secondaryContainer=Color(0xFF16271F),onSecondaryContainer=Color(0xFFE8F1EC),
        surface=Color(0xFF101A15),background=Color(0xFF08100C),onSurface=Color(0xFFE8F1EC),onBackground=Color(0xFFE8F1EC),
        surfaceContainer=Color(0xFF14221A),surfaceContainerLow=Color(0xFF101A15),surfaceContainerHigh=Color(0xFF1F2E26),
        onSurfaceVariant=Color(0xFFA6B9AE),outline=Color(0xFF8FA398))
    else lightColorScheme(primary=base,onPrimary=Color.White,primaryContainer=lerp(base,Color.White,0.88f),onPrimaryContainer=base,
        secondary=base,onSecondary=Color.White,secondaryContainer=lerp(base,Color.White,0.91f),onSecondaryContainer=base,
        surface=Color(0xFFFFFFFF),background=Color(0xFFF3F6F4),onSurface=Color(0xFF0C1A14),onBackground=Color(0xFF0C1A14),
        surfaceContainer=Color(0xFFE6F3EC),surfaceContainerLow=Color(0xFFF3F6F4),surfaceContainerHigh=Color(0xFFE1E9E4),
        onSurfaceVariant=Color(0xFF5D6F66),outline=Color(0xFF667A6C))
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
