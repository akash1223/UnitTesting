package com.example.lysnclient.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityWbtLearnMoreBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.WBTLearnMoreViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class WBTLearnMoreActivity : BaseActivity() {
    private lateinit var mWbtLearnMoreBinding: ActivityWbtLearnMoreBinding
    private val mWbtLearnMoreViewModel: WBTLearnMoreViewModel by viewModel()
    private var isFromSignUpScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWbtLearnMoreBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_wbt_learn_more
        ) as ActivityWbtLearnMoreBinding
        setup()

    }

    override fun setup() {
        isFromSignUpScreen = intent.getBooleanExtra(
            AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, false
        )

        mWbtLearnMoreBinding.lifecycleOwner = this
        mView = mWbtLearnMoreBinding.learnMoreWbtLayout
        mWbtLearnMoreBinding.viewModel = mWbtLearnMoreViewModel

        mWbtLearnMoreViewModel.navigateBackOnCloseObservable.observe(this, Observer {
            if (it != null && it) {
                finish()
            }
        })

        mWbtLearnMoreViewModel.onStartWBTButtonObservable.observe(this, Observer {
            if (it != null && it) {
                fetchData()
            }
        })

        MixPanelData.getInstance(this)
            .addEvent(MixPanelData.eventLandedToAboutLysnWellBeingScreen)
    }

    private fun fetchData() {
        mWbtLearnMoreViewModel.getWBTQuestionData()
        showLoading()
        mWbtLearnMoreViewModel.mConfigurationData.observe(this, Observer { response ->
            hideLoading()
            when (response.status) {
                ResponseStatus.SUCCESS -> {
                    response.apiResponse?.let {
                        if (it.wellBeingTrackerData.mWBTQuestionList.isNullOrEmpty()) {
                            showAlertDialogWithOK(
                                getString(R.string.title_wellbeing_tracker),
                                getString(R.string.wbt_questions_not_available)
                            ) {
                            }
                        } else {
                            MixPanelData.getInstance(this)
                                .addEvent(MixPanelData.eventStartedWBTOnAboutLysnWellBeingScreen)
                            val intent = Intent(this, WBTQuestionsActivity::class.java)
                            intent.putExtra(
                                AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN,
                                isFromSignUpScreen
                            )
                            startActivityForResult(intent, AppConstants.REQUEST_CODE_START_ACTIVITY)
                        }
                    }
                }
                ResponseStatus.NO_INTERNET -> {
                    showNoInternetDialog()
                }
                ResponseStatus.FAILURE -> {
                    showSnackMsg(response.message)
                }
                ResponseStatus.BAD_PARAMS -> {
                    showSnackMsg(response.message)
                }
                else -> {
                    showSnackMsg(response.message)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_CODE_START_ACTIVITY && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }
}
