package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.lysnclient.R
import com.example.lysnclient.utils.AppConstants

/**
 *  This class splash screen
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setup()
    }

    override fun setup() {
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, WizardScreenActivity::class.java)
            startActivity(intent)
            finish()
        }, AppConstants.SPLASH_SCREEN_TIMEOUT_IN_MILLI_SECOND.toLong())
    }
}