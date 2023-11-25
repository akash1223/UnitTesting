package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.repository.AppRepository

class WBTOutputScreenViewModel(val appRepository: AppRepository) : ViewModel() {

    var currentQuestionIndex = MutableLiveData<Int>()
    var listOfInterpretation: List<String> = ArrayList()
    var interpretationText = SingleLiveEvent<String>()
    var btnContinueToHomeObservable = SingleLiveEvent<Boolean>()
    var btnFindPsychologistObservable = SingleLiveEvent<Boolean>()

    init {
        currentQuestionIndex.value = 0
    }

    fun getWBTInterpretationList() {
        listOfInterpretation = appRepository.getWBTInterpretation()
        if (listOfInterpretation.isNotEmpty())
            interpretationText.value = listOfInterpretation[currentQuestionIndex.value ?: 0]
    }

    fun btnBackOnclick() {
        if (0 < currentQuestionIndex.value ?: 0) {
            currentQuestionIndex.value = currentQuestionIndex.value?.minus(1)
            interpretationText.value = listOfInterpretation.get(currentQuestionIndex.value ?: 0)
        }
    }

    fun btnNextOnClick() {
        if (currentQuestionIndex.value ?: 0 < listOfInterpretation.size - 1) {
            currentQuestionIndex.value = currentQuestionIndex.value?.plus(1)
            interpretationText.value = listOfInterpretation.get(currentQuestionIndex.value ?: 0)
        }
    }

    fun btnBackContinueToHomeClick() {
        btnContinueToHomeObservable.value = true
    }

    fun btnFindPsychologistClick() {
        btnFindPsychologistObservable.value = true
    }
}
