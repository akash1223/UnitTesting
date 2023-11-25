package com.example.lysnclient.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lysnclient.view.WBTQuestionsActivity
import com.example.lysnclient.view.fragment.WBTQuestionFragment
import com.example.lysnclient.viewmodel.WBTQuestionsViewModel

class WbtQuestionPagerAdapter(
    context: WBTQuestionsActivity,
    val viewModel: WBTQuestionsViewModel
) : FragmentStateAdapter(context) {

    override fun getItemCount(): Int {
        return viewModel.mWBTQuestionList.size
    }

    override fun createFragment(position: Int): Fragment {
        return WBTQuestionFragment.newInstance(position, viewModel)
    }
}
