package com.example.lysnclient.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityWbtIntroBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.viewmodel.WBTIntroViewModel
import kotlinx.android.synthetic.main.view_toolbar_wbt.view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This class is used for display WBT introduction
 */
class WBTIntroActivity : BaseActivity() {
    private lateinit var mWBTIntroBinding: ActivityWbtIntroBinding
    private val mWBTIntroViewModel: WBTIntroViewModel by viewModel()
    private var isFromSignUpScreen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wbt_intro)
        setup()
    }

    override fun setup() {
        isFromSignUpScreen = intent.getBooleanExtra(
            AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, false
        )
        mWBTIntroBinding = DataBindingUtil.setContentView(this, R.layout.activity_wbt_intro)
        mView = mWBTIntroBinding.layoutWbtIntro
        mWBTIntroBinding.lifecycleOwner = this
        mWBTIntroBinding.viewModel = mWBTIntroViewModel
        mWBTIntroBinding.layoutToolbar.toolbar_title.text =
            getString(R.string.wellbeing_tracker_toolbar_title)
        setSupportActionBar(mWBTIntroBinding.layoutToolbar.toolbar)
        supportActionBar?.title = AppConstants.EMPTY_VALUE
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.wbtBackground
        )

        mWBTIntroViewModel.onStartWBTBtnObservable.observe(this, Observer {
            if (it != null && it) {
                fetchData()
            }
        })

        mWBTIntroViewModel.continueToHomeClickObservable.observe(this, Observer {
            if (it != null && it) {
                launchHomeActivity()
            }
        })

        mWBTIntroViewModel.learnMoreClickObservable.observe(this, Observer {
            if (it != null && it) {
                val intent = Intent(this, WBTLearnMoreActivity::class.java)
                intent.putExtra(
                    AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN,
                    isFromSignUpScreen
                )
                startActivityForResult(intent, AppConstants.REQUEST_CODE_START_ACTIVITY)
            }
        })

        MixPanelData.getInstance(this).addEvent(MixPanelData.eventLandedToWellBeingTrackerScreen)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_assessment_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        navigate()
    }

    private fun fetchData() {
        mWBTIntroViewModel.getWBTQuestionData()
        showLoading()
        mWBTIntroViewModel.mConfigurationData.observe(this, Observer { response ->
            hideLoading()
            when (response.status) {
                ResponseStatus.SUCCESS -> {
                    response.apiResponse?.let {
                        if (it.wellBeingTrackerData.mWBTQuestionList.isNullOrEmpty()) {
                            MixPanelData.getInstance(this)
                                .addEvent(MixPanelData.eventStartedWellBeingTrackerScreen)
                            showAlertDialogWithOK(
                                getString(R.string.title_wellbeing_tracker),
                                getString(R.string.wbt_questions_not_available)
                            ) {
                            }
                        } else {
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
            finish()
        }
    }

    private fun navigate() {
        if (isFromSignUpScreen) {
            launchHomeActivity()
        } else {
            super.onBackPressed()
        }
    }
}
