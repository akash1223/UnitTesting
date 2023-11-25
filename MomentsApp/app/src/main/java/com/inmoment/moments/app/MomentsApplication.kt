package com.inmoment.moments.app

import android.app.Application
import android.os.StrictMode
import com.inmoment.moments.BuildConfig
import com.inmoment.moments.framework.common.Logger
import dagger.hilt.android.HiltAndroidApp

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

@HiltAndroidApp
class MomentsApplication : Application() {

    override fun onCreate() {
        Logger.updateDebugBuildStatus(BuildConfig.DEBUG)
        Logger.init(this)
        enableStrictMode()
        super.onCreate()
    }

    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().build())
        }
    }
}