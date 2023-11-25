package com.inmoment.moments.framework.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_CONTENT_SHARED

class ShareContentCallBackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action?.equals(AppConstants.ACTION_CONTENT_SHARE_SUCCESSFULLY) == true) {
            val sharedPref =
                context.getSharedPreferences("MomentsApplicationSP", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean(PREF_CONTENT_SHARED, true).apply()
        }
    }
}