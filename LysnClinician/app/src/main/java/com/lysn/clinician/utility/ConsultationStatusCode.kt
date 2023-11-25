package com.lysn.clinician.utility

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import com.lysn.clinician.R
import java.util.*

class ConsultationStatusCode(val context: Context) {

    fun getColorRes(status: String): Int {
        when (status) {

            "in_progress" ->  return context.getColor(R.color.status_color_blue)
            "pending", "requested_by_therapist", "confirmed", "in_progress","session_started" ->
                return context.getColor(R.color.status_color_teal)
            "refunded","started" -> return context.getColor(R.color.status_color_orange)
            "cancelled", "client_noshow", "therapist_noshow", "both_noshow" ->
                return context.getColor(R.color.status_color_red)
            "finished","start_session"->
                return context.getColor(R.color.status_color_green)
        }
        return context.getColor(R.color.darkFontColor)
    }
}