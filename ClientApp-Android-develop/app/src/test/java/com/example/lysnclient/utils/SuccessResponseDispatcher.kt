package com.example.lysnclient.utils

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

object SuccessResponseDispatcher {
    val dispatcher = object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_EMAIL -> {
                    val successResponse =
                        MockResponseFileReader("EmailVerifyResponse.json").content
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200, successResponse
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_PASSWORD -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        "{}"
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REQUEST_FOR_OTP -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("RequestForOtpResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_VERIFY_OTP -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("VerifyOtpResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_LOGIN -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("AuthResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REGISTER -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("SignUpResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_GET_ASSESSMENTS_LIST -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("AssessmentListResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_REFRESH_TOKEN -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("AuthResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_ASSESSMENTS_QUESTION_ANSWER -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_201,
                        MockResponseFileReader("SubmitAssessmentResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_GET_WBT_QUESTIONS -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("GetWBTQuestionResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_GET_USER_PROFILE -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("GetProfileResponse.json").content
                    )
                }
                TestData.SLASH + "clients/1234/well-beings/" -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_201,
                        MockResponseFileReader("WBTSubmitResponse.json").content
                    )
                }
                TestData.SLASH + "clients/1234/well-beings/by-factors/" -> {
                    getMockResponse(
                        HttpConstants.STATUS_CODE_OK_200,
                        MockResponseFileReader("WBTOutputScreenInterResponse.json").content
                    )
                }
                TestData.SLASH + HttpConstants.METHOD_POST_LOGOUT -> {
                    getMockResponse2(
                        HttpConstants.STATUS_CODE_205,
                        MockResponseFileReader("empty.json").content
                    )
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

    private fun getMockResponse2(statusCode: Int, response: String): MockResponse {
        return MockResponse().setResponseCode(statusCode)
            .setBody(
                response
            )
            .addHeader("Content-length","0")
    }



}