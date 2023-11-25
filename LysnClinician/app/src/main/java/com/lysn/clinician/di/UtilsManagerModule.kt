package com.lysn.clinician.di

import com.lysn.clinician.ui.video_session.VideoSessionActivity
import com.lysn.clinician.ui.video_session.chat.ChannelManager
import com.lysn.clinician.ui.video_session.chat.ChatClientManager
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.NetworkManager
import com.lysn.clinician.utils.PreferenceUtil
import org.koin.core.qualifier.named
import org.koin.dsl.module


val UtilsManagerModule = module {
    single { NetworkManager(get()) }
    single { LocalizeTextProvider(get()) }
    single { PreferenceUtil(get()) }

    //chat classes

    scope(named(AppConstants.VIDEO_SESSION_SCOPED_NAME)) {
        scoped { ChatClientManager(get()) }
        scoped { ChannelManager(get()) }
    }
}