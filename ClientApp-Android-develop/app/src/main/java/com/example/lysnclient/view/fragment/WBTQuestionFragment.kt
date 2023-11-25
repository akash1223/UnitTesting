package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentWbtQuestionBinding
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.WBTQuestionsViewModel
import org.json.JSONObject

class WBTQuestionFragment(
    val quePosition: Int,
    val mViewModel: WBTQuestionsViewModel
) : BaseFragment() {

    private lateinit var mBinding: FragmentWbtQuestionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_wbt_question, container, false
        )
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        mBinding.lifecycleOwner = this
        mView = mBinding.wbtQuestionLayout
        mBinding.quePosition = quePosition
        mBinding.viewModel = mViewModel
        mBinding.answerMap = mViewModel.mapQuePosAndAnswerDetails
    }

    override fun onResume() {
        super.onResume()
        mBinding.seekBarProgressTracker.setOnSeekBarChangeListener(seekBarChangeListener)
        if (!mViewModel.listVisitedQuePosition.contains(quePosition)) {
            mViewModel.listVisitedQuePosition.add(quePosition)
            addVisitedQueEvent()
        } else {
            // If already visited store previously selected value, to track changes
            mViewModel.previouslySelectedAnswerValue =
                mViewModel.mapQuePosAndAnswerDetails[mViewModel.currentQuestionIndex.value
                    ?: 0]?.mWBTSeekBarValue ?: 0
        }
    }

    private fun addVisitedQueEvent(
    ) {
        val jsonEvent = JSONObject()
        jsonEvent.put(
            MixPanelData.KEY_QUESTION_NUMBER,
            quePosition
        )
        jsonEvent.put(
            MixPanelData.KEY_QUESTION,
            mViewModel.mWBTQuestionList[quePosition].question
        )
        MixPanelData.getInstance(requireActivity())
            .addEvent(jsonEvent, MixPanelData.eventWBTQuestionVisited)
    }

    private val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            mViewModel.setSeekBarProgressValue(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }

    companion object {
        @JvmStatic
        fun newInstance(
            position: Int,
            viewModel: WBTQuestionsViewModel
        ) = WBTQuestionFragment(position, viewModel).apply { }
    }
}
