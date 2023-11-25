package com.inmoment.moments.userprofile

import androidx.annotation.VisibleForTesting
import com.inmoment.moments.framework.datamodel.UserProfileResponseData
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

private const val TAG = "UserProfileService"

@Suppress("RedundantSuspendModifier")
class UserProfileService @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf
) : TaskManager() {

    fun getUserDetails(coroutineScope: CoroutineScope) = execute(::getUserInfo, coroutineScope)

    @VisibleForTesting
    suspend fun getUserInfo(): OperationResult<UserProfileResponseData> {
        return apiHelper.getUserInfo(
            sharedPrefsInf.get(
                SharedPrefsInf.PREF_USER_EMAIL_ID,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )
        )
    }

    fun saveUserData(firstName: String, lastName: String, userId: String) {
        sharedPrefsInf.put(SharedPrefsInf.PREF_FIRST_NAME, firstName)
        sharedPrefsInf.put(SharedPrefsInf.PREF_LAST_NAME, lastName)
        sharedPrefsInf.put(SharedPrefsInf.PREF_USER_ID, userId)
    }

    fun logout() {
        sharedPrefsInf.clearLoginDetails()
    }
}