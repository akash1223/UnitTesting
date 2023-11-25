package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class AssessmentSubmittedViewModelTest {
    private lateinit var viewModel: AssessmentSubmittedViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = AssessmentSubmittedViewModel()
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verifyOnBtnFindPsychologistClickListener()Invoke`() {
        viewModel.onBtnFindPsychologistClick()
        assert(viewModel.onFindPsychologistObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()PublishTrueValue`() {
        viewModel.navigateOnClose()
        Assert.assertTrue(viewModel.navigateBackOnCloseObservable.value ?: false)
    }

    @Test
    fun `verifyNavigateBack()NeverPublishFalseValue`() {
        viewModel.navigateOnClose()
        Assert.assertNotSame(false, viewModel.navigateBackOnCloseObservable.value ?: false)
    }
}
