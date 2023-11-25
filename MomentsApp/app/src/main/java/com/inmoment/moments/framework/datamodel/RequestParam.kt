package com.inmoment.moments.framework.datamodel

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

abstract class RequestParam

data class HomeDashBoardRequestParam(val pageNumber: Int, val pageSize: Int) : RequestParam()

data class RewardsPointRequestParam(
    val email: String,
    val amount: String,
    val note: String,
    val rewardedBy: String
) : RequestParam()

data class ActivityLogRequestParam(
    val activityType: String,
    val description: String,
    val experienceId: String
) : RequestParam()

data class CreateCollectionRequestParam(
    val name: String,
    val record: List<String>,
    val id :String = ""
) : RequestParam()

data class SingleParamRequest<T>(val value: T) : RequestParam()
