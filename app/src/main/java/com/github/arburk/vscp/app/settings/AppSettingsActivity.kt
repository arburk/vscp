package com.github.arburk.vscp.app.settings

import android.net.Uri
import android.os.Build
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
const val pref_key_notify_settings: String = "notification_settings"
const val pref_key_sound_warning_of_next_round: String = "sound_warning_of_next_round"

class AppSettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

  override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
    // Instantiate the new Fragment
    val args = pref.extras
    val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment!!)
    fragment.arguments = args
    supportFragmentManager.beginTransaction()
      .replace(R.id.settings, fragment)
      .addToBackStack(null)
      .commit()
    return true
  }

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

    private val soundSelectionEnabler = SoundSelectionEnabler()

    private val nextLevelPicker =
      registerForActivityResult(PickRingtoneContract().apply {
        sharedPrefKeyRingtone = pref_key_sound_next_round
      }) { uri: Uri? ->
        if (uri != null) {
          applySoundSelection(pref_key_sound_next_round, uri)
          Log.v("AppSettingsActivity", "set nextLevel sound to $uri")
        }
      }

    private val warningPicker =
      registerForActivityResult(PickRingtoneContract().apply {
        sharedPrefKeyRingtone = pref_key_sound_warning_of_next_round
      }) { uri: Uri? ->
        if (uri != null) {
          applySoundSelection(pref_key_sound_warning_of_next_round, uri)
          Log.v("AppSettingsActivity", "set warning sound to $uri")
        }
      }

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

      findPreference<NotificationPreference>(pref_key_notify_settings)?.isVisible =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun applySoundSelection(prefName: String, uri: Uri) {
      findPreference<NotificationSoundSelector>(prefName)?.apply {
        updateRingtone(uri)
      }
    }

    inner class SoundSelectionEnabler : OnPreferenceChangeListener {
      override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val isSoundEnabled = newValue as Boolean
        findPreference<NotificationSoundSelector>(pref_key_sound_next_round)?.apply {
          isEnabled = isSoundEnabled
          ringtoneSelectorLauncher = nextLevelPicker
        }

        findPreference<NotificationSoundSelector>(pref_key_sound_warning_of_next_round)?.apply {
          isEnabled = isSoundEnabled
          ringtoneSelectorLauncher = warningPicker
        }
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

  }
}