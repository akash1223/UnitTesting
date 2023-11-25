package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class WBTAnswerOption(
    @SerializedName("title") val title: String,
    @SerializedName("range_start") val rangeStart: Int,
    @SerializedName("range_end") val rangeEnd: Int
)