package com.example.lysnclient.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.model.AssessmentQuestion
import com.example.lysnclient.model.AssessmentType
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import java.util.*
import kotlin.collections.ArrayList

class AssessmentQuestionViewModel(private val appRepository: AppRepository) : ViewModel() {
    var listOfQuestion: List<AssessmentQuestion> = ArrayList()
    var currentQuestionNumber = SingleLiveEvent<Int>()
    var totalQuestionNumber = SingleLiveEvent<Int>()
    var moveToNextQues = SingleLiveEvent<Boolean>()
    var moveToPreviousQues = SingleLiveEvent<Boolean>()
    var mapQuePosAndAnswerDetails: TreeMap<Int, AssessmentAnswer> = TreeMap()
    var listVisitedQuePosition = ArrayList<Int>()
    var listAnsweredQuePosition = ArrayList<Int>()
    var selectedAssessmentDetails: AssessmentType? = null
    val onCloseBtnClickObservable = SingleLiveEvent<Boolean>()
    private var mLastClickTime: Long = 0

    init {
        totalQuestionNumber.value = 1
    }

    fun fetchAssessmentQueList(id: Int) {
        listOfQuestion = appRepository.getAssessmentQuestionsById(id)
        selectedAssessmentDetails = appRepository.getAssessmentById(id)

        if (listOfQuestion.isNotEmpty())
            totalQuestionNumber.value = listOfQuestion.size
    }

    fun onBtnBackClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        moveToPreviousQues.value = true
    }

    fun onBtnCloseClickListener() {
        onCloseBtnClickObservable.value = true
    }
}

