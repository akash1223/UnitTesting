package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lysn.clinician.ui.signin.SignInViewModel
import com.lysn.clinician.ui.terms_condition.TermsAndConditionViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class TermsAndConditionViewModelTest {

    lateinit var viewModel: TermsAndConditionViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = TermsAndConditionViewModel()
    }

    @Test
    fun `verify_onBtnReviewClick`() {
        viewModel.onBtnReviewAndAcceptTermsClick()
        assertEquals(viewModel.onTermsAndConditionObservable.value, true)
    }

    @Test
    fun `verify_onBtnAcceptTermsClick`() {
        viewModel.onReviewObservable.postValue(true)
        viewModel.onBtnReviewAndAcceptTermsClick()
        assertTrue((viewModel.onTermsAndConditionObservable.value!! && viewModel.onReviewObservable.value!!))
    }

}