package com.inmoment.moments.home

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.VisibleForTesting
import com.inmoment.moments.R
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.datamodel.ActivityLogRequestParam
import com.inmoment.moments.framework.datamodel.ActivityLogResponseData
import com.inmoment.moments.framework.datamodel.RequestParam
import com.inmoment.moments.framework.datamodel.SingleParamRequest
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.home.model.ActivityLogModel
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@Suppress("RedundantSuspendModifier")
class ActivityLogDataManager @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf
) : TaskManager() {

    init {
        TAG = this.javaClass.simpleName
    }

    fun logActivity(
        activityLogRequestParam: ActivityLogRequestParam,
        coroutineScope: CoroutineScope
    ) = execute(activityLogRequestParam, ::logActivityInfo, coroutineScope)

    @VisibleForTesting
    suspend fun logActivityInfo(requestParam: RequestParam): OperationResult<ActivityLogResponseData> {
        return apiHelper.logMomentActivity(requestParam as ActivityLogRequestParam)
    }

    fun getActivityLog(
        experienceId: String,
        coroutineScope: CoroutineScope
    ) = execute(SingleParamRequest<String>(experienceId), ::getActivityLogInfo, coroutineScope)

    @VisibleForTesting
    suspend fun getActivityLogInfo(requestParam: RequestParam): OperationResult<List<ActivityLogModel>> {
        val activityLogResponseData =
            apiHelper.getMomentActivityLog((requestParam as SingleParamRequest<String>).value)
        return convertToActivityLogModel(activityLogResponseData)
    }

    private fun convertToActivityLogModel(activityLogModelResponseData: OperationResult<List<ActivityLogResponseData>>): OperationResult<List<ActivityLogModel>> {
        val operationResult = OperationResult<List<ActivityLogModel>>()
        operationResult.result =
            activityLogModelResponseData.result?.map { it -> convertActivityResponseToModel(it) }

        return operationResult
    }

    fun convertActivityResponseToModel(activityLogResponseData: ActivityLogResponseData): ActivityLogModel {
        val timeAgo = getTimeAgo(activityLogResponseData.activityDate)
        val description = getShowDescription(
            activityLogResponseData.userName,
            activityLogResponseData.description
        )
        val initialsName = initialsName(activityLogResponseData.userName)
        val iconRes = getActivityIcon(activityLogResponseData.activityType)
        return ActivityLogModel(
            timeAgo,
            description.toString(),
            initialsName,
            iconRes,
            activityLogResponseData.experienceId
        )
    }


    fun initialsName(userName: String?): String {
        return userName?.split(' ')?.mapNotNull { it.firstOrNull()?.toString() }
            ?.reduce { acc, s -> acc + s }
            ?: ""
    }

    fun getShowDescription(userName: String?, description: String?): String {

        val firstNameAndLastName = userName?.split(" ")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                "<b>${firstNameAndLastName?.get(0)} ${
                    firstNameAndLastName?.get(1)?.first()
                }.</b> $description"

        } else {
          "<b>$userName</b> $description"
        }
    }

    fun getTimeAgo(activityDate: String?): String {
        val dateParser = SimpleDateFormat(AppConstants.ACTIVITY_DATE_FORMAT)
        dateParser.timeZone = TimeZone.getTimeZone("UTC")
        val activityDateInLongFormat = dateParser.parse(activityDate).time

        val currentCalendar = Calendar.getInstance()
        return dateConverterToMicroFormat(currentCalendar.timeInMillis - activityDateInLongFormat)
    }

    private fun dateConverterToMicroFormat(timeInMilliSec: Long): String {
        var loopTimeInMilliSec = timeInMilliSec
        var timeDiffCounter = 0
        var timeInWork = "1s"
        while (loopTimeInMilliSec > 1) {
            when (timeDiffCounter) {
                0 -> {
                    loopTimeInMilliSec /= 1000
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "s"
                    }
                }
                1 -> {
                    loopTimeInMilliSec /= 60
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "m"
                    }
                }
                2 -> {
                    loopTimeInMilliSec /= 60
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "h"
                    }
                }
                3 -> {
                    loopTimeInMilliSec /= 24
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "d"
                    }
                }
                4 -> {
                    loopTimeInMilliSec /= 7
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "w"
                    }
                }
                5 -> {
                    loopTimeInMilliSec /= 5
                    if (loopTimeInMilliSec > 0) {
                        timeDiffCounter++
                        timeInWork = loopTimeInMilliSec.toString() + "m"
                    }
                }
                6 -> {
                    loopTimeInMilliSec /= 12
                    timeDiffCounter++
                    timeInWork = loopTimeInMilliSec.toString() + "y"
                }

            }
        }

        return timeInWork

    }

    private fun getActivityIcon(activityType: String?): Int {
        return when (activityType) {
            ActivityLogEnum.SHARE.value -> R.drawable.share_blue
            ActivityLogEnum.COLLECTION.value -> R.drawable.favorite_blue
            ActivityLogEnum.CASE_MOMENT.value -> R.drawable.seekbar
            ActivityLogEnum.EMPLOYEE_RECOGNITION.value -> R.drawable.reward_blue
            else -> R.drawable.seekbar
        }
    }
}

enum class ActivityLogEnum(var value: String) {
    UNSPECIFIED("Unspecified"),
    SHARE("Share"),
    EMPLOYEE_RECOGNITION("EmployeeRecognition"),
    COLLECTION("Collection"),
    CASE_MOMENT("Case")
}