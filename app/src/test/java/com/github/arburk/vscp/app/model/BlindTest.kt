package com.github.arburk.vscp.app.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlindTest {
  @Test
  fun getBig() {
    assertEquals(0, Blind(0).getBig())
    assertEquals(2, Blind(1).getBig())
    assertEquals(4, Blind(2).getBig())
    assertEquals(6, Blind(3).getBig())
    assertEquals(200, Blind(100).getBig())
  }

  @Test
  fun getBig_NegativeTurnsPositive() {
    assertEquals(2, Blind(-1).also { assertEquals(1, it.small) }.getBig())
    assertEquals(4, Blind(-2).also { assertEquals(2, it.small) }.getBig())
    assertEquals(6, Blind(-3).also { assertEquals(3, it.small) }.getBig())
    assertEquals(200, Blind(-100).also { assertEquals(100, it.small) }.getBig())
  }

  @Test
  fun getBigAsString() {
    assertEquals("0", Blind(0).getBigAsString())
    assertEquals("2", Blind(1).getBigAsString())
    assertEquals("4", Blind(2).getBigAsString())
    assertEquals("6", Blind(3).getBigAsString())
    assertEquals("200", Blind(100).getBigAsString())
  }
}

