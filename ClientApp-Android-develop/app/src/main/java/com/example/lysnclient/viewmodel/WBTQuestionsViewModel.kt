package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.R
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList

class WBTQuestionsViewModel(private val appRepository: AppRepository) : ViewModel() {

    var currentQuestionIndex = SingleLiveEvent<Int>()
    var totalQuestion = SingleLiveEvent<Int>()
    var moveToPreviousQues = SingleLiveEvent<Boolean>()
    var moveToNextQues = SingleLiveEvent<Boolean>()
    val onCloseBtnClickObservable = SingleLiveEvent<Boolean>()
    var listVisitedQuePosition = ArrayList<Int>()
    var listAnsweredQuePosition = ArrayList<Int>()
    var mWBTQuestionList: List<WBTQuestion> = ArrayList()
    var mapQuePosAndAnswerDetails: TreeMap<Int, WBTSelectedAnswer> = TreeMap()
    var previouslySelectedAnswerValue = 0
    private var mLastClickTime: Long = 0

    init {
        currentQuestionIndex.value = 0
        totalQuestion.value = 1
    }

    fun getWBTQuestionList() {
        mWBTQuestionList = appRepository.getWBTQuestionList()

        for (index in mWBTQuestionList.indices) {
            val mWBTSelectedAnswer = WBTSelectedAnswer(
                index,
                MutableLiveData(mWBTQuestionList[index].answerOptionList[2].title),
                AppConstants.DEFAULT_WBT_SEEKBAR_VALUE,
                MutableLiveData(R.drawable.ic_face_type_neutral),
                MutableLiveData(R.color.wbtNeutralBgColor),
                MutableLiveData(R.color.wbtNeutralBgColor),
                mWBTQuestionList[index].value
            )
            mapQuePosAndAnswerDetails[index] = mWBTSelectedAnswer
        }
        totalQuestion.value = mWBTQuestionList.size
    }

    fun onBtnNextClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        moveToNextQues.value = true
    }

    fun onBtnPreviousClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        moveToPreviousQues.value = true
    }

    fun setSeekBarProgressValue(progress: Int) {
        currentQuestionIndex.value?.let { queIndex ->
            val optionList = mWBTQuestionList[queIndex].answerOptionList
            val answerLabel: String
            val faceTypeImgId: Int
            val backgroundImgId: Int
            val backgroundColorId: Int

            if (optionList.size > 4) {
                when (progress) {
                    in optionList[0].rangeStart..optionList[0].rangeEnd -> {
                        answerLabel = optionList[0].title
                        faceTypeImgId = R.drawable.ic_face_type_very_unhappy
                        backgroundImgId = R.mipmap.very_unhappy
                        backgroundColorId = R.color.wbtVeryUnhappyBgColor
                    }
                    in optionList[1].rangeStart..optionList[1].rangeEnd -> {
                        answerLabel = optionList[1].title
                        faceTypeImgId = R.drawable.ic_face_type_fairly_unhappy
                        backgroundImgId = R.mipmap.fairly_unhappy
                        backgroundColorId = R.color.wbtFairlyUnhappyBgColor
                    }
                    in optionList[2].rangeStart..optionList[2].rangeEnd -> {
                        answerLabel = optionList[2].title
                        faceTypeImgId = R.drawable.ic_face_type_neutral
                        backgroundImgId = R.color.wbtNeutralBgColor
                        backgroundColorId = R.color.wbtNeutralBgColor
                    }
                    in optionList[3].rangeStart..optionList[3].rangeEnd -> {
                        answerLabel = optionList[3].title
                        faceTypeImgId = R.drawable.ic_face_type_happy
                        backgroundImgId = R.mipmap.happy
                        backgroundColorId = R.color.wbtHappyBgColor
                    }
                    in optionList[4].rangeStart..optionList[4].rangeEnd -> {
                        answerLabel = optionList[4].title
                        faceTypeImgId = R.drawable.ic_face_type_very_happy
                        backgroundImgId = R.mipmap.very_happy
                        backgroundColorId = R.color.wbtVeryHappyBgColor
                    }
                    else -> {
                        answerLabel = AppConstants.EMPTY_VALUE
                        faceTypeImgId = R.drawable.ic_face_type_neutral
                        backgroundImgId = R.color.wbtNeutralBgColor
                        backgroundColorId = R.color.wbtNeutralBgColor
                    }
                }
                val mWBtAnswer = mapQuePosAndAnswerDetails[queIndex]
                if (mWBtAnswer != null) {
                    previouslySelectedAnswerValue = mWBtAnswer.mWBTSeekBarValue
                    mWBtAnswer.answerLabel.value = answerLabel
                    mWBtAnswer.faceTypeImgId.value = faceTypeImgId
                    mWBtAnswer.backgroundImgId.value = backgroundImgId
                    mWBtAnswer.backgroundColorId.value = backgroundColorId
                    mWBtAnswer.mWBTSeekBarValue = progress

                    mapQuePosAndAnswerDetails[queIndex] = mWBtAnswer
                }
            }
        }
    }

    fun onBtnCloseClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onCloseBtnClickObservable.value = true
    }

    fun submitUserWBTResponse(userId: Int): MutableLiveData<BaseResponse<WBTSubmitResponse>> {
        val jsonEvent = JsonObject()
        mapQuePosAndAnswerDetails.forEach { (_, value) ->
            jsonEvent.addProperty(
                value.value,
                value.mWBTSeekBarValue
            )
        }
        return appRepository.submitWBTUserResponse(userId, jsonEvent)
    }

    fun getWBTOutPutScreenData(userId: Int): MutableLiveData<BaseResponse<WBTOutputScreenResponse>> {
        return appRepository.getWBTOutputScreenList(userId)
    }
}

