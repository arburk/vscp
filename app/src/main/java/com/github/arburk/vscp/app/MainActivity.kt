package com.github.arburk.vscp.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.withStarted
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.arburk.vscp.app.activity.PokerTimer
import com.github.arburk.vscp.app.databinding.ActivityMainBinding
import com.github.arburk.vscp.app.service.NotificationManagerWrapper
import com.github.arburk.vscp.app.service.TimerService
import com.github.arburk.vscp.app.settings.AppSettingsActivity
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding
  private lateinit var _timerService: TimerService
  lateinit var permissionActivity : ActivityResultLauncher<String>

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
    deepNavigationHandler(navController)
    registerNotificationService()
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

  private fun registerNotificationService() {
    NotificationManagerWrapper().also {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && it.isLackOfNotificationPermission(this)) {
        // Create activity to ask for permission to post notifications
        permissionActivity = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
      }
    }
  }
}