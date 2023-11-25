package com.lysn.clinician.utility

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.lysn.clinician.utility.CustomProgressBar



/*
*Configure the client to use when opening URLs
*
* Dismissing the dialog in onPageFinished method or in onReceivedError
* Showing the dialog in shouldOverrideUrlLoading
* */
class CustomWebViewClient(private val progressDialog: CustomProgressBar, val context: Context) :
    WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        if (url != null) {
            view.loadUrl(url)
        }
        if (!progressDialog.isShowing)
            progressDialog.show()
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
            if (progressDialog.isShowing)
                progressDialog.dismiss()
    }

    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String,
        failingUrl: String?
    ) {
            if (progressDialog.isShowing)
                progressDialog.dismiss()

        Toast.makeText(context, "Error:$description", Toast.LENGTH_SHORT).show()
    }
}