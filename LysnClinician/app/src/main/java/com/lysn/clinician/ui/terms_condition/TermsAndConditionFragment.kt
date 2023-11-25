package com.lysn.clinician.ui.terms_condition

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lysn.clinician.R
import com.lysn.clinician.databinding.FragmentTermsAndConditionBinding
import com.lysn.clinician.ui.MainActivity
import com.lysn.clinician.ui.base.BaseFragment
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.utils.PreferenceUtil
import com.lysn.clinician.utils.PreferenceUtil.Companion.TERM_AND_CONDITION_PREFERENCE_KEY
import kotlinx.android.synthetic.main.fragment_terms_and_condition.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TermsAndConditionFragment : BaseFragment() {

    private val mViewModel: TermsAndConditionViewModel by viewModel()
    private lateinit var mTermsAndConditionBinding: FragmentTermsAndConditionBinding
    private val preferenceUtil: PreferenceUtil by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mTermsAndConditionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_terms_and_condition, container, false
        )
        return mTermsAndConditionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mixPanelScreenVisitedEvent(MixPanelData.TERMS_AND_CONDITION_VIEW_SHOWN_EVENT)
        setup()
    }

    override fun setup() {
        mNavController.currentDestination?.label?.let { setToolbarTitle(it.toString()) }
        mTermsAndConditionBinding.lifecycleOwner = this
        mTermsAndConditionBinding.viewModel = mViewModel
        scrollView.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollView.getChildAt(0).bottom
                    <= (scrollView.height + scrollView.scrollY)
                ) {
                    //scroll view is at bottom
                    mViewModel.onReviewObservable.value = true
                }
            }

        mViewModel.onTermsAndConditionObservable.observe(this, Observer {
            if (it != null && it) {
                if (mViewModel.onReviewObservable.value!!) {
                    preferenceUtil.putValue(TERM_AND_CONDITION_PREFERENCE_KEY, true)
                    requireActivity().startActivity(
                        Intent(
                            requireContext(),
                            MainActivity::class.java
                        )
                    )
                    mixPanelButtonClickEvent(mTermsAndConditionBinding.btnReview,MixPanelData.TERMS_AND_CONDITION_ACCEPT_BUTTON_CLICKED_EVENT)

                    requireActivity().finish()
                } else {
                    mixPanelButtonClickEvent(mTermsAndConditionBinding.btnReview,MixPanelData.TERMS_AND_CONDITION_REVIEW_BUTTON_CLICKED_EVENT)
                    startScrollAnimation()
                }
            }
        })
    }

    private fun startScrollAnimation() {
        if (scrollView.getChildAt(0) != null) {
            val objectAnimator =
                ObjectAnimator.ofInt(
                    scrollView,
                    "scrollY",
                    scrollView.getChildAt(0).height - scrollView.height
                )
                    .setDuration(15000)
            objectAnimator.start()
        }
    }

}