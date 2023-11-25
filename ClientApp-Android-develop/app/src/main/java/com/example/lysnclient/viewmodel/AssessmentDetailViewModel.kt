package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants

class AssessmentDetailViewModel(private val appRepository: AppRepository) : ViewModel() {
    var assessmentTitleField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var assessmentIDField = MutableLiveData<Int>()
    var assessmentIntroField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var assessmentCodeField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    var assessmentEstimatedTimeField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    private var mLastClickTime: Long = 0
    var onBeginAssessmentObservable = SingleLiveEvent<Boolean>()
    var navigateBackObservable = SingleLiveEvent<Boolean>()
    var selectedAssessmentDetails: AssessmentType? = null

    fun fetchAssessmentDetailById(id: Int) {
        selectedAssessmentDetails = appRepository.getAssessmentById(id)

        assessmentTitleField.value = if (selectedAssessmentDetails?.name.isNullOrEmpty()) {
            AppConstants.NOT_AVAILABLE
        } else {
            selectedAssessmentDetails?.name
        }

        assessmentCodeField.value = if (selectedAssessmentDetails?.code.isNullOrEmpty()) {
            AppConstants.NOT_APPLICABLE
        } else {
            selectedAssessmentDetails?.code
        }

        assessmentIntroField.value = if (selectedAssessmentDetails?.intro.isNullOrEmpty()) {
            AppConstants.NOT_AVAILABLE
        } else {
            selectedAssessmentDetails?.intro
        }

        assessmentEstimatedTimeField.value =
            if (selectedAssessmentDetails?.estimatedTime.isNullOrEmpty()) {
                AppConstants.EMPTY_VALUE
            } else {
                selectedAssessmentDetails?.estimatedTime
            }
        assessmentIDField.value = selectedAssessmentDetails?.id
    }

    fun onBtnBeginAssessmentClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onBeginAssessmentObservable.value = true
    }

    fun navigateBack() {
        navigateBackObservable.value = true
    }
}