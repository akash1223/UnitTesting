package com.example.lysnclient.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.repository.AppRepository
import com.example.lysnclient.utils.LocalizeTextProvider
import com.example.lysnclient.utils.TestData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class UserAuthenticateViewModelTest {

    private lateinit var repository: AppRepository
    private lateinit var viewModel: UserAuthenticateViewModel
    private lateinit var localizeProvider: LocalizeTextProvider

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mock()
        localizeProvider = mock()
        viewModel = UserAuthenticateViewModel(repository, localizeProvider)
    }

    @Test
    fun shouldNotBeNull() {
        Assert.assertNotNull(repository)
        Assert.assertNotNull(localizeProvider)
        Assert.assertNotNull(viewModel)
    }

    @Test
    fun verifyExecuteVerifyEmailMethodInvoke() {
        viewModel.executeVerifyEmail()
        Mockito.verify(repository, times(1)).verifyEmailAPI(viewModel.emailField.value.toString())
    }

    @Test
    fun verifyExecuteVerifyPasswordMethodInvoke() {
        viewModel.executeVerifyPassword()
        Mockito.verify(repository, times(1))
            .verifyPasswordAPI(viewModel.createPasswordField.value.toString())
    }

    @Test
    fun verifyExecuteRequestOTPMethodInvoke() {
        viewModel.executeRequestOTP()
        Mockito.verify(repository, times(1))
            .requestOtpAPI(
                viewModel.countryCodeField.value.toString() + " " + viewModel.phoneNumberField.value.toString()
            )
    }

    @Test
    fun verifyExecuteVerifyOTPMethodInvoke() {
        viewModel.executeVerifyOTP()
        Mockito.verify(repository, times(1))
            .verifyOtpAPI(
                viewModel.countryCodeField.value.toString() + " " + viewModel.phoneNumberField.value.toString(),
                viewModel.otpCodeField.value.toString()
            )
    }

    @Test
    fun verifyExecuteUserLoginMethodInvoke() {
        viewModel.executeUserLogin()
        Mockito.verify(repository, times(1)).executeUserLoginAPI(
            viewModel.emailField.value.toString(),
            viewModel.loginPasswordField.value.toString()
        )
    }

    @Test
    fun verifyOnBtnEmailVerifyClickListenerMethodValidatesEmailId() {
        viewModel.emailField.value = TestData.TEST_VALID_EMAIL
        viewModel.onBtnEmailVerifyClickListener()
        Assert.assertTrue(viewModel.emailErrorMsg.value.toString().isEmpty())
        Assert.assertTrue(viewModel.onEmailValidatedObservable.value!!)
    }

    @Test
    fun verifyOnBtnEmailVerifyClickListenerMethodValidatesInvalidEmail() {
        val errorMessage = TestData.invalidEmailMessage
        whenever(localizeProvider.getInvalidEmailMessage()).thenReturn(
            errorMessage
        )
        viewModel.emailField.value = TestData.TEST_INVALID_EMAIL
        viewModel.onBtnEmailVerifyClickListener()
        Assert.assertTrue(viewModel.emailErrorMsg.value.toString().isNotEmpty())
        Assert.assertEquals(
            errorMessage, viewModel.emailErrorMsg.value.toString()
        )

    }

    @Test
    fun verifyOnBtnCreatePasswordClickListenerMethodValidatePassword() {
        viewModel.createPasswordField.value = TestData.TEST_VALID_PASSWORD
        viewModel.onBtnCreatePasswordClickListener()
        Assert.assertTrue(viewModel.createPasswordErrorMsg.value.toString().isEmpty())
        Assert.assertTrue(viewModel.onPasswordCreatedObservable.value!!)
        Assert.assertTrue(viewModel.hideKeyboardObservable.value ?: false)
    }

    @Test
    fun verifyOnBtnAddMobileClickListenerMethodValidatesPhoneNumber() {
        viewModel.phoneNumberField.value = TestData.TEST_VALID_MOBILE
        viewModel.onBtnAddMobileClickListener()
        Assert.assertTrue(viewModel.onPhoneCreatedObservable.value!!)
        Assert.assertTrue(viewModel.phoneNumberErrorMsg.value.toString().isEmpty())
    }

    @Test
    fun `verify_onBtnAddMobileClickListener()_invalid_phoneNumber`() {
        val errorMessage = TestData.invalidMobileMessage
        viewModel.phoneNumberField.value = TestData.TEST_EMPTY_MOBILE
        whenever(localizeProvider.getInvalidMobileNumberMessage()).thenReturn(
            errorMessage
        )
        viewModel.onBtnAddMobileClickListener()
        Assert.assertTrue(viewModel.phoneNumberErrorMsg.value.toString().isNotEmpty())
        Assert.assertEquals(errorMessage, viewModel.phoneNumberErrorMsg.value.toString())
    }

    @Test
    fun verifyOnBtnOtpVerifyClickListenerMethodValidatesOtp() {
        viewModel.otpCodeField.value = TestData.TEST_VALID_OTP
        viewModel.onBtnOtpVerifyClickListener()
        Assert.assertTrue(viewModel.onOtpVerifiedObservable.value!!)
        Assert.assertTrue(viewModel.otpCodeFieldErrorMsg.value.toString().isEmpty())
    }

    @Test
    fun verifyOnBtnOtpVerifyClickListenerMethodValidatesInvalidOtp() {
        val errorMessage = TestData.invalidOtpMessage
        viewModel.otpCodeField.value = TestData.TEST_INVALID_OTP
        whenever(localizeProvider.getInvalidOtpMessage()).thenReturn(
            errorMessage
        )
        viewModel.onBtnOtpVerifyClickListener()
        Assert.assertTrue(viewModel.otpCodeFieldErrorMsg.value.toString().isNotEmpty())
        Assert.assertEquals(errorMessage, viewModel.otpCodeFieldErrorMsg.value.toString())
    }

    @Test
    fun verifyInitEmailFragmentMethodResetTheData() {
        viewModel.initEmailFragment()
        Assert.assertTrue(viewModel.emailField.value.toString().isEmpty())
        Assert.assertTrue(viewModel.emailField.value.toString().isEmpty())
        Assert.assertNull(viewModel.onEmailValidatedObservable.value)
    }

    @Test
    fun verifyInitCreatePasswordFragmentMethodResetTheData() {
        viewModel.initCreatePasswordFragment()
        Assert.assertTrue(viewModel.createPasswordField.value.toString().isEmpty())
        Assert.assertTrue(viewModel.createPasswordErrorMsg.value.toString().isEmpty())
        Assert.assertNull(viewModel.onPasswordCreatedObservable.value)
    }

    @Test
    fun verifyInitPhoneFragmentMethodResetTheData() {
        viewModel.initPhoneFragment()
        Assert.assertTrue(viewModel.phoneNumberField.value.toString().isEmpty())
        Assert.assertTrue(viewModel.phoneNumberErrorMsg.value.toString().isEmpty())
        Assert.assertNull(viewModel.onPhoneCreatedObservable.value)
    }

    @Test
    fun verifyInitOtpFragmentMethodResetTheData() {
        viewModel.initOtpFragment()
        Assert.assertTrue(viewModel.otpCodeField.value.toString().isEmpty())
        Assert.assertTrue(viewModel.otpCodeFieldErrorMsg.value.toString().isEmpty())
        Assert.assertNull(viewModel.onOtpVerifiedObservable.value)
        Assert.assertNull(viewModel.onResendOtpObservable.value)
    }

    @Test
    fun verifyInitLoginFragmentMethodResetTheData() {
        viewModel.initLoginFragment()
        Assert.assertTrue(viewModel.loginPasswordField.value.toString().isEmpty())
        Assert.assertNull(viewModel.onLoginPasswordAddedObservable.value)
    }

    @Test
    fun verifyNavigateBackMethodPublishTrueValue() {
        viewModel.navigateBack()
        Assert.assertTrue(viewModel.navigateBackObservable.value!!)
    }

    @Test
    fun verifyNavigateBackMethodPublishFalseValue() {
        viewModel.navigateBack()
        Assert.assertNotSame(false, viewModel.navigateBackObservable.value!!)
    }

    @Test
    fun verifyOnBtnResentOtpClickListenerMethodChangeTheValue() {
        viewModel.onBtnResentOtpClickListener()
        Assert.assertTrue(viewModel.onResendOtpObservable.value!!)
    }

    @Test
    fun verifyOnBtnNavForgotPassClickListenerMethodChangeTheValue() {
        viewModel.onBtnNavForgotPassClickListener()
        Assert.assertTrue(viewModel.onNavForgotPassObservable.value!!)
    }

    @Test
    fun verifyInitForgotPassFragmentResetData() {
        viewModel.initForgotPassFragment()
        Assert.assertNull(viewModel.onNavForgotPassObservable.value)
    }

    @Test
    fun verifyGetUserProfileMethodInvoke() {
        viewModel.getUserProfile()
        Mockito.verify(repository, times(1)).callGetUserProfile()
    }

    @Test
    fun verifyOnBtnLoginClickListenerMethodChangeTheObserverValue() {
        viewModel.onBtnLoginClickListener()
        Assert.assertTrue(viewModel.onLoginPasswordAddedObservable.value ?: false)
        Assert.assertTrue(viewModel.hideKeyboardObservable.value ?: false)
    }
}

