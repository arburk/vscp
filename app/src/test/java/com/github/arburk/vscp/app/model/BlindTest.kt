package com.github.arburk.vscp.app.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class BlindTest {
  @Test
  fun getBig() {
    Assertions.assertEquals(0, Blind(0).getBig())
    Assertions.assertEquals(2, Blind(1).getBig())
    Assertions.assertEquals(4, Blind(2).getBig())
    Assertions.assertEquals(6, Blind(3).getBig())
    Assertions.assertEquals(200, Blind(100).getBig())
  }

  @Test
  fun getBig_NegativeTurnsPositive() {
    Assertions.assertEquals(2, Blind(-1).also { Assertions.assertEquals(1, it.small) }.getBig())
    Assertions.assertEquals(4, Blind(-2).also { Assertions.assertEquals(2, it.small) }.getBig())
    Assertions.assertEquals(6, Blind(-3).also { Assertions.assertEquals(3, it.small) }.getBig())
    Assertions.assertEquals(200, Blind(-100).also { Assertions.assertEquals(100, it.small) }.getBig())
  }

  @Test
  fun getBigAsString() {
    Assertions.assertEquals("0", Blind(0).getBigAsString())
    Assertions.assertEquals("2", Blind(1).getBigAsString())
    Assertions.assertEquals("4", Blind(2).getBigAsString())
    Assertions.assertEquals("6", Blind(3).getBigAsString())
    Assertions.assertEquals("200", Blind(100).getBigAsString())
  }
}

