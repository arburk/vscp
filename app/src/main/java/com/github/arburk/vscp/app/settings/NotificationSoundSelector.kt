package com.github.arburk.vscp.app.settings

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.preference.Preference
import androidx.preference.PreferenceManager


class NotificationSoundSelector(context: Context, attrs: AttributeSet?) : Preference(context, attrs),
  Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

  private var _ringtone: Uri? = null
  val ringtone: Uri? get() = _ringtone

  var ringtoneSelectorLauncher: ActivityResultLauncher<Int>? = null

  init {
    Log.v("NotificationSoundSelector", "configure $key")
    onPreferenceClickListener = this

    // TODO add a PreferenceManagerClass handling those things

    PreferenceManager.getDefaultSharedPreferences(context)
      .getString(key, _ringtone?.toString())?.also {
        if (it.isNotBlank()) {
          _ringtone = Uri.parse(it)
          Log.v("NotificationSoundSelector", "initialized to $_ringtone")
        }
      }
  }

  fun updateRingtone(uri: Uri) {
    Log.v("NotificationSoundSelector","update $key to $uri")
    _ringtone = uri
    PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
      putString(key, uri.toString())
      apply()
    }
  }

  override fun onPreferenceClick(preference: Preference): Boolean {
    ringtoneSelectorLauncher?.launch(RingtoneManager.TYPE_NOTIFICATION)
    return true
  }

  override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
    Log.v("NotificationSoundSelector", "onPreferenceChange: $newValue")
    return true
  }

}