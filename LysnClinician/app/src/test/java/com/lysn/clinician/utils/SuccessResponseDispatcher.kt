package com.lysn.clinician.utils

import com.lysn.clinician.http.HttpConstants
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object SuccessResponseDispatcher {
    val dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {

            val requestPath = request.path?.removePrefix(TestData.SLASH)
            return when (requestPath) {
                HttpConstants.METHOD_POST_SIGN_IN -> {
                    getMockResponseFromJGON("UserAuthResponse.json")
                }
                HttpConstants.METHOD_POST_REFRESH_TOKEN ->{
                    getMockResponseFromJGON("UserAuthResponse.json")
                }
                HttpConstants.METHOD_GET_CONSULTATION_DETAILS ->{
                    getMockResponseFromJGON("ConsultationsDetailsResponse.json")
                }

                //Custom message response
                /* METHOD_POST_SIGN_IN -> {
                     getMockResponse(
                         HttpConstants.STATUS_CODE_OK, "{}"
                     )
                 }*/
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private fun getMockResponseFromJGON(JsonString: String): MockResponse {
        val successResponse =
            MockResponseFileReader(JsonString).content
        return getMockResponse(HttpConstants.STATUS_CODE_OK,successResponse)
    }

    private fun getMockResponse(statusCode: Int, response: String): MockResponse {
        return MockResponse().setResponseCode(statusCode)
            .setBody(
                response
            ).addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
    }
}