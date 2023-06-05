package com.github.arburk.vscp.app.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.model.ConfigModel
import com.github.arburk.vscp.app.settings.pref_key_min_per_round
import com.github.arburk.vscp.app.settings.pref_key_min_per_warning

class TimerService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

  private lateinit var config: ConfigModel

  private val binder = TimerServiceBinder()

  private var currentRound: Int = 0
  private var running = false // Observable?

  inner class TimerServiceBinder : Binder() {
    fun getService(): TimerService = this@TimerService
  }

  /**
   * The system invokes this method by calling bindService() when another component wants to bind
   * with the service (such as to perform RPC). In your implementation of this method, you must
   * provide an interface that clients use to communicate with the service by returning an IBinder.
   * You must always implement this method; however, if you don't want to allow binding,
   * you should return null.
   */
  override fun onBind(intent: Intent?): IBinder {
    return binder
  }

  override fun onCreate() {
    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    initConfig(sharedPreferences)
    HandlerThread("TimerService", THREAD_PRIORITY_FOREGROUND).apply {
      start()
    }
  }

  fun getCurrentBlind(): Blind = config.rounds[currentRound]

  fun getRounds(): Array<Blind> = config.rounds

  fun setRounds(rounds: Array<Blind>) {
    config.rounds = rounds
  }

  fun startTimer() {
    Log.v("TimerService", "start timer was requested")
    running = true
    // TODO: implement timer functionality
  }

  fun pauseTimer() {
    Log.v("TimerService", "pause timer was requested")
    running = false
    // TODO implement timer functionality
  }

  fun resetTimer() {
    Log.v("TimerService", "reset timer was requested")
    currentRound = 0
  }

  fun jumpLevel(i: Int) {
    val newLevel = currentRound + i
    if (newLevel < 0 || newLevel >= config.rounds.size) {
      return
    }
    currentRound = newLevel
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
    Log.v("TimerService", "called MyListener#onSharedPreferenceChanged for $sharedPreferences")

    when (key) {
      pref_key_min_per_round -> config.minPerRound =
        sharedPreferences.getString(pref_key_min_per_round, config.minPerRound.toString())!!.toInt()

      pref_key_min_per_warning -> config.minPerWarning =
        sharedPreferences.getString(pref_key_min_per_warning, config.minPerWarning.toString())!!.toInt()

      else -> Log.i("TimerService", "unknown key[$key] detected in onSharedPreferenceChanged")
    }
    //TODO: save the changes?
    Log.v("TimerService", "onSharedPreferenceChanged changed config ${config}")
  }

  fun initConfig(sharedPreferences: SharedPreferences) {
    val min_per_round = sharedPreferences.getString(pref_key_min_per_round, "12")!!.toInt()
    val minute_per_warning = sharedPreferences.getString(pref_key_min_per_warning, "1")!!.toInt()

    // TODO: init vscpConfig from saved state if available
    config = ConfigModel(min_per_round, minute_per_warning, readBlindConfigFromDevice())
    Log.v("TimerService", "initConfig conducted ${config}")
  }

  fun updateBlindsConfig(blindConfigToApply: Array<Blind>) {
    config.rounds = blindConfigToApply
    // TODO: save state to file
    Log.v("TimerService", "update of config conducted ${config}")
  }

  /**
   * TODO: read it from file before if available, return default if not found
   */
  private fun readBlindConfigFromDevice() = arrayOf(
    Blind(25),
    Blind(50),
    Blind(75),
    Blind(100),
    Blind(150),
    Blind(300),
    Blind(400),
    Blind(600),
    Blind(800),
    Blind(1000)
  )
}