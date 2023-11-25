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

class WbtLearnMoreViewModelTest {
    private lateinit var mWBTLearnMoreViewModel: WBTLearnMoreViewModel
    private lateinit var repository: AppRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        mWBTLearnMoreViewModel = WBTLearnMoreViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(mWBTLearnMoreViewModel)
        Assert.assertNotNull(repository)
    }

    @Test
    fun `verify_navigateBack()_publish_true_value`() {
        mWBTLearnMoreViewModel.navigateOnClose()
        Assert.assertTrue(mWBTLearnMoreViewModel.navigateBackOnCloseObservable.value!!)
    }

    @Test
    fun `verify_navigateBack()_never_publish_false_value`() {
        mWBTLearnMoreViewModel.navigateOnClose()
        Assert.assertNotSame(false, mWBTLearnMoreViewModel.navigateBackOnCloseObservable.value!!)
    }

    @Test
    fun `verify_onBtnStartWbtBtnClick()_invoke`() {
        mWBTLearnMoreViewModel.onBtnStartWBTClick()
        assert(mWBTLearnMoreViewModel.onStartWBTButtonObservable.value ?: false)
    }

    @Test
    fun `verify_onBtnStartWbtBtnClick()_invoke_times`() {
        mWBTLearnMoreViewModel = mock()
        mWBTLearnMoreViewModel.onBtnStartWBTClick()
        Mockito.verify(mWBTLearnMoreViewModel, times(1)).onBtnStartWBTClick()
    }

    @Test
    fun `verify_getWBTQuestionData()_Execute_and_getWBTQuestions_invoke`() {
        mWBTLearnMoreViewModel.getWBTQuestionData()
        Mockito.verify(repository, times(1)).getWBTQuestions()
    }

    @Test
    fun `verify_getWBTQuestionData()_invoke_and_return_success_with_data`() {
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

        mWBTLearnMoreViewModel.getWBTQuestionData()
        val actualResponse: ConfigurationData? =
            mWBTLearnMoreViewModel.mConfigurationData.value?.apiResponse
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
