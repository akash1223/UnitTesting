package com.lysn.clinician.utility.extensions

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


fun LocalDate.dateFormat(pattern:String): String? = this.format(DateTimeFormatter.ofPattern(pattern))
fun LocalTime.dateFormat(pattern:String): String? = this.format(DateTimeFormatter.ofPattern(pattern))
fun LocalDateTime.dateFormat(pattern:String): String? = this.format(DateTimeFormatter.ofPattern(pattern))
fun ZonedDateTime.dateFormat(pattern:String): String? = this.format(DateTimeFormatter.ofPattern(pattern))

fun LocalDateTime.cardViewDate( date:String):String {
    val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
    return parsedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mma", Locale.UK))
}
fun LocalDateTime.minutesDiff(date:String?):Long{
    val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
    val current = LocalDateTime.now()
    val duration: Duration = Duration.between(current, parsedDate)
    var extraMin = 0
    if (duration.seconds > 30)
        extraMin = 1
    return  duration.toMinutes() + extraMin
}


