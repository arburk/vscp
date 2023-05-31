package com.github.arburk.vscp.app.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.arburk.vscp.app.model.Blind


class RoundSettingsViewModel : ViewModel() {

  private val _rounds: MutableLiveData<Blind> = MutableLiveData()
  val rounds: LiveData<Blind> = _rounds

  fun initData(rounds: Blind) {
    _rounds.value = rounds
  }
}