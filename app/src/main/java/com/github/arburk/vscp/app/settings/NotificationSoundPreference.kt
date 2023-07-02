package com.github.arburk.vscp.app.settings

import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import androidx.preference.Preference
import com.github.arburk.vscp.app.R


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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.getText(R.string.package_name))
        putExtra(Settings.EXTRA_CHANNEL_ID, context.getText(R.string.notification_channel_id))
      }

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        intent.putExtra(Settings.EXTRA_CHANNEL_FILTER_LIST, NotificationChannel.EDIT_SOUND)
      }

      context.startActivity(intent)
    }



/*
    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri)
    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, RingtoneManager.TITLE_COLUMN_INDEX)
    */
    return true
  }

}