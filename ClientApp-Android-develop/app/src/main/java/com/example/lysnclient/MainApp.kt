package com.example.lysnclient

import android.app.Application
import com.example.lysnclient.di.mainModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(
            this@MainApp, mainModule
        )
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // will call when refactor the application package
        // we need to generate hashcode using package name (appSignatureHelper.getAppSignatures()).
/*
        val appSignatureHelper = AppSignatureUtil(this)
        appSignatureHelper.getAppSignatures()
        Timber.d("App sign ${appSignatureHelper.getAppSignatures()[0]} ")
*/
    }
}