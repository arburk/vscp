package com.github.arburk.vscp.app.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.VisibleForTesting
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
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timerTask

class TimerService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

  companion object {
    private const val FOREGROUND_NOTIFICATION_ID = 1
  }

  @VisibleForTesting
  internal lateinit var config: ConfigModel

  @VisibleForTesting
  internal lateinit var sharedPreferences: SharedPreferences

  private val binder = TimerServiceBinder()

  @VisibleForTesting
  @Volatile
  internal var currentRound: Int = 0

  @Volatile private var running = false
  @Volatile private var remainingSeconds = -1
  @Volatile private var viewModels: List<PokerTimerViewModel> = emptyList()

  private val timer = Timer()
  private var timerTask: TimerTask? = null
  private val mainHandler = Handler(Looper.getMainLooper())

  private lateinit var soundPool: SoundPool
  private var fightCountdownSoundId = 0
  private var oneMinuteWarningSoundId = 0

  inner class TimerServiceBinder : Binder() {
    fun getService(): TimerService = this@TimerService
  }

  override fun onBind(intent: Intent?): IBinder = binder

  override fun onCreate() {
    initConfig()
    preloadSounds()
    NotificationManagerWrapper().createForegroundServiceChannel(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    timer.cancel()
    stopForeground(Service.STOP_FOREGROUND_REMOVE)
    if (this::soundPool.isInitialized) soundPool.release()
  }

  private fun preloadSounds() {
    // Kept as separate statements on retained builder references (no method chaining):
    // the Android unit-test stubs return null from chained builder calls, which would
    // NPE here even though the same chain works fine on a real device.
    val audioAttributesBuilder = AudioAttributes.Builder()
    audioAttributesBuilder.setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
    audioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)

    val soundPoolBuilder = SoundPool.Builder()
    soundPoolBuilder.setMaxStreams(2)
    soundPoolBuilder.setAudioAttributes(audioAttributesBuilder.build())

    soundPool = soundPoolBuilder.build() ?: return
    fightCountdownSoundId = soundPool.load(this, R.raw.countdown_fight, 1)
    oneMinuteWarningSoundId = soundPool.load(this, R.raw.one_minute_warning, 1)
  }

  private fun initConfig() {
    getSharedPreferences().apply {
      registerOnSharedPreferenceChangeListener(this@TimerService)
      val minPerRound = getString(pref_key_min_per_round, "12")!!.toInt()
      val minutePerWarning = getString(pref_key_min_per_warning, "1")!!.toInt()
      config = ConfigModel(minPerRound, minutePerWarning, readBlindConfigFromDevice())
      resetTimer()
      Log.v("TimerService", "initConfig conducted $config")
    }
  }

  private fun getSharedPreferences(): SharedPreferences {
    if (!this::sharedPreferences.isInitialized) {
      sharedPreferences = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
    }
    return sharedPreferences
  }

  fun getCurrentBlind(): Blind = config.rounds[currentRound]

  fun getRounds(): List<Blind> = config.rounds

  fun getTimeLeft(): String = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)

  fun isRunning(): Boolean = running

  fun setRounds(rounds: List<Blind>) {
    config.rounds = rounds
    updateViewModels()
  }

  fun startTimer() {
    Log.v("TimerService", "start timer was requested")
    if (!running) {
      running = true
      // Background execution limits/Doze (the actual cause of unreliable playback) only
      // apply from Android 8 (O) onward, so foreground promotion is only needed there.
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification())
      }
      timerTask = createTimerTask()
      timer.scheduleAtFixedRate(timerTask, 1000, 1000)
    }
  }

  private fun buildForegroundNotification(): Notification =
    NotificationCompat.Builder(this, getString(R.string.foreground_notification_channel_id))
      .setContentTitle(getString(R.string.app_name))
      .setContentText(getString(R.string.foreground_notification_text))
      .setSmallIcon(R.mipmap.icon_webp)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setOngoing(true)
      .build()

  private fun createTimerTask() = timerTask {
    when (remainingSeconds) {
      -1 -> resetTimerTaskToMaxTime()
      0 -> {
        if (currentRound + 1 >= config.rounds.size) {
          resetTimer()
          remainingSeconds++
        } else {
          jumpLevel(1)
          processNextRoundNotification()
        }
      }
      4 -> mainHandler.post {
        soundPool.play(fightCountdownSoundId, 1f, 1f, 1, 0, 1f)
      }
      config.minPerWarning * 60 + 1 -> mainHandler.post {
        soundPool.play(oneMinuteWarningSoundId, 1f, 1f, 1, 0, 1f)
      }
    }
    remainingSeconds--
    updateViewModels()
    Log.v("TimerService", "remainingSeconds: $remainingSeconds")
  }

  private fun processNextRoundNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notifyMgr = NotificationManagerCompat.from(this)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(this@TimerService, Manifest.permission.POST_NOTIFICATIONS)
          != PackageManager.PERMISSION_GRANTED
        ) {
          Log.v("TimerService", "Skip notification due to missing permissions")
          return
        }
      }
      notificationNextRound().also { notifyMgr.notify(it.hashCode(), it) }
      return
    }

    notificationNextRound().also {
      (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).notify(it.hashCode(), it)
    }
  }

  private fun notificationNextRound(): Notification =
    NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
      .setContentTitle("Next level ${currentRound + 1}")
      .setContentText("${getCurrentBlind().small} / ${getCurrentBlind().getBig()}")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setTimeoutAfter(config.minPerRound * 60L * 1000)
      .setSmallIcon(R.mipmap.icon_webp)
      .setSound(PreferenceManagerWrapper.getChannelNotificationSound(this))
      .setDefaults(Notification.DEFAULT_VIBRATE)
      .setVibrate(LongArray(1) { 500L })
      .build()

  fun pauseTimer() {
    Log.v("TimerService", "pause timer was requested")
    if (running) {
      running = false
      timerTask?.cancel()
      stopForeground(Service.STOP_FOREGROUND_REMOVE)
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
    if (newLevel < 0 || newLevel >= config.rounds.size) return
    currentRound = newLevel
    resetTimerTaskToMaxTime()
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
    key ?: return
    Log.v("TimerService", "called MyListener#onSharedPreferenceChanged for key=$key")

    when (key) {
      pref_key_min_per_round -> {
        val newMinPerRound = sharedPreferences.getString(pref_key_min_per_round, config.minPerRound.toString())!!.toInt()
        val oldMinPerRound = config.minPerRound
        config.minPerRound = newMinPerRound
        remainingSeconds = if (newMinPerRound > oldMinPerRound) {
          remainingSeconds + (newMinPerRound - oldMinPerRound) * 60
        } else {
          newMinPerRound * 60
        }
      }
      pref_key_min_per_warning -> config.minPerWarning =
        sharedPreferences.getString(pref_key_min_per_warning, config.minPerWarning.toString())!!.toInt()
      else -> Log.i("TimerService", "unknown key[$key] detected in onSharedPreferenceChanged")
    }
    updateViewModels()
    Log.v("TimerService", "onSharedPreferenceChanged changed config $config")
  }

  private fun readBlindConfigFromDevice(): List<Blind> = listOf(
    Blind(25),
    Blind(50),
    Blind(75),
    Blind(100),
    Blind(150),
    Blind(200),
    Blind(300),
    Blind(400),
    Blind(600),
    Blind(800),
    Blind(1000)
  )

  fun updateBlind(id: Int, newSmallValue: Int) {
    Log.v("TimerService", "updateBlind for round id $id to $newSmallValue")
    val index = config.rounds.indexOfFirst { it.id == id }
    if (index < 0) return
    config.rounds = config.rounds.toMutableList().also { it[index] = it[index].withSmall(maxOf(1, newSmallValue)) }
    updateViewModels()
  }

  fun registerViewModel(viewModelToAdd: PokerTimerViewModel) {
    if (!viewModels.contains(viewModelToAdd)) {
      viewModels = viewModels + viewModelToAdd.also { it.initData(this) }
      Log.v("TimerService", "registered new viewModel $viewModelToAdd")
    }
  }

  fun unregisterViewModel(viewModelToRemove: PokerTimerViewModel) {
    viewModels = viewModels.filter { it != viewModelToRemove }
  }

  private fun updateViewModels() {
    Log.v("TimerService", "updateViewModels triggered")
    viewModels.forEach { it.update(this) }
  }
}
