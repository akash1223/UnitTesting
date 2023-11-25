package com.example.lysnclient.utils

import android.content.Context
import com.example.lysnclient.R

/**
 * This class provide string messages
 */
class LocalizeTextProvider(private val context: Context) {

    fun getSomethingWrongMessage(): String {
        return context.getString(R.string.something_went_wrong)
    }

    fun getInvalidEmailMessage(): String {
        return context.getString(R.string.invalid_email_msg)
    }

    fun getInvalidMobileNumberMessage(): String {
        return context.getString(R.string.phone_number_must_be_10_digit)
    }

    fun getInvalidOtpMessage(): String {
        return context.getString(R.string.invalid_otp)
    }

    fun getLogoutUserMessage(): String {
        return context.getString(R.string.login_again_token_expired)
    }

    fun getServerNotReachableMessage(): String {
        return context.getString(R.string.server_not_reachable)
    }
}
