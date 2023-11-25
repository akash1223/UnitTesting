package com.example.lysnclient.http

enum class ResponseStatus {
    SUCCESS,
    FAILURE,
    NO_INTERNET,
    BAD_PARAMS,
    CONFLICT_USER_INPUTS,
    UNAUTHORIZED_TOKEN_EXPIRED,
    SUCCESS_201,
    LOGOUT,
    BAD_INPUT_500
}