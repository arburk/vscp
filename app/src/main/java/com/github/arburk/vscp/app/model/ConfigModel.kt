package com.github.arburk.vscp.app.model

data class ConfigModel(

  var minPerRound: Int,
  var minPerWarning: Int,
  var rounds: Array<Blind>
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ConfigModel

    if (minPerRound != other.minPerRound) return false
    if (minPerWarning != other.minPerWarning) return false
    return rounds.contentEquals(other.rounds)
  }

  override fun hashCode(): Int {
    var result = minPerRound
    result = 31 * result + minPerWarning
    result = 31 * result + rounds.contentHashCode()
    return result
  }

  override fun toString(): String {
    return "ConfigModel(minPerRound=$minPerRound, minPerWarning=$minPerWarning, rounds=${rounds.contentToString()})"
  }

}


