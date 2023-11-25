package com.example.lysnclient.model
import com.google.gson.annotations.SerializedName


data class AssessmentValueRequest (
	@SerializedName("id") val id : Int,
	@SerializedName("label") val label : String,
	@SerializedName("answer") val answerRequest : AssessmentAnswerRequest
)