package com.github.arburk.vscp.app.settings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.github.arburk.vscp.app.R

class BlindSettingsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    Log.v(javaClass.simpleName, "onCreate")
    super.onCreate(savedInstanceState)
    setContentView(R.layout.blind_settings_activity)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  class SettingsFragment : PreferenceFragmentCompat() {

    init {
      Log.v(javaClass.simpleName, "BlindSettingsActivity#SettingsFragment")
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      Log.v(javaClass.simpleName, "onCreatePreferences")
     /* R.id.nav_host_fragment_content_main
      setPreferencesFromResource(R.xml.root_preferences, rootKey)*/
    }
  }
}