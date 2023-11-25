package com.example.lysnclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.model.DashboardUser
import com.example.lysnclient.model.LogoutResponse
import com.example.lysnclient.model.UserTransactions
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import java.util.ArrayList

class HomeDashboardViewModel(val appRepository: AppRepository) : ViewModel() {
    var navigateToDetailObservable = SingleLiveEvent<Int>()
    var listOfYouScreen = ArrayList<DashboardUser>()
    private var mLastClickTime: Long = 0
    var onLogoutUserObservable = SingleLiveEvent<Boolean>()

    fun onAssessmentItemClick(itemPosition: Int) {
        if (!AppConstants.allowToPerformClick(mLastClickTime)) return
        mLastClickTime = AppConstants.getCurrentTimeMills()
        navigateToDetailObservable.value = itemPosition

        if (itemPosition == 4) {
            onLogoutUserObservable.value = true
        }
    }

    fun callUserLogoutAPI(refreshToken : String): MutableLiveData<BaseResponse<LogoutResponse>> {
        return appRepository.callUserLogoutAPI(refreshToken)
    }
}
