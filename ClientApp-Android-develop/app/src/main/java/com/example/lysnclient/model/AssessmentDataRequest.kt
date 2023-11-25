package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class AssessmentDataRequest(
    @SerializedName("form") val form: Int,
    @SerializedName("value") var valueRequest: ArrayList<AssessmentValueRequest>
) {
}