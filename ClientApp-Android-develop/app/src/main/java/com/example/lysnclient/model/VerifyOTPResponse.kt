package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class VerifyOTPResponse(
    val phone: String,
    @SerializedName("verification_code") val verificationCode: String
)