package com.example.lysnclient.repository

import com.example.lysnclient.http.NoInternetException
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.*
import com.example.lysnclient.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BaseRepositoryTest {
    private lateinit var baseRepository: BaseRepository
    private lateinit var localizationProvider: LocalizeTextProvider

    @Before
    fun before() {
        localizationProvider = mock()
        baseRepository = BaseRepository(localizationProvider)
    }

    @After
    fun shutdown() {
    }

    @Test
    fun testParseSuccessResponseMethodReturnBaseResponseWithSuccessCode() {
        val baseRequest: Response<EmailVerifyResponse> = Response.success(
            HttpConstants.STATUS_CODE_OK_200,
            EmailVerifyResponse(TestData.TEST_VALID_EMAIL)
        )

        val result =
            baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.SUCCESS)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
        val emailResponse: EmailVerifyResponse? = result.apiResponse
        Assert.assertNotNull(emailResponse?.email?.isNotEmpty())
    }

    @Test
    fun testParseSuccessResponseMethodReturnBaseResponseWithLogoutCode() {
        val baseRequest: Response<LogoutResponse> = Response.success(
            HttpConstants.STATUS_CODE_205,
            LogoutResponse()
        )
        val result =
            baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.LOGOUT)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
        val logoutResponse: LogoutResponse? = result.apiResponse
        Assert.assertNotNull(logoutResponse)
    }


    @Test
    fun testParseSuccessResponseMethodReturnBaseResponseWithSuccessCode201() {
        val baseRequest: Response<WBTSubmitResponse> = Response.success(
            HttpConstants.STATUS_CODE_201,
            WBTSubmitResponse(TestData.TEST_VALID_USER_ID)
        )

        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.SUCCESS_201)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
        val wbtSubmitResponse: WBTSubmitResponse? = result.apiResponse
        Assert.assertNotNull(wbtSubmitResponse?.client == TestData.TEST_VALID_USER_ID)
    }

    @Test
    fun testParseSuccessResponseMethodReturnSuccessWithListOfAssessment() {
        val response = MockResponseFileReader("AssessmentListResponse.json").content
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        val listOfAssessmentResponse =
            Gson().fromJson<List<AssessmentType>>(response, myType)

        val baseRequest: Response<List<AssessmentType>> = Response.success(
            HttpConstants.STATUS_CODE_OK_200,
            listOfAssessmentResponse
        )
        val result =
            baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.SUCCESS)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
        val listOfAssessment: List<AssessmentType>? = result.apiResponse
        Assert.assertTrue(listOfAssessment?.isNotEmpty() ?: false)
        Assert.assertTrue(listOfAssessment?.size ?: 0 > 1)
        Assert.assertNotNull(listOfAssessment?.get(0)?.id)
        Assert.assertNotNull(listOfAssessment?.get(0)?.name)
        Assert.assertNotNull(listOfAssessment?.get(0)?.code)
        Assert.assertNotNull(listOfAssessment?.get(0)?.intro)
        Assert.assertNotNull(listOfAssessment?.get(0)?.slug)
        Assert.assertTrue(listOfAssessment?.get(0)?.isClinical ?: false)
        Assert.assertNotNull(listOfAssessment?.get(0)?.estimatedTime)
        Assert.assertNotNull(listOfAssessment?.get(0)?.assessmentType)
        Assert.assertNotNull(listOfAssessment?.get(0)?.lastTakenDate)
        //
        val assessmentQuestion: ArrayList<AssessmentQuestion>? =
            listOfAssessment?.get(0)?.listOfQuestions
        Assert.assertTrue(assessmentQuestion?.isNotEmpty() ?: false)
        Assert.assertNotNull(assessmentQuestion?.get(0)?.id)
        Assert.assertNotNull(assessmentQuestion?.get(0)?.label)
        Assert.assertNotNull(assessmentQuestion?.get(0)?.questionOptionType)
        Assert.assertNotNull(assessmentQuestion?.get(0)?.questionOptionType)

        val listOfOptionType: ArrayList<OptionType>? = assessmentQuestion?.get(0)?.listOfOptions
        Assert.assertTrue(listOfOptionType?.isNotEmpty() ?: false)
        Assert.assertNotNull(listOfOptionType?.get(0)?.label)
        Assert.assertNotNull(listOfOptionType?.get(0)?.value)
    }

    @Test
    fun testParseSuccessResponseMethodReturnSuccessWithWBTData() {

        val response = MockResponseFileReader("GetWBTQuestionResponse.json").content
        val myType = object : TypeToken<ConfigurationData>() {}.type
        val configurationData: ConfigurationData =
            Gson().fromJson<ConfigurationData>(response, myType)

        val baseRequest: Response<ConfigurationData> = Response.success(
            HttpConstants.STATUS_CODE_OK_200,
            configurationData
        )
        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.SUCCESS)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
        val parseResponse: ConfigurationData? = result.apiResponse
        val wbtData: WellBeingTrackerData? = parseResponse?.wellBeingTrackerData
        val wbtQuestion: ArrayList<WBTQuestion>? = wbtData?.mWBTQuestionList
        val question = wbtQuestion?.get(0)
        val wBTAnswerOptionList: ArrayList<WBTAnswerOption>? = question?.answerOptionList

        Assert.assertNotNull(parseResponse)
        Assert.assertNotNull(wbtData)
        Assert.assertNotNull(wbtQuestion?.isNotEmpty())
        Assert.assertTrue(wbtQuestion?.size ?: 0 > 1)
        Assert.assertTrue(question?.question?.isNotEmpty() ?: false)
        Assert.assertTrue(question?.label?.isNotEmpty() ?: false)
        Assert.assertTrue(question?.value?.isNotEmpty() ?: false)
        Assert.assertTrue(question?.minValueLabel?.isNotEmpty() ?: false)
        Assert.assertTrue(question?.maxValueLabel?.isNotEmpty() ?: false)
        Assert.assertTrue(wBTAnswerOptionList?.size ?: 0 > 1)
        Assert.assertTrue(wBTAnswerOptionList?.get(0)?.rangeStart ?: -1 > -1)
        Assert.assertTrue(wBTAnswerOptionList?.get(0)?.rangeEnd ?: 0 > -1)
    }

    @Test
    fun testParseSuccessResponseMethodReturnFailureWithCode500() {
        whenever(localizationProvider.getSomethingWrongMessage()).thenReturn("Something went wrong!")
        val responseBody: ResponseBody = "".toResponseBody(null)
        val baseRequest: Response<VerifyOTPResponse> = Response.error(
            HttpConstants.STATUS_CODE_500,
            responseBody
        )

        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.BAD_INPUT_500)
        Assert.assertTrue(result.message == localizationProvider.getSomethingWrongMessage())
    }

    @Test
    fun testParseSuccessResponseMethodReturnFailureWithWithMessage() {
        whenever(localizationProvider.getSomethingWrongMessage()).thenReturn("Something went wrong!")
        val responseBody: ResponseBody = "".toResponseBody(null)
        val baseRequest: Response<PasswordVerifyResponse> = Response.error(
            455,
            responseBody
        )

        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.FAILURE)
        Assert.assertTrue(result.message == localizationProvider.getSomethingWrongMessage())
    }

    @Test
    fun testParseSuccessResponseMethodReturnFailureWitConflictCode409() {
        val responseBody: ResponseBody = "".toResponseBody(null)
        val baseRequest: Response<PasswordVerifyResponse> = Response.error(
            HttpConstants.STATUS_CODE_CONFLICT_INPUT_409,
            responseBody
        )

        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.CONFLICT_USER_INPUTS)
    }

    @Test
    fun testParseSuccessResponseMethodReturnFailureWitUnAuthorized401() {
        whenever(localizationProvider.getLogoutUserMessage()).thenReturn("Sorry Please login again")
        val responseBody: ResponseBody = "".toResponseBody(null)
        val baseRequest: Response<PasswordVerifyResponse> = Response.error(
            HttpConstants.STATUS_CODE_UNAUTHORIZED_401,
            responseBody
        )
        val result = baseRepository.parseSuccessResponse(baseRequest)
        Assert.assertTrue(result.status == ResponseStatus.UNAUTHORIZED_TOKEN_EXPIRED)
        Assert.assertTrue(result.message == localizationProvider.getLogoutUserMessage())
    }

    @Test
    fun testParseFailureResponseReturnNoInternetConnection() {
        val result =
            baseRepository.parseFailureResponse<BaseResponse<Throwable>>(NoInternetException())
        Assert.assertTrue(result.status == ResponseStatus.NO_INTERNET)
        Assert.assertTrue(result.message == AppConstants.EMPTY_VALUE)
    }

    @Test
    fun testParseFailureResponseReturnWithResponseStatusFailure() {
        whenever(localizationProvider.getServerNotReachableMessage()).thenReturn("We’re sorry!  Unable to proceed, please try again later.")
        val result =
            baseRepository.parseFailureResponse<BaseResponse<Throwable>>(Throwable())
        Assert.assertTrue(result.status == ResponseStatus.FAILURE)
        Assert.assertTrue(result.message == "We’re sorry!  Unable to proceed, please try again later.")
    }

    @Test
    fun testGetAssessmentByIdReturnAssessmentType() {
        val response = MockResponseFileReader("AssessmentListResponse.json").content
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        baseRepository.listOfAssessmentType =
            Gson().fromJson<List<AssessmentType>>(response, myType)
        val assessmentDetail = baseRepository.getAssessmentById(TestData.TEST_ASSESSMENT_ID)
        Assert.assertTrue(assessmentDetail?.id == TestData.TEST_ASSESSMENT_ID)
        Assert.assertTrue(assessmentDetail?.listOfQuestions?.isNotEmpty() ?: false)
    }

    @Test
    fun testGetAssessmentByIdReturnAssessmentTypeQuestionList() {
        val response = MockResponseFileReader("AssessmentListResponse.json").content
        val myType = object : TypeToken<List<AssessmentType>>() {}.type
        baseRepository.listOfAssessmentType =
            Gson().fromJson<List<AssessmentType>>(response, myType)
        val assessmentQueList =
            baseRepository.getAssessmentQuestionsById(TestData.TEST_ASSESSMENT_ID)
        Assert.assertTrue(assessmentQueList.isNotEmpty())
        Assert.assertTrue(assessmentQueList.size > 0)
    }

    @Test
    fun testGetWBTQuestionListReturnsQuestionList() {
        val response = MockResponseFileReader("GetWBTQuestionResponse.json").content
        val myType = object : TypeToken<ConfigurationData>() {}.type
        baseRepository.mWBTQuestionList = Gson().fromJson<ConfigurationData>(
            response,
            myType
        ).wellBeingTrackerData.mWBTQuestionList
        val wbtQueList: List<WBTQuestion> = baseRepository.getWBTQuestionList()
        Assert.assertTrue(wbtQueList.isNotEmpty())
    }

    @Test
    fun testGetWBTQuestionListReturnsWBTInterpretationList() {
        val response = MockResponseFileReader("WBTOutputScreenInterResponse.json").content
        val myType = object : TypeToken<WBTOutputScreenResponse>() {}.type
        baseRepository.mWBTOutputObservation =
            Gson().fromJson<WBTOutputScreenResponse>(response, myType).mWBTOutputObservation
        val wbtInterpretationList = baseRepository.getWBTInterpretation()
        Assert.assertTrue(wbtInterpretationList.isNotEmpty())
    }
}
