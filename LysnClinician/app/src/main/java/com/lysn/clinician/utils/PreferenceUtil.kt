package com.lysn.clinician.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil constructor(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun putValue(key: String, value: Any) {
        when (value) {
            is String -> {
                if (value.isEmpty()) return
                sharedPref.edit().putString(key, Cryptography.encryptData(value)).apply()
            }
            is Int -> sharedPref.edit().putInt(key, value).apply()
            is Boolean -> sharedPref.edit().putBoolean(key, value).apply()
            is Long -> sharedPref.edit().putLong(key, value).apply()
            is Float -> sharedPref.edit().putFloat(key, value).apply()
        }
    }

    fun getValue(key: String, defaultValue: String): String {
        val value = sharedPref.getString(key, defaultValue) ?: AppConstants.EMPTY_VALUE
        if (value.isNotEmpty()) {
            return Cryptography.decryptData(value) ?: AppConstants.EMPTY_VALUE
        }
        return value
    }

    fun getValue(key: String, defaultValue: Int): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Float): Float {
        return sharedPref.getFloat(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    fun clearAll() {
        sharedPref.edit().clear().apply()
    }

    /* region check login completed */
    fun isUserLogin() = getValue(ACCESS_TOKEN_PREFERENCE_KEY,
        AppConstants.EMPTY_VALUE) != AppConstants.EMPTY_VALUE

    fun isTermsAndConditionAccepted()= getValue(TERM_AND_CONDITION_PREFERENCE_KEY,false)

    fun isVideoCallEnabled()  = getValue(IS_VIDEO_CALL_ENABLED,true)
    fun isAudioCallEnabled()  = getValue(IS_AUDIO_CALL_ENABLED,true)
    fun isAudioDeviceSpeaker() = getValue(IS_AUDIO_DEVICE_SPEAKER, true)
    fun isFrontCameraEnabled() = getValue(IS_FRONT_CAMERA_ENABLED, true)
    fun getUserProfile() = getValue(USER_PROFILE_PREFERENCE_KEY, "")



    fun clearVideoSessionPref()
   {
       putValue(IS_VIDEO_CALL_ENABLED, true)
       putValue(IS_AUDIO_CALL_ENABLED, true)
       putValue(IS_FRONT_CAMERA_ENABLED, true)
       putValue(IS_AUDIO_DEVICE_SPEAKER, true)
   }


    /*endregion*/

    companion object{
        const val SHARED_PREFERENCE_NAME = "LysnClinicianApp"
        const val ACCESS_TOKEN_PREFERENCE_KEY = "access_token"
        const val REFRESH_TOKEN_PREFERENCE_KEY = "refresh_token"
        const val IS_SIGN_IN_PREFERENCE_KEY = "is_sign_in"
        const val TERM_AND_CONDITION_PREFERENCE_KEY="terms_and_condition_accepted"
        const val IS_VIDEO_CALL_ENABLED = "is_video_call_enabled"
        const val IS_AUDIO_CALL_ENABLED = "is_audio_call_enabled"
        const val IS_AUDIO_DEVICE_SPEAKER = "is_audio_device_speaker"
        const val IS_FRONT_CAMERA_ENABLED = "is_front_camera_enabled"
        const val REFRESH_CONSULTATION_LIST = "refresh_consultation_list"
        const val USER_PROFILE_PREFERENCE_KEY = "user_profile"
    }

}