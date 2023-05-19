package com.github.arburk.vscp.app.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.model.ConfigModel

class TimerService : Service() {

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
    initConfig()

    HandlerThread("ServiceStartArguments", THREAD_PRIORITY_FOREGROUND).apply {
      Log.v("Trace", "onCreate")
      start()
    }
  }

  fun getCurrentBlind(): Blind = config.rounds[currentRound]

  fun startTimer() {
    Log.v("TimerService", "start timer was requested")
    running = true
    // TODO
  }

  fun pauseTimer() {
    Log.v("TimerService", "pause timer was requested")
    running = false
    // TODO
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

  private fun initConfig() {
    // TODO: initConfig
    val vscpConfig = arrayOf(
      Blind(25, 50),
      Blind(50, 100),
      Blind(75, 150),
      Blind(100, 200),
      Blind(150, 300),
      Blind(300, 600),
      Blind(400, 800),
      Blind(600, 1200),
      Blind(800, 1600),
      Blind(1000, 2000)
    )
    config = ConfigModel(12, 1, vscpConfig)
    Log.v("TimerService", "initConfig conducted")
  }

}