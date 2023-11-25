package com.lysn.clinician.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.AllowNotificationRequestData
import com.lysn.clinician.model.UserProfileResponse
import com.lysn.clinician.repository.ProfileRepository
import com.lysn.clinician.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class SettingsViewModel(private val mRepository: ProfileRepository) :ViewModel()
{
    val onSaveSettingsObservable = SingleLiveEvent<Boolean>()
    private var _mAllowNotificationResponseDetails = MutableLiveData<Resource<UserProfileResponse>>()
    private val mAllowNotificationResponseDetails: LiveData<Resource<UserProfileResponse>>
        get() = _mAllowNotificationResponseDetails


    fun onSaveSettingsOnClickListener(){
        onSaveSettingsObservable.value = true
    }

    fun saveSettings(allowNotificationRequestData: AllowNotificationRequestData):LiveData<Resource<UserProfileResponse>>{
        _mAllowNotificationResponseDetails.value = (Resource.loading(null))
        viewModelScope.launch {
            _mAllowNotificationResponseDetails.postValue(
                mRepository.executeAllowNotification(allowNotificationRequestData)

            )
        }
        return mAllowNotificationResponseDetails
    }

}