package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.TestData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class TermsConditionViewModelTest {
    private lateinit var repository: AppRepository
    private lateinit var viewModel: TermsConditionViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        viewModel = TermsConditionViewModel(repository)
    }

    @Test
    fun `verifyExecuteRegisterUserApi()InvokeTheRepositoryMethod`() {
        viewModel.executeRegisterUserApi(
            TestData.TEST_VALID_EMAIL,
            TestData.TEST_VALID_PASSWORD,
            TestData.TEST_VALID_MOBILE
        )
        Mockito.verify(repository, times(1)).registerUserAPI(
            TestData.TEST_VALID_EMAIL,
            TestData.TEST_VALID_PASSWORD,
            TestData.TEST_VALID_MOBILE,
            AppConstants.SIGN_UP_ACCEPT_TERMS
        )
    }

    @Test
    fun verifyOnBtnReviewAndAcceptTermsClickMethodChangeTheObserverValue() {
        viewModel.onBtnReviewAndAcceptTermsClick()
        Assert.assertTrue(viewModel.onTermsAndConditionObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()ChangeTheObserverValue`() {
        viewModel.navigateBack()
        Assert.assertTrue(viewModel.navigateBackObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()NevrChangeChangeTheObserverValueToFalse`() {
        viewModel.navigateBack()
        Assert.assertNotSame(false, viewModel.navigateBackObservable.value ?: false)
    }
}