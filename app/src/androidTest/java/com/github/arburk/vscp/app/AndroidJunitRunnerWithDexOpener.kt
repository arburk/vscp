package com.github.arburk.vscp.app

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.test.runner.AndroidJUnitRunner
import com.github.tmurakami.dexopener.DexOpener

class AndroidJunitRunnerWithDexOpener : AndroidJUnitRunner() {
  override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      // MockK supports for mocking final classes on Android 9+.
      DexOpener.install(this)
    }
    return super.newApplication(cl, className, context)
  }
}