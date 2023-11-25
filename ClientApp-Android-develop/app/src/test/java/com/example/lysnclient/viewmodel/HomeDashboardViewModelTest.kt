package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.model.*
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.MockResponseFileReader
import com.example.lysnclient.utils.TestData
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

class HomeDashboardViewModelTest {
    private lateinit var viewModel: HomeDashboardViewModel
    private lateinit var repository: AppRepository

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = HomeDashboardViewModel(repository)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(viewModel)
        Assert.assertNotNull(repository)
    }

    @Test
    fun `verifyOnAssessmentItemClickListener()ChangeObservableValue`() {
        viewModel.onAssessmentItemClick(0)
        Assert.assertTrue(viewModel.navigateToDetailObservable.value == 0)
    }

    @Test
    fun `verifyCallUserLogoutAPI()InvokeRepositoryMethod`() {
        viewModel.callUserLogoutAPI(TestData.TEST_REFRESH_TOKEN)
        Mockito.verify(repository, times(1)).callUserLogoutAPI(TestData.TEST_REFRESH_TOKEN)
    }

    @Test
    fun `verifyCallUserLogoutAPI()InvokeRepositoryMethodAndReturnSuccessResult`() {
        val myType = object : TypeToken<LogoutResponse>() {}.type
        val response = Gson().fromJson<LogoutResponse>(
            MockResponseFileReader("empty.json").content,
            myType
        )
        val logoutResponse: LogoutResponse? = response
        Assert.assertNotNull(logoutResponse)
        whenever(repository.callUserLogoutAPI(TestData.TEST_REFRESH_TOKEN)).thenReturn(
            MutableLiveData(BaseResponse(ResponseStatus.LOGOUT, "", response))
        )
        val result = viewModel.callUserLogoutAPI(TestData.TEST_REFRESH_TOKEN)
        Assert.assertTrue(result.value?.status == ResponseStatus.LOGOUT)
        Assert.assertTrue(result.value?.message == AppConstants.EMPTY_VALUE)
    }
}