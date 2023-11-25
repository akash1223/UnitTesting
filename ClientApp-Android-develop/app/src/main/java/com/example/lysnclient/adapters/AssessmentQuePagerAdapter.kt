package com.example.lysnclient.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lysnclient.view.AssessmentQuestionsActivity
import com.example.lysnclient.view.fragment.SingleChoiceQueFragment
import com.example.lysnclient.viewmodel.AssessmentQuestionViewModel

class AssessmentQuePagerAdapter(
    context: AssessmentQuestionsActivity,
    val viewModel: AssessmentQuestionViewModel
) : FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return viewModel.listOfQuestion.size
    }

    override fun createFragment(position: Int): Fragment {
        // Here check type of layout require for answer option based on that load a fragment
//        viewModel.listOfQuestion[position].questionOptionType=="dynamic_forms.formfields.ChoiceField"
        return SingleChoiceQueFragment.newInstance(position, viewModel)
    }
}
