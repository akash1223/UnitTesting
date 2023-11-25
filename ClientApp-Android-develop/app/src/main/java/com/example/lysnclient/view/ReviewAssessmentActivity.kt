package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.adapters.AdapterReviewAssessmentList
import com.example.lysnclient.databinding.ActivityReviewAssessmentBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.AssessmentAnswerRequest
import com.example.lysnclient.model.AssessmentValueRequest
import com.example.lysnclient.model.AssessmentDataRequest
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.view.dialog.EditSingleChoiceAnsDialog
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.viewmodel.ReviewAssessmentViewModel
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class ReviewAssessmentActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {
    private lateinit var mAdapter: AdapterReviewAssessmentList
    private lateinit var reviewAssessmentBinding: ActivityReviewAssessmentBinding
    private val reviewAssessmentViewModel: ReviewAssessmentViewModel by viewModel()
    private var selectedAssessmentId = 0
    private var selectedAssessmentCode = AppConstants.EMPTY_VALUE
    private var selectedAssessmentTitle = AppConstants.EMPTY_VALUE
    private lateinit var listOfAssessmentAnswer: ArrayList<AssessmentAnswer>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reviewAssessmentBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_review_assessment
        ) as ActivityReviewAssessmentBinding

        selectedAssessmentId = intent.getIntExtra(AppConstants.INTENT_ASSESSMENT_ID, 0)
        selectedAssessmentCode =
            intent.getStringExtra(AppConstants.INTENT_ASSESSMENT_CODE) ?: AppConstants.EMPTY_VALUE
        selectedAssessmentTitle =
            intent.getStringExtra(AppConstants.INTENT_ASSESSMENT_TITLE) ?: AppConstants.EMPTY_VALUE
        listOfAssessmentAnswer =
            intent.getParcelableArrayListExtra<AssessmentAnswer>(AppConstants.INTENT_List_OF_ASSESSMENT_ANSWER)
                ?: ArrayList()

        setup()
    }

    override fun setup() {
        reviewAssessmentBinding.lifecycleOwner = this
        mView = reviewAssessmentBinding.assessmentListLayout
        reviewAssessmentBinding.viewModel = reviewAssessmentViewModel
        reviewAssessmentBinding.txtAssessmentTitle.text = selectedAssessmentTitle
        reviewAssessmentBinding.chkBoxAcceptPolicy.setOnCheckedChangeListener(this)

        mAdapter = AdapterReviewAssessmentList(this, ArrayList(), reviewAssessmentViewModel)
        reviewAssessmentBinding.myAdapter = mAdapter
        reviewAssessmentViewModel.setListOfAssessmentAnswers(listOfAssessmentAnswer)
        reviewAssessmentViewModel.setAssessmentId(selectedAssessmentId)

        reviewAssessmentViewModel.listOfAssessmentAnswer.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                mAdapter.setDataList(it)
            }
        })
        reviewAssessmentViewModel.onSubmitAssessmentObservable.observe(this, Observer {
            if (it != null && it) {
                submitAssessment()
            }
        })

        reviewAssessmentViewModel.navigateBackOnCloseObservable.observe(this, Observer {
            if (it != null && it) {
                showExitConfirmDialog(
                    getString(R.string.exit_assessment_title),
                    getString(R.string.exit_assessment_message)
                ) {
                    addMixPanelEvent(MixPanelData.eventStoppedAssessment)
                    super.onBackPressed()
                }
            }
        })

        reviewAssessmentViewModel.editItemPositionObservable.observe(
            this,
            Observer { questionPosition ->
                if (questionPosition != null) {
                    //here check questionType from question position and the show dialog accordingly
                    val assessmentAnswer =
                        reviewAssessmentViewModel.listOfAssessmentAnswer.value?.get(questionPosition)
                    if (assessmentAnswer != null) {
                        EditSingleChoiceAnsDialog(this).show(
                            assessmentAnswer,
                            reviewAssessmentViewModel,
                            questionPosition,
                            selectedAssessmentCode
                        ) { questionIndex ->
                            mAdapter.notifyItemChanged(questionIndex)
                        }
                    }
                }
            })
        addMixPanelEvent(MixPanelData.eventLandedAssessmentReview)
    }

    override fun onBackPressed() {
        showExitConfirmDialog(
            getString(R.string.exit_assessment_title),
            getString(R.string.exit_assessment_message)
        ) {
            addMixPanelEvent(MixPanelData.eventStoppedAssessment)
            super.onBackPressed()
        }
    }

    private fun submitAssessment() {
        showLoading()
        val requestListAnswers = ArrayList<AssessmentValueRequest>()
        val listOfQueAnswer = reviewAssessmentViewModel.listOfAssessmentAnswer.value
        listOfQueAnswer?.let {
            for (index in 0 until listOfQueAnswer.size) {
                requestListAnswers.add(
                    AssessmentValueRequest(
                        listOfQueAnswer[index].questionId,
                        listOfQueAnswer[index].questionLabel,
                        AssessmentAnswerRequest(listOfQueAnswer[index].userAnswer)
                    )
                )
            }
            val assessmentDataRequest =
                AssessmentDataRequest(selectedAssessmentId, requestListAnswers)
            reviewAssessmentViewModel.executeSubmitAssessmentApi(assessmentDataRequest).observe(
                this,
                Observer { response ->
                    hideLoading()
                    when (response.status) {
                        ResponseStatus.CONFLICT_USER_INPUTS, ResponseStatus.BAD_PARAMS -> {
                            showSnackMsg(response.message)
                        }
                        ResponseStatus.NO_INTERNET -> {
                            showNoInternetDialog()
                        }
                        ResponseStatus.SUCCESS, ResponseStatus.SUCCESS_201 -> {
                            addMixPanelEvent(MixPanelData.eventCompletedAssessment)
                            val intent = Intent(this, AssessmentSubmittedActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        }
                        ResponseStatus.BAD_INPUT_500 -> {
                            showSnackMsg(response.message)
                        }
                        else -> {
                            showSnackMsg(response.message)
                        }
                    }
                })
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        reviewAssessmentViewModel.isCheck.value = isChecked
    }

    private fun addMixPanelEvent(eventName: String) {
        val assessmentEvent = JSONObject()

        assessmentEvent.put(
            MixPanelData.KEY_ASSESSMENT_CODE,
            selectedAssessmentCode
        )
        MixPanelData.getInstance(this)
            .addEvent(assessmentEvent, eventName)
    }
}
