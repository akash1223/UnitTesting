package com.example.lysnclient.utils

/**
 *  This file contains all the constant used in http calls
 */

object HttpConstants {

    const val STATUS_CODE_500: Int = 500
    const val STATUS_CODE_BAD_REQ_PARAM_400: Int = 400

    const val STATUS_CODE_UNAUTHORIZED_401: Int = 401
    const val STATUS_CODE_OK_200: Int = 200
    const val STATUS_CODE_201: Int = 201
    const val STATUS_CODE_205: Int = 205
    const val STATUS_CODE_CONFLICT_INPUT_409: Int = 409

    // API REQUEST PARAMS
    const val REQUEST_PARAM_EMAIL = "email"
    const val REQUEST_PARAM_PASSWORD = "password"
    const val HEADER_PARAM_AUTHORIZATION = "Authorization"
    const val REQUEST_PARAM_VALIDATE_OTP_VERIFY_CODE = "verification_code"
    const val REQUEST_PARAM_OTP = "phone"
    const val REQUEST_PARAM_APPROVED_TERMS = "approved_terms"
    const val REQUEST_PARAM_PHONE = "phone"
    const val REQUEST_PARAM_REFRESH = "refresh"

    const val REQUEST_PARAM_USER_TYPE = "user_type"
    const val REQUEST_PARAM_SOURCE = "source"
    const val REQUEST_PARAM_TIMEZONE = "timezone"

    const val REQUEST_PARAM_USER_TYPE_VALUE = "client"
    const val REQUEST_PARAM_SOURCE_VALUE = "lysn_mobile"

    //    API METHODS END POINT
    const val METHOD_POST_VERIFY_EMAIL = "profiles/create/validate-email/"
    const val METHOD_POST_VERIFY_PASSWORD = "profiles/create/validate-password/"
    const val METHOD_POST_REQUEST_FOR_OTP = "profiles/create/validate-phone/"
    const val METHOD_POST_VERIFY_OTP = "profiles/create/validate-code/"
    const val METHOD_POST_REGISTER = "profiles/create/mobile/"
    const val METHOD_POST_LOGIN = "token/obtain/"
    const val METHOD_POST_REFRESH_TOKEN = "token/refresh/"

    const val FORGOT_PASSWORD_WEB_URL = "/users/forgot-password/"
    const val METHOD_GET_ASSESSMENTS_LIST = "assessments/"
    const val BEARER = "bearer "
    const val METHOD_POST_ASSESSMENTS_QUESTION_ANSWER = "assessments-data/"
    const val METHOD_GET_USER_PROFILE = "profiles/"
    const val METHOD_GET_WBT_QUESTIONS = "configuration/"
    const val METHOD_POST_WBT_ANSWER = "clients/{id}/well-beings/"
    const val METHOD_GET_WBT_OUTPUT_SCREEN_LIST = "clients/{id}/well-beings/by-factors/"
    const val METHOD_GET_WBT_OUTPUT_SCREEN_LIST_TEMP = "well-beings/by-factors/"

    const val METHOD_POST_LOGOUT = "token/logout/"
}
