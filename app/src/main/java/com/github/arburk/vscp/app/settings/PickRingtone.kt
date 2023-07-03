package com.github.arburk.vscp.app.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract

class PickRingtone : ActivityResultContract<Int, Uri?>() {

  var selectedRingtone: Uri? = null

  override fun createIntent(context: Context, ringtoneType: Int) =
    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
      putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, ringtoneType)
      if (selectedRingtone != null) {
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtone)
      } else {
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(
          RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
          RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        )
      }
    }

  override fun parseResult(resultCode: Int, result: Intent?): Uri? {
    if (resultCode != Activity.RESULT_OK) {
      return null
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      return result?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
    }

    return result?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
  }
}