package com.inmoment.moments.framework.datamodel

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class MomentsResponseData(
    @SerializedName("id") val id: Int?,
    @SerializedName("accountId") val accountId: String?,
    @SerializedName("dataSourceId") val dataSourceId: String?,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("experienceId") val experienceId: String?,
    @SerializedName("dateOfService") val dateOfService: String?,
    @SerializedName("dateOfSurvey") val dateOfSurvey: String?,
    @SerializedName("impact") val impact: Float?,
    @SerializedName("experienceScore") val experienceScore: Float?,
    @SerializedName("employeeRecognized") val employeeRecognized: Boolean?,
    @SerializedName("sentimentScore") val sentimentScore: Float?,
    @SerializedName("wasRead") val wasRead: Boolean?,
    @SerializedName("experience") val experience: Experience?,
    @SerializedName("activityCount") val activityCount: Int?,
    @SerializedName("mainMetric") val mainMetric: Float?,
    @SerializedName("metadata") val metadata: Metadata?,
    @SerializedName("programId") val programId: String?,
) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Experience(
        @SerializedName("id") val id: String?,
        @SerializedName("dateOfService") val dateOfService: String?,
        @SerializedName("dateOfSurvey") val dateOfSurvey: String?,
        @SerializedName("location") val location: Location?,
        @SerializedName("responseSourceType") val responseSourceType: String?,
        @SerializedName("comments") val comments: List<Comments>?,
        @SerializedName("fields") val fields: List<Fields>?,
        @SerializedName("experienceScore") val experienceScore: Float?,
        @SerializedName("sentimentScore") val sentimentScore: Float?,
        @SerializedName("name1") val name1: String?,
        @SerializedName("name2") val name2: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("phoneNumber") val phoneNumber: String?,
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Comments(
        @SerializedName("sqlId") val sqlId: Int?,
        @SerializedName("externalId") val externalId: String?,
        @SerializedName("answerId") val answerId: String?,
        @SerializedName("dataFieldId") val dataFieldId: String?,
        @SerializedName("dataFieldLabel") val dataFieldLabel: String?,
        @SerializedName("promptLabel") val promptLabel: String?,
        @SerializedName("text") val text: String?,
        @SerializedName("sentimentAnnotations") val sentimentAnnotations: List<SentimentAnnotations>?,
        @SerializedName("tagAnnotations") val tagAnnotations: List<TagAnnotations>?,
        @SerializedName("audioSrcUrl") val audioSrcUrl: String?,
        @SerializedName("imageSrcUrls") val imageSrcUrls: List<String>?,
        @SerializedName("videoSrcUrl") val videoSrcUrl: String?,
        @SerializedName("sentimentScore") val sentimentScore: Float?,
        @SerializedName("sentimentConfidence") val sentimentConfidence: Float?,
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class SentimentAnnotations(
        @SerializedName("sqlId") val sqlId: String?,
        @SerializedName("sentiment") val sentiment: Int?,
        @SerializedName("tagIds") val tagIds: List<String>?,
        @SerializedName("beginIndex") val beginIndex: Int?,
        @SerializedName("endIndex") val endIndex: Int?,
        @SerializedName("sentimentScore") val sentimentScore: Float?,
        @SerializedName("sentimentConfidence") val sentimentConfidence: Float?,
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class TagAnnotations(
        @SerializedName("annotation") val annotation: String?,
        @SerializedName("sentiment") val sentiment: Int?,
        @SerializedName("tagId") val tagId: String?,
        @SerializedName("beginIndex") val beginIndex: Int?,
        @SerializedName("endIndex") val endIndex: Int?,
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Location(
        @SerializedName("id") val id: String?,
        @SerializedName("label") val label: String?
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Fields(
        @SerializedName("externalId") val externalId: String?,
        @SerializedName("answerLabel") val answerLabel: String?,
        @SerializedName("dataFieldLabel") val dataFieldLabel: String?,
    ) : Parcelable

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Metadata(
        @SerializedName("date_of_survey")
        val dateOfSurvey: String?,
        @SerializedName("Location")
        val location: String?
    ) : Parcelable
}
