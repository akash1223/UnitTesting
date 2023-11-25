package com.lysn.clinician.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.repository.SignInRepository
import com.lysn.clinician.http.Resource
import com.lysn.clinician.utils.*
import com.lysn.clinician.utils.Validator.validateEmailAddress
import kotlinx.coroutines.launch


class SignInViewModel(
    private val signInRepository: SignInRepository,
    private val localizeTextProvider: LocalizeTextProvider,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    var emailField = MutableLiveData(AppConstants.EMPTY_VALUE)
    var passwordField = MutableLiveData(AppConstants.EMPTY_VALUE)
    var emailErrorMsg = MutableLiveData(AppConstants.EMPTY_VALUE)
    private var hideKeyboardObservable = MutableLiveData(false)
    var errorMessageLiveData = MutableLiveData<String>(AppConstants.EMPTY_VALUE)
    val onForgotPasswordClickObservable = SingleLiveEvent<Boolean>()
    val onMixPanelEventObservable = SingleLiveEvent<Boolean>()
    private var _signInResponseLiveData = MutableLiveData<Resource<UserAuthResponse>>()
    val signInResponseLiveData: LiveData<Resource<UserAuthResponse>>
        get() = _signInResponseLiveData

    fun signInCallUser() {
        viewModelScope.launch {
            _signInResponseLiveData.postValue(Resource.loading())
            _signInResponseLiveData.postValue(
                signInRepository.executeSignInUser(
                    emailField.value.toString().trim(),
                    passwordField.value.toString()
                )
            )
        }
    }

    fun getUserData():LiveData<Resource<UserAuthResponse>>{
        return signInResponseLiveData
    }

    fun onBtnSignInClickListener() {
        onMixPanelEventObservable.value = true
        hideKeyboard()
        clearErrorMessages()
        if (validateEmailAddress(emailField.value.toString())) {
            signInCallUser()
            clearErrorMessages()

        } else {
            emailErrorMsg.value =
                localizeTextProvider.getInvalidEmailMessage()
        }
    }

    fun onForgotPasswordClickListener() {
        onForgotPasswordClickObservable.value = true
    }

    private fun hideKeyboard() {
        hideKeyboardObservable.value = !(hideKeyboardObservable.value)!!
    }

    // Save tokens locally in shared preference
    fun saveDataInPreference(signData: UserAuthResponse?) {
        signData?.let {
            preferenceUtil.putValue(PreferenceUtil.ACCESS_TOKEN_PREFERENCE_KEY, signData.access)
            preferenceUtil.putValue(PreferenceUtil.REFRESH_TOKEN_PREFERENCE_KEY, signData.refresh)
        }
    }

    fun showErrorMessage() {
        errorMessageLiveData.value = localizeTextProvider.getLoginFailMessage()
    }


    fun clearErrorMessages() {
        emailErrorMsg.value = AppConstants.EMPTY_VALUE
        errorMessageLiveData.value = AppConstants.EMPTY_VALUE
    }


}
