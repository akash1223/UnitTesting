package com.example.lysnclient.view.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.databinding.DialogEditSingleChoiceAnsBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.viewmodel.ReviewAssessmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import timber.log.Timber

class EditSingleChoiceAnsDialog(val context: Activity) : View.OnClickListener {
    private var selectedAnswer: String = AppConstants.EMPTY_VALUE

    fun show(
        assessmentAnswer: AssessmentAnswer,
        viewModel: ReviewAssessmentViewModel,
        questionPosition: Int,
        assessmentCode: String,
        callback: (Int) -> Unit
    ) {
        val dataBinding: DialogEditSingleChoiceAnsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_edit_single_choice_ans, null, false
        )

        dataBinding.assessmentAnswer = assessmentAnswer
        dataBinding.viewModel = viewModel
        dataBinding.optionList = viewModel.fetchSelectedQuestionOptions(assessmentAnswer.questionId)

        val alertDialogView = MaterialAlertDialogBuilder(
            context,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        ).setView(dataBinding.root)
            .show()
        alertDialogView.setCancelable(false)

        // Set previously selected answer
        selectedAnswer = assessmentAnswer.userAnswer
        when (assessmentAnswer.singleChoiceOptionPosition) {
            0 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option1)
            }
            1 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option2)
            }
            2 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option3)
            }
            3 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option4)
            }
            4 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option5)
            }
            5 -> {
                dataBinding.radioGroupOptions.check(R.id.rdo_btn_option6)
            }
        }

        //Set click listener for radio buttons options
        dataBinding.rdoBtnOption1.setOnClickListener(this)
        dataBinding.rdoBtnOption2.setOnClickListener(this)
        dataBinding.rdoBtnOption3.setOnClickListener(this)
        dataBinding.rdoBtnOption4.setOnClickListener(this)
        dataBinding.rdoBtnOption5.setOnClickListener(this)
        dataBinding.rdoBtnOption6.setOnClickListener(this)

        // Handle dialog OK click, and update answer and position in  assessmentAnswer of viewmodel.listOfAssessmentAnswer
        dataBinding.btnOk.setOnClickListener {
            var selectedOptionIndex = 0
            val optionList = viewModel.fetchSelectedQuestionOptions(assessmentAnswer.questionId)
            for (index in 0 until optionList.size) {
                if (selectedAnswer == optionList[index].label) {
                    selectedOptionIndex = index
                    Timber.e("Selected option position from list ==> $selectedOptionIndex")
                    break
                }
            }

            // Add mix panel event for change in answer
            if (assessmentAnswer.userAnswer != selectedAnswer) {
                callEditAnswerMixPanelEvent(
                    assessmentCode,
                    assessmentAnswer.questionLabel,
                    selectedAnswer,
                    MixPanelData.eventEditedSingleChoiceQueInReview, assessmentAnswer.userAnswer
                )
                assessmentAnswer.userAnswer = selectedAnswer
                assessmentAnswer.singleChoiceOptionPosition = selectedOptionIndex
                viewModel.listOfAssessmentAnswer.value?.set(questionPosition, assessmentAnswer)
                callback.invoke(questionPosition)
                /*
                Below code for update current answer in list, and set value in viewModel.listOfAssessmentAnswer.value
                  val list = viewModel.listOfAssessmentAnswer.value
                  list?.let {
                      list[questionPosition] = assessmentAnswer
                      viewModel.setListOfAssessmentAnswers(list)
                  }*/
            }
            alertDialogView.dismiss()
        }

        // Handle dialog cancel click
        dataBinding.btnCancel.setOnClickListener {
            alertDialogView.dismiss()
        }
    }

    private fun callEditAnswerMixPanelEvent(
        assessmentCode: String,
        assQuestion: String,
        answer: String,
        eventName: String,
        oldAnswer: String
    ) {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_ASSESSMENT_CODE,
            assessmentCode
        )
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            assQuestion
        )
        jsonEvent.put(
            MixPanelData.KEY_PREVIOUS_ANSWER,
            oldAnswer
        )
        jsonEvent.put(
            MixPanelData.KEY_New_ANSWER,
            answer
        )
        MixPanelData.getInstance(context).addEvent(jsonEvent, eventName)
    }

    // Handle on click of radio button options
    override fun onClick(radioBtnView: View?) {
        if (radioBtnView == null) return
        when (radioBtnView) {
            is RadioButton -> {
                if (!radioBtnView.isChecked) return
                selectedAnswer = radioBtnView.text.toString()
                Timber.e("Selected option text ==> $selectedAnswer")
            }
        }
    }
}
