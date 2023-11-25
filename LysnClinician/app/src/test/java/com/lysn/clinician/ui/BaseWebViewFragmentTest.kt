package com.lysn.clinician.ui

import android.os.Build
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.http.HttpConstants
import com.lysn.clinician.utility.CustomProgressBar
import com.lysn.clinician.utility.CustomWebViewClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class BaseWebViewFragmentTest {

    private lateinit var webView: WebView
    private lateinit var webViewClient: CustomWebViewClient
    private lateinit var customProgressBar: CustomProgressBar

    @Before
    fun setUp() {
        webView = WebView(ApplicationProvider.getApplicationContext())
        customProgressBar = CustomProgressBar(ApplicationProvider.getApplicationContext())
        webViewClient =
            CustomWebViewClient(
                customProgressBar,
                ApplicationProvider.getApplicationContext()
            )
    }

    @After
    fun clearData() {
        stopKoin()
    }

    @Test
    fun shouldRecordLastLoadedUrl() {
        webView.loadUrl(BuildConfig.WEB_BASE_URL + HttpConstants.FORGOT_PASSWORD_WEB_URL)
        assertThat(shadowOf(webView).lastLoadedUrl).isEqualTo(BuildConfig.WEB_BASE_URL + HttpConstants.FORGOT_PASSWORD_WEB_URL)
    }

    @Test
    fun shouldRecordWebViewClient() {
        assertThat(shadowOf(webView).webViewClient).isNull()
        webView.webViewClient = webViewClient
        assertThat(shadowOf(webView).webViewClient).isSameInstanceAs(webViewClient)
    }

    @Test
    fun shouldCheckProgressBarNotNull() {
        assertThat(customProgressBar).isNotNull()
    }

    @Test
    fun shouldCheckWebViewClientNotNull() {
        webView.webViewClient = webViewClient
        assertThat(webView.webViewClient).isNotNull()
    }

    @Test
    fun shouldReturnSettings() {
        val webSettings = webView.settings
        assertThat(webSettings).isNotNull()
    }

}