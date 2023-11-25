package com.inmoment.moments.framework.manager.network

import com.inmoment.moments.BuildConfig
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.reward.model.RewardSearchModel
import retrofit2.Call
import retrofit2.http.*

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

interface RestApiInterfaceDao {

    @POST(BuildConfig.GraphQL)
    fun getUserData(@Body userProfileRequestData: UserProfileRequestData): Call<UserProfileResponseData>

    @POST(BuildConfig.GraphQL)
    fun getCollections(
        @Header("xi-context") xiContext: String,
        @Body collectionsRequestData: CollectionsRequestData
    ): Call<CollectionsResponseData>

    @POST(BuildConfig.GraphQL)
    fun createCollections(
        @Header("xi-context") xiContext: String,
        @Body collectionsRequestData: CollectionsRequestData
    ): Call<CollectionOperationResponseData>

    @POST(BuildConfig.GraphQL)
    fun deleteCollections(
        @Header("xi-context") xiContext: String,
        @Body collectionsRequestData: DeleteCollectionsRequestData
    ): Call<CollectionOperationResponseData>

    @GET(BuildConfig.ServiceEndPointBaseURL + "/api/datasources/{dataSourcesId}/{menuType}/{menuId}/moments")
    fun getAllMoments(
        @Path("dataSourcesId") dataSourcesId: String,
        @Path("menuType") menuType: String,
        @Path("menuId") menuId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Header("xi-context") xiContext: String
    ): Call<ArrayList<MomentsResponseData>>

    @POST(BuildConfig.ServiceEndPointBaseURL + "/api/datasources/{dataSourcesId}/{menuType}/{menuId}/read")
    fun markReadMoment(
        @Path("dataSourcesId") dataSourcesId: String,
        @Path("menuType") menuType: String,
        @Path("menuId") menuId: String,
        @Header("xi-context") xiContext: String,
        @Body experienceIdList: List<String>
    ): Call<Int>

    @GET
    fun getUserDataFromOAuth(@Url url: String): Call<UserDataFromOAuth>

    @GET(BuildConfig.ServiceEndPointBaseURL + "/api/SavedViews")
    fun getSavedViews(@Header("xi-context") xiContext: String): Call<ArrayList<SavedViewsListResponseData>>

    @GET(BuildConfig.ServiceEndPointBaseURL + "/api/UserPrograms")
    fun getUserPrograms(): Call<UserProgramsResponseData>

    @GET(BuildConfig.ServiceEndPointBaseURL + "/api/Rewards/users/search")
    suspend fun getEmployeeList(
        @Query("name") name: String,
        @Header("xi-context") xiContext: String
    ): List<RewardSearchModel>

    @POST(BuildConfig.ServiceEndPointBaseURL + "/api/Rewards")
    fun postRewardData(
        @Header("xi-context") xiContext: String,
        @Body rewardsPointRequestParam: RewardsPointRequestParam
    ): Call<Boolean>

    @POST(BuildConfig.ServiceEndPointBaseURL + "/api/ActivityLog")
    fun logMomentActivity(@Body activityLogRequestParam: ActivityLogRequestParam): Call<ActivityLogResponseData>

    @GET(BuildConfig.ServiceEndPointBaseURL + "/api/ActivityLog")
    fun getMomentActivityLog(@Query("id") id: String): Call<List<ActivityLogResponseData>>
}