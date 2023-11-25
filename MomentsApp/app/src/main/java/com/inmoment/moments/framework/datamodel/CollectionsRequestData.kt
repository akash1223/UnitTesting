package com.inmoment.moments.framework.datamodel

import com.google.gson.annotations.SerializedName

data class CollectionsRequestData(
    var operationName: String,
    var variables: String?,
    var query: String?
)
data class DeleteCollectionsRequestData(
    @SerializedName("query") var query: String
)