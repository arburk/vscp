package com.github.arburk.vscp.app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.arburk.vscp.app.activity.PokerTimer
import com.github.arburk.vscp.app.common.PreferenceManagerWrapper
import com.github.arburk.vscp.app.databinding.ActivityMainBinding
import com.github.arburk.vscp.app.service.TimerService
import com.github.arburk.vscp.app.settings.AppSettingsActivity
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding
  private lateinit var _timerService: TimerService

  val timerService: TimerService get() = _timerService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    // adding navigation to when changing activity
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

    // start & bind the timer service
    applicationContext.startService(Intent(this, TimerService::class.java))
    Intent(this, TimerService::class.java).also { intent ->
      bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE)
    }
    createNotificationChannel()
    deepNavigationHandler(navController)
  }

  private fun deepNavigationHandler(navController: NavController) {
    val menuFragment = intent.getStringExtra("targetFragment")
    if (menuFragment != null && menuFragment == PokerTimer::class.java.simpleName) {
      navController.navigate(R.id.action_MainScreen_to_Timer)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    Log.i("Lifecycle", "onCreateOptionsMenu $menu")
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    Log.i("MainActivity", "Lifecycle: onOptionsItemSelected $item")
    return when (item.itemId) {
      R.id.action_settings -> {
        startActivity(Intent(this, AppSettingsActivity::class.java))
        return true
      }

      R.id.action_webpage -> {
        openVscpWebsite()
        return true
      }

      R.id.action_exit -> exitProcess(0)
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun openVscpWebsite() {
    startActivity(Intent(Intent.ACTION_VIEW).apply {
      data = Uri.parse(getString(R.string.vscp_url))
    })
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()
  }

  /** Defines callbacks for service binding, passed to bindService().  */
  private val timerServiceConnection = object : ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
      Log.i("MainActivity", "get TimerService")
      _timerService = (service as TimerService.TimerServiceBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      Log.i("MainActivity", "disconnected TimerService")
      // nothing to be done here
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.v("MainActivity", "I say goodbye.")
    unbindService(timerServiceConnection)
  }

  private fun createNotificationChannel() {
    Log.v("MainActivity", "createNotificationChannel")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channelId = getString(R.string.notification_channel_id)
      NotificationChannel(
        channelId,
        getString(R.string.channel_name),
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = getString(R.string.channel_description)
      }.also {
        it.setSound(PreferenceManagerWrapper.getChannelNotificationSound(this), null)
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        ContextCompat.getSystemService(this, NotificationManager::class.java)!!
          .createNotificationChannel(it)
      }
      Log.v("MainActivity", "$channelId created")

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Ask for permission to post notifications
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
          != PackageManager.PERMISSION_GRANTED
        ) {
          registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
            .launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      }
    }
  }

}