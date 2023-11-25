package com.inmoment.moments.framework.manager.network

import com.inmoment.moments.framework.common.Logger
import com.inmoment.moments.framework.datamodel.*
import com.inmoment.moments.framework.dto.Error
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.reward.model.RewardSearchModel
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */



class RestApiHelperImpl constructor(private val restApiInterfaceDao: RestApiInterfaceDao) :
    RestApiHelper {
    private  val TAG = "RestApiHelperImpl"
    override fun getUserInfo(email: String): OperationResult<UserProfileResponseData> {
        val operationResult = OperationResult<UserProfileResponseData>()
        return try {
            val query =
                "query getUserProfile {\n userProfiles(where: { email: \"$email\" }){\n id\n  firstName\n lastName\n email\n profilePicture\n authId}}"
            operationResult.result =
                restApiInterfaceDao.getUserData(UserProfileRequestData(query)).execute().body()
            Logger.d(TAG,"Get User result=>"+operationResult.result)
            Logger.d(TAG, "Get User operationResult=>$operationResult")
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
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
        //remove dev base api, once we get production api ready
        return try {
            operationResult.result = restApiInterfaceDao.getAllMoments(
                dataSourcesId,
                menuType,
                menuId,
                pageNumber,
                pageSize,
                xiContext
            ).execute().body()
            Logger.d(TAG, "Collection api call result:>" + operationResult.result)
            operationResult
        } catch (e: Exception) {
            Logger.e(TAG, "Collection api call failed")
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun getUserDataFromOAuth(userInfoUrl: String): OperationResult<UserDataFromOAuth> {
        val operationResult = OperationResult<UserDataFromOAuth>()
        return try {
            operationResult.result =
                restApiInterfaceDao.getUserDataFromOAuth(userInfoUrl).execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    @Inject
    override fun getSavedViews(xiContextHeader: String): OperationResult<List<SavedViewsListResponseData>> {
        val operationResult = OperationResult<List<SavedViewsListResponseData>>()
        return try {
            operationResult.result =
                restApiInterfaceDao.getSavedViews(xiContextHeader).execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun getUserPrograms(): OperationResult<UserProgramsResponseData> {
        val operationResult = OperationResult<UserProgramsResponseData>()
        return try {
            operationResult.result = restApiInterfaceDao.getUserPrograms().execute().body()
            Logger.d(TAG,"Get Program result=>"+operationResult.result)
            Logger.d(TAG, "Get Program operationResult=>$operationResult")
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override suspend fun getEmployeeList(
        name: String,
        xiContextHeader: String
    ): List<RewardSearchModel> {
        return restApiInterfaceDao.getEmployeeList(name, xiContextHeader)
    }


    override fun postRewardData(
        xiContextHeader: String,
        requestParam: RewardsPointRequestParam
    ): OperationResult<Boolean> {
        val operationResult = OperationResult<Boolean>()
        return try {
            operationResult.result =
                restApiInterfaceDao.postRewardData(xiContextHeader, requestParam).execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun logMomentActivity(requestParam: ActivityLogRequestParam): OperationResult<ActivityLogResponseData> {
        val operationResult = OperationResult<ActivityLogResponseData>()
        return try {
            operationResult.result =
                restApiInterfaceDao.logMomentActivity(requestParam).execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun getMomentActivityLog(experienceId: String): OperationResult<List<ActivityLogResponseData>> {
        val operationResult = OperationResult<List<ActivityLogResponseData>>()
        return try {
            operationResult.result =
                restApiInterfaceDao.getMomentActivityLog(experienceId).execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }


    override fun getAllCollections(
        xiContextHeader: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionsResponseData> {
        val operationResult = OperationResult<CollectionsResponseData>()
        return try {
            operationResult.result =
                restApiInterfaceDao.getCollections(xiContextHeader, collectionsRequestData)
                    .execute().body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun createCollections(
        xiContext: String,
        collectionsRequestData: CollectionsRequestData
    ): OperationResult<CollectionOperationResponseData> {
        val operationResult = OperationResult<CollectionOperationResponseData>()
        return try {
            operationResult.result =
                restApiInterfaceDao.createCollections(xiContext, collectionsRequestData).execute()
                    .body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun deleteCollections(
        xiContext: String,
        collectionsRequestData: DeleteCollectionsRequestData
    ): OperationResult<CollectionOperationResponseData> {
        val operationResult = OperationResult<CollectionOperationResponseData>()
        return try {
            operationResult.result =
                restApiInterfaceDao.deleteCollections(xiContext, collectionsRequestData).execute()
                    .body()
            operationResult
        } catch (e: Exception) {
            operationResult.exception = e
            operationResult.error = Error.getError(e)
            operationResult
        }
    }

    override fun markReadMoment(
        dataSourcesId: String,
        menuType: String,
        menuId: String,
        xiContext: String,
        experienceIdList: List<String>
    ): Int {
        Logger.i("${TAG} markReadMoment =>readMomentList->",experienceIdList.size.toString())
        return try {
            val response =
                restApiInterfaceDao.markReadMoment(
                    dataSourcesId,
                    menuType,
                    menuId,
                    xiContext,
                    experienceIdList
                ).execute()
            Logger.i("${TAG} markReadMoment =>restApiInterfaceDao->", response.body().toString()!!)
            if (response.isSuccessful)
                response.body()!!
            else
                0
        } catch (e: Exception) {
            val sw =StringWriter()
            e.printStackTrace(PrintWriter(sw))

            Logger.i("${TAG} markReadMoment =>Exception->",sw.toString())
            0
        }
    }

}