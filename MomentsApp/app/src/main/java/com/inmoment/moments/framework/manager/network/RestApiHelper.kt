package com.inmoment.moments.framework.manager.network

import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.reward.model.RewardSearchModel
import retrofit2.http.Body
import retrofit2.http.Header
import javax.inject.Named

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

interface RestApiHelper {
    fun getAllCollections(
        xiContextHeader: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionsResponseData>

    fun createCollections(
        xiContext: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionOperationResponseData>

    fun deleteCollections(
        @Header("xi-context") xiContext: String,
        @Body collectionsRequestData: DeleteCollectionsRequestData
    ): OperationResult<CollectionOperationResponseData>

    fun getUserInfo(email: String): OperationResult<UserProfileResponseData>

    // fun getAllMoments(): OperationResult<List<MomentsResponseData>>
    fun getAllMoments(
        dataSourcesId: String,
        menuType: String,
        menuId: String,
        @Named("xiContextHeader") xiContext: String,
        pageNumber: Int,
        pageSize: Int
    ): OperationResult<List<MomentsResponseData>>

    fun getUserDataFromOAuth(userInfoUrl: String): OperationResult<UserDataFromOAuth>
    fun getSavedViews(@Named("xiContextHeader") xiContextHeader: String): OperationResult<List<SavedViewsListResponseData>>
    fun getUserPrograms(): OperationResult<UserProgramsResponseData>
    suspend fun getEmployeeList(
        name: String,
        @Named("xiContextHeader") xiContextHeader: String
    ): List<RewardSearchModel>

    fun postRewardData(
        xiContextHeader: String,
        requestParam: RewardsPointRequestParam
    ): OperationResult<Boolean>

    fun logMomentActivity(requestParam: ActivityLogRequestParam): OperationResult<ActivityLogResponseData>
    fun getMomentActivityLog(experienceId: String): OperationResult<List<ActivityLogResponseData>>

    fun markReadMoment(
        dataSourcesId: String,
        menuType: String,
        menuId: String,
        xiContext: String,
        experienceIdList: List<String>
    ): Int
}