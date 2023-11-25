package com.example.lysnclient.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.adapters.WbtQuestionPagerAdapter
import com.example.lysnclient.databinding.ActivityWbtQuestionsBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.viewmodel.WBTQuestionsViewModel
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This class is used for display WBT questions and loads WBT Question fragment screen
 */
class WBTQuestionsActivity : BaseActivity() {

    private lateinit var mWBTQuestionsBinding: ActivityWbtQuestionsBinding
    private val mViewModel: WBTQuestionsViewModel by viewModel()
    private var isFromSignUpScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_wbt_questions)
        setup()
    }

    override fun setup() {
        isFromSignUpScreen = intent.getBooleanExtra(
            AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, false
        )
        mWBTQuestionsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_wbt_questions)
        mView = mWBTQuestionsBinding.assessmentQueLayout
        mWBTQuestionsBinding.lifecycleOwner = this
        mWBTQuestionsBinding.viewModel = mViewModel
        mWBTQuestionsBinding.answerMap = mViewModel.mapQuePosAndAnswerDetails
        mWBTQuestionsBinding.viewPagerQuestions.isUserInputEnabled = false
        mViewModel.getWBTQuestionList()
        mWBTQuestionsBinding.viewPagerQuestions.adapter = WbtQuestionPagerAdapter(this, mViewModel)

        mViewModel.moveToNextQues.observe(this, Observer {
            if (it != null && it) {
                moveToNextQuestion()
            }
        })

        mViewModel.moveToPreviousQues.observe(this, Observer {
            if (it != null && it) {
                moveToPreviousQuestion()
            }
        })

        mViewModel.onCloseBtnClickObservable.observe(this, Observer {
            if (it != null && it) {
                showExitConfirmDialog(
                    getString(R.string.exit_wbt_title),
                    getString(R.string.exit_wbt_message)
                ) {
                    navigateOnCloseAndBackButton()
                }
            }
        })
    }

    private fun navigateOnCloseAndBackButton() {
        addEventStopWBTQuestion()
        if (isFromSignUpScreen) {
            launchHomeActivity()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        showExitConfirmDialog(
            getString(R.string.exit_wbt_title),
            getString(R.string.exit_wbt_message)
        ) {
            navigateOnCloseAndBackButton()
        }
    }

    private fun addEventStopWBTQuestion() {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            mViewModel.mWBTQuestionList[mViewModel.currentQuestionIndex.value ?: 0].question
        )
        jsonEvent.put(
            MixPanelData.KEY_QUESTION_NUMBER,
            mViewModel.currentQuestionIndex.value?.plus(1)
        )
        MixPanelData.getInstance(this)
            .addEvent(jsonEvent, MixPanelData.eventStopWBTQuestion)
    }

    private fun moveToNextQuestion() {
        // Check whether its answering first time or updating the answer
        if (!mViewModel.listAnsweredQuePosition.contains(mViewModel.currentQuestionIndex.value)) {
            mViewModel.listAnsweredQuePosition.add(
                mViewModel.currentQuestionIndex.value ?: 0
            )
            addQueAnsweredEvent(
                mViewModel.currentQuestionIndex.value ?: 0,
                false,
                MixPanelData.eventWBTQuestionAnswered
            )
        } else {
            if (mViewModel.previouslySelectedAnswerValue !=
                mViewModel.mapQuePosAndAnswerDetails[mViewModel.currentQuestionIndex.value
                    ?: 0]?.mWBTSeekBarValue
            ) {
                addQueAnsweredEvent(
                    mViewModel.currentQuestionIndex.value ?: 0,
                    true,
                    MixPanelData.eventWBTAnswerEdited
                )
            }
        }
        val currentItem = mWBTQuestionsBinding.viewPagerQuestions.currentItem + 1
        if (currentItem < mViewModel.totalQuestion.value ?: 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewModel.currentQuestionIndex.value = currentItem
            }, 10)
            mWBTQuestionsBinding.viewPagerQuestions.currentItem = currentItem
        } else {
            submitUserResponse()
        }
    }

    private fun moveToPreviousQuestion() {
        val currentItem = mWBTQuestionsBinding.viewPagerQuestions.currentItem
        if (currentItem > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewModel.currentQuestionIndex.value = currentItem - 1
            }, 10)
            mWBTQuestionsBinding.viewPagerQuestions.currentItem = currentItem - 1
        } else {
            // showing alert dialog before dismissing question screen
            showExitConfirmDialog(
                getString(R.string.title_wellbeing_tracker),
                getString(R.string.navigate_back_message),
                getString(R.string.continues)
            )
            {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun addQueAnsweredEvent(
        questionIndex: Int,
        isUpdatedAnswer: Boolean,
        eventName: String
    ) {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            mViewModel.mWBTQuestionList[questionIndex].question
        )
        jsonEvent.put(
            MixPanelData.SLIDER_VALUE,
            mViewModel.mapQuePosAndAnswerDetails[questionIndex]?.mWBTSeekBarValue
        )
        if (isUpdatedAnswer) {
            jsonEvent.put(
                MixPanelData.KEY_PREVIOUS_ANSWER,
                mViewModel.previouslySelectedAnswerValue
            )
            jsonEvent.put(
                MixPanelData.KEY_New_ANSWER,
                mViewModel.mapQuePosAndAnswerDetails[questionIndex]?.answerLabel?.value.toString()
            )
        } else {
            jsonEvent.put(
                MixPanelData.KEY_ANSWER,
                mViewModel.mapQuePosAndAnswerDetails[questionIndex]?.answerLabel?.value.toString()
            )
        }
        jsonEvent.put(
            MixPanelData.KEY_QUESTION_NUMBER,
            questionIndex + 1
        )
        MixPanelData.getInstance(this).addEvent(jsonEvent, eventName)
    }

    private fun submitUserResponse() {
        showLoading()
        mViewModel.submitUserWBTResponse(
            PreferenceUtil.getInstance(this).getValue(PreferenceUtil.KEY_USER_ID, 0)
        ).observe(
            this,
            Observer { response ->
                if (response.status != ResponseStatus.SUCCESS ||
                    response.status != ResponseStatus.SUCCESS_201
                ) {
                    hideLoading()
                }
                when (response.status) {
                    ResponseStatus.CONFLICT_USER_INPUTS, ResponseStatus.BAD_PARAMS -> {
                        showSnackMsg(response.message)
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.SUCCESS, ResponseStatus.SUCCESS_201 -> {
                        fetchWbtOutputScreenData()
                    }
                    ResponseStatus.BAD_INPUT_500 -> {
                        showAlertDialogWithOK(
                            getString(R.string.title_wellbeing_tracker),
                            getString(R.string.not_have_privilege_message)
                        ) {}
                    }
                    else -> {
                        showSnackMsg(response.message)
                    }
                }
            })
    }

    private fun fetchWbtOutputScreenData() {
        mViewModel.getWBTOutPutScreenData(
            PreferenceUtil.getInstance(this).getValue(PreferenceUtil.KEY_USER_ID, 0)
        ).observe(this, Observer { response ->
            hideLoading()
            when (response.status) {
                ResponseStatus.SUCCESS -> {
                    if (response.apiResponse?.mWBTOutputObservation?.insightsMessages?.isNotEmpty() == true) {

                        val intent = Intent(this, WBTOutputScreenActivity::class.java)
                        intent.putExtra(
                            AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, isFromSignUpScreen
                        )
                        if (!isFromSignUpScreen) {
                            startActivityForResult(intent, AppConstants.REQUEST_CODE_START_ACTIVITY)
                        } else {
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        showAlertDialogWithOK(
                            getString(R.string.title_wellbeing_tracker),
                            getString(R.string.interpretations_not_found_message)
                        ) {}
                    }
                }
                ResponseStatus.NO_INTERNET -> {
                    showNoInternetDialog()
                }
                ResponseStatus.FAILURE -> {
                    showSnackMsg(response.message)
                }
                ResponseStatus.BAD_PARAMS -> {
                    showSnackMsg(response.message)
                }
                else -> {
                    showSnackMsg(response.message)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_START_ACTIVITY && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
