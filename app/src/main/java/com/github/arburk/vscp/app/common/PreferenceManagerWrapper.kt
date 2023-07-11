package com.github.arburk.vscp.app.common

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.arburk.vscp.app.settings.pref_key_min_per_warning
import com.github.arburk.vscp.app.settings.pref_key_sound_next_round

class PreferenceManagerWrapper {

  companion object {
    fun getWarningNotificationSound(context: Context): Uri {
      return getRingtoneUri(context, pref_key_min_per_warning).also {
        Log.v("PreferenceManagerWrapper", "WarningNotificationSound: $it") }
    }

    fun getChannelNotificationSound(context: Context): Uri {
      return getRingtoneUri(context, pref_key_sound_next_round).also {
        Log.v("PreferenceManagerWrapper", "ChannelNotificationSound: $it") }
    }

    private fun getRingtoneUri(context: Context, key: String): Uri {
      PreferenceManager.getDefaultSharedPreferences(context)
        .getString(key, null)?.also {
        if (it.isNotBlank()) {
          return Uri.parse(it)
        }
      }
      return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }
  }
}
