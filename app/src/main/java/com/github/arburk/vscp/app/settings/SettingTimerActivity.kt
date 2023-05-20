package com.github.arburk.vscp.app.settings

import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.github.arburk.vscp.app.R

const val pref_key_min_per_round: String = "min_per_round"
const val pref_key_min_per_warning: String = "min_per_warning"
const val pref_key_sound_enabled: String = "sound_enabled"
const val pref_key_sound_next_round: String = "sound_next_round"
const val pref_key_sound_warning_of_next_round: String = "sound_warning_of_next_round"

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

    val soundSelectionEnabler = SoundSelectionEnabler()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.root_preferences, rootKey)
      setPreferencesTypes()
    }

    private fun setPreferencesTypes() {
      setFieldToNumberInput(pref_key_min_per_round)
      setFieldToNumberInput(pref_key_min_per_warning)

      val soundPreference: CheckBoxPreference? = findPreference(pref_key_sound_enabled)
      soundSelectionEnabler.onPreferenceChange(soundPreference!!, soundPreference.isChecked)
      soundPreference.onPreferenceChangeListener = soundSelectionEnabler
    }

    inner class SoundSelectionEnabler : OnPreferenceChangeListener {
      override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val soundPreference: NotificationSoundPreference? = findPreference(pref_key_sound_next_round)
        val soundWrningPreference: NotificationSoundPreference? = findPreference(pref_key_sound_warning_of_next_round)
        val isEnabled = newValue as Boolean
        soundPreference?.isEnabled = isEnabled
        soundWrningPreference?.isEnabled = isEnabled
        return true
      }
    }

    private fun setFieldToNumberInput(prefName: String) {
      val prefField: EditTextPreference? = findPreference(prefName)
      Log.i("Settings", "setFieldToNumberInput for ${prefName}")
      prefField?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
      }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
      super.onConfigurationChanged(newConfig)
      Log.v("SettingTimerActivity", "onConfigurationChanged ${newConfig}")
      // TODO: update timer via initConfig
    }
  }


}