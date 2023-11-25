package com.example.lysnclient.di

import com.example.lysnclient.utility.SmsBroadcastReceiver
import com.example.lysnclient.utils.LocalizeTextProvider
import com.example.lysnclient.utils.NetworkManager
import org.koin.dsl.module.module

val UtilsManagerModule = module {
    single { NetworkManager(get()) }
    single { LocalizeTextProvider(get()) }
    single { SmsBroadcastReceiver() }
}