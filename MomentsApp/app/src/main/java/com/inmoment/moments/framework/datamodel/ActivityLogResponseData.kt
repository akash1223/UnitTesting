package com.inmoment.moments.framework.datamodel


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ActivityLogResponseData(
    @SerializedName("activityDate")
    val activityDate: String?,
    @SerializedName("activityType")
    val activityType: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("experienceId")
    val experienceId: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("userName")
    val userName: String?
) : Parcelable