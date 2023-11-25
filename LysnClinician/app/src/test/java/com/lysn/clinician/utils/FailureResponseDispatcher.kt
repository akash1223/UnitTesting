package com.lysn.clinician.utils

import com.lysn.clinician.http.HttpConstants
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object FailureResponseDispatcher {
    val dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            val requestPath = request.path?.removePrefix(TestData.SLASH)

            return when (requestPath) {
                HttpConstants.METHOD_POST_SIGN_IN -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, TestData.SIGN_IN_ERROR_DATA)
                }
                HttpConstants.METHOD_POST_REFRESH_TOKEN -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, TestData.REFRESH_TOKEN_ERROR_DATA)
                }
                HttpConstants.METHOD_GET_CONSULTATION_DETAILS -> {
                    getMockResponseFromJGON("InvalidToken.json")
                }
                else -> MockResponse().setResponseCode(404)
            }
        }
    }


    private fun getMockResponseFromJGON(JsonString: String,statusCode: Int = HttpConstants.STATUS_CODE_UNAUTHORIZED_401): MockResponse {
        val successResponse =
            MockResponseFileReader(JsonString).content
        return getMockResponse(statusCode,successResponse)
    }

    private fun getMockResponse(statusCode: Int, response: String): MockResponse {
        return MockResponse().setResponseCode(statusCode)
            .setBody(
                response
            ).addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
    }
}