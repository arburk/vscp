package com.github.arburk.vscp.app.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.preference.PreferenceManager

class PickRingtoneContract : ActivityResultContract<Int, Uri?>() {

  var sharedPrefKeyRingtone: String? = null

  override fun createIntent(context: Context, input: Int) =
    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
      putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, input)
      val selectedRingtone = PreferenceManager.getDefaultSharedPreferences(context)
        .getString(sharedPrefKeyRingtone, null)
      if (!selectedRingtone.isNullOrBlank()) {
        Log.v("PickRingtoneContract", "initialize to $selectedRingtone")
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(selectedRingtone))
      } else {
        Log.v("PickRingtoneContract", "initialize to default")
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(
          RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
          RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        )
      }
    }

  override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
    if (resultCode != Activity.RESULT_OK) {
      return null
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
    } else {
      @Suppress("DEPRECATION") // can be removed with removal of support for Build.VERSION.SDK_INT < 33
      intent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
    }
  }
}