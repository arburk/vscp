package com.github.arburk.vscp.app.model

data class ConfigModel(
  var minPerRound: Int,
  var minPerWarning: Int,
  var rounds: List<Blind>
)
