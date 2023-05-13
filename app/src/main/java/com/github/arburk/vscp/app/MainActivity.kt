package com.github.arburk.vscp.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.arburk.vscp.app.databinding.ActivityMainBinding
import com.github.arburk.vscp.app.settings.SettingTimerActivity
import kotlin.system.exitProcess

private const val vscp_url = "http://vscp.ch"

class MainActivity : AppCompatActivity() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolbar)

    // adding navigation to when changing activity
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    Log.i("Lifecycle","onCreateOptionsMenu $menu" )
    menuInflater.inflate(R.menu.menu_main, menu)

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    Log.i("Lifecycle","onOptionsItemSelected $item" )
    return when (item.itemId) {
      R.id.action_settings ->  {
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

}