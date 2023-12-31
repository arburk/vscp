/*
package com.github.arburk.vscp.app.activity

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.DefaultAsserter.fail

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MockKExtension.CheckUnnecessaryStub
class RoundSettingsInstrumentedTest  {

  //https://github.com/mockk/mockk/issues/182

  private lateinit var scenario: FragmentScenario<RoundSettings>

  private lateinit var tsMock : TimerService

  @Before
  fun setUp() {
    val mockedBlinds = arrayOf(Blind(2), Blind(5))

    //tsMock = mockk<TimerService>(name = "timerServiceMock", relaxed = true, relaxUnitFun = true)
    every { tsMock.isRunning() } returns true
    every { tsMock.getRounds() } returns mockedBlinds
    every { tsMock.getRoundsAsPokerTimerModel() } returns mockedBlinds.map {
      PokerTimerViewModel().apply { initBlind(it) }
    }

    scenario = launchFragmentInContainer { RoundSettings().apply { timerService = tsMock } }
    scenario.moveToState(Lifecycle.State.STARTED)
  }

  @Test
  fun testInitalLayout() {
    val onView = onView(withId(R.id.rounds_row_list_view))
    onView.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    fail("test")
  }


}*/
