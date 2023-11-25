package com.example.lysnclient.view

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityWbtOutputScreenBinding
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.viewmodel.WBTOutputScreenViewModel
import kotlinx.android.synthetic.main.view_toolbar_wbt.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class WBTOutputScreenActivity : BaseActivity() {

    private lateinit var mWbtOutputScreenBinding: ActivityWbtOutputScreenBinding
    private val mWbtOutputScreenViewModel: WBTOutputScreenViewModel by viewModel()
    private lateinit var animCrossFadeIn: Animation
    private var isFromSignUpScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wbt_output_screen)
        setup()
    }

    override fun setup() {
        isFromSignUpScreen = intent.getBooleanExtra(
            AppConstants.INTENT_KEY_IS_FROM_SIGN_UP_SCREEN, false
        )
        mWbtOutputScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_wbt_output_screen)
        mView = mWbtOutputScreenBinding.layoutWbtOutputScreen
        mWbtOutputScreenBinding.lifecycleOwner = this
        mWbtOutputScreenBinding.viewModel = mWbtOutputScreenViewModel
        mWbtOutputScreenBinding.layoutToolbar.toolbar_title.text =
            getString(R.string.wellbeing_tracker_toolbar_title)
        setSupportActionBar(mWbtOutputScreenBinding.layoutToolbar.toolbar)
        supportActionBar?.title = AppConstants.EMPTY_VALUE
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.wbtBackground
        )
        mWbtOutputScreenViewModel.getWBTInterpretationList()
        animCrossFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        mWbtOutputScreenViewModel.interpretationText.observe(this, Observer {
            mWbtOutputScreenBinding.txtWbtInterpretation.text =
                mWbtOutputScreenViewModel.interpretationText.value.toString()
            mWbtOutputScreenBinding.txtWbtInterpretation.startAnimation(animCrossFadeIn)
        })

        mWbtOutputScreenViewModel.btnFindPsychologistObservable.observe(this, Observer {
            showAlertDialogWithOK(
                getString(R.string.title_wellbeing_tracker),
                getString(R.string.coming_soon)
            ) {}
        })

        mWbtOutputScreenViewModel.btnContinueToHomeObservable.observe(this, Observer {
            launchHomeActivity()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_assessment_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isFromSignUpScreen) {
            launchHomeActivity()
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
