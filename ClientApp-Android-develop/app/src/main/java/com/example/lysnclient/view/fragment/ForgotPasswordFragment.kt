package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentForgotPasswordBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.CustomWebViewClient
import com.example.lysnclient.utils.HttpConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.MixPanelData.Companion.KEY_EMAIL
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel

class ForgotPasswordFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment() {

    private lateinit var dataBinding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_forgot_password, container, false
        )
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setup()
        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = userAuthenticateViewModel
        MixPanelData.getInstance(requireActivity())
            .addEvent(
                KEY_EMAIL,
                userAuthenticateViewModel.emailField.value ?: AppConstants.EMPTY_VALUE,
                MixPanelData.eventForgotPasswordVisited
            )
        userAuthenticateViewModel.initForgotPassFragment()
        setUpWebView()
        loadWebUrl()
    }

    private fun setUpWebView() {
        dataBinding.webView.webViewClient = createWebViewClient()
        setWebViewSettings()
        setPropertiesToWebView()
    }

    private fun createWebViewClient(): WebViewClient {
        return CustomWebViewClient(getProgressBar(), context = requireContext())
    }

    private fun loadWebUrl() {
        showLoading()
        dataBinding.webView.loadUrl(com.example.lysnclient.BuildConfig.WEB_VIEW_BASE_URL + HttpConstants.FORGOT_PASSWORD_WEB_URL)
    }

    //Configure related browser settings
    private fun setWebViewSettings() {
        val settings = dataBinding.webView.settings

        settings.loadsImagesAutomatically = true
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        // Enable responsive layout
        settings.useWideViewPort = true
        // Zoom out if the content width is greater than the width of the viewport
        settings.loadWithOverviewMode = true
    }

    private fun setPropertiesToWebView() {
        dataBinding.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        dataBinding.webView.accessibilityDelegate = View.AccessibilityDelegate()
    }

    companion object {
        @JvmStatic
        fun newInstance(userAuthenticateViewModel: UserAuthenticateViewModel) =
            ForgotPasswordFragment(userAuthenticateViewModel).apply {

            }
    }
}
