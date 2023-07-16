package com.github.arburk.vscp.app.settings

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.github.arburk.vscp.app.R

const val pref_key_min_per_round: String = "min_per_round"
const val pref_key_min_per_warning: String = "min_per_warning"
const val pref_key_notify_settings: String = "notification_settings"
const val pref_key_sound_next_round: String = "sound_next_round"
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

    private val nextLevelPicker =
      registerForActivityResult(PickRingtoneContract().apply {
        sharedPrefKeyRingtone = pref_key_sound_next_round
      }) { uri: Uri? ->
        if (uri != null) {
          applySoundSelection(pref_key_sound_next_round, uri)
          Log.v("AppSettingsActivity", "set nextLevel sound to $uri")

          // update channel with new sound
          // TODO: does not work - needs further investigation
          // https://stackoverflow.com/questions/55717770/cant-update-sound-programmatically-for-notification-channel-on-android-oreo
          /*
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.getSystemService(requireContext(), NotificationManager::class.java)!!
              .getNotificationChannel(getString(R.string.notification_channel_id))
              .setSound(uri, null)
          }
          */
        }
      }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.root_preferences, rootKey)
      setPreferencesTypes()
    }

    private fun setPreferencesTypes() {
      setFieldToNumberInput(pref_key_min_per_round)
      setFieldToNumberInput(pref_key_min_per_warning)

      findPreference<NotificationPreference>(pref_key_notify_settings)?.isVisible =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

      findPreference<NotificationSoundSelector>(pref_key_sound_next_round)?.apply {
        ringtoneSelectorLauncher = nextLevelPicker
        // beginning of version Build.VERSION_CODES.O this sound is defined on NotificationChannel
        isVisible = (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
      }

      findPreference<NotificationSoundSelector>(pref_key_sound_warning_of_next_round)?.apply {
        ringtoneSelectorLauncher = warningPicker
      }
    }

    private val warningPicker = registerForActivityResult(PickRingtoneContract().apply {
      sharedPrefKeyRingtone = pref_key_sound_warning_of_next_round
    }) { uri: Uri? ->
      if (uri != null) {
        Log.v("AppSettingsActivity", "set warning sound to $uri")
        applySoundSelection(pref_key_sound_warning_of_next_round, uri)
      }
    }

    private fun applySoundSelection(prefName: String, uri: Uri) {
      findPreference<NotificationSoundSelector>(prefName)?.apply {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
          putString(key, uri.toString())
          apply()
        }
      }
    }

    private fun setFieldToNumberInput(prefName: String) {
      val prefField: EditTextPreference? = findPreference(prefName)
      Log.i("Settings", "setFieldToNumberInput for $prefName")
      prefField?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
      }
    }
  }

}
