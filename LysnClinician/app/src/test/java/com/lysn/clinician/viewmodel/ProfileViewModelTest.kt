package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lysn.clinician.repository.ProfileRepository
import com.lysn.clinician.repository.SignInRepository
import com.lysn.clinician.ui.profile.ProfileViewModel
import com.lysn.clinician.utils.PreferenceUtil
import com.lysn.clinician.utils.TestCoroutineRule
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var repository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel
    private lateinit var mSignInRepository: SignInRepository
    private lateinit var preferenceUtil: PreferenceUtil

    @Before
    fun setup() {
        repository = mock()
        mSignInRepository = mock()
        preferenceUtil = mock()
        viewModel =
            ProfileViewModel(
                repository,
                mSignInRepository,
                preferenceUtil
            )
    }

    @Test
    fun test_objects_not_null() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(mSignInRepository)
        Assert.assertNotNull(preferenceUtil)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun `onLogoutUserClickListener()_invoke`() {
        viewModel.onLogoutUserClickListener()
        Assert.assertTrue(viewModel.onLogoutObservable.value ?: false)
    }

    @Test
    fun `verify_signInUser()_invoke`() {

        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(Any())
                .`when`(repository)
                .executeLogoutUser()
            val mViewModel = ProfileViewModel(
                repository, mSignInRepository,preferenceUtil
            )
            mViewModel.logoutUser()
            Mockito.verify(repository, Mockito.times(1)).executeLogoutUser()
        }
    }
}