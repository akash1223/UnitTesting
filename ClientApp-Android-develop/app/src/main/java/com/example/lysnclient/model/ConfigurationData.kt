package com.example.lysnclient.model

import com.google.gson.annotations.SerializedName

data class ConfigurationData(@SerializedName("wellbeing_tracker") val wellBeingTrackerData: WellBeingTrackerData) {
}