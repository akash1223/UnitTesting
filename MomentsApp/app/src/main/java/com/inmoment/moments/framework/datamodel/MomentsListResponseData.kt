package com.inmoment.moments.framework.datamodel

data class MomentsListResponseData(
    val momentsResponseData: List<MomentsResponseData>?,
    val accountName: String?
) : RequestParam()