package com.github.arburk.vscp.app.settings

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.preference.Preference


class NotificationSoundPreference (context: Context, attrs: AttributeSet?) : Preference(context, attrs),
  Preference.OnPreferenceClickListener {

  private var ringtoneUri: Uri? = null

  init {
    onPreferenceClickListener = this
    Log.v(javaClass.simpleName, "NotificationSoundPreference created")
  }

  override fun onSetInitialValue(defaultValue: Any?) {
    ringtoneUri = getPersistedString(defaultValue as? String)?.let { Uri.parse(it) }
    Log.v(javaClass.simpleName, "onSetInitialValue to '$ringtoneUri'")
  }

  override fun onPreferenceClick(preference: Preference): Boolean {
    Log.v(javaClass.simpleName, "onPreferenceClick for '${preference.key}'")
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, RingtoneManager.TITLE_COLUMN_INDEX)
    context.startActivity(intent)
    return true
  }

}