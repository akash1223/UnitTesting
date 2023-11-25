package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.adapters.AssessmentQuePagerAdapter
import com.example.lysnclient.databinding.ActivityAssessmentQuestionsBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.viewmodel.AssessmentQuestionViewModel
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class AssessmentQuestionsActivity : BaseActivity() {
    private lateinit var activityAssQuestionBinding: ActivityAssessmentQuestionsBinding
    private val viewModel: AssessmentQuestionViewModel by viewModel()
    private var selectedAssessmentId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_questions)
        selectedAssessmentId = intent.getIntExtra(AppConstants.INTENT_ASSESSMENT_ID, 0)
        setup()
    }

    override fun setup() {
        activityAssQuestionBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_assessment_questions)
        mView = activityAssQuestionBinding.assessmentQueLayout
        activityAssQuestionBinding.lifecycleOwner = this
        activityAssQuestionBinding.viewModel = viewModel
        viewModel.fetchAssessmentQueList(selectedAssessmentId)
        activityAssQuestionBinding.viewPagerQuestions.isUserInputEnabled = false
        activityAssQuestionBinding.viewPagerQuestions.adapter =
            AssessmentQuePagerAdapter(this, viewModel)

        viewModel.moveToNextQues.observe(this, Observer {
            if (it != null && it) {
                val currentItem = activityAssQuestionBinding.viewPagerQuestions.currentItem + 1
                moveToNextQuestion(currentItem)
            }
        })

        viewModel.moveToPreviousQues.observe(this, Observer {
            if (it != null && it) {
                val currentItem = activityAssQuestionBinding.viewPagerQuestions.currentItem
                if (currentItem > 0)
                    activityAssQuestionBinding.viewPagerQuestions.currentItem =
                        currentItem - 1
                else {
                    // showing alert dialog before dismissing question screen
                    showExitConfirmDialog(
                        getString(R.string.assessment),
                        getString(R.string.navigate_back_message),
                        getString(R.string.continues)
                    )
                    {
                        super.onBackPressed()
                    }
                }
            }
        })

        viewModel.onCloseBtnClickObservable.observe(this, Observer {
            if (it != null && it) {
                showExitConfirmDialog(
                    getString(R.string.exit_assessment_title),
                    getString(R.string.exit_assessment_message)
                ) {
                    addStopAssessmentMixPanelEvent()
                    super.onBackPressed()
                }
            }
        })
    }

    private fun moveToNextQuestion(currentItem: Int) {
        if (currentItem < viewModel.totalQuestionNumber.value ?: 0) {
            activityAssQuestionBinding.viewPagerQuestions.currentItem =
                currentItem
        } else {
            val listOfAnswer = ArrayList<AssessmentAnswer>()
            viewModel.mapQuePosAndAnswerDetails.forEach { (_, value) ->
                listOfAnswer.add(value)
            }
            val intent = Intent(this, ReviewAssessmentActivity::class.java)
            intent.putExtra(AppConstants.INTENT_ASSESSMENT_ID, selectedAssessmentId)
            intent.putExtra(
                AppConstants.INTENT_ASSESSMENT_CODE,
                viewModel.selectedAssessmentDetails?.code.toString()
            )
            intent.putExtra(
                AppConstants.INTENT_ASSESSMENT_TITLE,
                viewModel.selectedAssessmentDetails?.name.toString() + " - " +
                        viewModel.selectedAssessmentDetails?.code.toString()
            )
            intent.putParcelableArrayListExtra(
                AppConstants.INTENT_List_OF_ASSESSMENT_ANSWER,
                listOfAnswer
            )
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {

        showExitConfirmDialog(
            getString(R.string.exit_assessment_title),
            getString(R.string.exit_assessment_message)
        ) {
            addStopAssessmentMixPanelEvent()
            super.onBackPressed()
        }
    }

    private fun addStopAssessmentMixPanelEvent() {
        val assessmentEvent = JSONObject()
        assessmentEvent.put(
            MixPanelData.KEY_ASSESSMENT_CODE,
            viewModel.selectedAssessmentDetails?.code.toString()
        )
        assessmentEvent.put(
            MixPanelData.KEY_QUESTION,
            viewModel.listOfQuestion[viewModel.currentQuestionNumber.value ?: 0].label
        )
        MixPanelData.getInstance(this)
            .addEvent(assessmentEvent, MixPanelData.eventStoppedAssessment)
    }
}
