package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lysn.clinician.repository.ProfileRepository
import com.lysn.clinician.ui.profile.SettingsViewModel
import com.lysn.clinician.utils.TestCoroutineRule
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var repository: ProfileRepository
    private lateinit var viewModel: SettingsViewModel
    private var mAllowNotificationRequestData =  AllowNotificationRequestData()




    @Before
    fun setup() {
        repository = mock()
        viewModel = SettingsViewModel(repository)
    }

    @Test
    fun test_objects_not_null() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `onSaveSettingsOnClickListener()_invoke`() {
        viewModel.onSaveSettingsOnClickListener()
        Assert.assertTrue(viewModel.onSaveSettingsObservable.value ?: false)
    }

    @Test
    fun `verify_saveSettings()_invoke`() {
        mAllowNotificationRequestData.send_email_reminders = true
        mAllowNotificationRequestData.send_sms_reminders = false

        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(Any())
                .`when`(repository)
                .executeAllowNotification(mAllowNotificationRequestData)
            val mViewModel = SettingsViewModel(repository)
            mViewModel.saveSettings(mAllowNotificationRequestData)
            Mockito.verify(repository, Mockito.times(1)).executeAllowNotification(mAllowNotificationRequestData)
        }
    }



}