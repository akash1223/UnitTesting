package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants

class ReviewAssessmentViewModel(private val appRepository: AppRepository) : ViewModel() {
    var navigateBackOnCloseObservable = SingleLiveEvent<Boolean>()
    var onSubmitAssessmentObservable = SingleLiveEvent<Boolean>()
    val isCheck = MutableLiveData<Boolean>(false)
    private var mLastClickTime: Long = 0

    var listOfAssessmentAnswer = MutableLiveData<ArrayList<AssessmentAnswer>>()
    var editItemPositionObservable = SingleLiveEvent<Int>()
    var listOfQuestion: List<AssessmentQuestion> = ArrayList()
    var selectedAssessmentId: Int = 0

    fun navigateOnClose() {
        navigateBackOnCloseObservable.value = true
    }

    fun onBtnSubmitAssessmentClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onSubmitAssessmentObservable.value = true
    }

    fun executeSubmitAssessmentApi(assessmentDataRequest: AssessmentDataRequest)
            : MutableLiveData<BaseResponse<SubmitAssessmentResponse>> {
        return appRepository.submitAssessmentAPI(assessmentDataRequest)
    }

    fun onEditItemClick(itemPosition: Int) {
        editItemPositionObservable.value = itemPosition
    }

    fun setListOfAssessmentAnswers(listOfAns: ArrayList<AssessmentAnswer>) {
        listOfAssessmentAnswer.value = listOfAns
    }

    fun setAssessmentId(id: Int) {
        selectedAssessmentId = id
        fetchAssessmentQueList(selectedAssessmentId)
    }

    private fun fetchAssessmentQueList(assessmentId: Int) {
        listOfQuestion = appRepository.getAssessmentQuestionsById(assessmentId)
    }

    fun fetchSelectedQuestionOptions(questionId: Int): ArrayList<OptionType> {
        for (item in listOfQuestion) {
            if (item.id == questionId) return item.listOfOptions
        }
        return ArrayList()
    }
}