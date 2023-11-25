package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityAssessmentSubmittedBinding
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.AssessmentSubmittedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AssessmentSubmittedActivity : BaseActivity() {
    private lateinit var assessmentSubmittedBinding: ActivityAssessmentSubmittedBinding
    private val assessmentSubmittedViewModel: AssessmentSubmittedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assessmentSubmittedBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_assessment_submitted
        )

        setup()
    }

    override fun setup() {
        assessmentSubmittedBinding.lifecycleOwner = this
        mView = assessmentSubmittedBinding.assessmentSubmittedLayout
        assessmentSubmittedBinding.viewModel = assessmentSubmittedViewModel

        assessmentSubmittedViewModel.navigateBackOnCloseObservable.observe(this, Observer {
            if (it != null && it) {
                navigateToDashboard()
            }
        })

        assessmentSubmittedViewModel.onFindPsychologistObservable.observe(this, Observer {
            if (it != null && it) {
                showAlertDialogWithOK(
                    getString(R.string.find_a_psychologist_button_text),
                    getString(R.string.coming_soon)
                ) {
                }
            }
        })
        MixPanelData.getInstance(this).addEvent(MixPanelData.eventLandedToFindPsychologistScreen)
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onBackPressed() {
        navigateToDashboard()
//        super.onBackPressed()
    }

}