package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("user") val userProfile: UserProfile,
    @SerializedName("token") val userTokens: UserAuthResponse
)
