package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.R
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
import com.example.lysnclient.utils.TestData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class WBTQuestionsViewModelTest {

    private lateinit var repository: AppRepository
    private lateinit var viewModel: WBTQuestionsViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = WBTQuestionsViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyOnBtnNextClick()ChangeTheObservableValue`() {
        viewModel.onBtnNextClick()
        Assert.assertTrue(viewModel.moveToNextQues.value ?: false)
    }

    @Test
    fun `verifyOnBtnPreviousClick()ChangeTheObservableValue`() {
        viewModel.onBtnPreviousClick()
        Assert.assertTrue(viewModel.moveToPreviousQues.value ?: false)
    }

    @Test
    fun `verifyOnBtnCloseClickListener()ChangeTheObservableValue`() {
        viewModel.onBtnCloseClickListener()
        Assert.assertTrue(viewModel.onCloseBtnClickObservable.value ?: false)
    }

    @Test
    fun `verifyGetWBTQuestionList()InvokeRepositoryMethodGetWBTQuestionList`() {
        viewModel.getWBTQuestionList()
        Mockito.verify(repository, times(1)).getWBTQuestionList()
    }

    @Test
    fun `verifySubmitUserWBTResponse()InvokeRepositoryMethodSubmitWBTUserResponse`() {
        viewModel.submitUserWBTResponse(TestData.TEST_VALID_USER_ID)
        Mockito.verify(repository, times(1))
            .submitWBTUserResponse(TestData.TEST_VALID_USER_ID, JsonObject())
    }

    private fun getWbtQuestionResponse(): ConfigurationData? {
        val myType = object : TypeToken<ConfigurationData>() {}.type
        val response = Gson().fromJson<ConfigurationData>(
            MockResponseFileReader("GetWBTQuestionResponse.json").content,
            myType
        )
        return response
    }

    @Test
    fun `verifygetWBTQuestionList()InvokeAndReturnListOfWbtQuestion`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        Assert.assertTrue(viewModel.mWBTQuestionList.isNotEmpty())
        Assert.assertEquals(mWBTData?.mWBTQuestionList?.size, viewModel.mWBTQuestionList.size)
    }

    @Test
    fun `verifySetSeekBarProgressValue()InvokeAndUpdateVeryHappyData`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        viewModel.setSeekBarProgressValue(81)

        Assert.assertTrue(viewModel.previouslySelectedAnswerValue >= 0)
        Assert.assertTrue(
            viewModel.mWBTQuestionList[viewModel.currentQuestionIndex.value
                ?: 0].answerOptionList.isNotEmpty()
        )
        val updatedObject: WBTSelectedAnswer? =
            viewModel.mapQuePosAndAnswerDetails[viewModel.currentQuestionIndex.value ?: 0]

        Assert.assertTrue(updatedObject != null)
        Assert.assertTrue(updatedObject?.quePosition != null)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue != null)
        Assert.assertTrue(updatedObject?.backgroundColorId?.value == R.color.wbtVeryHappyBgColor)
        Assert.assertTrue(updatedObject?.faceTypeImgId?.value == R.drawable.ic_face_type_very_happy)
        Assert.assertTrue(updatedObject?.backgroundImgId?.value == R.mipmap.very_happy)
    }

    @Test
    fun `verifySetSeekBarProgressValue()InvokeAndUpdateVeryUnHappyData`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        viewModel.setSeekBarProgressValue(15)

        Assert.assertTrue(viewModel.previouslySelectedAnswerValue >= 0)
        Assert.assertTrue(
            viewModel.mWBTQuestionList[viewModel.currentQuestionIndex.value
                ?: 0].answerOptionList.isNotEmpty()
        )
        val updatedObject: WBTSelectedAnswer? =
            viewModel.mapQuePosAndAnswerDetails[viewModel.currentQuestionIndex.value ?: 0]

        Assert.assertTrue(updatedObject != null)
        Assert.assertTrue(updatedObject?.quePosition != null)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue != null)
        Assert.assertTrue(updatedObject?.backgroundColorId?.value == R.color.wbtVeryUnhappyBgColor)
        Assert.assertTrue(updatedObject?.faceTypeImgId?.value == R.drawable.ic_face_type_very_unhappy)
        Assert.assertTrue(updatedObject?.backgroundImgId?.value == R.mipmap.very_unhappy)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue == 15)
        Assert.assertTrue(updatedObject?.value?.isNotEmpty() ?: false)
        Assert.assertTrue(updatedObject?.value == viewModel.mWBTQuestionList[0].value)
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList.isNotEmpty())
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList[0].title.isNotEmpty())
    }
    @Test
    fun `verifySetSeekBarProgressValue()InvokeAndUpdateUnHappyData`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        viewModel.setSeekBarProgressValue(25)

        Assert.assertTrue(viewModel.previouslySelectedAnswerValue >= 0)
        Assert.assertTrue(
            viewModel.mWBTQuestionList[viewModel.currentQuestionIndex.value
                ?: 0].answerOptionList.isNotEmpty()
        )
        val updatedObject: WBTSelectedAnswer? =
            viewModel.mapQuePosAndAnswerDetails[viewModel.currentQuestionIndex.value ?: 0]

        Assert.assertTrue(updatedObject != null)
        Assert.assertTrue(updatedObject?.quePosition != null)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue != null)
        Assert.assertTrue(updatedObject?.backgroundColorId?.value == R.color.wbtFairlyUnhappyBgColor)
        Assert.assertTrue(updatedObject?.faceTypeImgId?.value == R.drawable.ic_face_type_fairly_unhappy)
        Assert.assertTrue(updatedObject?.backgroundImgId?.value == R.mipmap.fairly_unhappy)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue == 25)
        Assert.assertTrue(updatedObject?.value?.isNotEmpty() ?: false)
        Assert.assertTrue(updatedObject?.value == viewModel.mWBTQuestionList[0].value)
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList.isNotEmpty())
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList[0].title.isNotEmpty())
    }


    @Test
    fun `verifySetSeekBarProgressValue()InvokeAndUpdateHappyData`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        viewModel.setSeekBarProgressValue(75)

        Assert.assertTrue(viewModel.previouslySelectedAnswerValue >= 0)
        Assert.assertTrue(
            viewModel.mWBTQuestionList[viewModel.currentQuestionIndex.value
                ?: 0].answerOptionList.isNotEmpty()
        )
        val updatedObject: WBTSelectedAnswer? =
            viewModel.mapQuePosAndAnswerDetails[viewModel.currentQuestionIndex.value ?: 0]

        Assert.assertTrue(updatedObject != null)
        Assert.assertTrue(updatedObject?.quePosition != null)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue != null)
        Assert.assertTrue(updatedObject?.backgroundColorId?.value == R.color.wbtHappyBgColor)
        Assert.assertTrue(updatedObject?.faceTypeImgId?.value == R.drawable.ic_face_type_happy)
        Assert.assertTrue(updatedObject?.backgroundImgId?.value == R.mipmap.happy)
        Assert.assertTrue(updatedObject?.mWBTSeekBarValue == 75)
        Assert.assertTrue(updatedObject?.value?.isNotEmpty() ?: false)
        Assert.assertTrue(updatedObject?.value == viewModel.mWBTQuestionList[0].value)
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList.isNotEmpty())
        Assert.assertTrue(viewModel.mWBTQuestionList[0].answerOptionList[0].title.isNotEmpty())
    }

    @Test
    fun `verifyGetWBTQuestionList()FetchDataAndFilledInMapQuePosAndAnswerDetails`() {
        val response = getWbtQuestionResponse()
        val mWBTData: WellBeingTrackerData? = response?.wellBeingTrackerData
        whenever(repository.getWBTQuestionList()).thenReturn(
            mWBTData?.mWBTQuestionList
        )
        viewModel.getWBTQuestionList()
        val mapObject: WBTSelectedAnswer? =
            viewModel.mapQuePosAndAnswerDetails[viewModel.currentQuestionIndex.value]
        Assert.assertTrue(viewModel.mapQuePosAndAnswerDetails.isNotEmpty())
        Assert.assertTrue(mapObject != null)
        Assert.assertTrue(mapObject?.quePosition != null)
        Assert.assertTrue(mapObject?.mWBTSeekBarValue != null)
        Assert.assertTrue(mapObject?.backgroundColorId?.value == R.color.wbtNeutralBgColor)
        Assert.assertTrue(mapObject?.faceTypeImgId?.value == R.drawable.ic_face_type_neutral)
        Assert.assertTrue(mapObject?.backgroundImgId?.value == R.color.wbtNeutralBgColor)
        Assert.assertTrue(
            mapObject?.value == viewModel.mWBTQuestionList[viewModel.currentQuestionIndex.value
                ?: 0].value
        )
        Assert.assertTrue(viewModel.totalQuestion.value == viewModel.mWBTQuestionList.size)
    }

    @Test
    fun `verifySubmitUserWBTResponse()InvokeAndReturnSuccessResult`() {
        val myType = object : TypeToken<WBTSubmitResponse>() {}.type
        val response = Gson().fromJson<WBTSubmitResponse>(
            MockResponseFileReader("WBTSubmitResponse.json").content,
            myType
        )
        val mainApiResponse = MutableLiveData<BaseResponse<WBTSubmitResponse>>()
        mainApiResponse.value = BaseResponse(
            ResponseStatus.SUCCESS, "",
            response
        )
        whenever(
            repository.submitWBTUserResponse(
                TestData.TEST_VALID_USER_ID,
                JsonObject()
            )
        ).thenReturn(
            mainApiResponse
        )

        val actualResponse = viewModel.submitUserWBTResponse(
            TestData.TEST_VALID_USER_ID
        )
        Assert.assertTrue(actualResponse.value?.apiResponse != null)
        Assert.assertTrue(actualResponse.value?.status == ResponseStatus.SUCCESS)
    }

    @Test
    fun `verifyGetWBTOutPutScreenData()InvokeRepositoryMethodGetWBTOutputScreenList()`() {
        viewModel.getWBTOutPutScreenData(TestData.TEST_VALID_USER_ID)
        Mockito.verify(repository, times(1))
            .getWBTOutputScreenList(TestData.TEST_VALID_USER_ID)
    }

    @Test
    fun `verifyGetWBTInterpretation()InvokeAndReturnSuccessResult`() {
        val myType = object : TypeToken<WBTOutputScreenResponse>() {}.type
        val response = Gson().fromJson<WBTOutputScreenResponse>(
            MockResponseFileReader("WBTOutputScreenInterResponse.json").content,
            myType
        )
        val mWBTData: WBTOutputObservation? = response?.mWBTOutputObservation
        whenever(repository.getWBTInterpretation()).thenReturn(
            mWBTData?.insightsMessages
        )

        val mainApiResponse = MutableLiveData<BaseResponse<WBTOutputScreenResponse>>()
        mainApiResponse.value = BaseResponse(
            ResponseStatus.SUCCESS, "",
            response
        )
        whenever(
            repository.getWBTOutputScreenList(TestData.TEST_VALID_USER_ID)
        ).thenReturn(
            mainApiResponse
        )
        val actualResponse = viewModel.getWBTOutPutScreenData(TestData.TEST_VALID_USER_ID)

        Assert.assertTrue(actualResponse.value?.apiResponse != null)
        Assert.assertTrue(actualResponse.value?.status == ResponseStatus.SUCCESS)
        Assert.assertTrue(actualResponse.value?.apiResponse?.mWBTOutputObservation != null)
        Assert.assertTrue(actualResponse.value?.apiResponse?.mWBTOutputObservation?.insightsMessages != null)
        Assert.assertTrue(
            actualResponse.value?.apiResponse?.mWBTOutputObservation?.insightsMessages?.isNotEmpty()
                ?: false
        )
    }
}
