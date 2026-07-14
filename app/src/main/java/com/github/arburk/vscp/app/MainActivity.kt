package com.github.arburk.vscp.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.arburk.vscp.app.activity.PokerTimer
import com.github.arburk.vscp.app.databinding.ActivityMainBinding
import com.github.arburk.vscp.app.service.TimerService
import com.github.arburk.vscp.app.settings.AppSettingsActivity

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding
  lateinit var permissionActivity: ActivityResultLauncher<String>

  private val _timerServiceLiveData = MutableLiveData<TimerService?>()
  val timerServiceLiveData: LiveData<TimerService?> = _timerServiceLiveData
  val timerService: TimerService? get() = _timerServiceLiveData.value

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

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
    Log.i("Lifecycle", "onCreateOptionsMenu $menu")
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    Log.i("MainActivity", "Lifecycle: onOptionsItemSelected $item")
    return when (item.itemId) {
      R.id.action_settings -> {
        startActivity(Intent(this, AppSettingsActivity::class.java))
        true
      }
      R.id.action_webpage -> {
        openVscpWebsite()
        true
      }
      R.id.action_exit -> {
        stopService(Intent(this, TimerService::class.java))
        finishAffinity()
        true
      }
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
    return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }

  private val timerServiceConnection = object : ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
      Log.i("MainActivity", "get TimerService")
      _timerServiceLiveData.value = (service as TimerService.TimerServiceBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      Log.i("MainActivity", "disconnected TimerService")
      _timerServiceLiveData.value = null
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.v("MainActivity", "I say goodbye.")
    unbindService(timerServiceConnection)
  }

  private fun registerNotificationService() {
    permissionActivity = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
  }
}
