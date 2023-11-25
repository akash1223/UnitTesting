package com.lysn.clinician.repository

import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.UserProfileResponse

/**
 * This class is used for fetch user data through web services
 */
class SignInRepository(private val httpIService: IHTTPService,localizeTextProvider: LocalizeTextProvider) :
    BaseRepository(localizeTextProvider) {

    // API call for sign in User
    suspend fun executeSignInUser(email: String, password: String): Resource<UserAuthResponse> {
        return getResult {  httpIService.callSignInUser(email,password)}
    }

    suspend fun getUserProfile(): Resource<UserProfileResponse> {
        return getResult {  httpIService.callGetUserProfile()}
    }

}