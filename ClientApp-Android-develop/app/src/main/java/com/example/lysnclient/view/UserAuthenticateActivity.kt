package com.example.lysnclient.view

import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.view.fragment.EmailVerifyFragment
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This class is used for register new user
 */
class UserAuthenticateActivity : BaseActivity() {
    private val userAuthenticateViewModel: UserAuthenticateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_authenticate)
        setup()
    }

    override fun setup() {
        loadEmailVerifyFragment()
        userAuthenticateViewModel.navigateBackObservable.observe(this, Observer {
            if (it)
                removeCurrentFragment()
        })
        if (intent.getBooleanExtra(
                AppConstants.INTENT_KEY_IS_FROM_SESSION_EXPIRED, false
            )
        ) {
            hideLoading()
            MixPanelData.getInstance(this).addEvent(MixPanelData.eventSessionExpired)
            showAlertDialogWithOK(
                getString(R.string.session_expired),
                getString(R.string.session_expired_msg)
            ) {}
        }
    }

    private fun loadEmailVerifyFragment() {
        replaceFragment(
            R.id.frame_layout,
            EmailVerifyFragment.newInstance(userAuthenticateViewModel),
            getString(R.string.emailVerify), false
        )
    }

    private fun removeCurrentFragment() {
        if (supportFragmentManager.backStackEntryCount != 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}

