package com.example.lysnclient.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentCreatePasswordBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.afterTextChanged
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel

class CreatePasswordFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment() {

    private lateinit var createPasswordFragmentBinding: FragmentCreatePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createPasswordFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_password, container, false
        )
        return createPasswordFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        createPasswordFragmentBinding.lifecycleOwner = this
        createPasswordFragmentBinding.viewModel = userAuthenticateViewModel
        mView = createPasswordFragmentBinding.createPasswordLayout
        userAuthenticateViewModel.initCreatePasswordFragment()

        userAuthenticateViewModel.hideKeyboardObservable.observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        userAuthenticateViewModel.onPasswordCreatedObservable.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null && it) {
                    validatePassword()
                }
            })
        createPasswordFragmentBinding.edtCreatePassword.afterTextChanged {
            userAuthenticateViewModel.createPasswordErrorMsg.value = AppConstants.EMPTY_VALUE
        }
    }

    private fun validatePassword() {
        showLoading()
        userAuthenticateViewModel.executeVerifyPassword().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        MixPanelData.getInstance(requireActivity())
                            .addEvent(
                                MixPanelData.KEY_EMAIL,
                                userAuthenticateViewModel.emailField.value.toString(),
                                MixPanelData.eventPasswordValidation
                            )
                        replaceFragment(
                            R.id.frame_layout,
                            AddPhoneNumberFragment.newInstance(userAuthenticateViewModel),
                            getString(R.string.createPassword), true
                        )
                    }
                    ResponseStatus.BAD_PARAMS -> {
                        userAuthenticateViewModel.createPasswordErrorMsg.value =
                            response.message
                    }
                    ResponseStatus.FAILURE -> {
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

    companion object {
        @JvmStatic
        fun newInstance(userAuthenticateViewModel: UserAuthenticateViewModel) =
            CreatePasswordFragment(userAuthenticateViewModel).apply {

            }
    }
}
