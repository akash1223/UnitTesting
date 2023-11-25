package com.lysn.clinician.ui.video_session.chat

import androidx.lifecycle.MutableLiveData
import com.lysn.clinician.ui.base.BaseViewModel
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.SingleLiveEvent


class ChatViewModel() : BaseViewModel() {
    var onSendMessageObservable = SingleLiveEvent<Boolean>()
    var isChannelConnected = MutableLiveData<Boolean>(false)
    var inputChatMessage = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    fun onBtnSendMessageClick() {
        onSendMessageObservable.value = true
    }
}
