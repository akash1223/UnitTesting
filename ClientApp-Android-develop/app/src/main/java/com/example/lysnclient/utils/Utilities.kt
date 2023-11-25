package com.example.lysnclient.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Utilities {

    fun convertDateFormat(startDate: String?): String {
        val parsedDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
        return parsedDate.format(
            DateTimeFormatter.ofPattern(
                "dd MMM, yyyy",
                Locale.getDefault()
            )
        )
    }

    fun getDeviceTimeZone(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
        val date = SimpleDateFormat("z", Locale.getDefault())
        val currentLocalTime = calendar.getTime()
        val localTime = date.format(currentLocalTime)
        return localTime
    }

    fun getDeviceTimeZoneID(): String {
        return TimeZone.getDefault().toZoneId().toString()
    }
}