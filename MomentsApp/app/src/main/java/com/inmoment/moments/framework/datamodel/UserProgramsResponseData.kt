package com.inmoment.moments.framework.datamodel


import com.google.gson.annotations.SerializedName

data class UserProgramsResponseData(
    @SerializedName("defaultAccountId")
    val defaultAccountId: String,
    @SerializedName("defaultProgramId")
    val defaultProgramId: String,
    @SerializedName("programs")
    val programs: List<Program>
) {
    data class Program(
        @SerializedName("account")
        val account: Account,
        @SerializedName("cloudType")
        val cloudType: String,
        @SerializedName("earliestMomentDate")
        val earliestMomentDate: String?,
        @SerializedName("enabled")
        val enabled: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("userProgramId")
        val userProgramId: String,
        @SerializedName("name")
        val name: String
    ) {
        data class Account(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
        )
    }
}