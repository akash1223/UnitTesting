package com.lysn.clinician.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Util {
     fun convertDateFormat(startDate: String?, durationMin:Int?):String{
        val parsedDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
        var formatDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
        formatDate = formatDate.plusMinutes(durationMin!!.toLong())

        return parsedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMM, h:mma",
            Locale.UK))+ " - " +formatDate.format(DateTimeFormatter.ofPattern("h:mma",Locale.UK))
    }

    fun getFormattedTime(startDate: String?, durationMin:Int?):String{
        val parsedDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
        var formatDate = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME)
        formatDate = formatDate.plusMinutes(durationMin!!.toLong())

        return parsedDate.format(DateTimeFormatter.ofPattern("hh:mma",
            Locale.UK))+ " - " +formatDate.format(DateTimeFormatter.ofPattern("h:mma",Locale.UK))
    }
}