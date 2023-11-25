package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentSingleChoiceQueBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.model.AssessmentAnswer
import com.example.lysnclient.viewmodel.AssessmentQuestionViewModel
import kotlinx.android.synthetic.main.fragment_single_choice_que.*

class SingleChoiceQueFragment(
    private val quePosition: Int,
    val viewModel: AssessmentQuestionViewModel
) :
    BaseFragment(), View.OnClickListener {

    private lateinit var singleChoiceQueBinding: FragmentSingleChoiceQueBinding
    private var mLastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        singleChoiceQueBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_single_choice_que, container, false
        )
        mView = singleChoiceQueBinding.singleChoiceQueLayout
        container?.layoutTransition?.setAnimateParentHierarchy(false)
        return singleChoiceQueBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        singleChoiceQueBinding.lifecycleOwner = this
        singleChoiceQueBinding.quePosition = quePosition
        singleChoiceQueBinding.viewModel = viewModel
        singleChoiceQueBinding.assessmentQue = viewModel.listOfQuestion[quePosition]
        rdo_btn_option1.setOnClickListener(this)
        rdo_btn_option2.setOnClickListener(this)
        rdo_btn_option3.setOnClickListener(this)
        rdo_btn_option4.setOnClickListener(this)
        rdo_btn_option5.setOnClickListener(this)
        rdo_btn_option6.setOnClickListener(this)

    }

    override fun onResume() {
        viewModel.currentQuestionNumber.value = quePosition
        if (viewModel.mapQuePosAndAnswerDetails.isNotEmpty() && viewModel.mapQuePosAndAnswerDetails.size > quePosition) {
            viewModel.mapQuePosAndAnswerDetails.get(quePosition)?.let {
                radio_group_options.check((radio_group_options.getChildAt(it.singleChoiceOptionPosition)).id)
            }
        }

        if (!viewModel.listVisitedQuePosition.contains(quePosition)) {
            viewModel.listVisitedQuePosition.add(quePosition)
            addVisitedQueEvent(
                viewModel.selectedAssessmentDetails?.code.toString(),
                viewModel.listOfQuestion[quePosition].label,
                MixPanelData.eventOpenSingleChoiceQue
            )
        }
        super.onResume()
    }

    override fun onClick(radioBtnView: View?) {
        if (radioBtnView == null) return
        when (radioBtnView) {
            is RadioButton -> {
                if (!radioBtnView.isChecked) return
                if (!AppConstants.allowToPerformClick(mLastClickTime)) return
                saveUserAnswer(radioBtnView)
            }
        }
    }

    // This method iterate and find selected option position, and save in viewModel map with quePosition
    private fun saveUserAnswer(radioBtnView: View) {
        mLastClickTime = AppConstants.getCurrentTimeMills()
        for (optionPosition in 0 until radio_group_options.childCount) {
            val btn = radio_group_options.getChildAt(optionPosition) as RadioButton
            if (btn.id == radioBtnView.id) {
                val questionId = viewModel.listOfQuestion[quePosition].id

                // Check its first time answered or updating it for mix
                if (!viewModel.listAnsweredQuePosition.contains(quePosition)) {
                    viewModel.listAnsweredQuePosition.add(quePosition)
                    addQueAnsweredEvent(
                        viewModel.selectedAssessmentDetails?.code.toString(),
                        viewModel.listOfQuestion[quePosition].label,
                        viewModel.listOfQuestion[quePosition].listOfOptions[optionPosition].value,
                        MixPanelData.eventCompletedSingleChoiceQue
                    )
                } else {
                    //already answered update it
                    addQueAnsweredEvent(
                        viewModel.selectedAssessmentDetails?.code.toString(),
                        viewModel.listOfQuestion[quePosition].label,
                        //here need to fetch answer from map using quePosition for fetch previous answer
                        viewModel.listOfQuestion[quePosition].listOfOptions[optionPosition].value,
                        MixPanelData.eventEditedSingleChoiceQue,
                        true,
                        viewModel.mapQuePosAndAnswerDetails[quePosition]?.userAnswer
                            ?: AppConstants.EMPTY_VALUE
                    )
                }
                // Check AssessmentAnswer object already contains or not, if contains update it else add it
                if (!viewModel.mapQuePosAndAnswerDetails.contains(quePosition)) {
                    val assessmentAnswer = AssessmentAnswer(
                        AppConstants.EMPTY_VALUE,
                        questionId,
                        quePosition,
                        viewModel.listOfQuestion[quePosition].label,
                        viewModel.listOfQuestion[quePosition].listOfOptions[optionPosition].value,
                        optionPosition
                    )
                    viewModel.mapQuePosAndAnswerDetails[quePosition] = assessmentAnswer
                } else {
                    val assessmentAnswer = viewModel.mapQuePosAndAnswerDetails[quePosition]
                    if (assessmentAnswer != null) {
                        assessmentAnswer.singleChoiceOptionPosition = optionPosition
                        assessmentAnswer.questionLabel = viewModel.listOfQuestion[quePosition].label
                        assessmentAnswer.userAnswer =
                            viewModel.listOfQuestion[quePosition].listOfOptions[optionPosition].value
                        viewModel.mapQuePosAndAnswerDetails[quePosition] = assessmentAnswer
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.moveToNextQues.value = true
                }, 200)
                break
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            quePosition: Int,
            viewModel: AssessmentQuestionViewModel
        ) =
            SingleChoiceQueFragment(quePosition, viewModel)
    }
}
