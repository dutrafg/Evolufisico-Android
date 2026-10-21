package br.com.evolufisico.android

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNode
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
    }
}
