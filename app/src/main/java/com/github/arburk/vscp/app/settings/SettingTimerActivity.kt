package com.github.arburk.vscp.app.settings

import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.arburk.vscp.app.R

class SettingTimerActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.settings_activity)
    if (savedInstanceState == null) {
      supportFragmentManager
        .beginTransaction()
        .replace(R.id.settings, SettingsFragment())
        .commit()
    }
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
  }

  class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.root_preferences, rootKey)
      setPreferencesTypes()
    }

    private fun setPreferencesTypes() {
      setFieldToNumberInput("min_per_round")
      setFieldToNumberInput("min_per_warning")

      setSoundPicker("sound_next_round")
      setSoundPicker("sound_warning_of_next_round")
    }

    private fun setFieldToNumberInput(prefName: String) {
      val prefField: EditTextPreference? = findPreference(prefName)
      Log.i("Settings", "setFieldToNumberInput for ${prefName}")
      prefField?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
      }
    }

    private fun setSoundPicker(prefName: String) {
      // TODO: implement sound feature instead of disabling field
      val prefField: Preference? = findPreference(prefName)
      prefField?.isVisible = false
    }



    override fun onConfigurationChanged(newConfig: Configuration) {
      super.onConfigurationChanged(newConfig)
      Log.v("SettingTimerActivity", "onConfigurationChanged ${newConfig}")

    // TODO: update timer via initConfig
    }
  }



}