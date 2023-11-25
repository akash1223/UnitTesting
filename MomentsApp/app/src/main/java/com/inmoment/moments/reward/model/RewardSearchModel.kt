package com.inmoment.moments.reward.model


import com.google.gson.annotations.SerializedName

data class RewardSearchModel(
    @SerializedName("balance")
    val balance: Balance?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("lastName")
    val lastName: String?,
    @SerializedName("userName")
    val userName: String?
) {
    data class Balance(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("value")
        val value: Int
    )
}
