package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityWizardScreenBinding
import com.example.lysnclient.adapters.ViewsSliderAdapter
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.viewmodel.WizardScreenViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_wizard_screen.into_tab_layout as pager_tab_layout
import kotlinx.android.synthetic.main.activity_wizard_screen.viewpager as digital_pager

class WizardScreenActivity : BaseActivity() {
    private lateinit var layouts: IntArray
    private lateinit var wizardScreenBinding: ActivityWizardScreenBinding
    private val wizardScreenViewModel: WizardScreenViewModel by viewModel()
    private var isWizardAtLast: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wizardScreenBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_wizard_screen
        ) as ActivityWizardScreenBinding
        setup()
    }

    override fun setup() {
        wizardScreenBinding.lifecycleOwner = this
        wizardScreenBinding.viewModel = wizardScreenViewModel

        addEvent(MixPanelData.eventLandingWizard)

        layouts = intArrayOf(
            R.layout.welcom_lysn,
            R.layout.therapy_made_easy,
            R.layout.research_validated_tools,
            R.layout.safe_and_secure
        )

        // val pagerAdapter = ViewsSliderAdapter(layouts)
        // digital_pager.adapter = pagerAdapter
        // wizardScreenBinding.myAdapter = ViewsSliderAdapter(layouts)
        digital_pager.adapter =
            ViewsSliderAdapter(layouts)
        TabLayoutMediator(pager_tab_layout, digital_pager)
        { _, _ -> }.attach()

        digital_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 3 && !isWizardAtLast) {
                    addEvent(MixPanelData.eventCompleteWizard)
                    isWizardAtLast = true
                }
            }
        })

        /*
        TabLayoutMediator(wizardScreenBinding.intoTabLayout, wizardScreenBinding.viewpager, object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                // Styling each tab here
            }
        }).attach()*/
        wizardScreenViewModel.onWizardContinueEmailObservable.observe(this, Observer {
            if (it != null && it) {
                val intent = if (PreferenceUtil.getInstance(this)
                        .getValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, false)
                ) {
                    Intent(this@WizardScreenActivity, HomeActivity::class.java)
                } else {
                    Intent(this@WizardScreenActivity, UserAuthenticateActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        })
    }

    private fun addEvent(eventName: String) {
        if (PreferenceUtil.getInstance(this)
                .getValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, false)
        ) {
            MixPanelData.getInstance(this).addEvent(
                MixPanelData.KEY_EMAIL,
                PreferenceUtil.getInstance(this)
                    .getValue(PreferenceUtil.KEY_USER_EMAIL, AppConstants.EMPTY_VALUE),
                eventName
            )
        } else {
            MixPanelData.getInstance(this).alias()
            MixPanelData.getInstance(this)
                .addEvent(eventName)
        }
    }
}
