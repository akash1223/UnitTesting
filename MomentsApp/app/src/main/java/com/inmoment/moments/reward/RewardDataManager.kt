package com.inmoment.moments.reward

import androidx.annotation.VisibleForTesting
import com.inmoment.moments.framework.datamodel.RequestParam
import com.inmoment.moments.framework.datamodel.RewardsPointRequestParam
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.reward.model.RewardSearchModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

private const val TAG = "UserProfileService"

@Suppress("RedundantSuspendModifier")
class RewardDataManager @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf
) : TaskManager() {

    fun postRewardPoints(
        rewardsPointRequestParam: RewardsPointRequestParam,
        coroutineScope: CoroutineScope
    ) = execute(rewardsPointRequestParam, ::postRewardPointsInfo, coroutineScope)

    suspend fun getSearchResult(query: String): List<RewardSearchModel> {
        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        return try {
            apiHelper.getEmployeeList(query, xiContextHeader)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @VisibleForTesting
    suspend fun postRewardPointsInfo(requestParam: RequestParam): OperationResult<Boolean> {
        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        return apiHelper.postRewardData(xiContextHeader, requestParam as RewardsPointRequestParam)
    }
}