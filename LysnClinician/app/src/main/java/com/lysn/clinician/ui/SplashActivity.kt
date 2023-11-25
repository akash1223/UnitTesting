package com.lysn.clinician.ui

import android.content.Intent
import android.os.Bundle
import com.lysn.clinician.R
import com.lysn.clinician.ui.base.BaseActivity
import com.lysn.clinician.ui.join_consultation.JoinConsultationActivity
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.PreferenceUtil
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject

class SplashActivity : BaseActivity() {

    val preferenceUtil : PreferenceUtil by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setup()
    }

    override fun setup() {
        activityScope.launch {

            delay(AppConstants.SPLASH_SCREEN_TIMEOUT_IN_MILLI_SECOND.toLong())
            if(preferenceUtil.isUserLogin() && preferenceUtil.isTermsAndConditionAccepted())
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, AuthenticateActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}