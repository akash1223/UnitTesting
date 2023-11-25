package com.lysn.clinician.model

import com.google.gson.annotations.SerializedName

data class  UserAuthResponse(
    @SerializedName("access")
    val access: String,
    @SerializedName("refresh")
    val refresh: String
)