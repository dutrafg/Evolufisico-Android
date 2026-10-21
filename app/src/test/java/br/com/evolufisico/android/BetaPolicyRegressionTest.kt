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
}
