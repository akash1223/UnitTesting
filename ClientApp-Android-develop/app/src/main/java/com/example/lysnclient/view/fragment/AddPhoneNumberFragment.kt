package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentAddPhoneNumberBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.afterTextChanged
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel

class AddPhoneNumberFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment() {

    private lateinit var addPhoneNumberFragmentBinding: FragmentAddPhoneNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addPhoneNumberFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_phone_number, container, false
        )
        return addPhoneNumberFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        setup()
        super.onActivityCreated(savedInstanceState)
    }

    override fun setup() {
        addPhoneNumberFragmentBinding.lifecycleOwner = this
        addPhoneNumberFragmentBinding.viewModel = userAuthenticateViewModel
        mView = addPhoneNumberFragmentBinding.addPhoneLayout
        userAuthenticateViewModel.initPhoneFragment()

        userAuthenticateViewModel.hideKeyboardObservable.observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        userAuthenticateViewModel.onPhoneCreatedObservable.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                requestUserOTP()
            }
        })
        addPhoneNumberFragmentBinding.edtAddPhoneNumber.afterTextChanged {
            userAuthenticateViewModel.phoneNumberErrorMsg.value = AppConstants.EMPTY_VALUE
        }
    }

    private fun requestUserOTP() {
        showLoading()
        userAuthenticateViewModel.executeRequestOTP().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        MixPanelData.getInstance(requireActivity())
                            .addEvent(
                                MixPanelData.KEY_EMAIL,
                                userAuthenticateViewModel.emailField.value.toString(),
                                MixPanelData.eventRequestOTP
                            )
                        replaceFragment(
                            R.id.frame_layout,
                            OtpVerificationFragment.newInstance(userAuthenticateViewModel),
                            getString(R.string.emailVerify), true
                        )
                    }
                    ResponseStatus.BAD_PARAMS, ResponseStatus.CONFLICT_USER_INPUTS -> {
                        userAuthenticateViewModel.phoneNumberErrorMsg.value = response.message
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.FAILURE -> {
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
            AddPhoneNumberFragment(userAuthenticateViewModel).apply {
            }
    }
}
