package com.lysn.clinician.ui.forgot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.lysn.clinician.R
import com.lysn.clinician.http.HttpConstants

import com.lysn.clinician.ui.base.BaseWebViewFragment
import com.lysn.clinician.ui.welcome.WelcomeFragment
import com.lysn.clinician.utils.MixPanelData


class ForgotPasswordFragment : BaseWebViewFragment() {

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun setup() {
        mNavController.currentDestination?.label?.let { setToolbarTitle(it.toString()) }
        handleBackPressCallback()
        loadWebUrl(HttpConstants.FORGOT_PASSWORD_WEB_URL)
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
        mixPanelScreenVisitedEvent(MixPanelData.FORGOT_PASSWORD_VIEW_DISMISSED_EVENT)
    }

    override fun onResume() {
        super.onResume()
        mixPanelScreenVisitedEvent(MixPanelData.FORGOT_PASSWORD_VIEW_SHOWN_EVENT)
    }


}