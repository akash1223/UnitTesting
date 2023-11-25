package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentEmailVerifyBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.afterTextChanged
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel

class EmailVerifyFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment() {

    private lateinit var emailVerifyFragmentBinding: FragmentEmailVerifyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        emailVerifyFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_email_verify, container, false
        )
        return emailVerifyFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setup()
        super.onActivityCreated(savedInstanceState)
    }


    override fun setup() {
        emailVerifyFragmentBinding.lifecycleOwner = this
        emailVerifyFragmentBinding.viewModel = userAuthenticateViewModel
        mView = emailVerifyFragmentBinding.emailVerfiyLayout
        userAuthenticateViewModel.initEmailFragment()
        userAuthenticateViewModel.hideKeyboardObservable.observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        userAuthenticateViewModel.onEmailValidatedObservable.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null && it) {
                    verifyUserEmail()
                }
            })

        emailVerifyFragmentBinding.edtEmail.afterTextChanged {
            userAuthenticateViewModel.emailErrorMsg.value = AppConstants.EMPTY_VALUE
        }
    }

    private fun verifyUserEmail() {
        showLoading()
        userAuthenticateViewModel.executeVerifyEmail().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        MixPanelData.getInstance(requireActivity())
                            .mapEvents(userAuthenticateViewModel.emailField.value.toString())
                        MixPanelData.getInstance(requireActivity())
                            .createProfile(
                                userAuthenticateViewModel.emailField.value.toString(),
                                AppConstants.EMPTY_VALUE
                            )
                        MixPanelData.getInstance(requireActivity())
                            .addEvent(
                                MixPanelData.KEY_EMAIL,
                                userAuthenticateViewModel.emailField.value.toString(),
                                MixPanelData.eventUserAuthorization
                            )
                        replaceFragment(
                            R.id.frame_layout,
                            CreatePasswordFragment.newInstance(userAuthenticateViewModel),
                            getString(R.string.emailVerify), true
                        )
                    }
                    ResponseStatus.CONFLICT_USER_INPUTS -> {
                        replaceFragment(
                            R.id.frame_layout,
                            LoginFragment.newInstance(userAuthenticateViewModel),
                            getString(R.string.login), true
                        )
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.FAILURE -> {
                        showSnackMsg(response.message)
                    }
                    ResponseStatus.BAD_PARAMS -> {
                        userAuthenticateViewModel.emailErrorMsg.value = response.message
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
            EmailVerifyFragment(userAuthenticateViewModel).apply {

            }
    }
}
