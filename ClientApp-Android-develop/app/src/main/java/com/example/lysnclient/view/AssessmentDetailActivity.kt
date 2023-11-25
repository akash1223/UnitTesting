package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityAssessmentDetailBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.AssessmentDetailViewModel
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class AssessmentDetailActivity : BaseActivity() {
    private lateinit var openAssessmentDetailBinding: ActivityAssessmentDetailBinding
    private val assessmentDetailViewModel: AssessmentDetailViewModel by viewModel()
    private var selectedAssessmentId = 0
    private var isNeedToScrollUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedAssessmentId = intent.getIntExtra(AppConstants.INTENT_ASSESSMENT_ID, 0)
        setup()
    }

    override fun onRestart() {
        super.onRestart()
        if (isNeedToScrollUp) {
            isNeedToScrollUp = false
            openAssessmentDetailBinding.assDetailLayout.scrollTo(0, 0)
        }
    }

    override fun setup() {
        openAssessmentDetailBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_assessment_detail)
        openAssessmentDetailBinding.lifecycleOwner = this
        mView = openAssessmentDetailBinding.assDetailLayout
        openAssessmentDetailBinding.viewModel = assessmentDetailViewModel
        assessmentDetailViewModel.fetchAssessmentDetailById(selectedAssessmentId)

        assessmentDetailViewModel.navigateBackObservable.observe(this, Observer {
            if (it != null && it)
                onBackPressed()
        })

        assessmentDetailViewModel.onBeginAssessmentObservable.observe(this, Observer {
            if (it != null && it) {
                if (selectedAssessmentId == AppConstants.DASS_10_ID
                    || selectedAssessmentId == AppConstants.K_10_ID
                    || selectedAssessmentId == AppConstants.BIPOLAR_DISORDER_ID
                    || selectedAssessmentId == AppConstants.DMI_ID
                ) {
                    isNeedToScrollUp = true
                    val jsonEvent = JSONObject()
                    jsonEvent.put(
                        MixPanelData.KEY_ASSESSMENT_CODE,
                        assessmentDetailViewModel.assessmentCodeField.value.toString()
                    )
                    jsonEvent.put(
                        MixPanelData.KEY_ASSESSMENT_TITLE,
                        assessmentDetailViewModel.assessmentTitleField.value.toString()
                    )
                    if (assessmentDetailViewModel.selectedAssessmentDetails?.listOfQuestions?.isNotEmpty() == true
                    ) {
                        MixPanelData.getInstance(this)
                            .addEvent(jsonEvent, MixPanelData.eventStartedAssessment)
                        startActivity(
                            Intent(this, AssessmentQuestionsActivity::class.java).putExtra(
                                AppConstants.INTENT_ASSESSMENT_ID,
                                selectedAssessmentId
                            )
                        )
                    } else {
                        showSnackMsg(getString(R.string.no_assessment_available))
                    }
                } else {
                    showSnackMsg("It is under development")
                }
            }
        })
    }
}
