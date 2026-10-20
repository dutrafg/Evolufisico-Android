package br.com.evolufisico.android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenshotPolicyTest {
    @Test fun previewsPermitScreenshots() {
        listOf("0.10.0-alpha", "1.0.0-beta", "1.0.0-rc1", "0.6.0-alpha")
            .forEach { assertFalse(ScreenshotPolicy.shouldBlock(it)) }
    }

    @Test fun stableReleasesProtectWindows() {
        listOf("1.0.0", "2.1.5", "10.0.0").forEach {
            assertTrue(ScreenshotPolicy.shouldBlock(it))
        }
    }

    @Test fun unknownLabelsDoNotActivateStableProtection() {
        assertFalse(ScreenshotPolicy.shouldBlock(""))
        assertFalse(ScreenshotPolicy.shouldBlock("dev"))
    }
}
