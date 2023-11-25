package com.lysn.clinician

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.lysn.clinician.di.UtilsManagerModule
import com.lysn.clinician.di.httpModule
import com.lysn.clinician.di.repositoryModule
import com.lysn.clinician.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber


class MainApp : Application() {

    companion object {
        private var INSTANCE: MainApp? = null
        fun instance() = INSTANCE
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
        startKoin {
            androidLogger()
            androidContext(this@MainApp)
            modules(listOf(httpModule, UtilsManagerModule, viewModelModule, repositoryModule))
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}