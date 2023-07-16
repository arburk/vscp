package com.github.arburk.vscp.app.settings

import android.content.Context
import android.media.RingtoneManager
import android.util.AttributeSet
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.preference.Preference


class NotificationSoundSelector(context: Context, attrs: AttributeSet?) : Preference(context, attrs),
  Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

  var ringtoneSelectorLauncher: ActivityResultLauncher<Int>? = null

  init {
    Log.v("NotificationSoundSelector", "configure $key")
    onPreferenceClickListener = this
  }

  override fun onPreferenceClick(preference: Preference): Boolean {
    ringtoneSelectorLauncher?.launch(RingtoneManager.TYPE_NOTIFICATION)
    return true
  }

  override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
    return true
  }

}