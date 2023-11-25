package com.example.lysnclient.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.ActivityHomeBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.view.fragment.AssessmentListFragment
import com.example.lysnclient.view.fragment.HomeFragment
import com.example.lysnclient.view.fragment.UserFragment
import com.example.lysnclient.viewmodel.HomeDashboardViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private val viewModel: HomeDashboardViewModel by viewModel()
    private lateinit var homeActivityBinding: ActivityHomeBinding
    private var selectedItemId = R.id.menu_item_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_home
        )
        setup()
    }

    override fun setup() {
        homeActivityBinding.lifecycleOwner = this
        mView = homeActivityBinding.homeLayout
        homeActivityBinding.navView.setOnNavigationItemSelectedListener(this)
        replaceFragment(
            R.id.btm_nav_container,
            HomeFragment.newInstance(),
            getString(R.string.home_menu_item), false
        )
        viewModel.navigateToDetailObservable.observe(
            this,
            Observer {
                if (it == AppConstants.ASSESSMENT_ITEM_POSITION) {
                    replaceFragment(
                        R.id.btm_nav_container,
                        AssessmentListFragment.newInstance(),
                        getString(R.string.assListFragment), true
                    )
                } else if (it == AppConstants.WBT_ITEM_POSITION) {
                    val intent = Intent(this, WBTIntroActivity::class.java)
                    startActivity(intent)
                } else if (it == AppConstants.USER_LOGOUT_POSITION) {
                    showExitConfirmDialog(
                        getString(R.string.logout),
                        getString(R.string.logout_title), getString(R.string.confirm_title)
                    ) { callLogoutAPI() }

                } else {
                    showAlertDialogWithOK(
                        getString(viewModel.listOfYouScreen[it].title),
                        getString(R.string.coming_soon)
                    ) {}
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_home -> {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                MixPanelData.getInstance(this).addEvent(MixPanelData.eventOpenHomeTab)
                selectedItemId = item.itemId
                replaceFragment(
                    R.id.btm_nav_container,
                    HomeFragment.newInstance(),
                    getString(R.string.home_menu_item), false
                )
                return true
            }
            R.id.menu_item_search -> {
                MixPanelData.getInstance(this).addEvent(MixPanelData.eventOpenSearchTab)
                showAlertDialogWithOK(
                    getString(R.string.search_menu_item),
                    getString(R.string.coming_soon)
                ) {
                    homeActivityBinding.navView.menu.findItem(selectedItemId).isChecked = true
                }
                return true
            }
            R.id.menu_item_learn -> {
                MixPanelData.getInstance(this).addEvent(MixPanelData.eventOpenLearnTab)
                showAlertDialogWithOK(
                    getString(R.string.learn_menu_item),
                    getString(R.string.coming_soon)
                ) {
                    homeActivityBinding.navView.menu.findItem(selectedItemId).isChecked = true
                }
                return true
            }
            R.id.menu_item_user -> {
                if (supportFragmentManager.findFragmentByTag(getString(R.string.user_menu_item)) == null) {
                    MixPanelData.getInstance(this).addEvent(MixPanelData.eventOpenUserTab)
                    selectedItemId = item.itemId
                    replaceFragment(
                        R.id.btm_nav_container,
                        UserFragment.newInstance(viewModel),
                        getString(R.string.user_menu_item), false
                    )
                }
                return true
            }
            else -> {
                return true
            }
        }
    }

    private fun callLogoutAPI() {
        showLoading()
        viewModel.callUserLogoutAPI(
            PreferenceUtil.getInstance(this)
                .getValue(PreferenceUtil.KEY_REFRESH_TOKEN, AppConstants.EMPTY_VALUE)
        ).observe(
            this,
            Observer {
                hideLoading()
                MixPanelData.getInstance(this)
                    .addEvent(MixPanelData.eventSignOut)
                PreferenceUtil.getInstance(this).clearAll()
                val intent = Intent(this, UserAuthenticateActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            })
    }
}
