package com.example.lysnclient.utils


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.lysnclient.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocalizeTextProviderTest() {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var localizeTextProvider: LocalizeTextProvider
    private val stringVerifyValue = "Resource data"

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        localizeTextProvider = LocalizeTextProvider(mockContext)
    }

    @Test
    fun notNullCheck() {
        assertNotNull(mockContext)
        assertNotNull(localizeTextProvider)
    }

    @Test
    fun verifyGetInvalidEmailMessage() {
        `when`(mockContext.getString(R.string.invalid_email_msg)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getInvalidEmailMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verifyGetInvalidMobileNumberMessage() {
        `when`(mockContext.getString(R.string.phone_number_must_be_10_digit)).thenReturn(
            stringVerifyValue
        )
        val result = localizeTextProvider.getInvalidMobileNumberMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verifyGetSomethingWrongMessage() {
        `when`(mockContext.getString(R.string.something_went_wrong)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getSomethingWrongMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verifyGetInvalidOtpMessage() {
        `when`(mockContext.getString(R.string.invalid_otp)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getInvalidOtpMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verifyGetLogoutUserMessage() {
        `when`(mockContext.getString(R.string.login_again_token_expired)).thenReturn(
            stringVerifyValue
        )
        val result = localizeTextProvider.getLogoutUserMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verifyGetServerNotReachableMessage() {
        `when`(mockContext.getString(R.string.server_not_reachable)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getServerNotReachableMessage()
        assertEquals(result, stringVerifyValue)
    }
}
