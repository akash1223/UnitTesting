package com.example.lysnclient.utils

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object FailureResponseDispatcher {
    val dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_EMAIL -> {
                    getMockResponse(HttpConstants.STATUS_CODE_CONFLICT_INPUT_409, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_PASSWORD -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REQUEST_FOR_OTP -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_OTP -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_LOGIN -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REGISTER -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_GET_ASSESSMENTS_LIST -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REFRESH_TOKEN -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_ASSESSMENTS_QUESTION_ANSWER -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_GET_USER_PROFILE -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_GET_WBT_QUESTIONS -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + "clients/1234/well-beings/" -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                TestData.SLASH + "clients/1234/well-beings/by-factors/" -> {
                    getMockResponse(HttpConstants.STATUS_CODE_UNAUTHORIZED_401, "{}")
                }
                TestData.SLASH + HttpConstants.METHOD_POST_LOGOUT -> {
                    getMockResponse(HttpConstants.STATUS_CODE_BAD_REQ_PARAM_400, "{}")
                }
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private fun getMockResponse(statusCode: Int, response: String): MockResponse {
        return MockResponse().setResponseCode(statusCode)
            .setBody(
                response
            ).addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
    }
}