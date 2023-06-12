package com.github.arburk.vscp.app.activity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.service.TimerService

class PokerTimerViewModel : ViewModel() {

  private val _blind: MutableLiveData<Blind> = MutableLiveData()
  private val _remainingTime: MutableLiveData<String> = MutableLiveData()

  // establish connection between private variable and public one
  // anytime the private var is updated, the public one is updated as well
  val blind: LiveData<Blind> = _blind
  val remainingTime: LiveData<String> = _remainingTime

  fun initBlind(blind: Blind) {
    _blind.value = blind
  }

  fun initData(timerService: TimerService) {
    _blind.value = timerService.getCurrentBlind()
    _remainingTime.value = timerService.getTimeLeft()
  }

  fun update(timerService: TimerService) {
    _blind.postValue(timerService.getCurrentBlind())
    _remainingTime.postValue(timerService.getTimeLeft())
  }

}