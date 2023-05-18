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
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.arburk.vscp.app.databinding.ActivityMainBinding
import com.github.arburk.vscp.app.service.TimerService
import com.github.arburk.vscp.app.settings.SettingTimerActivity
import kotlin.system.exitProcess

const val vscp_url = "http://vscp.ch"

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding
  private lateinit var timerService: TimerService

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
    Log.i("Lifecycle", "onOptionsItemSelected $item")
    return when (item.itemId) {
      R.id.action_settings -> {
        startActivity(Intent(this, SettingTimerActivity::class.java))
        return true
      }

      R.id.action_webpage -> openVscpWebsite()
      R.id.action_exit -> exitProcess(0)
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun openVscpWebsite(): Boolean {
    val vscpIntent = Intent(Intent.ACTION_VIEW)
    vscpIntent.data = Uri.parse(vscp_url)
    startActivity(vscpIntent)
    return true
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()
  }

  /** Defines callbacks for service binding, passed to bindService().  */
  private val timerServiceConnection = object : ServiceConnection {

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
      Log.i("TimerService", "get Service")
      timerService = (service as TimerService.TimerServiceBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
      Log.i("TimerService", "disconnected Service")
      // nothing to be done here
    }
  }

  fun getTimerService() : TimerService = timerService

  override fun onDestroy() {
    super.onDestroy()
    unbindService(timerServiceConnection)
  }
}