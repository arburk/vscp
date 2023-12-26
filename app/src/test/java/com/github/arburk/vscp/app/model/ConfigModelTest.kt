package com.github.arburk.vscp.app.model

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test


class ConfigModelTest {

  @Test
  fun baseFunctionTest() {
    val testee = ConfigModel(5, 2, arrayOf(Blind(2), Blind(9), Blind(401)))
    val testee2 = ConfigModel(5, 2, arrayOf(Blind(2), Blind(9), Blind(401)))
    assertEquals(
      "ConfigModel(minPerRound=5, minPerWarning=2, rounds=[Blind(small=2), Blind(small=9), Blind(small=401)])",
      testee.toString()
    )
    assertEquals(37260, testee.hashCode())
    assertTrue(testee.equals(testee))
    assertTrue(testee.equals(testee2))
    assertFalse(testee.equals(ConfigModel(4, 2, arrayOf(Blind(2), Blind(9), Blind(401)))))
    assertFalse(testee.equals(ConfigModel(5, 1, arrayOf(Blind(2), Blind(9), Blind(401)))))
    assertFalse(testee.equals(ConfigModel(5, 2, arrayOf(Blind(1), Blind(9), Blind(401)))))
    assertFalse(testee.equals(ConfigModel(5, 2, arrayOf(Blind(401)))))
  }
}