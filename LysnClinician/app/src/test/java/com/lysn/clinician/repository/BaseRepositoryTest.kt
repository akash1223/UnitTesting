package com.lysn.clinician.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import com.lysn.clinician.R
import com.lysn.clinician.http.IHTTPService
import com.lysn.clinician.utils.AppConstants
import com.lysn.clinician.utils.LocalizeTextProvider
import com.lysn.clinician.utils.TestCoroutineRule
import com.lysn.clinician.utils.TestData
import com.lysn.clinician.utils.TestData.SOME_THING_WRONG_MESSAGE
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.ResponseBody
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

open class BaseRepositoryTest  {


    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

     @get:Rule
     val testCoroutineRule = TestCoroutineRule()

    protected lateinit var iHttpService: IHTTPService

    protected val mockWebServer: MockWebServer = MockWebServer()

    @Mock
    lateinit var localizeTextProvider: LocalizeTextProvider

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun before() {
        mockContext = Mockito.mock(Context::class.java)
        localizeTextProvider = LocalizeTextProvider(mockContext)
        Mockito.`when`(mockContext.getString(R.string.something_went_wrong)).thenReturn(SOME_THING_WRONG_MESSAGE)
        mockWebServer.start()
        iHttpService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
            .create(IHTTPService::class.java)
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    protected fun parseAuthError(response: ResponseBody?): String {
        var errorMessage = TestData.SOME_THING_WRONG_MESSAGE
        response?.let {
            val jsonObj = JSONObject(it.charStream().readText())
            try {
                if (jsonObj.has("detail")) {
                    errorMessage = jsonObj.optString("detail")
                }
            } catch (e: Exception) {
                Timber.d("JSON Parsing error ")
            }
        }
        return errorMessage.trim()
    }

}