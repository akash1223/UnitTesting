package com.lysn.clinician.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.repository.SignInRepository
import com.lysn.clinician.ui.signin.SignInViewModel
import com.lysn.clinician.utils.*
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignInViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var apiObserver: Observer<Resource<UserAuthResponse>>

    @Mock
    private lateinit var repository: SignInRepository
    @Mock
    private lateinit var localizeTextProvider: LocalizeTextProvider
    @Mock
    private lateinit var preferenceUtil: PreferenceUtil
    private lateinit var viewModel: SignInViewModel
    @Mock
    private lateinit var httpIService: IHTTPService

    @Mock
    private lateinit var userAuthResponse: UserAuthResponse








    @Before
    fun setup() {

        /*  repository = mock()
          localizeTextProvider = mock()
          preferenceUtil = mock()
          httpIService = mock()*/
        //  repository = SignInRepository(httpIService,localizeTextProvider)
        viewModel = SignInViewModel(
            repository,
            localizeTextProvider,
            preferenceUtil
        )
    }


    @Test
    fun test_objects_not_null() {
        assertNotNull(repository)
        assertNotNull(localizeTextProvider)
        assertNotNull(viewModel)
    }

    @Test
    fun `verify_signInUser()_invoke`() {

        testCoroutineRule.runBlockingTest {
            doReturn(Resource.success(userAuthResponse))
                .`when`(repository)
                .executeSignInUser("abc@lysn.com", "123lysn123")
            val mViewModel = SignInViewModel(
                repository,
                localizeTextProvider,
                preferenceUtil
            )
            mViewModel.emailField.value="abc@lysn.com"
            mViewModel.passwordField.value="123lysn123"
            mViewModel.signInCallUser()
            mViewModel.getUserData().observeForever(apiObserver)
            verify(repository, times(1)).executeSignInUser("abc@lysn.com", "123lysn123")
            verify(apiObserver).onChanged(Resource.success(userAuthResponse))
            mViewModel.getUserData().removeObserver(apiObserver)
        }
    }

//    @Test
//    fun `verify_onBtnSignInClickListener()_valid_email`() {
//        viewModel.emailField.value = TestData.TEST_VALID_EMAIL
//        viewModel.onBtnSignInClickListener()
//
//        if (Validator.validateEmailAddress(viewModel.emailField.value.toString())) {
//            viewModel.signInCallUser()
//            viewModel.clearErrorMessages()
//            verify(viewModel, times(1)).clearErrorMessages()
//
//        } else {
//            viewModel.emailErrorMsg.value =
//                localizeTextProvider.getInvalidEmailMessage()
//            assertTrue(viewModel.errorMessageLiveData.value.toString().isEmpty())
//        }
//
//    }

    @Test
    fun `verify_onBtnSignInClickListener()_invalid_email`() {
        val errorMessage = TestData.invalidEmailMessage
        whenever(localizeTextProvider.getInvalidEmailMessage()).thenReturn(errorMessage)
        viewModel.emailField.value = TestData.TEST_INVALID_EMAIL
        viewModel.onBtnSignInClickListener()
        assertTrue(viewModel.emailErrorMsg.value.toString().isNotEmpty())
        assertEquals(errorMessage, viewModel.emailErrorMsg.value.toString())
    }

    @Test
    fun `verify_saveDataInPreference()_invoke`() {
        val signInData =  UserAuthResponse(TestData.TEST_ACCESS_TOKEN,TestData.TEST_REFRESH_TOKEN)
        viewModel.saveDataInPreference(signInData)
        verify(preferenceUtil, times(1)).putValue(PreferenceUtil.ACCESS_TOKEN_PREFERENCE_KEY, signInData.access)
    }

    @Test
    fun `onForgotPasswordClickListener()_invoke`() {
        viewModel.onForgotPasswordClickListener()
        assertTrue(viewModel.onForgotPasswordClickObservable.value ?: false)
    }


    @Test
    fun `verify_clearErrorMessages()_invoke`() {
        viewModel.clearErrorMessages()
        viewModel.emailErrorMsg.value = AppConstants.EMPTY_VALUE
        viewModel.errorMessageLiveData.value = AppConstants.EMPTY_VALUE
        assertTrue(viewModel.emailErrorMsg.value.toString().isEmpty())
        assertTrue(viewModel.errorMessageLiveData.value.toString().isEmpty())
    }

    @Test
    fun `showErrorMessage()_invoke`() {
        viewModel.showErrorMessage()
        viewModel.errorMessageLiveData.value = TestData.errorMessage
        assertTrue(viewModel.errorMessageLiveData.value.toString().isNotEmpty())
    }
}