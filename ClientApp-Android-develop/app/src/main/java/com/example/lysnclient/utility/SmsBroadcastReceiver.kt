package com.example.lysnclient.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import timber.log.Timber

class SmsBroadcastReceiver : BroadcastReceiver() {
    var otpReceiveInterface: OtpReceivedInterface? = null
    fun setOnOtpListeners(otpReceiveInterface: OtpReceivedInterface?) {
        this.otpReceiveInterface = otpReceiveInterface
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("$TAG onReceive: ")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val mStatus: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            if (mStatus != null) {
                when (mStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents'
                        val message =
                            extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        Timber.d("$TAG onReceive:  $message")
                        if (otpReceiveInterface != null) {
                            otpReceiveInterface?.onOtpReceived(message)
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Waiting for SMS timed out (5 minutes)
                        Timber.d("$TAG onReceive: failure")
                        otpReceiveInterface?.onOtpTimeout()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }
}