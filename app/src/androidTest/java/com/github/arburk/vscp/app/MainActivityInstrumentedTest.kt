package com.github.arburk.vscp.app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
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