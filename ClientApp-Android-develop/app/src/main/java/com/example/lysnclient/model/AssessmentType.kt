package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

class AssessmentType(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val type: String,
    val version: Int,
    val intro: String,
    val code: String,
    @SerializedName("is_clinical") val isClinical: Boolean,
    @SerializedName("estimated_time") val estimatedTime: String,
    @SerializedName("assessment_type") val assessmentType: String,
    @SerializedName("get_last_taken") val lastTakenDate: String,
    @SerializedName("fields") val listOfQuestions: ArrayList<AssessmentQuestion> = ArrayList()
)
