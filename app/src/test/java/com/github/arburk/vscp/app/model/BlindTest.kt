package com.github.arburk.vscp.app.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


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
  fun getBig_NegativeIsRejected() {
    assertThrows<IllegalArgumentException> { Blind(-1) }
    assertThrows<IllegalArgumentException> { Blind(-2) }
    assertThrows<IllegalArgumentException> { Blind(-100) }
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

