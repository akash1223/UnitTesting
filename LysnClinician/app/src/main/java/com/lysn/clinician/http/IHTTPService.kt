package com.lysn.clinician.http

import com.lysn.clinician.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IHTTPService {

    @POST(HttpConstants.METHOD_POST_SIGN_IN)
    @FormUrlEncoded
    suspend fun callSignInUser(
        @Field(HttpConstants.REQUEST_PARAM_EMAIL) email: String,
        @Field(HttpConstants.REQUEST_PARAM_PASSWORD) password: String
    ): Response<UserAuthResponse>

    @POST(HttpConstants.METHOD_POST_REFRESH_TOKEN)
    @FormUrlEncoded
    fun callRefreshAccessToken(
        @Field(HttpConstants.REQUEST_PARAM_REFRESH) refreshToken: String
    ): Call<UserAuthResponse>

    @GET(HttpConstants.METHOD_USER_PROFILE)
    suspend fun callGetUserProfile(): Response<UserProfileResponse>

    @GET(HttpConstants.METHOD_GET_CONSULTATION_DETAILS)
    suspend fun callGetConsultationDetails(): Response<ConsultationsDetailsResponse>

    @PATCH(HttpConstants.METHOD_PATCH_CANCEL_CONSULTATION)
    suspend fun callCancelConsultation(@Body data: Empty, @Path("id") id:String): Response<CancelConsultationDetails>

    @GET(HttpConstants.METHOD_GET_JOIN_CONSULTATION)
    suspend fun callJoinConsultation( @Path("id") id:String): Response<VideoSessionTokenResponse>

    @POST(HttpConstants.METHOD_POST_LOGOUT)
    @FormUrlEncoded
    suspend fun callLogoutUser(@Field(HttpConstants.REQUEST_PARAM_REFRESH) refresh: String): Response<ResponseBody>

    @PATCH(HttpConstants.METHOD_USER_PROFILE)
    suspend fun callAllowNotification(@Body allowNotificationRequestData: AllowNotificationRequestData): Response<UserProfileResponse>


}