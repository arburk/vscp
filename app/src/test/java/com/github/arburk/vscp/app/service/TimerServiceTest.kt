package com.github.arburk.vscp.app.service


import android.content.SharedPreferences
import com.github.arburk.vscp.app.settings.pref_key_min_per_round
import com.github.arburk.vscp.app.settings.pref_key_min_per_warning
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.mockito.Matchers.eq
import org.mockito.Mockito
import java.time.Duration

class TimerServiceTest {

  private lateinit var mockSharedPreferences: SharedPreferences

  private lateinit var testee: TimerService

  @BeforeEach
  fun setUp() {
    mockSharedPreferences = Mockito.mock(SharedPreferences::class.java)
    testee = TimerService().apply {
      sharedPreferences = mockSharedPreferences
    }
  }

  @AfterEach
  fun tearDown() {
    testee.onDestroy()
  }

  @Test
  fun verify_init_by_sharedPrefs() {
    val defaultMinPerRoundCaptor = ArgumentCaptor.forClass(String::class.java)
    given(mockSharedPreferences.getString(eq(pref_key_min_per_round), defaultMinPerRoundCaptor.capture()))
      .willReturn("6")

    val defaultMinPerWarningCaptor = ArgumentCaptor.forClass(String::class.java)
    given(mockSharedPreferences.getString(eq(pref_key_min_per_warning), defaultMinPerWarningCaptor.capture()))
      .willReturn("5")

    testee.onCreate()

    assertEquals(6, testee.config.minPerRound)
    assertEquals(5, testee.config.minPerWarning)
    assertEquals("12", defaultMinPerRoundCaptor.value)
    assertEquals("1", defaultMinPerWarningCaptor.value)
  }

  @Test
  fun verify_init_by_Defaults() {
    given(mockSharedPreferences.getString(anyString(), anyString()))
      .will { invocationOnMock -> invocationOnMock.arguments[1] }

    testee.onCreate()

    assertEquals(12, testee.config.minPerRound)
    assertEquals(1, testee.config.minPerWarning)
  }

  @Test
  fun verifyTimeLeftFormat() {
    given(mockSharedPreferences.getString(anyString(), anyString()))
      .will { invocationOnMock -> invocationOnMock.arguments[1] }

    testee.onCreate()

    assertEquals( "12:00", testee.getTimeLeft(), "Expected default of 12 minutes",)

    given(mockSharedPreferences.getString(eq(pref_key_min_per_round), anyString()))
      .willReturn("2")
    testee.onSharedPreferenceChanged(mockSharedPreferences, pref_key_min_per_round)
    assertEquals("02:00", testee.getTimeLeft())

    testee.startTimer()
    await()
      .timeout(Duration.ofSeconds(2))
      .until({ testee.getTimeLeft().equals("01:59")})
    testee.pauseTimer()
    assertEquals("01:59", testee.getTimeLeft())
  }

  @Test
  fun testTimerPause() {
    testee.apply {
      startTimer()
      assertTrue(isRunning())
      pauseTimer()
      assertFalse(isRunning())
    }
  }

  @Test
  fun jumpLevelUp() {
    given(mockSharedPreferences.getString(anyString(), anyString()))
      .will { invocationOnMock -> invocationOnMock.arguments[1] }

    testee.onCreate()

    val rounds = testee.config.rounds.size
    assertTrue(rounds > 1)
    assertEquals(0, testee.currentRound)

    testee.jumpLevel(-1)
    assertEquals(0, testee.currentRound)

    testee.jumpLevel(1)
    assertEquals(1, testee.currentRound)

    testee.jumpLevel(1)
    assertEquals(2, testee.currentRound)

    testee.jumpLevel(-1)
    assertEquals(1, testee.currentRound)

    testee.currentRound = testee.config.rounds.size
    assertEquals(testee.config.rounds.size, testee.currentRound)
    testee.jumpLevel(1)
    assertEquals(testee.config.rounds.size, testee.currentRound)
  }

}