package com.github.arburk.vscp.app.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.arburk.vscp.app.model.Blind

class PokerTimerModel : ViewModel() {

  private val _blind : MutableLiveData<Blind> = MutableLiveData()
  // establish connection between private variable and public one
  // anytime the private var is updated, the public one is updated as well
  val liveDataBlind : LiveData<Blind> = _blind

  fun initData(blind: Blind) {
    Log.v("PokerTimerModel", "initData called ${blind}")
    _blind.value = blind
  }

}