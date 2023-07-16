package com.github.arburk.vscp.app.settings

import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import androidx.preference.Preference
import com.github.arburk.vscp.app.R

class NotificationPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs),
  Preference.OnPreferenceClickListener {

  init {
    onPreferenceClickListener = this
  }

  override fun onPreferenceClick(preference: Preference): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.getText(R.string.package_name))
        putExtra(Settings.EXTRA_CHANNEL_ID, context.getText(R.string.notification_channel_id))
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        intent.putExtra(Settings.EXTRA_CHANNEL_FILTER_LIST, NotificationChannel.EDIT_SOUND)
      }

      context.startActivity(intent)
    }

    return true
  }

}