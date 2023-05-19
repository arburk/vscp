package com.github.arburk.vscp.app.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.arburk.vscp.app.model.Blind

class PokerTimerModel : ViewModel() {

  private val _smallBlind : MutableLiveData<Int> = MutableLiveData()
  // establish connection between private variable and public one
  // anytime the private var is updated, the public one is updated as well
  val smallBlind : LiveData<Int> = _smallBlind

  private val _bigBlind : MutableLiveData<Int> = MutableLiveData()
  val bigBlind : LiveData<Int> = _bigBlind

  init {
    Log.v("PokerTimerModel", "created")
  }

  fun initData(blind: Blind) {
    Log.v("PokerTimerModel", "initData called ${blind}")
    _smallBlind.value = blind.small
    _bigBlind.value = blind.big
  }

}