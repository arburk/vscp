package com.github.arburk.vscp.app.activity

import com.github.arburk.vscp.app.service.TimerService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RoundSettingsListViewAdapterTest {

  @Test
  fun testConstants() {
    val timerService = TimerService()

    assertNotNull(timerService)
  }
}