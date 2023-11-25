package com.lysn.clinician.ui.video_session.chat

import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message
import timber.log.Timber

interface ChannelListenerWrapperInterface
{
     fun onMessageAdded(msg: Message?)
}

class ChannelListenerWrapper(private val channelListenerWrapperInterface: ChannelListenerWrapperInterface) : ChannelListener
{
    override fun onMessageAdded(msg: Message?) {
        channelListenerWrapperInterface.onMessageAdded(msg)
    }

    override fun onMessageUpdated(p0: Message?, p1: Message.UpdateReason?) {
        Timber.d("onMessageUpdated")
    }

    override fun onMessageDeleted(p0: Message?) {
        Timber.d("onMessageDeleted")
    }

    override fun onMemberAdded(p0: Member?) {
        Timber.d("onMemberAdded")
    }

    override fun onMemberUpdated(p0: Member?, p1: Member.UpdateReason?) {
        Timber.d("onMemberUpdated")
    }

    override fun onMemberDeleted(p0: Member?) {
        Timber.d("onMemberDeleted")
    }

    override fun onTypingStarted(p0: Channel?, p1: Member?) {
        Timber.d("onTypingEnded")
    }

    override fun onTypingEnded(p0: Channel?, p1: Member?) {
        Timber.d("onTypingEnded")
    }

    override fun onSynchronizationChanged(p0: Channel?) {
        Timber.d("onSynchronizationChanged")
    }

}