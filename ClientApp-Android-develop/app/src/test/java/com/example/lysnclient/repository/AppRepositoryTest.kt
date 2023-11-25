package com.example.lysnclient.repository

import com.example.lysnclient.http.IHttpService
import com.example.lysnclient.model.*
import com.example.lysnclient.utils.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.mock
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class AppRepositoryTest {
    private lateinit var iHttpService: IHttpService
    private val mockWebServer: MockWebServer = MockWebServer()
    private lateinit var repository: AppRepository

    @Before
    fun before() {
        mockWebServer.start()
        iHttpService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
            .create(IHttpService::class.java)
        repository = AppRepository(mock(), mock())
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test_callVerifyEmailId()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyEmailId(TestData.TEST_VALID_EMAIL)
        val response = call.execute()
        val emailResponse: EmailVerifyResponse? = response.body()
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(emailResponse?.email.toString() == TestData.TEST_VALID_EMAIL)
    }

    @Test
    fun `test_callVerifyEmailId()_return_409_email_already_in_used`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyEmailId(TestData.TEST_VALID_EMAIL)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_CONFLICT_INPUT_409)
    }

    @Test
    fun `test_callVerifyPassword()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyPassword(TestData.TEST_VALID_PASSWORD)
        val response = call.execute()
        val passwordVerifyResponse: PasswordVerifyResponse? = response.body()
        assertNotNull(passwordVerifyResponse)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
    }

    @Test
    fun `test_callVerifyPassword()_return_bad_params_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyPassword(TestData.TEST_INVALID_PASSWORD)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }


    @Test
    fun `test_callRequestOTP()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callRequestOTP(TestData.TEST_VALID_MOBILE)
        val response = call.execute()
        val otpResponse: RequestOTPResponse? = response.body()
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(otpResponse?.phone.toString() == TestData.TEST_VALID_MOBILE)
    }

    @Test
    fun `test_callRequestOTP()_return_bad_params_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callRequestOTP(TestData.TEST_INVALID_MOBILE)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }

    @Test
    fun `test_callVerifyOTP()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyOTP(TestData.TEST_VALID_OTP, TestData.TEST_VALID_MOBILE)
        val response = call.execute()
        val otpResponse: VerifyOTPResponse? = response.body()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(otpResponse?.phone.toString() == TestData.TEST_VALID_MOBILE)
        assertTrue(otpResponse?.verificationCode.toString() == TestData.TEST_VALID_OTP)
    }

    @Test
    fun `test_callVerifyOTP()_return_bad_params_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callVerifyOTP(TestData.TEST_INVALID_OTP, TestData.TEST_VALID_MOBILE)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }

    @Test
    fun `test_callRegisterUser()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callRegisterUser(
            TestData.TEST_VALID_EMAIL, TestData.TEST_VALID_PASSWORD,
            TestData.TEST_VALID_MOBILE, AppConstants.SIGN_UP_ACCEPT_TERMS
        )
        val response = call.execute()
        val emailResponse: SignUpResponse? = response.body()
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(emailResponse?.userProfile?.id == TestData.TEST_VALID_USER_ID)
        assertTrue(emailResponse?.userTokens?.access.toString().isNotEmpty())
        assertTrue(emailResponse?.userTokens?.refresh.toString().isNotEmpty())
    }

    @Test
    fun `test_callRegisterUser()_return_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call =
            iHttpService.callRegisterUser(
                TestData.TEST_VALID_EMAIL, TestData.TEST_VALID_PASSWORD,
                AppConstants.EMPTY_VALUE, false
            )
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }

    @Test
    fun `test_callLoginUser()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call =
            iHttpService.callLoginUser(TestData.TEST_VALID_EMAIL, TestData.TEST_VALID_PASSWORD)
        val response = call.execute()
        val emailResponse: UserAuthResponse? = response.body()
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(emailResponse?.access.toString().isNotEmpty())
        assertTrue(emailResponse?.refresh.toString().isNotEmpty())
    }

    @Test
    fun `test_callLoginUser()_return_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call =
            iHttpService.callLoginUser(TestData.TEST_VALID_PASSWORD, TestData.TEST_VALID_PASSWORD)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }

    @Test
    fun `test_callRefreshAccessToken()_return_success`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call =
            iHttpService.callRefreshAccessToken(TestData.TEST_REFRESH_TOKEN)
        val response = call.execute()
        val refreshTokenResponse: UserAuthResponse? = response.body()
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(refreshTokenResponse?.access.toString().isNotEmpty())
        assertTrue(refreshTokenResponse?.refresh.toString().isNotEmpty())
    }

    @Test
    fun `test_callRefreshAccessToken()_return_failure`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call =
            iHttpService.callRefreshAccessToken(TestData.TEST_REFRESH_TOKEN)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }

    @Test
    fun `test_GetUserProfile()_return_success_with_user_data`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetUserProfile()
        val response = call.execute()
        val responseBody: UserProfile? = response.body()
        assertTrue(responseBody != null)
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        val userId = response.body()?.id
        val userData: UserData? = response.body()?.userData
        val activeConsultationsList: ArrayList<ActiveConsultations>? =
            response.body()?.userData?.listOfActiveConsultations
        val userTransactions: ArrayList<UserTransactions>? = response.body()?.listOfTransaction
        val userCoupons: ArrayList<UserCoupons>? = response.body()?.listOfCoupons
        assertTrue(userId != 0)
        assertNotNull(activeConsultationsList)
        assertNotNull(userTransactions)
        assertNotNull(userCoupons)
        assertTrue(userData != null)
    }

    @Test
    fun `test_GetUserProfile()_return_failure_with_401`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callGetUserProfile()
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }

    @Test
    fun `test_callGetAssessmentsList()_return_success_with_list_of_assessments`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetAssessmentsList()
        val response = call.execute()
        val listOfAssessmentType: List<AssessmentType>? = response.body()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertNotNull(listOfAssessmentType)
        assertTrue(listOfAssessmentType?.isNotEmpty() ?: false)
        assertTrue(listOfAssessmentType!![0].id.toString().isNotEmpty())
        assertTrue(listOfAssessmentType[0].code.isNotEmpty())
    }

    @Test
    fun `test_callGetAssessmentsList()_success_response_contain_list_of_question_and_single_choice_options`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetAssessmentsList()
        val response = call.execute()
        val listOfAssessmentType: List<AssessmentType>? = response.body()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertNotNull(listOfAssessmentType)
        assertTrue(listOfAssessmentType?.isNotEmpty() ?: false)
        val listOFQuestions: ArrayList<AssessmentQuestion> =
            listOfAssessmentType!![0].listOfQuestions
        assertTrue(listOFQuestions.isNotEmpty())
        val listOfOptions: ArrayList<OptionType> =
            listOfAssessmentType[0].listOfQuestions[0].listOfOptions
        assertTrue(listOfOptions.isNotEmpty())
    }

    @Test
    fun `test_callGetAssessmentsList()_return_failure_with_401`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callGetAssessmentsList()
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }

    @Test
    fun `test_callSubmitAssessment_return_success()`() {
        val listOfAssessmentValueRequest = ArrayList<AssessmentValueRequest>()
        listOfAssessmentValueRequest.add(
            AssessmentValueRequest(
                22,
                AppConstants.EMPTY_VALUE,
                AssessmentAnswerRequest(AppConstants.EMPTY_VALUE)
            )
        )
        val assessmentDataRequest = AssessmentDataRequest(6, listOfAssessmentValueRequest)
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callSubmitAssessmentAPI(assessmentDataRequest)
        val response = call.execute()
        val responseBody: SubmitAssessmentResponse? = response.body()
        assertTrue(responseBody != null)
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_201)
    }

    @Test
    fun `test_callSubmitAssessment()_return_failure_with_401`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val listOfAssessmentValueRequest = ArrayList<AssessmentValueRequest>()
        listOfAssessmentValueRequest.add(
            AssessmentValueRequest(
                22,
                AppConstants.EMPTY_VALUE,
                AssessmentAnswerRequest(AppConstants.EMPTY_VALUE)
            )
        )
        val assessmentDataRequest = AssessmentDataRequest(6, listOfAssessmentValueRequest)
        val call = iHttpService.callSubmitAssessmentAPI(assessmentDataRequest)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }


    @Test
    fun `test_getWBTQuestions()_return_success()`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetWBTQuestions()
        val response = call.execute()
        val responseBody: ConfigurationData? = response.body()
        assertTrue(responseBody != null)
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertTrue(response.body()?.wellBeingTrackerData != null)
    }

    @Test
    fun `test_getWBTQuestions()_return_success_with_list_of_question`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetWBTQuestions()
        val response = call.execute()
        val responseBody: ConfigurationData? = response.body()
        assertTrue(responseBody != null)
        assertTrue(response.body() != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        val wBTQuestionList: ArrayList<WBTQuestion>? =
            response.body()?.wellBeingTrackerData?.mWBTQuestionList
        assertTrue(wBTQuestionList?.isNotEmpty() ?: false)
        val wBTAnswerOption: ArrayList<WBTAnswerOption>? = wBTQuestionList?.get(0)?.answerOptionList
        assertTrue(wBTAnswerOption != null)
    }

    @Test
    fun `test_getWBTQuestions()_return_failure_with_bed_params`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callGetWBTQuestions()
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }

    @Test
    fun `test_callPostWBTAnswer()_return_success()`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val requestJson = JsonObject()
        requestJson.addProperty("relationships", 50)
        requestJson.addProperty("sleep", 54)
        requestJson.addProperty("worry", 50)
        requestJson.addProperty("satisfaction", 80)
        requestJson.addProperty("experiences", 70)
        val call = iHttpService.callPostWBTAnswer(
            requestJson,
            TestData.TEST_VALID_USER_ID
        )
        val response = call.execute()
        val responseBody: WBTSubmitResponse? = response.body()
        assertTrue(responseBody != null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_201)
    }

    @Test
    fun `test_callPostWBTAnswer()_return_failure_with_bad_params`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val requestJson = JsonObject()
        val call = iHttpService.callPostWBTAnswer(
            requestJson,
            TestData.TEST_VALID_USER_ID
        )
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }

    @Test
    fun test_getWBTOutputScreenData_return__success_with_WBT_Interpretation_list() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callGetWBTOutputScreenList(TestData.TEST_VALID_USER_ID)
        val response = call.execute()
        val mainResponse: WBTOutputScreenResponse? = response.body()
        val listOfInterpretation = mainResponse?.mWBTOutputObservation?.insightsMessages
        assertTrue(response.code() == HttpConstants.STATUS_CODE_OK_200)
        assertNotNull(mainResponse)
        assertTrue(listOfInterpretation?.isNotEmpty() ?: false)
    }

    @Test
    fun `test_getWBTOutputScreenData()_return_failure_with_401`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callGetWBTOutputScreenList(TestData.TEST_VALID_USER_ID)
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
    }

    @Test
    fun `test_call_user_logout_api()_return_success()`() {
        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call = iHttpService.callLogoutUserAPI(TestData.TEST_REFRESH_TOKEN)
        val response = call.execute()
        val responseBody: LogoutResponse? = response.body()
        assertTrue(responseBody == null)
        assertTrue(response.code() == HttpConstants.STATUS_CODE_205)
    }

    @Test
    fun `call_user_logout_api()_return_failure_with_bad_params`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call = iHttpService.callLogoutUserAPI(
            TestData.TEST_REFRESH_TOKEN
        )
        val response = call.execute()
        assertTrue(response.code() == HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400)
    }
}
