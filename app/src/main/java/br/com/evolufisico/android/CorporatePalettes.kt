package br.com.evolufisico.android

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class CorporatePalette(
    val id:String, val label:String, val deep:Color, val bright:Color
)

/** As cinco primeiras cores correspondem às opções do EvoluFísico2. */
object CorporatePalettes {
    val options=listOf(
        CorporatePalette("verde","Verde",Color(0xFF086849),Color(0xFF75D7A9)),
        CorporatePalette("azul","Azul",Color(0xFF235CBD),Color(0xFF73AEF9)),
        CorporatePalette("roxo","Roxo",Color(0xFF6244BA),Color(0xFFAD95FF)),
        CorporatePalette("laranja","Laranja",Color(0xFFA85517),Color(0xFFF9B276)),
        CorporatePalette("rosa","Rosa",Color(0xFFA53C71),Color(0xFFF3A5C5)),
        CorporatePalette("ciano","Ciano",Color(0xFF086C83),Color(0xFF5DD2E9)),
        CorporatePalette("turquesa","Turquesa",Color(0xFF086F69),Color(0xFF5CCFBC)),
        CorporatePalette("indigo","Índigo",Color(0xFF3946A5),Color(0xFFA4AAFF)),
        CorporatePalette("vermelho","Vermelho",Color(0xFF9D2F3B),Color(0xFFF59AA2)),
        CorporatePalette("grafite","Grafite",Color(0xFF374151),Color(0xFFB7C4D4)),
        // Tons fortes da edição Desktop 9.1.1.beta.4; não substituir IDs já salvos.
        CorporatePalette("verde-escuro","Verde escuro",Color(0xFF085139),Color(0xFF166B50)),
        CorporatePalette("azul-escuro","Azul escuro",Color(0xFF174178),Color(0xFF245B9B)),
        CorporatePalette("roxo-escuro","Roxo escuro",Color(0xFF4B317E),Color(0xFF65479C)),
        CorporatePalette("laranja-escuro","Laranja escuro",Color(0xFF7D3616),Color(0xFFA64C22)),
        CorporatePalette("rosa-escuro","Rosa escuro",Color(0xFF79274D),Color(0xFFA13B68))
    )
    fun byId(id:String):CorporatePalette=options.firstOrNull { it.id==id }
        ?: options.first { it.id=="azul" }

    fun scheme(id:String,dark:Boolean):ColorScheme {
        val selected=byId(id)
        val strong=id.endsWith("-escuro")
        val accentText=if(strong) Color.White else Color(0xFF13202D)
        return if(dark) darkColorScheme(
            primary=selected.bright,onPrimary=accentText,
            secondary=selected.bright,onSecondary=Color(0xFF13202D),
            background=Color(0xFF101825),onBackground=Color(0xFFE7ECF5),
            surface=Color(0xFF172335),onSurface=Color(0xFFE7ECF5),
            surfaceVariant=Color(0xFF253449),onSurfaceVariant=Color(0xFFBBC9DC),
            outline=Color(0xFF708298),outlineVariant=Color(0xFF3D4A5E),
            primaryContainer=Color(0xFF273C51),
            onPrimaryContainer=if(strong) Color.White else selected.bright,
            secondaryContainer=Color(0xFF1F4237),onSecondaryContainer=Color(0xFFC0F0D9),
            tertiaryContainer=Color(0xFF4A3B22),onTertiaryContainer=Color(0xFFFFE0A6),
            errorContainer=Color(0xFF522E3C),onErrorContainer=Color(0xFFFFC0D2)
        ) else lightColorScheme(
            primary=selected.deep,onPrimary=Color.White,
            secondary=selected.deep,onSecondary=Color.White,
            background=Color(0xFFF7F9FC),onBackground=Color(0xFF162238),
            surface=Color.White,onSurface=Color(0xFF162238),
            surfaceVariant=Color(0xFFE9EFF7),onSurfaceVariant=Color(0xFF50617A),
            outline=Color(0xFFBBCADA),outlineVariant=Color(0xFFE0E7F0),
            primaryContainer=Color(0xFFE4EDF7),onPrimaryContainer=selected.deep,
            secondaryContainer=Color(0xFFE3F5EC),onSecondaryContainer=Color(0xFF126D50),
            tertiaryContainer=Color(0xFFFFF2DB),onTertiaryContainer=Color(0xFF895900),
            errorContainer=Color(0xFFFBE8EE),onErrorContainer=Color(0xFF943552)
        )
    }
}
