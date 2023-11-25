package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.model.ConfigurationData
import com.example.lysnclient.repository.AppRepository

class WBTLearnMoreViewModel(val appRepository: AppRepository) : ViewModel() {
    var navigateBackOnCloseObservable = SingleLiveEvent<Boolean>()
    var onStartWBTButtonObservable = SingleLiveEvent<Boolean>()
    var mConfigurationData = MutableLiveData<BaseResponse<ConfigurationData>>()

    fun navigateOnClose() {
        navigateBackOnCloseObservable.value = true
    }

    fun onBtnStartWBTClick() {
        onStartWBTButtonObservable.value = true
    }

    fun getWBTQuestionData() {
        mConfigurationData = appRepository.getWBTQuestions()
    }
}