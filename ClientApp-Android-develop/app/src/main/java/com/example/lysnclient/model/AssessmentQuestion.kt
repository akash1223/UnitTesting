package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class AssessmentQuestion(
    val id: Int, val label: String, val position: Int,
    val step: String, val placeholder: String,
    val required: Boolean,
    @SerializedName("parent_field_enable_value") val parentFieldEnableValue: String,
    @SerializedName("field_type") val questionOptionType: String,
    @SerializedName("answers") val listOfOptions: ArrayList<OptionType> = ArrayList()
)
