package com.github.arburk.vscp.app.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.HandlerThread
import android.os.IBinder
import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.activity.PokerTimerViewModel
import com.github.arburk.vscp.app.common.PreferenceManagerWrapper
import com.github.arburk.vscp.app.model.Blind
import com.github.arburk.vscp.app.model.ConfigModel
import com.github.arburk.vscp.app.settings.pref_key_min_per_round
import com.github.arburk.vscp.app.settings.pref_key_min_per_warning
import com.github.arburk.vscp.app.settings.pref_key_sound_next_round
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
  private val timerServiceThread: HandlerThread =
    HandlerThread(TimerService::class.simpleName, THREAD_PRIORITY_FOREGROUND)

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
    timerServiceThread.start()
  }

  override fun onDestroy() {
    super.onDestroy()
    timerServiceThread.quit()
  }

  private fun initConfig() {
    val sharedPreferences = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
    sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    val minPerRound = sharedPreferences.getString(pref_key_min_per_round, "12")!!.toInt()
    val minutePerWarning = sharedPreferences.getString(pref_key_min_per_warning, "1")!!.toInt()

    // TODO: init vscpConfig from saved state if available
    config = ConfigModel(minPerRound, minutePerWarning, readBlindConfigFromDevice())
    resetTimer()
    Log.v("TimerService", "initConfig conducted $config")
  }

  fun getCurrentBlind(): Blind = config.rounds[currentRound]

  fun getRounds(): Array<Blind> = config.rounds

  fun getTimeLeft(): String = String.format("%02d:%02d", remainingSeconds / 60, this.remainingSeconds % 60)

  fun isRunning(): Boolean = running

  fun getRoundsAsPokerTimerModel(): List<PokerTimerViewModel> {
    return config.rounds.map {
      PokerTimerViewModel().apply { initBlind(it) }
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
      0 -> {
        if (currentRound + 1 >= config.rounds.size) {
          resetTimer()
          remainingSeconds++
        } else {
          jumpLevel(1)
          postNotification(pref_key_sound_next_round)
        }
      }
      60 -> postNotification(pref_key_min_per_warning)
    }
    remainingSeconds--
    updateViewModels()
    Log.v("TimerService", "remainingSeconds: $remainingSeconds")
  }

  private fun postNotification(preKey: String) {
    if (pref_key_min_per_warning == preKey) {
      RingtoneManager.getRingtone(this, PreferenceManagerWrapper.getWarningNotificationSound(this))
        .play()
      return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notifyMgr = NotificationManagerCompat.from(this)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // check permissions to prevent exception to not yet opted in by user
        notifyMgr.apply {
          ActivityCompat.checkSelfPermission(this@TimerService, Manifest.permission.POST_NOTIFICATIONS)
            .also {
              if (it != PackageManager.PERMISSION_GRANTED) {
                Log.v("TimerService", "Skip notification due to missing permissions")
                return
              }
            }
        }
      }

      if (pref_key_sound_next_round == preKey) {
        notificationNextRound().also { notifyMgr.notify(it.hashCode(), it) }
      }
    }

    // handle lagacy versions
    if (pref_key_sound_next_round == preKey) {
     // TODO send notification
    }
  }

  private fun notificationNextRound(): Notification =
    NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
      .setContentTitle("Next level ${currentRound + 1}")
      .setContentText("${getCurrentBlind().small} / ${getCurrentBlind().getBig()}")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setTimeoutAfter(config.minPerRound * 60L * 1000)
      .setSmallIcon(R.mipmap.icon_webp) // TODO: add proper icon, see also  issue #10

      /**
       * TODO: apply on channel to set proper sound
       *
       * On platforms Build.VERSION_CODES.O and above this value is ignored in favor of the value set
       * on the notification's channel. On older platforms, this value is still used, so it is still
       * required for apps supporting those platforms.
       */
      .setSound(PreferenceManagerWrapper.getChannelNotificationSound(this))

      .setVibrate(LongArray(1) { 500L })
      // TOODO: Fix issue with correct timer handling
      // .setContentIntent(pendingIntentTimer)
      .build()

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
    // TODO: remove -50 after testing
    remainingSeconds = config.minPerRound * 60 - 50
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
      it.update(this)
    }
  }
}