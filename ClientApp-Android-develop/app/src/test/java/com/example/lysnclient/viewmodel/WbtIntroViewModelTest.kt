package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.BaseResponse
import com.example.lysnclient.model.ConfigurationData
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.MockResponseFileReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class WbtIntroViewModelTest {
    private lateinit var mWBTIntroViewModel: WBTIntroViewModel
    private lateinit var repository: AppRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        mWBTIntroViewModel = WBTIntroViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(mWBTIntroViewModel)
    }

    @Test
    fun `verifyOnBtnStartWbtBtnClick()_invoke`() {
        mWBTIntroViewModel.onBtnStartWbtBtnClick()
        assert(mWBTIntroViewModel.onStartWBTBtnObservable.value ?: false)
    }

    @Test
    fun `verifyBtnContinueToHomeClick()ChangeObservableToTrueValue`() {
        mWBTIntroViewModel.onBtnContinueToHomeClick()
        Assert.assertTrue(mWBTIntroViewModel.continueToHomeClickObservable.value ?: false)
    }

    @Test
    fun `verifyBtnContinueToHomeClick()NeverPublishFalseValue`() {
        mWBTIntroViewModel.onBtnContinueToHomeClick()
        Assert.assertNotSame(false, mWBTIntroViewModel.continueToHomeClickObservable.value ?: false)
    }

    @Test
    fun `verifyOnBtnLearnMoreClick()ChangeObservableToTrueValue`() {
        mWBTIntroViewModel.onBtnLearnMoreClick()
        Assert.assertTrue(mWBTIntroViewModel.learnMoreClickObservable.value ?: false)
    }

    @Test
    fun `verifyOnBtnLearnMoreClick()NeverPublishFalseValue`() {
        mWBTIntroViewModel.onBtnLearnMoreClick()
        Assert.assertNotSame(false, mWBTIntroViewModel.learnMoreClickObservable.value ?: false)
    }

    @Test
    fun `verifyGetWBTQuestionData()ExecuteAndGetWBTQuestionsInvoke`() {
        mWBTIntroViewModel.getWBTQuestionData()
        Mockito.verify(repository, times(1)).getWBTQuestions()
    }

    @Test
    fun `verifyGetWBTQuestionData()InvokeAndReturnSuccessWithData`() {
        val myType = object : TypeToken<ConfigurationData>() {}.type
        val response = Gson().fromJson<ConfigurationData>(
            MockResponseFileReader("GetWBTQuestionResponse.json").content,
            myType
        )
        val mainApiResponse = MutableLiveData<BaseResponse<ConfigurationData>>()
        mainApiResponse.value = BaseResponse(
            ResponseStatus.SUCCESS, "",
            response
        )
        whenever(repository.getWBTQuestions()).thenReturn(
            mainApiResponse
        )

        mWBTIntroViewModel.getWBTQuestionData()
        val actualResponse: ConfigurationData? =
            mWBTIntroViewModel.mConfigurationData.value?.apiResponse
        Assert.assertTrue(actualResponse != null)
        Assert.assertTrue(actualResponse?.wellBeingTrackerData != null)
        Assert.assertTrue(
            actualResponse?.wellBeingTrackerData?.mWBTQuestionList?.isNotEmpty() ?: false
        )
        Assert.assertTrue(
            actualResponse?.wellBeingTrackerData?.mWBTQuestionList?.get(0)?.question?.isNotEmpty()
                ?: false
        )
        Assert.assertTrue(
            actualResponse?.wellBeingTrackerData?.mWBTQuestionList?.get(0)?.answerOptionList?.isNotEmpty()
                ?: false
        )
        Assert.assertTrue(
            actualResponse?.wellBeingTrackerData?.mWBTQuestionList?.get(0)?.answerOptionList?.get(0)?.title?.isNotEmpty()
                ?: false
        )
    }
}
