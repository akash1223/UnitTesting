package com.inmoment.moments.home.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.datamodel.MomentsResponseData.TagAnnotations
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class Feed(
    var wasRead: Boolean? = false,
    var videoUrl: String? = "",
    val sentiment: Float? = 0.0f,
    val text: String? = "",
    val images: ArrayList<String>? = arrayListOf(),
    var audio: String? = "",
    val tagAnnotations: List<TagAnnotations>? = listOf(),
    val experienceId: String? = "",
    var activityLogCount: Int? = 0,
    var id: Int = 0,
    var currentSeekValue: Long = 0,
    var rowType: Int = -1,
    var accountName: String? = null,
    var noOfDays: String = "",
    var dateTime: String? = "",
    var location: String = "",
    var activityLogClick: Boolean = false,
    var loadingEffect: Boolean = false,
    var activitiesLogModel: MutableList<ActivityLogModel> = mutableListOf()
) : Parcelable {

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getTimeAgo(activityDate: String?): String {
        return if (!activityDate.isNullOrEmpty()) {
            val dateParser = SimpleDateFormat(AppConstants.FEEDS_DATE_FORMAT, Locale.UK)
            dateParser.timeZone = TimeZone.getTimeZone("UTC")
            val activityDateInLongFormat = dateParser.parse(activityDate)
            val calInstance = Calendar.getInstance()
            calInstance.time = activityDateInLongFormat
            dateConverterToMicroFormat(calInstance)
        } else {
            AppConstants.EMPTY_VALUE
        }
    }

    private fun dateConverterToMicroFormat(currentCalendar: Calendar): String {

        val feedDateTime = LocalDate.of(
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH) + 1,
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        )
        val currentDate = LocalDate.now(ZoneId.of("UTC"))
        val period: Period = feedDateTime.until(currentDate)

        return if (period.years > 0) {
            if (period.years == 1) {
                "${period.years} year ago"
            } else {
                "${period.years} years ago"
            }
        } else if (period.months > 0) {
            if (period.months == 1) {
                "${period.months} month ago"
            } else {
                "${period.months} months ago"
            }
        } else {
            if (period.days > 6) {
                if (period.days == 7) {
                    "${period.days} week ago"
                } else {
                    "${period.days / 7} weeks ago"
                }
            } else if (period.days == 0) {
                "Today"
            } else if (period.days == 1) {
                "Yesterday"
            } else {
                "${period.days} days ago"
            }
        }

    }
}