package com.lysn.clinician.http

object HttpConstants {
    const val STATUS_CODE_UNAUTHORIZED_401: Int = 401
    const val STATUS_CODE_OK: Int = 200
    const val STATUS_CODE_500: Int = 500
    const val STATUS_CODE_NO_INTERNET: Int = 999
    const val STATUS_CODE_EMAIL_IN_USE_409: Int = 409
    const val STATUS_CODE_BAD_REQ_PARAM_400: Int = 400
    const val HEADER_PARAM_AUTHORIZATION = "Authorization"
    const val BEARER = "bearer "
    const val EMPTY_VALUE = ""
    const val STATUS_CODE_RESET_CONTENT_205: Int = 205


    // API REQUEST PARAM
    const val REQUEST_PARAM_EMAIL = "email"
    const val REQUEST_PARAM_PASSWORD = "password"
    const val REQUEST_PARAM_REFRESH = "refresh"

    // API METHODS END POINT
    const val METHOD_POST_SIGN_IN = "token/obtain/"
    const val METHOD_POST_REFRESH_TOKEN = "token/refresh/"
    const val FORGOT_PASSWORD_WEB_URL = "/users/forgot-password/"
    const val SIGN_UP_WEB_URL = "/therapists/signup/"
    const val METHOD_GET_CONSULTATION_DETAILS = "consultations/"
    const val METHOD_PATCH_CANCEL_CONSULTATION = "consultations/{id}/cancel/"
    const val METHOD_GET_JOIN_CONSULTATION = "consultations/{id}/video-session/"
    const val METHOD_POST_LOGOUT = "token/logout/"
    const val METHOD_USER_PROFILE = "profiles/"



}