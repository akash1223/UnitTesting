package com.lysn.clinician.utils

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lysn.clinician.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LocalizeTextProviderTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    lateinit var localizeTextProvider: LocalizeTextProvider
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
    fun verify_getNoInternetMessage() {
        `when`(mockContext.getString(R.string.no_internet)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getNoInternetMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verify_getSomethingWrongMessage() {
        `when`(mockContext.getString(R.string.something_went_wrong)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getSomethingWrongMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verify_getInvalidEmailMessage() {
        `when`(mockContext.getString(R.string.invalid_email_msg)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getInvalidEmailMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verify_getLoginFailMessage() {
        `when`(mockContext.getString(R.string.login_failed_msg)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getLoginFailMessage()
        assertEquals(result, stringVerifyValue)
    }

    @Test
    fun verify_getConsultationMinutesMessage() {
        val minuteSingular = "STARTS IN 1 MINUTE"
        val minutePlural = "STARTS IN 24 MINUTES"
        `when`(
            mockContext.getString(
                R.string.consultation_starts_in_minute_singular,
                1
            )
        ).thenReturn(minuteSingular)
        `when`(mockContext.getString(R.string.consultation_starts_in_minute_plural, 3)).thenReturn(
            minutePlural
        )
        val resultMSingular = localizeTextProvider.getConsultationMinutesMessage(1)
        val resultPlural = localizeTextProvider.getConsultationMinutesMessage(3)
        assertEquals(resultMSingular, minuteSingular)
        assertEquals(resultPlural, minutePlural)
    }


    @Test
    fun verify_getConsultationStartedMessage() {
        `when`(mockContext.getString(R.string.consultation_started_message, 4)).thenReturn(
            stringVerifyValue
        )
        val result = localizeTextProvider.getConsultationStartedMessage(4)
        assertEquals(result, stringVerifyValue)
    }


    @Test
    fun verify_getConsultationFinishedMessage() {
        `when`(mockContext.getString(R.string.finished)).thenReturn(stringVerifyValue)
        val result = localizeTextProvider.getConsultationFinishedMessage()
        assertEquals(result, stringVerifyValue)
    }
}