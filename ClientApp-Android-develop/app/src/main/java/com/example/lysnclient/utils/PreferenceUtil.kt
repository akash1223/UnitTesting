package com.example.lysnclient.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil private constructor(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun saveValue(key: String, value: Any?) {
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
        remove(KEY_IS_USER_LOGGED_IN)
        sharedPref.edit().clear().apply()
    }

    companion object : SingletonHolder<PreferenceUtil, Context>(::PreferenceUtil) {
        const val SHARED_PREFERENCE_NAME = "LysnClientApp"
        const val KEY_ACCESS_TOKEN = "keyAccessToken"
        const val KEY_REFRESH_TOKEN = "keyRefreshToken"
        const val KEY_USER_EMAIL = "keyUserEmail"

        const val KEY_IS_USER_LOGGED_IN = "keyIsUserLoggedIn"
        const val KEY_USER_ID = "keyUserId"
    }
}
