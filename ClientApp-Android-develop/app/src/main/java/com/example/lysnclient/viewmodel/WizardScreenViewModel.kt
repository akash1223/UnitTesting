package com.example.lysnclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lysnclient.utils.AppConstants

class WizardScreenViewModel : ViewModel() {
    var onWizardContinueEmailObservable = SingleLiveEvent<Boolean>()
    private var mLastClickTime: Long = 0
    fun onBtnContinueEmailClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onWizardContinueEmailObservable.value = true
    }
}

