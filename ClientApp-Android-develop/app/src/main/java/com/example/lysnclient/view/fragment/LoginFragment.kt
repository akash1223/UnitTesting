package com.example.lysnclient.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentLoginBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.*
import com.example.lysnclient.view.HomeActivity
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel
import org.koin.android.ext.android.inject

/**
 * This class is used for login with password for existing user
 */
class LoginFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment() {

    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private val networkManager: NetworkManager by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        return fragmentLoginBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        fragmentLoginBinding.lifecycleOwner = this
        fragmentLoginBinding.viewModel = userAuthenticateViewModel
        mView = fragmentLoginBinding.loginLayout
        userAuthenticateViewModel.initLoginFragment()

        userAuthenticateViewModel.hideKeyboardObservable.observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        userAuthenticateViewModel.onLoginPasswordAddedObservable.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null && it) {
                    performUserLogin()
                }
            })
        userAuthenticateViewModel.onNavForgotPassObservable.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null && it) {
                    if (networkManager.isNetworkAvailable)
                        replaceFragment(
                            R.id.frame_layout,
                            ForgotPasswordFragment.newInstance(userAuthenticateViewModel),
                            getString(R.string.login), true
                        )
                    else
                        showNoInternetDialog()
                }
            })
    }

    private fun performUserLogin() {
        showLoading()
        userAuthenticateViewModel.executeUserLogin().observe(viewLifecycleOwner,
            Observer { response ->
                if (response.status != ResponseStatus.SUCCESS) hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        userAuthenticateViewModel.saveTokenAndEmailInSharedPreference(
                            requireActivity(), response.apiResponse
                        )
                        callGetUserProfile()
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.FAILURE -> {
                        showSnackMsg(response.message)
                    }
                    ResponseStatus.BAD_PARAMS -> {
                        userAuthenticateViewModel.loginPasswordField.value = response.message
                    }
                    else -> {
                        showSnackMsg(response.message)
                    }
                }
            })
    }

    private fun callGetUserProfile() {
        userAuthenticateViewModel.getUserProfile().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        PreferenceUtil.getInstance(requireActivity()).saveValue(
                            PreferenceUtil.KEY_USER_ID,
                            response.apiResponse?.id
                        )
                        MixPanelData.getInstance(requireActivity())
                            .createProfile(
                                userAuthenticateViewModel.emailField.value.toString(),
                                response.apiResponse?.userData?.phone.toString()
                            )
                        MixPanelData.getInstance(requireActivity())
                            .addEvent(
                                MixPanelData.KEY_EMAIL,
                                userAuthenticateViewModel.emailField.value.toString(),
                                MixPanelData.eventSignInCompleted
                            )
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
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

    companion object {
        @JvmStatic
        fun newInstance(userAuthenticateViewModel: UserAuthenticateViewModel) =
            LoginFragment(userAuthenticateViewModel).apply {

            }
    }
}
