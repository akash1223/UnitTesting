package com.lysn.clinician.repository

import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.AllowNotificationRequestData
import com.lysn.clinician.model.UserProfileResponse
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.PreferenceUtil
import okhttp3.ResponseBody

class ProfileRepository(private val httpIService: IHTTPService, localizeTextProvider: LocalizeTextProvider,private val preferenceUtil: PreferenceUtil) :
    BaseRepository(localizeTextProvider) {

    // API call for logout user
    suspend fun executeLogoutUser() : Resource<ResponseBody> {
        return getResult {  httpIService.callLogoutUser(preferenceUtil.getValue(PreferenceUtil.REFRESH_TOKEN_PREFERENCE_KEY, AppConstants.EMPTY_VALUE))}
    }

    // API call for allow notifications
    suspend fun executeAllowNotification(allowNotificationRequestData: AllowNotificationRequestData) : Resource<UserProfileResponse> {
        return getResult {  httpIService.callAllowNotification(allowNotificationRequestData)}
    }
}