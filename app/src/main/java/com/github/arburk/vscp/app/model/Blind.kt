package com.github.arburk.vscp.app.model

import kotlin.math.absoluteValue

data class Blind(var small: Int) {
  init {
    if(small < 0) { small = small.absoluteValue }
  }

  fun getBig(): Int = small * 2

  fun getBigAsString(): String = getBig().toString()
}
