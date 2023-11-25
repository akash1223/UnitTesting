package com.inmoment.moments.home

import androidx.annotation.VisibleForTesting
import com.inmoment.moments.framework.common.*
import com.inmoment.moments.framework.datamodel.HomeDashBoardRequestParam
import com.inmoment.moments.framework.datamodel.MomentsResponseData
import com.inmoment.moments.framework.datamodel.RequestParam
import com.inmoment.moments.framework.datamodel.UserProfileResponseData
import com.inmoment.moments.framework.dto.OperationResult
import com.inmoment.moments.framework.manager.TaskManager
import com.inmoment.moments.framework.manager.network.RestApiHelper
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.home.model.Feed
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

@Suppress("RedundantSuspendModifier")
class DashboardService @Inject constructor(
    private val apiHelper: RestApiHelper,
    private val sharedPrefsInf: SharedPrefsInf
) : TaskManager() {

    init {
        TAG = this.javaClass.simpleName
    }

    fun getUserDetails(coroutineScope: CoroutineScope) = execute(::getUserInfo, coroutineScope)
    fun getMoments(homeDashBoardRequestParam: HomeDashBoardRequestParam,coroutineScope: CoroutineScope) = execute(homeDashBoardRequestParam,::getAllMoments, coroutineScope)

    @VisibleForTesting
    suspend fun getUserInfo(): OperationResult<UserProfileResponseData> {
        Logger.v(TAG, " get user data")
        return apiHelper.getUserInfo(
            sharedPrefsInf.get(
                SharedPrefsInf.PREF_USER_EMAIL_ID,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )
        )
    }

    @VisibleForTesting
    suspend fun getAllMoments(requestParam: RequestParam): OperationResult<List<MomentsResponseData>> {

        val momentRequestParam = requestParam as HomeDashBoardRequestParam
        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val momentType = sharedPrefsInf.get(
            SharedPrefsInf.PREF_MOMENT_TYPE,
            MomentType.SAVED_VIEWS.value
        )
        val (dataSourceId, dataSourceName, accountName) = getDataSourceIdAndName(momentType)

        val momentResult = apiHelper.getAllMoments(
            dataSourceId, momentType, dataSourceName,
            xiContextHeader, momentRequestParam.pageNumber, momentRequestParam.pageSize
        )
        Logger.v(TAG, "Account Name=>$accountName")

        return momentResult
    }

    private fun getDataSourceIdAndName(
        momentType: String
    ): Triple<String, String, String> {

        return if (momentType == MomentType.SAVED_VIEWS.value) {
            Triple(
                sharedPrefsInf.get(
                    SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_ID,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                ), sharedPrefsInf.get(
                    SharedPrefsInf.PREF_SAVED_VIEW_ID,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                ), sharedPrefsInf.get(
                    SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_NAME,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                )
            )
        } else {
            Triple(
                sharedPrefsInf.get(
                    SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_ID,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                ),
                sharedPrefsInf.get(
                    SharedPrefsInf.PREF_DEFAULT_COLLECTION_ID,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                ),
                sharedPrefsInf.get(
                    SharedPrefsInf.PREF_DEFAULT_COLLECTION_NAME,
                    SharedPrefsInf.PREF_STRING_DEFAULT
                )
            )
        }
    }

     fun markMomentRead(listOfMomentId: List<Feed>): Int {
        val xiContextHeader = sharedPrefsInf.getXiContextHeader()
        val dataSourceId = sharedPrefsInf.get(
            SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        val dataSourceName = sharedPrefsInf.get(
            SharedPrefsInf.PREF_SAVED_VIEW_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        return apiHelper.markReadMoment(
            dataSourceId, MomentType.SAVED_VIEWS.value, dataSourceName,
            xiContextHeader, listOfMomentId.map { it.experienceId!! }
        )
    }


    private fun printDifference(startDate: Date, endDate: Date): Long {
        //milliseconds
        val different = endDate.time - startDate.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        return different / daysInMilli
    }

    fun convertMomentResponseToFeed(momentsList: List<MomentsResponseData>): List<Feed> {
        val feeds: ArrayList<Feed> = ArrayList()
        val dateList = ArrayList<Date?>()
        Logger.d(TAG, "MomentsResponseData Item Count=>${momentsList?.size}")
        var dateOfSurvey: String
        if (!momentsList.isNullOrEmpty()) {
            momentsList.forEach { param ->

                var feed: Feed

                val experience = param.experience
                if (experience != null) {

                    val comments = experience.comments
                    if (comments != null && comments.isNotEmpty()) {
                        val comment = comments[0]
                        val imageList: ArrayList<String> = ArrayList()
                        var audioUrl = ""
                        var videoUrl = ""

                        if (comment.imageSrcUrls != null && comment.imageSrcUrls.isNotEmpty()) {
                            imageList.addAll(comment.imageSrcUrls.filterNotNull())
                        } else if (comment.audioSrcUrl != null && comment.audioSrcUrl.isNotEmpty()) {
                            audioUrl = comment.audioSrcUrl
                        } else if (comment.videoSrcUrl != null && comment.videoSrcUrl.isNotEmpty()) {
                            videoUrl = comment.videoSrcUrl
                        }

                        feed = Feed(
                            param.wasRead,
                            videoUrl,
                            param.mainMetric,
                            comment.text?.trim(),
                            imageList,
                            audioUrl,
                            comment.tagAnnotations,
                            param.experienceId,
                            param.activityCount
                        )
                        param.id?.let {
                            feed.id = it
                        }
                        when {
                            imageList.isNotEmpty() -> {
                                feed.rowType = IMAGES_ROW
                            }
                            audioUrl.isNotEmpty() -> {
                                feed.rowType = AUDIO_ROW
                            }
                            videoUrl.isNotEmpty() -> {
                                feed.rowType = VIDEO_ROW
                            }
                            else -> {
                                feed.rowType = COMMENT_ROW
                            }
                        }
                        val surveyDate = experience.dateOfSurvey
                        if (surveyDate != null && surveyDate.isNotEmpty()) {
                            dateOfSurvey = surveyDate.substring(0, 19) + "Z"
                            val formattedSurveyDate =
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(
                                    dateOfSurvey
                                )
                            if (formattedSurveyDate != null) {
                                val formattedDate =
                                    SimpleDateFormat("dd MMM yyyy hh:mm aa").format(
                                        formattedSurveyDate
                                    )
                                feed.dateTime = formattedDate
                                dateList.add(formattedSurveyDate)
                            }
                        }
                        val location = experience.location
                        if (location?.label != null) {
                            feed.location = location.label
                        }
                        feeds.add(feed)
                    }

                }
            }
        }
        return feeds
    }

    fun getMomentsTitle(): String {
        val momentType = sharedPrefsInf.get(
            SharedPrefsInf.PREF_MOMENT_TYPE,
            MomentType.SAVED_VIEWS.value
        )
        return if (momentType == MomentType.SAVED_VIEWS.value) {

            sharedPrefsInf.get(
                SharedPrefsInf.PREF_DEFAULT_DATA_SOURCE_NAME,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )

        } else {
            sharedPrefsInf.get(
                SharedPrefsInf.PREF_DEFAULT_COLLECTION_NAME,
                SharedPrefsInf.PREF_STRING_DEFAULT
            )
        }
    }

    fun getMomentType(): String {
        return sharedPrefsInf.get(
            SharedPrefsInf.PREF_MOMENT_TYPE,
            MomentType.SAVED_VIEWS.value
        )
    }
}

enum class MomentType(var value: String) {
    SAVED_VIEWS("savedviews"),
    COLLECTION("collections"),
}