package mihon.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(TARGET_PACKAGE_NAME) {
            pressHome()
            startActivityAndWait()

            // Library: exercise the cover grid scroll path (the most perf-critical screen)
            device.waitAndClick(By.text("Library"))
            device.waitForIdle()
            repeat(3) {
                device.swipe(540, 1000, 540, 400, 50)
                device.waitForIdle()
            }
            repeat(2) {
                device.swipe(540, 400, 540, 1000, 50)
                device.waitForIdle()
            }

            device.waitAndClick(By.text("Updates"))
            device.waitForIdle()

            device.waitAndClick(By.text("History"))
            device.waitForIdle()

            device.waitAndClick(By.text("Browse"))
            device.waitForIdle()
            device.waitAndClick(By.text("Extensions"))
            device.waitForIdle()

            device.waitAndClick(By.text("More"))
            device.waitForIdle()
        }
    }
}

private fun UiDevice.waitAndClick(by: BySelector) {
    wait(Until.findObject(by), 60_000).click()
}
