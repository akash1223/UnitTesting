package com.inmoment.moments.framework.datamodel

import com.google.gson.annotations.SerializedName

data class UserDataFromOAuth(
    @SerializedName("sub") val sub: String?,
    @SerializedName("updated_at") val updatedAt: Float?,
    @SerializedName("name") val name: String?,
    @SerializedName("given_name") val givenName: String?,
    @SerializedName("family_name") val familyName: String?,
    @SerializedName("email") val email: String?,
)