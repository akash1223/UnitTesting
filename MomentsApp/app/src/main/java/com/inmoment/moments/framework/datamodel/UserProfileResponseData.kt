package com.inmoment.moments.framework.datamodel

import com.google.gson.annotations.SerializedName

data class UserProfileResponseData(
    @SerializedName("data") val data: UserProfiles?,
)

data class UserProfiles(
    @SerializedName("userProfiles") val userProfiles: List<UserData?>?
)

data class UserData(
    @SerializedName("id") val id: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("authId") val authId: String?,
)
