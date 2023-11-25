package com.example.lysnclient.model

import com.example.lysnclient.http.ResponseStatus
import com.example.lysnclient.utils.AppConstants

class BaseResponse<T>(
    var status: ResponseStatus,
    var message: String = AppConstants.EMPTY_VALUE,
    var apiResponse: T? = null
) {
}