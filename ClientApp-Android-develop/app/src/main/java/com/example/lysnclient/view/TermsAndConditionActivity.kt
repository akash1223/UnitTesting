package com.example.lysnclient.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityTermsAndConditionBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.viewmodel.TermsConditionViewModel
import kotlinx.android.synthetic.main.activity_terms_and_condition.*
import org.koin.android.viewmodel.ext.android.viewModel

class TermsAndConditionActivity : BaseActivity() {
    private var email: String = AppConstants.EMPTY_VALUE
    private var password: String = AppConstants.EMPTY_VALUE
    private var phoneNumber: String = AppConstants.EMPTY_VALUE
    private val termsConditionViewModel: TermsConditionViewModel by viewModel()
    private lateinit var termsandConditionActivityBinding: ActivityTermsAndConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        termsandConditionActivityBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_terms_and_condition
            ) as ActivityTermsAndConditionBinding
        email = intent.getStringExtra(AppConstants.SIGN_UP_EMAIL) ?: AppConstants.EMPTY_VALUE
        password = intent.getStringExtra(AppConstants.SIGN_UP_PASSWORD) ?: AppConstants.EMPTY_VALUE
        phoneNumber = intent.getStringExtra(AppConstants.SIGN_UP_PHONE) ?: AppConstants.EMPTY_VALUE
        setup()
    }

    override fun setup() {
        termsandConditionActivityBinding.lifecycleOwner = this
        termsandConditionActivityBinding.viewModel = termsConditionViewModel
        mView = termsandConditionActivityBinding.termConditionLayout
        scrollView.viewTreeObserver
            .addOnScrollChangedListener {
                if (scrollView.getChildAt(0).bottom
                    <= (scrollView.height + scrollView.scrollY)
                ) {
                    //scroll view is at bottom
                    termsConditionViewModel.onReviewObservable.value = true
                }
            }

        termsConditionViewModel.onTermsAndConditionObservable.observe(this, Observer {
            if (it != null && it) {
                if (termsConditionViewModel.onReviewObservable.value!!) {
                    registerUserApi()
                } else {
                    startScrollAnimation()
                }
            }
        })

        termsConditionViewModel.navigateBackObservable.observe(this, Observer {
            if (it != null && it)
                onBackPressed()
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

    private fun registerUserApi() {
        showLoading()
        termsConditionViewModel.executeRegisterUserApi(email, password, phoneNumber).observe(
            this,
            Observer { response ->
                if (response.status != ResponseStatus.SUCCESS ||
                    response.status != ResponseStatus.SUCCESS_201
                ) {
                    hideLoading()
                }
                when (response.status) {
                    ResponseStatus.SUCCESS, ResponseStatus.SUCCESS_201 -> {
                        MixPanelData.getInstance(this)
                            .createProfile(email, phoneNumber)
                        MixPanelData.getInstance(this)
                            .addEvent(
                                MixPanelData.KEY_EMAIL,
                                email,
                                MixPanelData.eventSignUpCompleted
                            )
                        termsConditionViewModel.saveTokenAndEmailInSharedPreference(
                            this,
                            response.apiResponse?.userTokens, email
                        )
                        PreferenceUtil.getInstance(this).saveValue(
                            PreferenceUtil.KEY_USER_ID,
                            response.apiResponse?.userProfile?.id
                        )
                        val intent = Intent(this, WBTIntroActivity::class.java)
                        intent.putExtra(AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, true)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    ResponseStatus.CONFLICT_USER_INPUTS, ResponseStatus.BAD_PARAMS -> {
                        showSnackMsg(response.message)
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    else -> {
                        showSnackMsg(response.message)
                    }
                }
            })
    }
}
