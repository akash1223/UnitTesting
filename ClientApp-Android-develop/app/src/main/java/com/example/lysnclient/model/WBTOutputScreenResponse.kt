package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class WBTOutputScreenResponse(
//    @SerializedName("by_days")
//    val byDays: ByDays,
    @SerializedName("by_total")
    val mWBTOutputObservation: WBTOutputObservation
//    @SerializedName("recommended_therapist")
//    val recommendedTherapist: RecommendedTherapist
)

data class WBTOutputObservation(
    @SerializedName("insights_messages")
    val insightsMessages: List<String>
)


/*
* This classes will be use later on
* */
//class ByDays(
//)

//data class RecommendedTherapist(
//    @SerializedName("therapist")
//    val therapist: Therapist,
//    @SerializedName("url")
//    val url: String
//)
//
//
//data class Therapist(
//    @SerializedName("first_name")
//    val firstName: String,
//    @SerializedName("get_full_name")
//    val getFullName: String,
//    @SerializedName("id")
//    val id: Int,
//    @SerializedName("last_name")
//    val lastName: String,
//    @SerializedName("photo")
//    val photo: String,
//    @SerializedName("photo_100x100")
//    val photo100x100: String,
//    @SerializedName("photo_40x40")
//    val photo40x40: String,
//    @SerializedName("slug")
//    val slug: String
//)