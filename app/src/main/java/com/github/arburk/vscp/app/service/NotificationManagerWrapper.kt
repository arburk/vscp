package com.github.arburk.vscp.app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.arburk.vscp.app.R
import com.github.arburk.vscp.app.common.PreferenceManagerWrapper

class NotificationManagerWrapper {

  @VisibleForTesting
  internal lateinit var mockedManager: NotificationManager

  fun get(ctx: Context): NotificationManager? {
    if (this::mockedManager.isInitialized) {
      return mockedManager
    }

    return ContextCompat.getSystemService(ctx, NotificationManager::class.java)
  }

  fun createNotificationChannel(ctx: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return // Skip for lower versions

    ctx.getString(R.string.notification_channel_id).also {
      if(notificationChannelMissing(ctx, it)) {
        // After notification channel creation, you cannot change the notification behaviors programmatically.
        // The user has complete control at that point so it is useless to recreate it over and over again
        executeChannelCreation(ctx, it)
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun executeChannelCreation(ctx: Context, channelId: String) {
    NotificationChannel(channelId, ctx.getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH)
      .apply {
        description = ctx.getString(R.string.channel_description)
        enableLights(true)
        lightColor = Color.RED
        setSound(
          PreferenceManagerWrapper.getChannelNotificationSound(ctx),
          AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        )
      }.also {
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        get(ctx)!!.createNotificationChannel(it)
      }
    Log.v("MainActivity", "$channelId created")
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun notificationChannelMissing(ctx: Context, channelId: String): Boolean {
    return get(ctx)!!.getNotificationChannel(channelId) == null
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  fun isLackOfNotificationPermission(ctx: Context) =
    (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED)

}