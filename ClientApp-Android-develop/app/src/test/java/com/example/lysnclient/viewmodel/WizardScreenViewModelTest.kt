package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WizardScreenViewModelTest {
    private lateinit var viewModel: WizardScreenViewModel
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = WizardScreenViewModel()
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `verify_onBtnContinueEmailClickListener()_invoke`() {
        viewModel.onBtnContinueEmailClickListener()
        assert(viewModel.onWizardContinueEmailObservable.value?:false)
    }
}