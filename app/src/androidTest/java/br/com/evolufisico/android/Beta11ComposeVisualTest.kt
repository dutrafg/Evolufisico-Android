package br.com.evolufisico.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.unit.Density
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import android.graphics.Bitmap
import android.content.ContentValues
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Smoke de UI real em Compose / Android 10: renderização, semântica e interação.
 * Não equivale a comparação de pixels ou homologação em dispositivo físico.
 */
@RunWith(AndroidJUnit4::class)
class Beta11ComposeVisualTest {
    @get:Rule val rule=createComposeRule()

    /** MediaStore mantém os PNGs após o Gradle desinstalar o APK de testes. */
    private fun saveCapture(name:String) {
        val resolver=InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
        val values=ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,name)
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/EvoluFisico-Beta11-QA")
            put(MediaStore.Images.Media.IS_PENDING,1)
        }
        val uri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
            ?: error("Não foi possível criar imagem de QA.")
        try {
            val image=rule.onRoot().captureToImage().asAndroidBitmap()
            resolver.openOutputStream(uri,"w")?.use { out ->
                check(image.compress(Bitmap.CompressFormat.PNG,100,out)) {
                    "Falha ao codificar captura de QA."
                }
            } ?: error("Não foi possível gravar captura de QA.")
            val ready=ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING,0) }
            check(resolver.update(uri,ready,null,null)>0) {
                "Não foi possível finalizar captura de QA."
            }
        } catch(error:Exception) {
            resolver.delete(uri,null,null)
            throw error
        }
    }

    @Test fun favoriteTileRendersAndHasIndependentRemoveAction() {
        var removed=0
        var opened=0
        val favorite=WorkspaceModules.all.first { it.route=="ASSESSMENTS" }
        rule.setContent {
            MaterialTheme(colorScheme=CorporatePalettes.scheme("azul",false),
                typography=CorporateTypography) {
                Box(Modifier.width(320.dp)) {
                    WorkspaceFavoriteTile(favorite,onOpen={opened++},onRemove={removed++})
                }
            }
        }
        rule.onNodeWithText("Avaliações").assertExists()
        rule.onNodeWithContentDescription("Remover Avaliações dos favoritos")
            .performClick()
        rule.runOnIdle {
            assertEquals(1,removed)
            assertEquals(0,opened)
        }
        val captured=rule.onRoot().captureToImage()
        assertTrue(captured.width>0&&captured.height>0)
        saveCapture("modulo-favorito.png")
    }

    @Test fun calendarSevenColumnsHaveDistinctCoordinatesAtLargeFontScale() {
        rule.setContent {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density,1.6f)) {
                MaterialTheme(colorScheme=CorporatePalettes.scheme("azul",false),
                    typography=CorporateTypography) {
                    Box(Modifier.width(360.dp)) {
                        AgendaCalendarPanel(emptyList(),null,onSelect={})
                    }
                }
            }
        }
        val labels=listOf("Seg","Ter","Qua","Qui","Sex","Sáb","Dom")
        labels.forEach { rule.onNodeWithText(it).assertExists() }
        val positions=labels.map { rule.onNodeWithText(it).getUnclippedBoundsInRoot().left }
        assertTrue("Colunas da agenda colapsadas ou invertidas: $positions",
            positions.zipWithNext().all { (left,right)->right-left>=35.dp })
        assertTrue("A grade ultrapassou seu espaço lógico",
            positions.last()-positions.first() >= 250.dp)
        val captured=rule.onRoot().captureToImage()
        assertTrue(captured.width>0 && captured.height>0)
        saveCapture("agenda-fonte-160-claro.png")
    }

    @Test fun calendarOnNarrowPhoneHasAccessibleTodayAndNavigation() {
        var chosen:LocalDate?=null
        rule.setContent {
            MaterialTheme(colorScheme=CorporatePalettes.scheme("azul",true),
                typography=CorporateTypography) {
                Box(Modifier.width(320.dp)) {
                    AgendaCalendarPanel(emptyList(),chosen,onSelect={chosen=it})
                }
            }
        }
        rule.onNode(hasContentDescription("hoje",substring=true)).assertExists()
        rule.onNodeWithText("Ir para hoje").performClick()
        rule.runOnIdle { assertEquals(LocalDate.now(),chosen) }
        val captured=rule.onRoot().captureToImage()
        assertTrue(captured.width>0&&captured.height>0)
        saveCapture("agenda-320-escuro.png")
    }
}
