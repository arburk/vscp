package com.github.arburk.vscp.app.common

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.settings.pref_key_sound_next_round
import com.github.arburk.vscp.app.settings.pref_key_sound_warning_of_next_round
import androidx.core.net.toUri

class PreferenceManagerWrapper {

  companion object {
    fun getWarningNotificationSound(context: Context): Uri {
      return getRingtoneUri(context, pref_key_sound_warning_of_next_round).also {
        Log.v("PreferenceManagerWrapper", "WarningNotificationSound: $it")
      }
    }

    fun getChannelNotificationSound(context: Context): Uri {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.getSystemService(context, NotificationManager::class.java)!!
          .getNotificationChannel(context.getString(R.string.notification_channel_id))?.sound?.also {
            Log.v("PreferenceManagerWrapper", "ChannelNotificationSound (from channel): $it")
            return it
          }
      }
      return getRingtoneUri(context, pref_key_sound_next_round).also {
        Log.v("PreferenceManagerWrapper", "ChannelNotificationSound (from prefs): $it")
      }
    }

    private fun getRingtoneUri(context: Context, key: String): Uri {
      PreferenceManager.getDefaultSharedPreferences(context)
        .getString(key, null)?.takeIf { it.isNotBlank() }?.let { return it.toUri() }

      if (key == pref_key_sound_warning_of_next_round) {
        return (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.one_minute_warning).toUri()
      }
      return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }
  }
}
