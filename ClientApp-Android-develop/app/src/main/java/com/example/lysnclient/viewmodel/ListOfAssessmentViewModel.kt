package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants

class ListOfAssessmentViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var mLastClickTime: Long = 0
    var navigateToDetailObservable = SingleLiveEvent<Int>()
    var listOfAssessmentLiveData = MutableLiveData<BaseResponse<List<AssessmentType>>>()

    init {
        getListOfAssessments()
    }

    private fun getListOfAssessments() {
        listOfAssessmentLiveData = appRepository.executeGetListOfAssessment()
    }

    fun onAssessmentItemClick(itemPosition: Int) {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        listOfAssessmentLiveData.value?.apiResponse?.let {
            navigateToDetailObservable.value = it[itemPosition].id
        }
    }
}
