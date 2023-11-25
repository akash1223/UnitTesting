package com.example.lysnclient.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PreferenceUtilTest {

    private lateinit var context: Context

    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testVerifyPreferenceSaveAndRetryStringValue() {
        mockkObject(Cryptography)
        every { Cryptography.encryptData(TestData.TEST_VALID_EMAIL) } returns "decryptedData"
        every { Cryptography.decryptData("decryptedData") } returns TestData.TEST_VALID_EMAIL

        PreferenceUtil.getInstance(context)
            .saveValue(PreferenceUtil.KEY_USER_EMAIL, TestData.TEST_VALID_EMAIL)
        val result = PreferenceUtil.getInstance(context).getValue(PreferenceUtil.KEY_USER_EMAIL, "")
        Assert.assertEquals(TestData.TEST_VALID_EMAIL, result)
    }

    @Test
    fun testVerifyPreferenceSaveAndRetryIntValue() {
        PreferenceUtil.getInstance(context)
            .saveValue(PreferenceUtil.KEY_USER_ID, TestData.TEST_VALID_USER_ID)
        val result = PreferenceUtil.getInstance(context).getValue(PreferenceUtil.KEY_USER_ID, 0)
        Assert.assertEquals(TestData.TEST_VALID_USER_ID, result)
    }

    @Test
    fun testVerifyPreferenceSaveAndRetryBooleanValue() {
        PreferenceUtil.getInstance(context)
            .saveValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, true)
        val result = PreferenceUtil.getInstance(context)
            .getValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, false)
        Assert.assertTrue(result)
    }

    @Test
    fun testVerifyPreferenceSaveAndRetryFloatValue() {
        PreferenceUtil.getInstance(context)
            .saveValue(PreferenceUtil.KEY_REFRESH_TOKEN, 100f)
        val result = PreferenceUtil.getInstance(context).getValue(PreferenceUtil.KEY_REFRESH_TOKEN, 0f)
        Assert.assertEquals(100f, result)
    }

    @Test
    fun testVerifyPreferenceSaveAndRetryLongValue() {
        PreferenceUtil.getInstance(context)
            .saveValue(PreferenceUtil.KEY_ACCESS_TOKEN, 1000L)
        val result = PreferenceUtil.getInstance(context).getValue(PreferenceUtil.KEY_ACCESS_TOKEN, 0L)
        Assert.assertEquals(1000L, result)
    }
}
