package com.example.lysnclient.viewmodel

import androidx.lifecycle.ViewModel

class AssessmentSubmittedViewModel : ViewModel() {
    var navigateBackOnCloseObservable = SingleLiveEvent<Boolean>()
    var onFindPsychologistObservable = SingleLiveEvent<Boolean>()

    fun navigateOnClose() {
        navigateBackOnCloseObservable.value = true
    }

    fun onBtnFindPsychologistClick() {
        onFindPsychologistObservable.value = true
    }
}
