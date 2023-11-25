package com.example.lysnclient.http

import com.example.lysnclient.model.*
import com.example.lysnclient.utils.HttpConstants
import com.example.lysnclient.utils.Utilities
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


/**
 *  This file contains all the http rest Apis
 */

interface IHttpService {
    @GET("marvel")
    fun dummyGetMethod(): Call<List<SubmitAssessmentResponse>>

    //    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST(HttpConstants.METHOD_POST_VERIFY_EMAIL)
    @FormUrlEncoded
    fun callVerifyEmailId(@Field(HttpConstants.REQUEST_PARAM_EMAIL) email: String): Call<EmailVerifyResponse>

    @POST(HttpConstants.METHOD_POST_VERIFY_PASSWORD)
    @FormUrlEncoded
    fun callVerifyPassword(@Field(HttpConstants.REQUEST_PARAM_PASSWORD) password: String): Call<PasswordVerifyResponse>

    @POST(HttpConstants.METHOD_POST_REQUEST_FOR_OTP)
    @FormUrlEncoded
    fun callRequestOTP(@Field(HttpConstants.REQUEST_PARAM_OTP) phone: String): Call<RequestOTPResponse>

    @POST(HttpConstants.METHOD_POST_VERIFY_OTP)
    @FormUrlEncoded
    fun callVerifyOTP(
        @Field(HttpConstants.REQUEST_PARAM_OTP) phone: String,
        @Field(HttpConstants.REQUEST_PARAM_VALIDATE_OTP_VERIFY_CODE) verificationCode: String
    ): Call<VerifyOTPResponse>

    @POST(HttpConstants.METHOD_POST_REGISTER)
    @FormUrlEncoded
    fun callRegisterUser(
        @Field(HttpConstants.REQUEST_PARAM_EMAIL) email: String,
        @Field(HttpConstants.REQUEST_PARAM_PASSWORD) password: String,
        @Field(HttpConstants.REQUEST_PARAM_PHONE) phone: String,
        @Field(HttpConstants.REQUEST_PARAM_APPROVED_TERMS) approvedTerms: Boolean,
        @Field(HttpConstants.REQUEST_PARAM_TIMEZONE) timezone: String = Utilities.getDeviceTimeZoneID(),
        @Field(HttpConstants.REQUEST_PARAM_SOURCE) source: String = HttpConstants.REQUEST_PARAM_SOURCE_VALUE,
        @Field(HttpConstants.REQUEST_PARAM_USER_TYPE) userType: String = HttpConstants.REQUEST_PARAM_USER_TYPE_VALUE
    ): Call<SignUpResponse>

    @POST(HttpConstants.METHOD_POST_LOGIN)
    @FormUrlEncoded
    fun callLoginUser(
        @Field(HttpConstants.REQUEST_PARAM_EMAIL) email: String,
        @Field(HttpConstants.REQUEST_PARAM_PASSWORD) password: String
    ): Call<UserAuthResponse>

    @POST(HttpConstants.METHOD_POST_REFRESH_TOKEN)
    @FormUrlEncoded
    fun callRefreshAccessToken(
        @Field(HttpConstants.REQUEST_PARAM_REFRESH) refreshToken: String
    ): Call<UserAuthResponse>

    @GET(HttpConstants.METHOD_GET_ASSESSMENTS_LIST)
    fun callGetAssessmentsList(): Call<List<AssessmentType>>

    @POST(HttpConstants.METHOD_POST_ASSESSMENTS_QUESTION_ANSWER)
    fun callSubmitAssessmentAPI(@Body assessmentDataRequest: AssessmentDataRequest): Call<SubmitAssessmentResponse>

    @GET(HttpConstants.METHOD_GET_WBT_QUESTIONS)
    fun callGetWBTQuestions(): Call<ConfigurationData>

    @GET(HttpConstants.METHOD_GET_USER_PROFILE)
    fun callGetUserProfile(): Call<UserProfile>

    @POST(HttpConstants.METHOD_POST_WBT_ANSWER)
    fun callPostWBTAnswer(@Body data: JsonObject, @Path("id") id: Int): Call<WBTSubmitResponse>

    @GET(HttpConstants.METHOD_GET_WBT_OUTPUT_SCREEN_LIST)
    fun callGetWBTOutputScreenList(@Path("id") id: Int): Call<WBTOutputScreenResponse>

    @POST(HttpConstants.METHOD_POST_LOGOUT)
    @FormUrlEncoded
    fun callLogoutUserAPI(@Field(HttpConstants.REQUEST_PARAM_REFRESH) refreshToken: String): Call<LogoutResponse>

}
