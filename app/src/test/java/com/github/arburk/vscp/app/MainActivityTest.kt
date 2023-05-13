package com.github.arburk.vscp.app

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MainActivityTest {

  @Test
  fun testConstants() {
    Assertions.assertEquals("http://vscp.ch", vscp_url)
  }

}