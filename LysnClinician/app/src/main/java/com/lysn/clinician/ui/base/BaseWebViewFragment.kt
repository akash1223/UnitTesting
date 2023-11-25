package com.lysn.clinician.ui.base

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.R
import com.lysn.clinician.utility.CustomProgressBar
import com.lysn.clinician.utility.CustomWebViewClient

/*
* BaseWebViewFragment to setup WebView configuration
* @webView : WebView instance for child class
* @progressBar:shows progress bar dialog
* */
abstract class BaseWebViewFragment : BaseFragment() {

    lateinit var webView: WebView
    lateinit var progressBar: CustomProgressBar
    private var navigateBrowserHistory = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById<WebView>(R.id.webView)
        setUpWebView()
    }

    fun loadWebUrl(webUrl: String) {
        progressBar.show()
        webView.loadUrl(BuildConfig.WEB_BASE_URL + webUrl)
    }

    fun setNavigateBrowserHistory(value: Boolean) {
        navigateBrowserHistory = value
    }

    private fun setUpWebView() {
        webView.webViewClient = createWebViewClient()
        setWebViewSettings()
        setPropertiesToWebView()
        setBackPressAction()
    }

    private fun createWebViewClient(): WebViewClient {
        progressBar = CustomProgressBar(requireActivity())
        return CustomWebViewClient(
            progressBar,
            context = requireContext()
        )
    }

    //Configure related browser settings
    private fun setWebViewSettings() {
        val settings = webView.settings

        settings.loadsImagesAutomatically = true
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        // Enable responsive layout
        settings.useWideViewPort = true
        // Zoom out if the content width is greater than the width of the viewport
        settings.loadWithOverviewMode = true
    }

    private fun setPropertiesToWebView() {
        webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        webView.accessibilityDelegate = View.AccessibilityDelegate()
    }

    //Handles the ActionBar back arrow button click
    private fun setBackPressAction() {
        backButton?.setOnClickListener {
            if (navigateBrowserHistory && webView.canGoBack()) {
                webView.goBack()
            } else {
                mNavController.popBackStack()
            }
        }
    }
}