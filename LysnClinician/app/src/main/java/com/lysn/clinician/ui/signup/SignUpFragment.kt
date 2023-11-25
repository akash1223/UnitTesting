package com.lysn.clinician.ui.signup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.lysn.clinician.R
import com.lysn.clinician.http.HttpConstants
import com.lysn.clinician.utils.MixPanelData
import com.lysn.clinician.ui.base.BaseWebViewFragment
import com.lysn.clinician.ui.welcome.WelcomeFragment


class SignUpFragment : BaseWebViewFragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    @SuppressLint("RestrictedApi")
    override fun setup() {
        mNavController.currentDestination?.label?.let { setToolbarTitle(it.toString()) }
        handleBackPressCallback()
        loadWebUrl(HttpConstants.SIGN_UP_WEB_URL)
    }

    //Handles the mobile back button click
    private fun handleBackPressCallback()
    {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(webView.canGoBack())
                {
                    webView.goBack()
                }else{
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mixPanelScreenVisitedEvent(MixPanelData.SIGN_UP_VIEW_DISMISSED_EVENT)
    }

    override fun onResume() {
        super.onResume()
        mixPanelScreenVisitedEvent(MixPanelData.SIGN_IN_VIEW_SHOWN_EVENT)
    }
}