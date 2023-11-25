package com.inmoment.moments.framework.datamodel


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "SavedViews", indices = [Index(value = ["savedViewId"], unique = true)])
data class SavedViewsListResponseData(

    @PrimaryKey
    @SerializedName("id")
    val savedViewId: String,
    @SerializedName("dataSourceId")
    val dataSourceId: String,
    @SerializedName("dataTimePeriod")
    val dataTimePeriod: String,
    @SerializedName("dataSourceName")

    val dataSourceName: String?,
    @SerializedName("momentCount")
    val momentCount: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("retentionDays")
    val retentionDays: Int,
    @SerializedName("savedViewSource")
    var savedViewSource: String,
    @SerializedName("unreadMomentCount")
    val unreadMomentCount: Int,
    @SerializedName("enableCollection")
    var enableCollection: Boolean,
    @SerializedName("enableCase")
    var enableCase: Boolean,
    @SerializedName("enableReward")
    var enableReward: Boolean,
    @SerializedName("enableShare")
    val enableShare: Boolean,
    var activeSavedView: Boolean = false,
    var isNonDxDataSource: Boolean = false
) : Parcelable {

    @Ignore
    var isVisitedSavedView: Boolean = false

}