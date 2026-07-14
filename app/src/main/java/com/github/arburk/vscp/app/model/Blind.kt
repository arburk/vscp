package com.github.arburk.vscp.app.model

data class Blind(val small: Int) {
  init {
    require(small >= 0) { "small blind must be non-negative" }
  }

  fun getBig(): Int = small * 2

  fun getBigAsString(): String = getBig().toString()
}
