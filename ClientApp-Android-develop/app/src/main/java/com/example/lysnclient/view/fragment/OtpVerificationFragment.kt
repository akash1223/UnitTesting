package com.example.lysnclient.view.fragment

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.lysnclient.R
import com.example.lysnclient.databinding.FragmentOtpVerificationBinding
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utility.OtpReceivedInterface
import com.example.lysnclient.utility.SmsBroadcastReceiver
import com.example.lysnclient.utils.MixPanelData
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.afterTextChanged
import com.example.lysnclient.viewmodel.UserAuthenticateViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

class OtpVerificationFragment(private val userAuthenticateViewModel: UserAuthenticateViewModel) :
    BaseFragment(), OtpReceivedInterface {
    private lateinit var otpVerificationFragmentBinding: FragmentOtpVerificationBinding
    private val mSmsBroadcastReceiver: SmsBroadcastReceiver by inject()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        otpVerificationFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_otp_verification, container, false
        )
        startSMSListener()
        return otpVerificationFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun setup() {
        otpVerificationFragmentBinding.lifecycleOwner = this
        otpVerificationFragmentBinding.viewModel = userAuthenticateViewModel
        mView = otpVerificationFragmentBinding.otpVerifyLayout
        userAuthenticateViewModel.initOtpFragment()
        userAuthenticateViewModel.hideKeyboardObservable.observe(viewLifecycleOwner, Observer {
            hideKeyboard()
        })

        userAuthenticateViewModel.onOtpVerifiedObservable.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                Timber.d("Navigate to terms & condition page")
                verifyUserOTP()
            }
        })

        userAuthenticateViewModel.onResendOtpObservable.observe(viewLifecycleOwner, Observer {
            if (it != null && it) {
                resendOtp()
            }
        })
        otpVerificationFragmentBinding.edtAddOtp.afterTextChanged {
            userAuthenticateViewModel.otpCodeFieldErrorMsg.value = AppConstants.EMPTY_VALUE
        }
    }

    override fun onResume() {
        mSmsBroadcastReceiver.setOnOtpListeners(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(mSmsBroadcastReceiver, intentFilter)
        super.onResume()
    }

    override fun onPause() {
        mSmsBroadcastReceiver.setOnOtpListeners(null)
        requireActivity().unregisterReceiver(mSmsBroadcastReceiver)
        super.onPause()
    }

    override fun onOtpReceived(otp: String?) {
        val pattern = Pattern.compile("\\d{6}")
        val matcher: Matcher = pattern.matcher(otp.toString())
        if (matcher.find()) {
            userAuthenticateViewModel.otpCodeField.value = matcher.group(0)
        }
    }

    override fun onOtpTimeout() {
//        showToast("Time out, please resend")
    }

    private fun startSMSListener() {
        val mClient = SmsRetriever.getClient(requireActivity())
        val mTask = mClient.startSmsRetriever()
        mTask.addOnSuccessListener {
            Timber.d("SMS Retriever starts")
        }
        mTask.addOnFailureListener {
            Timber.d("Error")
        }
    }

    private fun verifyUserOTP() {
        showLoading()
        userAuthenticateViewModel.executeVerifyOTP().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        MixPanelData.getInstance(requireActivity())
                            .addEvent(
                                MixPanelData.KEY_MOBILE,
                                userAuthenticateViewModel.emailField.value.toString(),
                                MixPanelData.eventOTPVerified
                            )
                        userAuthenticateViewModel.openTermsAndConditionActivity(requireActivity())
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.BAD_PARAMS -> {
                        userAuthenticateViewModel.otpCodeFieldErrorMsg.value = response.message
                    }
                    ResponseStatus.CONFLICT_USER_INPUTS -> {
                        showSnackMsg(response.message)
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

    private fun resendOtp() {
        userAuthenticateViewModel.otpCodeFieldErrorMsg.value = AppConstants.EMPTY_VALUE
        showLoading()
        userAuthenticateViewModel.executeRequestOTP().observe(viewLifecycleOwner,
            Observer { response ->
                hideLoading()
                MixPanelData.getInstance(requireActivity())
                    .addEvent(
                        MixPanelData.KEY_EMAIL,
                        userAuthenticateViewModel.emailField.value.toString(),
                        MixPanelData.eventReGenerateOtp
                    )
                when (response.status) {
                    ResponseStatus.SUCCESS -> {
                        startSMSListener()
                        userAuthenticateViewModel.otpCodeField.value = AppConstants.EMPTY_VALUE
                        showSnackMsg(getString(R.string.otp_resent_successfully), false)
                    }
                    ResponseStatus.NO_INTERNET -> {
                        showNoInternetDialog()
                    }
                    ResponseStatus.FAILURE -> {
                        showSnackMsg(response.message)
                    }
                    ResponseStatus.BAD_PARAMS, ResponseStatus.CONFLICT_USER_INPUTS -> {
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
            OtpVerificationFragment(userAuthenticateViewModel).apply {

            }
    }
}

