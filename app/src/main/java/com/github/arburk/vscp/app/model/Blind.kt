package com.github.arburk.vscp.app.model

import java.util.concurrent.atomic.AtomicInteger

class Blind(val small: Int, val id: Int = nextId()) {
  init {
    require(small >= 0) { "small blind must be non-negative" }
  }

  fun getBig(): Int = small * 2

  fun getBigAsString(): String = getBig().toString()

  fun withSmall(newSmall: Int): Blind = Blind(newSmall, id)

  override fun equals(other: Any?): Boolean = other is Blind && other.small == small

  override fun hashCode(): Int = small.hashCode()

  override fun toString(): String = "Blind(small=$small)"

  companion object {
    private val idSequence = AtomicInteger(0)
    private fun nextId(): Int = idSequence.getAndIncrement()
  }
}
