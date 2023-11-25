package com.lysn.clinician.ui.terms_condition

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.lysn.clinician.http.Resource
import com.lysn.clinician.repository.SignInRepository
import com.lysn.clinician.ui.base.BaseViewModel
import com.lysn.clinician.utils.PreferenceUtil
import com.lysn.clinician.utils.SingleLiveEvent
import kotlinx.coroutines.launch


class TermsAndConditionViewModel(private val signInRepository: SignInRepository,private val preferenceUtil: PreferenceUtil) : BaseViewModel() {
    var onTermsAndConditionObservable = SingleLiveEvent<Boolean>()
    var onReviewObservable = MutableLiveData<Boolean>(false)

    fun onBtnReviewAndAcceptTermsClick() {
        onTermsAndConditionObservable.value = true
    }
    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
              val response = signInRepository.getUserProfile()
            if(response.status == Resource.Status.SUCCESS)
            {
                val profileData:String = Gson().toJson(response.data)
                preferenceUtil.putValue(PreferenceUtil.USER_PROFILE_PREFERENCE_KEY,profileData)
            }
        }
    }
}
