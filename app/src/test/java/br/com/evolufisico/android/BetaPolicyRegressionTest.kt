package br.com.evolufisico.android

import br.com.evolufisico.android.updates.ReleasePolicy
import br.com.evolufisico.android.updates.UpdatePolicy
import br.com.evolufisico.android.backup.BackupStatusRules
import br.com.evolufisico.android.data.ClientRules
import org.junit.Assert.*
import org.junit.Test

/**
 * Regressões cumulativas das Alphas 42–88.
 * Cada caso mantém uma garantia verificada para a decisão de Beta.
 */
class BetaPolicyRegressionTest {
    @Test fun alpha42ParsesOfficialStableTag() {
        assertEquals(1,ReleasePolicy.parse("v1.2.3")?.major)
        assertEquals(2,ReleasePolicy.parse("v1.2.3")?.minor)
        assertEquals(3,ReleasePolicy.parse("v1.2.3")?.patch)
    }
    @Test fun alpha43ReleaseRegression() {
        assertEquals(0,ReleasePolicy.parse("1.0.0-alpha")?.channel)
        assertEquals(7,ReleasePolicy.parse("1.0.0-alpha.7")?.channelNumber)
    }

    @Test fun alpha44ReleaseRegression() {
        assertEquals(1,ReleasePolicy.parse("1.0.0-beta4")?.channel)
        assertEquals(4,ReleasePolicy.parse("1.0.0-beta4")?.channelNumber)
    }

    @Test fun alpha45ReleaseRegression() {
        assertEquals(2,ReleasePolicy.parse("v1.0.0-rc.2")?.channel)
        assertEquals(2,ReleasePolicy.parse("v1.0.0-rc.2")?.channelNumber)
    }

    @Test fun alpha46ReleaseRegression() {
        assertTrue(ReleasePolicy.newer("1.0.0","1.0.0-rc.9"))
        assertFalse(ReleasePolicy.newer("1.0.0-beta.1","1.0.0"))
    }

    @Test fun alpha47ReleaseRegression() {
        assertNull(ReleasePolicy.parse("version-1.2.3"))
        assertNull(ReleasePolicy.parse("1.2"))
    }

    @Test fun alpha48ReleaseRegression() {
        assertNull(ReleasePolicy.parse("9999999999999999.0.0"))
        assertNull(ReleasePolicy.parse("1.2.3-alpha.99999999999999"))
    }

    @Test fun alpha49ReleaseRegression() {
        assertNull(ReleasePolicy.parse("1.2.3-gamma"))
        assertNull(ReleasePolicy.parse("1.2.3 beta.1"))
    }

    @Test fun alpha50ReleaseRegression() {
        assertFalse(ReleasePolicy.newer("2.0.0","2.0.1"))
        assertFalse(ReleasePolicy.newer("2.0.0","2.0.0"))
    }

    @Test fun alpha51UpdateUrlRegression() {
        assertTrue(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/official.apk"))
    }

    @Test fun alpha52UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("http://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk"))
    }

    @Test fun alpha53UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com.evil.example/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk"))
    }

    @Test fun alpha54UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/../app.apk"))
    }

    @Test fun alpha55UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/%2Fapp.apk"))
    }

    @Test fun alpha56UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk?file=evil"))
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk#evil"))
    }

    @Test fun alpha57UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://bad@github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk"))
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com:443/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk"))
    }

    @Test fun alpha58UpdateUrlRegression() {
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.exe"))
        assertFalse(UpdatePolicy.validReleaseUrl("https://github.com/dutrafg/Evolufisico-Android/releases/download/v1.0.0/app.apk/second"))
    }

    @Test fun alpha59PolicyRegression() {
        assertTrue(UpdatePolicy.isUpgrade(137,138))
        assertFalse(UpdatePolicy.isUpgrade(138,137))
    }

    @Test fun alpha60PolicyRegression() {
        val old="original".toByteArray()
        assertTrue(UpdatePolicy.sameSigningCertificate(listOf(old),listOf(old.clone())))
    }

    @Test fun alpha61PolicyRegression() {
        assertFalse(UpdatePolicy.sameSigningCertificate(listOf("a".toByteArray()),listOf("b".toByteArray())))
        assertFalse(UpdatePolicy.sameSigningCertificate(emptyList(),listOf("a".toByteArray())))
    }

    @Test fun alpha62PolicyRegression() {
        assertEquals(setOf("GOALS","FORMS"),WorkspaceFavorites.parse("GOALS,FORMS,GOALS"))
    }

    @Test fun alpha63PolicyRegression() {
        val selected=WorkspaceFavorites.toggle(emptySet(),"GOALS")
        assertEquals(setOf("GOALS"),selected)
        assertTrue(WorkspaceFavorites.toggle(selected,"GOALS").isEmpty())
    }

    @Test fun alpha64PolicyRegression() {
        val filled=linkedSetOf("GOALS","NOTES","FORMS","ASSESSMENTS")
        assertEquals(filled,WorkspaceFavorites.toggle(filled,"SERVICES"))
    }

    @Test fun alpha65PolicyRegression() {
        val before=linkedSetOf("GOALS","FORMS")
        assertEquals(listOf("FORMS","GOALS"),WorkspaceFavorites.move(before,"FORMS",-1).toList())
    }

    @Test fun alpha66PolicyRegression() {
        assertEquals("GOALS",WorkspaceFavorites.serialize(setOf("GOALS","INVALID")))
        assertEquals(setOf("GOALS"),WorkspaceFavorites.parse("INVALID,GOALS"))
    }

    @Test fun alpha67LocalDataRegression() {
        assertEquals(listOf("GOALS"),WorkspaceRecents.open(emptyList(),"GOALS"))
    }

    @Test fun alpha68LocalDataRegression() {
        assertEquals(listOf("NOTES","GOALS"),WorkspaceRecents.open(listOf("GOALS","NOTES"),"NOTES"))
    }

    @Test fun alpha69LocalDataRegression() {
        assertEquals(listOf("GOALS","FORMS","NOTES"),
            WorkspaceRecents.parse("GOALS,FORMS,NOTES,REPORTS"))
    }

    @Test fun alpha70LocalDataRegression() {
        assertEquals("GOALS,FORMS",WorkspaceRecents.serialize(listOf("GOALS","UNKNOWN","FORMS")))
    }

    @Test fun alpha71LocalDataRegression() {
        assertEquals(listOf("GOALS"),WorkspaceRecents.open(listOf("GOALS"),"BAD_ROUTE"))
    }

    @Test fun alpha72LocalDataRegression() {
        assertNull(ClientRules.validateName("Cliente Exemplo"))
    }

}
