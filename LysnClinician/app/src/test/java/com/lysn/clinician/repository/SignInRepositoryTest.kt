package com.lysn.clinician.repository

import com.google.gson.Gson
import com.lysn.clinician.http.HttpConstants
import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.http.Resource
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.utils.*
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SignInRepositoryTest : BaseRepositoryTest() {


    private lateinit var signInRepository:SignInRepository

    @Before
    fun subSetUp() {
        signInRepository = SignInRepository(iHttpService, localizeTextProvider)
    }

    @Test
    fun `test_callVerifyEmailId()_return_success`() {
        runBlocking {
            mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
             val call = signInRepository.executeSignInUser(TestData.TEST_VALID_EMAIL, TestData.TEST_VALID_PASSWORD)
            assertTrue(call.data != null)
            assertTrue(call.status == Resource.Status.SUCCESS)
            assertNotNull(call?.data?.access)
            assertNotNull(call?.data?.refresh)
        }
    }

    @Test
    fun `test_callVerifyEmailId()_return_401_email_or_password_incorrect`() {
        runBlocking {
            mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
            val call = signInRepository.executeSignInUser(TestData.TEST_INVALID_EMAIL, TestData.TEST_VALID_PASSWORD)
            assertTrue(call.status == Resource.Status.ERROR)
            assertNotNull(call.message)
        }
    }

    @Test
    fun `test_callRefreshToken()_executeSignInUser`() {

        mockWebServer.dispatcher = SuccessResponseDispatcher.dispatcher
        val call =
            iHttpService.callRefreshAccessToken(TestData.TEST_REFRESH_TOKEN).execute()
        assertTrue(call != null)
        assertTrue(call.code() == HttpConstants.STATUS_CODE_OK)
        val authResponse = call.body()
        assertNotNull(authResponse?.access)
        assertNotNull(authResponse?.refresh)

    }

    @Test
    fun `test_callRefreshToken()_return_401_email_or_password_incorrect`() {
        mockWebServer.dispatcher = FailureResponseDispatcher.dispatcher
        val call =
            iHttpService.callRefreshAccessToken(TestData.TEST_INVALID_REFRESH_TOKEN).execute()
        assertTrue(call.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401)
        assertNotNull(parseAuthError(call.errorBody()))
    }
/*
    @Test
    fun `test_executeSignInUser()`() {
        runBlocking {
            val mockJson = MockResponseFileReader("UserAuthResponse.json").content
            var authResponse = Gson().fromJson(mockJson, UserAuthResponse::class.java)
            *//*Mockito.doReturn(Resource.success(authResponse))
                .`when`(baseRepository).getResult { mockIHttpService.callSignInUser(TestData.TEST_VALID_EMAIL, TestData.TEST_VALID_PASSWORD) }
*//*
            `when`(baseRepository.getResult<UserAuthResponse>()).thenReturn(Resource.success(authResponse))

            var signInRepository = SignInRepository(mockIHttpService, localizeTextProvider)
            var call = signInRepository.executeSignInUser(
                TestData.TEST_VALID_EMAIL,
                TestData.TEST_VALID_PASSWORD,
                baseRepository
            )

                assertNotNull(call.data?.access)
                assertNotNull(call.data?.refresh)


        }
    }*/
}