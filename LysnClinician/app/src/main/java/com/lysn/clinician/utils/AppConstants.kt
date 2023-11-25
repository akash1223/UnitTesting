package com.lysn.clinician.utils

import java.util.regex.Pattern

/**
 *  This file contains all the constant used in application
 */
object AppConstants {

    const val PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@%\\[\\}+'!/#\$^?:;,\\(\"\\)~`.*=&\\{>\\]<_])(?=\\S+$).{10,}$"

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    const val MOBILE_NUMBER_LENGTH = 10
    const val OTP_CODE_LENGTH = 6
    const val SPLASH_SCREEN_TIMEOUT_IN_MILLI_SECOND = 1000
    const val EMPTY_VALUE = ""
    const val REQUEST_APP_SETTINGS = 168
    const val REQUEST_PERMISSION = 10
    const val ROOM_SMALL_GROUP = "group-small"
    const val VIDEO_SESSION_SCOPED_SESSION_ID = "VideoSessionActivity"
    const val VIDEO_SESSION_SCOPED_NAME = "chat"
    const val CHAT_CHANNEL_NAME_PREFIX = "Consultation - "
}