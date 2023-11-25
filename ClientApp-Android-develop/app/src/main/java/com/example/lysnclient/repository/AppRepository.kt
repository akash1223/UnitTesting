package com.example.lysnclient.repository

import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.http.IHttpService
import com.example.lysnclient.model.*
import com.example.lysnclient.utils.LocalizeTextProvider
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AppRepository(
    private val httpService: IHttpService,
    localizeProvider: LocalizeTextProvider
) : BaseRepository(localizeProvider) {


    fun verifyEmailAPI(email: String): MutableLiveData<BaseResponse<EmailVerifyResponse>> {
        return executeAPI(httpService.callVerifyEmailId(email))
    }

    fun verifyPasswordAPI(password: String): MutableLiveData<BaseResponse<PasswordVerifyResponse>> {
        return executeAPI(httpService.callVerifyPassword(password))
    }

    fun requestOtpAPI(phone: String): MutableLiveData<BaseResponse<RequestOTPResponse>> {
        return executeAPI(httpService.callRequestOTP(phone))
    }

    fun verifyOtpAPI(
        phone: String,
        verificationCode: String
    ): MutableLiveData<BaseResponse<VerifyOTPResponse>> {
        return executeAPI(httpService.callVerifyOTP(phone, verificationCode))
    }

    fun registerUserAPI(
        email: String,
        password: String,
        phone: String,
        approvedTerms: Boolean
    ): MutableLiveData<BaseResponse<SignUpResponse>> {
        return executeAPI(httpService.callRegisterUser(email, password, phone, approvedTerms))
    }

    fun executeUserLoginAPI(
        email: String,
        password: String
    ): MutableLiveData<BaseResponse<UserAuthResponse>> {
        return executeAPI(httpService.callLoginUser(email, password))
    }

    fun executeGetListOfAssessment(): MutableLiveData<BaseResponse<List<AssessmentType>>> {
        return executeAPI(httpService.callGetAssessmentsList())
    }

    fun submitAssessmentAPI(
        assessmentDataRequest: AssessmentDataRequest
    ): MutableLiveData<BaseResponse<SubmitAssessmentResponse>> {
        return executeAPI(httpService.callSubmitAssessmentAPI(assessmentDataRequest))
    }

    fun getWBTQuestions(): MutableLiveData<BaseResponse<ConfigurationData>> {
        return executeAPI(httpService.callGetWBTQuestions())
    }

    fun callGetUserProfile(): MutableLiveData<BaseResponse<UserProfile>> {
        return executeAPI(httpService.callGetUserProfile())
    }

    fun submitWBTUserResponse(
        userId: Int,
        jsonObject: JsonObject
    ): MutableLiveData<BaseResponse<WBTSubmitResponse>> {
        return executeAPI(httpService.callPostWBTAnswer(jsonObject, userId))
    }

    fun getWBTOutputScreenList(userId: Int): MutableLiveData<BaseResponse<WBTOutputScreenResponse>> {
        return executeAPI(httpService.callGetWBTOutputScreenList(userId))
    }

    fun callUserLogoutAPI(refreshToken: String): MutableLiveData<BaseResponse<LogoutResponse>> {
        return executeAPI(httpService.callLogoutUserAPI(refreshToken))
    }

    fun fetchDummyData(): MutableLiveData<List<SubmitAssessmentResponse>> {
        var data = MutableLiveData<List<SubmitAssessmentResponse>>()

        httpService.dummyGetMethod().enqueue(object : Callback<List<SubmitAssessmentResponse>> {
            override fun onFailure(call: Call<List<SubmitAssessmentResponse>>, t: Throwable) {
                data = MutableLiveData()
                Timber.e("failure: ${t.message}")
            }

            override fun onResponse(
                call: Call<List<SubmitAssessmentResponse>>,
                response: Response<List<SubmitAssessmentResponse>>
            ) {
                data.value = response.body()
                Timber.e("Response: $response")
            }
        })
        return data
    }
}
