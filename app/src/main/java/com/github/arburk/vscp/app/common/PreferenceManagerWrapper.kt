package com.github.arburk.vscp.app.common

import android.app.NotificationManager
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

class PreferenceManagerWrapper {

  companion object {
    fun getWarningNotificationSound(context: Context): Uri {
      return getRingtoneUri(context, pref_key_sound_warning_of_next_round).also {
        Log.v("PreferenceManagerWrapper", "WarningNotificationSound: $it")
      }
    }

    fun getChannelNotificationSound(context: Context): Uri {
      // TODO: use app defined on if nothing set
      //  Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.mytone);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.getSystemService(context, NotificationManager::class.java)!!
          .getNotificationChannel(context.getString(R.string.notification_channel_id))?.sound.also {
            Log.v("PreferenceManagerWrapper", "ChannelNotificationSound: $it")
            if (it != null)
              return it
          }
      }

      return getRingtoneUri(context, pref_key_sound_next_round).also {
        Log.v("PreferenceManagerWrapper", "ChannelNotificationSound: $it")
      }
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
