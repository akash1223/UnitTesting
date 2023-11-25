package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class WellBeingTrackerData(@SerializedName("options") val mWBTQuestionList: ArrayList<WBTQuestion>)