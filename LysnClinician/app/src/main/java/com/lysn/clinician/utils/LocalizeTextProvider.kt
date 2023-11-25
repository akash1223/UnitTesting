package com.lysn.clinician.utils

import android.content.Context
import com.lysn.clinician.R

/**
 * This class provide string messages
 */
class LocalizeTextProvider(var context: Context) {

    fun getNoInternetMessage(): String {
        return context.getString(R.string.no_internet)
    }

    fun getSomethingWrongMessage(): String {
        return context.getString(R.string.something_went_wrong)
    }

    fun getInvalidEmailMessage(): String {
        return context.getString(R.string.invalid_email_msg)
    }

    fun getLoginFailMessage():String{
        return context.getString(R.string.login_failed_msg)
    }

    fun getConsultationMinutesMessage(minutes:Int):String{
        return if(minutes==1)
           context.getString(R.string.consultation_starts_in_minute_singular,minutes)
        else
           context.getString(R.string.consultation_starts_in_minute_plural,minutes)
    }

    fun getConsultationStartedMessage(minutes:Int):String{
        return context.getString(R.string.consultation_started_message,minutes)
    }

    fun getConsultationFinishedMessage():String{
        return context.getString(R.string.finished)
    }

    fun getUnableToJoinMessage():String
    {
        return context.getString(R.string.message_join_room)
    }

    fun getLogoutUserMessage(): String {
        return context.getString(R.string.logout_text)
    }

}