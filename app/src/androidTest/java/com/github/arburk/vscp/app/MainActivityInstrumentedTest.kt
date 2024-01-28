package com.github.arburk.vscp.app

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class MainActivityInstrumentedTest {

  @JvmField
  @RegisterExtension
  val scenarioExtension = ActivityScenarioExtension.launch<MainActivity>()

  @BeforeEach
  fun setUp(scenario: ActivityScenario<MainActivity>) {
    // Avoid system interaction which would block the app asking for permission by adding minimum disruptive channel
    scenario.onActivity {
      it.permissionActivity = MockedActivityResultLauncher()
    }
  }

  class MockedActivityResultLauncher : ActivityResultLauncher<String> () {
    override fun launch(p0: String?, p1: ActivityOptionsCompat?) { // do nothing
    }

    override fun unregister() { // do nothing
    }

    override fun getContract(): ActivityResultContract<String, *> {
      throw NotImplementedError("mock only")
    }

  }

  @Test
  fun navigateToTimer(scenario: ActivityScenario<MainActivity>) {
    // Context of the app under test.
    scenario.onActivity {
      assertEquals("com.github.arburk.vscp.app", it.packageName)
      assertEquals(1, it.supportFragmentManager.fragments.size)
      val currentFragment = it.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
      assertEquals("com.github.arburk.vscp.app.activity.MainScreen", currentFragment.javaClass.name)
    }

    onView(ViewMatchers.withId(R.id.poker_timer)).perform(click())

    scenario.onActivity {
      assertEquals(1, it.supportFragmentManager.fragments.size)
      val currentFragment = it.supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
      assertEquals("com.github.arburk.vscp.app.activity.PokerTimer", currentFragment.javaClass.name)
    }
  }

}
