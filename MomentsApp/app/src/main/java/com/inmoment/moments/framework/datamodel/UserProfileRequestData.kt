package com.inmoment.moments.framework.datamodel

import com.google.gson.annotations.SerializedName

data class UserProfileRequestData(
    @SerializedName("query") var query: String
)