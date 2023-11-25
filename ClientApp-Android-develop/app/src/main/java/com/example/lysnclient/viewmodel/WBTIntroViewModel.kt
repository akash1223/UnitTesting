package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.model.ConfigurationData
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants

class WBTIntroViewModel(val appRepository: AppRepository) : ViewModel() {
    var onStartWBTBtnObservable = SingleLiveEvent<Boolean>()
    var continueToHomeClickObservable = SingleLiveEvent<Boolean>()
    var learnMoreClickObservable = SingleLiveEvent<Boolean>()
    var mConfigurationData = MutableLiveData<BaseResponse<ConfigurationData>>()

    private var mLastClickTime: Long = 0

    fun onBtnStartWbtBtnClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onStartWBTBtnObservable.value = true
    }

    fun onBtnContinueToHomeClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        continueToHomeClickObservable.value = true
    }

    fun onBtnLearnMoreClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        learnMoreClickObservable.value = true
    }

    fun getWBTQuestionData() {
        mConfigurationData = appRepository.getWBTQuestions()
    }
}