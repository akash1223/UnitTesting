package com.inmoment.moments.login.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.datamodel.UserDataFromOAuth
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.AUTH_STATE_PREFERENCE_KEY
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_STRING_DEFAULT
import com.inmoment.moments.login.UserSignConfigAuthRequestWrapper
import com.inmoment.moments.login.UserSignConfigWrapper
import com.inmoment.moments.login.UserSignManager
import com.inmoment.moments.login.model.AccessToken
import com.inmoment.moments.login.model.UserDetails
import com.lysn.clinician.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPrefsInf: SharedPrefsInf,
    private val userSignManager: UserSignManager
) :
    ViewModel() {

    private var emailErrorMsg = ""
    private var passwordErrorMsg = ""

    val emailErrorMsgMLD = MutableLiveData("")
    val passwordErrorMsgMLD = MutableLiveData("")
    private val _makeSignInRequest = MutableLiveData<Boolean>()
    private val onDatabaseUpdate = SingleLiveEvent<Boolean>()
    val makeSignInRequest: LiveData<Boolean>
        get() = _makeSignInRequest

    private val _isFormValid = MutableLiveData<Boolean>()

    val isFormValid: LiveData<Boolean>
        get() = _isFormValid


    var email = ""
        set(value) {
            field = value
            validateForm()
        }

    var password = ""
        set(value) {
            field = value
            validateForm()
        }

    fun setErrorMessages(emailErrorMsg: String, passwordErrorMsg: String) {
        this.emailErrorMsg = emailErrorMsg
        this.passwordErrorMsg = passwordErrorMsg
    }

    private fun validateForm() {
        if (password.trim().isNotEmpty() && email.trim()
                .isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
                email.trim()
            ).matches()
        ) {
            _isFormValid.postValue(true)
        } else {
            emailErrorMsgMLD.value = ""
            passwordErrorMsgMLD.value = ""
            _isFormValid.postValue(false)
        }
    }

    fun signInUser() {
        if (_isFormValid.value == true) {
            _makeSignInRequest.value = true
        } else {
            if (email.trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                emailErrorMsgMLD.value = emailErrorMsg
            }
            if (password.trim().isEmpty()) {
                passwordErrorMsgMLD.value = passwordErrorMsg
            }
        }
    }

    fun saveAuthTokensToSharedPref(
        response: TokenResponse,
        ex: AuthorizationException?,
        authorizationResponse: AuthorizationResponse? = null
    ): OperationResult<String> {
        var authState = AuthState()
        if (authorizationResponse == null) {
            authState.update(getAuthResponse(response), ex)
        }
        authState.update(response, ex)
        val authJsonString = authState.jsonSerializeString()
        sharedPrefsInf.put(AUTH_STATE_PREFERENCE_KEY, authJsonString)

        return userSignManager.saveAccessTokenToSharedPref(
            AccessToken(
                response.accessToken!!,
                Calendar.getInstance().timeInMillis
            ), viewModelScope
        )
    }

    fun saveQuery(email: String) {
        sharedPrefsInf.put(SharedPrefsInf.PREF_USER_EMAIL_ID, email)
    }

    fun getUserSignManager(userDetails: UserDetails): OperationResult<UserSignConfigWrapper> {
        return userSignManager.getSignInConfig(userDetails, viewModelScope)
    }

    public fun getAuthResponse(tokenResponse: TokenResponse): AuthorizationResponse {
        return userSignManager.getAuthResponse(tokenResponse)
    }

    fun getUserSignAuthManager(userDetails: UserDetails): OperationResult<UserSignConfigAuthRequestWrapper> {
        return userSignManager.getSignInAuthConfig(userDetails, viewModelScope)
    }

    fun getUserInfoFromOAuth(): OperationResult<UserDataFromOAuth> {
        return userSignManager.getUserInfoFromOAuth(viewModelScope)
    }

    fun checkLastLoginDetails(loginUser: String): SingleLiveEvent<Boolean> {

        val getLastLoginUser =
            sharedPrefsInf.get(SharedPrefsInf.PREF_USER_EMAIL_ID, PREF_STRING_DEFAULT)
        if (getLastLoginUser != loginUser) {
            sharedPrefsInf.clearAllSharedPrefs()
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    userSignManager.deleteDatabase()
                }
                onDatabaseUpdate.value = true
            }
        } else {
            onDatabaseUpdate.value = true
        }
        return onDatabaseUpdate
    }

}