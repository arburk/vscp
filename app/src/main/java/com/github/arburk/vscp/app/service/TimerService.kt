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
import com.github.arburk.vscp.app.activity.PokerTimerViewModel
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.model.ConfigModel
import com.github.arburk.vscp.app.settings.pref_key_min_per_round
import com.github.arburk.vscp.app.settings.pref_key_min_per_warning
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

class TimerService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

  private lateinit var config: ConfigModel

  private val binder = TimerServiceBinder()

  private var currentRound: Int = 0
  private var running = false
  private var remainingSeconds = -1
  private val timer = Timer()
  private var timerTask: TimerTask? = null

  private var viewModels: List<PokerTimerViewModel> = arrayListOf()

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

  fun getTimeLeft(): String = String.format("%02d:%02d", remainingSeconds / 60, this.remainingSeconds % 60)

  fun isRunning(): Boolean = running

  fun getRoundsAsPokerTimerModel(): List<PokerTimerViewModel> {
    return config.rounds.map { _ ->
      PokerTimerViewModel().apply { initData(this@TimerService) }
    }
  }

  fun setRounds(rounds: Array<Blind>) {
    config.rounds = rounds
    updateViewModels()
  }

  fun startTimer() {
    Log.v("TimerService", "start timer was requested")
    if (!running) {
      running = true
      timerTask = createTimerTask()
      timer.scheduleAtFixedRate(timerTask, 1000, 1000)
    }
  }

  private fun createTimerTask() = timerTask {
    when (remainingSeconds) {
      -1 -> resetTimerTaskToMaxTime()
      0 -> jumpLevel(1)
    }
    remainingSeconds--
    updateViewModels()
    Log.v("TimerService", "remainingSeconds: $remainingSeconds")
  }

  fun pauseTimer() {
    Log.v("TimerService", "pause timer was requested")
    if (running) {
      running = false
      timerTask?.cancel()
    }
  }

  private fun resetTimer() {
    Log.v("TimerService", "reset timer was requested")
    pauseTimer()
    currentRound = 0
    resetTimerTaskToMaxTime()
  }

  private fun resetTimerTaskToMaxTime() {
    remainingSeconds = config.minPerRound * 60
    updateViewModels()
  }

  fun jumpLevel(i: Int) {
    Log.v("TimerService", "called jumpLevel for $i")
    val newLevel = currentRound + i
    if (newLevel < 0 || newLevel >= config.rounds.size) {
      return
    }

    currentRound = newLevel
    resetTimerTaskToMaxTime()
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
    updateViewModels()
    Log.v("TimerService", "onSharedPreferenceChanged changed config $config")
  }

  private fun initConfig(sharedPreferences: SharedPreferences) {
    val minPerRound = sharedPreferences.getString(pref_key_min_per_round, "12")!!.toInt()
    val minutePerWarning = sharedPreferences.getString(pref_key_min_per_warning, "1")!!.toInt()

    // TODO: init vscpConfig from saved state if available
    config = ConfigModel(minPerRound, minutePerWarning, readBlindConfigFromDevice())
    resetTimer()
    Log.v("TimerService", "initConfig conducted $config")
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

  fun updateBlind(oldSmallValue: Int, newSmallValue: Int) {
    Log.v("TimerService", "change $oldSmallValue to $newSmallValue")
    val blind2change = getRounds().firstOrNull { it.small == oldSmallValue }
    Log.v("TimerService", "blind o change $blind2change")
    if (blind2change != null) {
      blind2change.small = newSmallValue
      updateViewModels()
    }
  }

  fun registerViewModel(viewModel2Add: PokerTimerViewModel) {
    if (!viewModels.contains(viewModel2Add)) {
      viewModels = viewModels.plus(viewModel2Add.also { viewModel2Add.initData(this) })
      Log.v("TimerService", "registered new viewModel $viewModel2Add")
    }
  }

  fun unregisterViewModel(viewModel2Add: PokerTimerViewModel) {
    if (viewModels.contains(viewModel2Add)) {
      viewModels.indexOf(viewModel2Add).also { viewModels = viewModels.drop(it) }
    }
  }

  private fun updateViewModels() {
    Log.v("TimerService", "updateViewModels triggered")
    viewModels.forEach {
      it.initData(this)
    }
  }
}