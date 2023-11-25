package com.inmoment.moments.framework.manager.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.reward.model.RewardSearchModel
import com.lysn.clinician.utility.extensions.readFileFromAssets

class FakeRestApiHelperImpl constructor(
    private val context: Context,
    val restApiInterfaceDao: RestApiInterfaceDao
) :
    RestApiHelper {
    override fun getAllCollections(
        xiContextHeader: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionsResponseData> {
        val operationResult = OperationResult<CollectionsResponseData>()

        val content = context.readFileFromAssets("collection.json")
        val userProfileDada = Gson().fromJson(content, CollectionsResponseData::class.java)
        operationResult.result = userProfileDada
        return operationResult
    }

    override fun getUserInfo(email: String): OperationResult<UserProfileResponseData> {
        val operationResult = OperationResult<UserProfileResponseData>()

        val content = context.readFileFromAssets("user_program.json")
        val userProfileDada = Gson().fromJson(content, UserProfileResponseData::class.java)
        operationResult.result = userProfileDada
        return operationResult
    }

    override fun getAllMoments(
        dataSourcesId: String,
        menuType: String,
        menuId: String,
        xiContext: String,
        pageNumber: Int,
        pageSize: Int
    ): OperationResult<List<MomentsResponseData>> {
        val operationResult = OperationResult<List<MomentsResponseData>>()

        val listType = object : TypeToken<List<MomentsResponseData?>?>() {}.type
        val content = context.readFileFromAssets("moments_list.json")
        val momentsList: List<MomentsResponseData> = Gson().fromJson(content, listType)
        operationResult.result = momentsList
        return operationResult
    }

    override fun getSavedViews(xiContextHeader: String): OperationResult<List<SavedViewsListResponseData>> {
        val operationResult = OperationResult<List<SavedViewsListResponseData>>()

        val listType = object : TypeToken<List<SavedViewsListResponseData?>?>() {}.type
        val content = context.readFileFromAssets("saved_views.json")
        val savedViews: List<SavedViewsListResponseData> = Gson().fromJson(content, listType)
        operationResult.result = savedViews
        return operationResult
    }

    override fun getUserPrograms(): OperationResult<UserProgramsResponseData> {
        val operationResult = OperationResult<UserProgramsResponseData>()

        val content = context.readFileFromAssets("user_program.json")
        val userProgram = Gson().fromJson(content, UserProgramsResponseData::class.java)
        operationResult.result = userProgram
        return operationResult
    }

    override suspend fun getEmployeeList(
        name: String,
        xiContextHeader: String
    ): List<RewardSearchModel> {
        TODO("Not yet implemented")
    }

    override fun postRewardData(
        xiContextHeader: String,
        requestParam: RewardsPointRequestParam
    ): OperationResult<Boolean> {
        TODO("Not yet implemented")
    }

    override fun logMomentActivity(requestParam: ActivityLogRequestParam): OperationResult<ActivityLogResponseData> {
        TODO("Not yet implemented")
    }

    override fun getMomentActivityLog(experienceId: String): OperationResult<List<ActivityLogResponseData>> {
        TODO("Not yet implemented")
    }

    override fun markReadMoment(
        dataSourcesId: String,
        menuType: String,
        menuId: String,
        xiContext: String,
        experienceIdList: List<String>
    ): Int {
        TODO("Not yet implemented")
    }

    override fun createCollections(
        xiContext: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionOperationResponseData> {
        TODO("Not yet implemented")
    }

    override fun deleteCollections(
        xiContext: String,
        collectionsRequestData: DeleteCollectionsRequestData
    ): OperationResult<CollectionOperationResponseData> {
        TODO("Not yet implemented")
    }

    override fun getUserDataFromOAuth(userInfoUrl: String): OperationResult<UserDataFromOAuth> {
        TODO("Not yet implemented")
    }

}