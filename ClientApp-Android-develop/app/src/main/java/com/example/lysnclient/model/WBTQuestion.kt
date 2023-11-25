package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class WBTQuestion(
    val question: String, val label: String, val value: String,
    @SerializedName("min_value_label") val minValueLabel: String,
    @SerializedName("max_value_label") val maxValueLabel: String,
    @SerializedName("value_labels") val answerOptionList: ArrayList<WBTAnswerOption>
)