package com.lysn.clinician.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.lysn.clinician.http.Resource
import com.lysn.clinician.repository.ProfileRepository
import com.lysn.clinician.repository.SignInRepository
import com.lysn.clinician.utils.PreferenceUtil
import com.lysn.clinician.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ProfileViewModel(
    private val mProfileRepository: ProfileRepository,
    private val mSignInRepository: SignInRepository,
    private val preferenceUtil: PreferenceUtil

) : ViewModel() {

    val onLogoutObservable = SingleLiveEvent<Boolean>()
    private var _mLogoutResponseDetails = MutableLiveData<Resource<ResponseBody>>()
    private val mLogoutResponseDetails: LiveData<Resource<ResponseBody>>
        get() = _mLogoutResponseDetails

    init {
        fetchUserProfile()
    }

    fun onLogoutUserClickListener() {
        onLogoutObservable.value = true
    }

    fun logoutUser(): LiveData<Resource<ResponseBody>> {
        _mLogoutResponseDetails.value = (Resource.loading(null))
        viewModelScope.launch {
            _mLogoutResponseDetails.postValue(
                mProfileRepository.executeLogoutUser(
                )
            )
        }
        return mLogoutResponseDetails
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            val response = mSignInRepository.getUserProfile()
            if(response.status == Resource.Status.SUCCESS)
            {
                val profileData:String = Gson().toJson(response.data)
                preferenceUtil.putValue(PreferenceUtil.USER_PROFILE_PREFERENCE_KEY,profileData)
            }
        }
    }

}