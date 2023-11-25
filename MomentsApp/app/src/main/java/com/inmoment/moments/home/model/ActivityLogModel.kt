package com.inmoment.moments.home.model

import android.annotation.SuppressLint
import android.os.Parcelable
import android.text.Spanned
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class ActivityLogModel(

    val timeAgo: String,
    val description: String,
    val initialsName: String,
    val iconRes: Int,
    val experienceId: String?,

    ): Parcelable
