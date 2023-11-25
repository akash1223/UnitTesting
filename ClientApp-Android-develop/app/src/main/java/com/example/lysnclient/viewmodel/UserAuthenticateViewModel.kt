package com.example.lysnclient.viewmodel

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.BuildConfig
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.LocalizeTextProvider
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.view.TermsAndConditionActivity
import timber.log.Timber

class UserAuthenticateViewModel(
    private val appRepository: AppRepository,
    private val localizeProvider: LocalizeTextProvider
) : ViewModel() {

    var emailField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var emailErrorMsg = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    var createPasswordField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var createPasswordErrorMsg = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    var phoneNumberField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var phoneNumberErrorMsg = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var countryCodeField = MutableLiveData<String>(BuildConfig.COUNTRY_CODE)

    var otpCodeField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    var otpCodeFieldErrorMsg = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    var loginPasswordField = MutableLiveData<String>(AppConstants.EMPTY_VALUE)

    val onEmailValidatedObservable = SingleLiveEvent<Boolean>()
    var onPasswordCreatedObservable = SingleLiveEvent<Boolean>()
    var onPhoneCreatedObservable = SingleLiveEvent<Boolean>()
    var onOtpVerifiedObservable = SingleLiveEvent<Boolean>()
    var onLoginPasswordAddedObservable = SingleLiveEvent<Boolean>()
    var onNavForgotPassObservable = SingleLiveEvent<Boolean>()

    var hideKeyboardObservable = MutableLiveData<Boolean>(false)
    var navigateBackObservable = SingleLiveEvent<Boolean>()
    var onResendOtpObservable = SingleLiveEvent<Boolean>()
    private var mLastClickTime: Long = 0

    fun executeVerifyEmail(): MutableLiveData<BaseResponse<EmailVerifyResponse>> {
        return appRepository.verifyEmailAPI(emailField.value.toString())

    }

    fun executeVerifyPassword(): MutableLiveData<BaseResponse<PasswordVerifyResponse>> {
        return appRepository.verifyPasswordAPI(createPasswordField.value.toString())
    }

    fun executeRequestOTP(): MutableLiveData<BaseResponse<RequestOTPResponse>> {
        return appRepository.requestOtpAPI("${countryCodeField.value.toString()} ${phoneNumberField.value.toString()}")
    }

    fun executeVerifyOTP(): MutableLiveData<BaseResponse<VerifyOTPResponse>> {
        return appRepository.verifyOtpAPI(
            "${countryCodeField.value.toString()} ${phoneNumberField.value.toString()}",
            otpCodeField.value.toString()
        )
    }

    fun executeUserLogin(): MutableLiveData<BaseResponse<UserAuthResponse>> {
        return appRepository.executeUserLoginAPI(
            emailField.value.toString(),
            loginPasswordField.value.toString()
        )
    }

    fun onBtnEmailVerifyClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        hideKeyboard()
        emailErrorMsg.value = AppConstants.EMPTY_VALUE
        if (validateEmailAddress()) {
            onEmailValidatedObservable.value = true
        } else {
            emailErrorMsg.value =
                localizeProvider.getInvalidEmailMessage()
        }
    }

    fun onBtnCreatePasswordClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        hideKeyboard()
        createPasswordErrorMsg.value = AppConstants.EMPTY_VALUE
        onPasswordCreatedObservable.value = true
    }

    fun onBtnLoginClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        hideKeyboard()
        onLoginPasswordAddedObservable.value = true
    }

    fun onBtnAddMobileClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        phoneNumberErrorMsg.value = AppConstants.EMPTY_VALUE
        hideKeyboard()
        if (validatePhoneNumber()) {
            onPhoneCreatedObservable.value = true
        } else {
            phoneNumberErrorMsg.value =
                localizeProvider.getInvalidMobileNumberMessage()
        }
    }

    fun onBtnOtpVerifyClickListener() {
        Timber.d("emailField ${emailField.value}")
        Timber.d("createPasswordField ${createPasswordField.value}")
        Timber.d("phoneNumberField ${phoneNumberField.value}")
        Timber.d("otpCodeField ${otpCodeField.value}")

        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()

        otpCodeFieldErrorMsg.value = AppConstants.EMPTY_VALUE
        hideKeyboard()
        if (validateOtp()) {
            onOtpVerifiedObservable.value = true
        } else {
            otpCodeFieldErrorMsg.value =
                localizeProvider.getInvalidOtpMessage()
        }
    }

    fun onBtnNavForgotPassClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onNavForgotPassObservable.value = true
    }

    fun onBtnResentOtpClickListener() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onResendOtpObservable.value = true
    }

    fun initEmailFragment() {
        emailField.value = AppConstants.EMPTY_VALUE
        emailErrorMsg.value = AppConstants.EMPTY_VALUE
        onEmailValidatedObservable.call()
    }

    fun initCreatePasswordFragment() {
        createPasswordField.value = AppConstants.EMPTY_VALUE
        createPasswordErrorMsg.value = AppConstants.EMPTY_VALUE
        onPasswordCreatedObservable.call()
    }

    fun initPhoneFragment() {
        phoneNumberField.value = AppConstants.EMPTY_VALUE
        phoneNumberErrorMsg.value = AppConstants.EMPTY_VALUE
        onPhoneCreatedObservable.call()
    }

    fun initOtpFragment() {
        otpCodeField.value = AppConstants.EMPTY_VALUE
        otpCodeFieldErrorMsg.value = AppConstants.EMPTY_VALUE
        onOtpVerifiedObservable.call()
        onResendOtpObservable.call()
    }

    fun initLoginFragment() {
        loginPasswordField.value = AppConstants.EMPTY_VALUE
        onLoginPasswordAddedObservable.call()
    }

    fun initForgotPassFragment() {
        onNavForgotPassObservable.call()
    }

    fun navigateBack() {
        navigateBackObservable.value = true
    }

    private fun validateEmailAddress(): Boolean {
        return emailField.value.toString().trim()
            .matches(AppConstants.EMAIL_ADDRESS_PATTERN.toRegex())
    }

    private fun validatePhoneNumber(): Boolean {
        val field = phoneNumberField.value?.toString() ?: AppConstants.EMPTY_VALUE
        if (field.trim().isEmpty()) {
            return false
        }
        return true
//        return phoneNumberField.value?.toString()?.length == AppConstants.MOBILE_NUMBER_LENGTH
    }

    private fun validateOtp(): Boolean {
        return otpCodeField.value?.toString()?.length == AppConstants.OTP_CODE_LENGTH
    }

    private fun hideKeyboard() {
        hideKeyboardObservable.value = !(hideKeyboardObservable.value)!!
    }

    fun openTermsAndConditionActivity(activity: FragmentActivity) {
        val intent =
            Intent(activity, TermsAndConditionActivity::class.java)
        intent.putExtra(
            AppConstants.SIGN_UP_EMAIL,
            emailField.value
        )
        intent.putExtra(
            AppConstants.SIGN_UP_PASSWORD,
            createPasswordField.value
        )
        intent.putExtra(
            AppConstants.SIGN_UP_PHONE,
            countryCodeField.value.toString() + AppConstants.EMPTY_VALUE +
                    phoneNumberField.value
        )
        activity.startActivity(intent)
    }

    fun saveTokenAndEmailInSharedPreference(
        requireActivity: FragmentActivity,
        response: UserAuthResponse?
    ) {
        PreferenceUtil.getInstance(requireActivity).saveValue(
            PreferenceUtil.KEY_ACCESS_TOKEN,
            response?.access
        )
        PreferenceUtil.getInstance(requireActivity).saveValue(
            PreferenceUtil.KEY_REFRESH_TOKEN,
            response?.refresh
        )
        PreferenceUtil.getInstance(requireActivity).saveValue(
            PreferenceUtil.KEY_USER_EMAIL,
            emailField.value
        )
        PreferenceUtil.getInstance(requireActivity).saveValue(
            PreferenceUtil.KEY_IS_USER_LOGGED_IN, true
        )
    }

    fun getUserProfile(): MutableLiveData<BaseResponse<UserProfile>> {
        return appRepository.callGetUserProfile()
    }
}
