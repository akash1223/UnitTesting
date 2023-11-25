package com.lysn.clinician.ui.video_session.chat

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.lysn.clinician.utils.AppConstants
import com.twilio.chat.*


class ChannelManager(private val chatClientManager: ChatClientManager?) {
    var generalChannel : Channel? =null

    var channelLoadFinished =  MutableLiveData<Boolean>(false)
    private var channelsObject: Channels? = null

    fun joinOrCreateGeneralChannelWithCompletion(
        defaultChannelUniqueName: String,
        listener: StatusListener
    ) {
        channelsObject = chatClientManager?.getChatClient()!!.channels
        channelsObject!!.getChannel(
            defaultChannelUniqueName,
            object : CallbackListener<Channel>() {
                override fun onSuccess(channel: Channel) {
                    if (channel != null) {
                        joinGeneralChannelWithCompletion(channel,listener)
                    } else {
                        createGeneralChannelWithCompletion(defaultChannelUniqueName, listener)
                    }
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    if (errorInfo?.code == 50300)
                        createGeneralChannelWithCompletion(defaultChannelUniqueName, listener)
                    else
                        listener.onError(errorInfo)
                }
            })
    }

    private fun joinGeneralChannelWithCompletion(channel: Channel,listener: StatusListener) {
        if (channel!!.status == Channel.ChannelStatus.JOINED) {
            generalChannel = channel
            listener.onSuccess()
            return
        }
        channel!!.join(object : StatusListener() {
            override fun onSuccess() {
                generalChannel = channel
                listener.onSuccess()
            }

            override fun onError(errorInfo: ErrorInfo) {
                generalChannel = channel
                listener.onError(errorInfo)
            }
        })
    }

    fun joinChannel(channel: Channel,listener: StatusListener)
    {
        channel!!.join(object : StatusListener() {
            override fun onSuccess() {
                generalChannel = channel
                listener.onSuccess()
            }

            override fun onError(errorInfo: ErrorInfo) {
                generalChannel = channel
                listener.onError(errorInfo)
            }
        })
    }

    private fun createGeneralChannelWithCompletion(
        defaultChannelUniqueName: String,
        listener: StatusListener
    ) {
        channelsObject?.let {
            it.channelBuilder()
                .withFriendlyName(AppConstants.CHAT_CHANNEL_NAME_PREFIX + defaultChannelUniqueName)
                .withUniqueName(defaultChannelUniqueName)
                .withType(Channel.ChannelType.PUBLIC)
                .build(object : CallbackListener<Channel>() {
                    override fun onSuccess(channel: Channel) {
                        joinGeneralChannelWithCompletion(channel,listener)
                    }

                    override fun onError(errorInfo: ErrorInfo) {
                        listener.onError(errorInfo)
                    }
                })
        }
    }

    fun shutdown()
    {
        channelLoadFinished.value = false
        channelsObject = null
        generalChannel = null
    }




}