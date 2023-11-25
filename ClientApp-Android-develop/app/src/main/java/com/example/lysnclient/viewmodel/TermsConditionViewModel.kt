package com.example.lysnclient.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.model.SignUpResponse
import com.example.lysnclient.model.UserAuthResponse
import com.example.lysnclient.model.UserProfile
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.PreferenceUtil

class TermsConditionViewModel(
    private val appRepository: AppRepository
) : ViewModel() {
    private var mLastClickTime: Long = 0
    var onTermsAndConditionObservable = SingleLiveEvent<Boolean>()
    var navigateBackObservable = SingleLiveEvent<Boolean>()
    var onReviewObservable = MutableLiveData<Boolean>(false)

    fun executeRegisterUserApi(
        email: String,
        password: String,
        phoneNumber: String
    ): MutableLiveData<BaseResponse<SignUpResponse>> {
        return appRepository.registerUserAPI(
            email,
            password,
            phoneNumber,
            AppConstants.SIGN_UP_ACCEPT_TERMS
        )
    }

    fun onBtnReviewAndAcceptTermsClick() {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        onTermsAndConditionObservable.value = true
    }

    fun navigateBack() {
        navigateBackObservable.value = true
    }

    fun saveTokenAndEmailInSharedPreference(
        requireActivity: FragmentActivity,
        response: UserAuthResponse?,
        email: String
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
            email
        )
        PreferenceUtil.getInstance(requireActivity).saveValue(
            PreferenceUtil.KEY_IS_USER_LOGGED_IN, true
        )
    }
}
