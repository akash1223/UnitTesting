package com.inmoment.moments.userprofile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inmoment.moments.framework.datamodel.UserProfileResponseData
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.userprofile.UserProfileService
import com.lysn.clinician.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileService: UserProfileService,
    private val sharedPrefsInf: SharedPrefsInf

) :
    ViewModel() {

    private val onDatabaseUpdate = SingleLiveEvent<Boolean>()

    fun getUserInfo(): OperationResult<UserProfileResponseData> {
        return userProfileService.getUserDetails(viewModelScope)
    }

    fun getUserFirstName(): String {
        return sharedPrefsInf.get(
            SharedPrefsInf.PREF_FIRST_NAME,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
    }

    fun getUserLastName(): String {
        return sharedPrefsInf.get(SharedPrefsInf.PREF_LAST_NAME, SharedPrefsInf.PREF_STRING_DEFAULT)
    }

    fun logout() {
        userProfileService.logout()
    }
}