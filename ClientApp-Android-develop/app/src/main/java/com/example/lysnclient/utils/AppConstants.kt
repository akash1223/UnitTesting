package com.example.lysnclient.utils

import java.util.regex.Pattern

/**
 *  This file contains all the constant used in application
 */
object AppConstants {

    const val REQUEST_CODE_START_ACTIVITY = 101
    private const val CLICK_EXPIRE_TIME_IN_MILLI_SECOND = 500
    const val PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-@%\\[\\}+'!/#\$^?:;,\\(\"\\)~`.*=&\\{>\\]<_])(?=\\S+$).{10,}$"

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" +
                ")+"
    )
    const val MOBILE_NUMBER_LENGTH = 10
    const val OTP_CODE_LENGTH = 6
    const val SPLASH_SCREEN_TIMEOUT_IN_MILLI_SECOND = 3000
    const val EMPTY_VALUE = ""
    const val NOT_APPLICABLE = "NA"
    const val NOT_AVAILABLE = "Not Available"

    //Signup intent data
    const val SIGN_UP_EMAIL = "email"
    const val SIGN_UP_PASSWORD = "password"
    const val SIGN_UP_PHONE = "phone"
    const val SIGN_UP_ACCEPT_TERMS = true

    // Open Assessment detail page intent data
    const val INTENT_ASSESSMENT_ID = "intentAssessmentId"
    const val INTENT_List_OF_ASSESSMENT_ANSWER = "intentListOfAssessmentAnswer"
    const val INTENT_ASSESSMENT_CODE = "intentAssessmentCode"
    const val INTENT_ASSESSMENT_TITLE = "intentAssessmenttitle"
    const val INTENT_KEY_IS_FROM_SIGN_UP_SCREEN = "isFromSignUpScreen"
    const val INTENT_KEY_IS_FROM_SESSION_EXPIRED = "keySessionExpired"

    const val DASS_10_ID = 6
    const val K_10_ID = 3
    const val BIPOLAR_DISORDER_ID = 7
    const val DMI_ID = 9
    const val WBT_ITEM_POSITION = 2
    const val ASSESSMENT_ITEM_POSITION = 3
    const val USER_LOGOUT_POSITION = 4
    const val DEFAULT_WBT_SEEKBAR_VALUE = 50
    const val USER_CHAT_POSITION = 1

    fun getCurrentTimeMills(): Long {
        return System.currentTimeMillis()
    }

    fun allowToPerformClick(mLastClickTime: Long): Boolean {
        if (getCurrentTimeMills() - mLastClickTime < CLICK_EXPIRE_TIME_IN_MILLI_SECOND) {
            return false
        }
        return true
    }
}

