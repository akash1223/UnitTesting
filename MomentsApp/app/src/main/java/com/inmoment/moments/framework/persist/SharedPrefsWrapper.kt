package com.inmoment.moments.framework.persist

import android.content.SharedPreferences
import com.inmoment.moments.framework.common.AppConstants
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.ACCESS_TOKEN_PREFERENCE_KEY
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.AUTH_STATE_PREFERENCE_KEY
import com.inmoment.moments.framework.persist.SharedPrefsInf.Companion.PREF_AUTH_TOKEN_SESSION
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

@Suppress("UNCHECKED_CAST")
@Singleton
class SharedPrefsWrapper @Inject constructor(private val sharedPref: SharedPreferences) :
    SharedPrefsInf {

    override fun put(key: String?, value: Any) {
        when (value) {
            is String -> sharedPref.edit().putString(key, value).apply()
            is Int -> sharedPref.edit().putInt(key, value).apply()
            is Boolean -> sharedPref.edit().putBoolean(key, value).apply()
            is Long -> sharedPref.edit().putLong(key, value).apply()
            is Float -> sharedPref.edit().putFloat(key, value).apply()
        }
    }

    override fun <T : Any> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is Int -> sharedPref.getInt(key, defaultValue) as T
            is Boolean -> sharedPref.getBoolean(key, defaultValue) as T
            is Long -> sharedPref.getLong(key, defaultValue) as T
            is Float -> sharedPref.getFloat(key, defaultValue) as T
            else -> sharedPref.getString(key, defaultValue.toString()) as T
        }
    }

    override fun removePref(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    override fun clearLoginDetails() {
        sharedPref.edit().remove(ACCESS_TOKEN_PREFERENCE_KEY)
            .remove(AUTH_STATE_PREFERENCE_KEY).remove(PREF_AUTH_TOKEN_SESSION).apply()
    }

    override fun clearAllSharedPrefs() {
        sharedPref.edit().clear().apply()
    }

    override fun getDefaultAccountAndProgramId(): Triple<String, String, String> {
        val defaultAccountId = sharedPref.getString(
            SharedPrefsInf.PREF_DEFAULT_ACCOUNT_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        val defaultProgramId = sharedPref.getString(
            SharedPrefsInf.PREF_DEFAULT_PROGRAM_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        val userProgramId = sharedPref.getString(
            SharedPrefsInf.PREF_USER_PROGRAM_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )

        return Triple(defaultAccountId!!, defaultProgramId!!, userProgramId!!)
    }

    /* region check login completed */
    override fun isUserLogin() = get(
        ACCESS_TOKEN_PREFERENCE_KEY,
        AppConstants.EMPTY_VALUE
    ) != AppConstants.EMPTY_VALUE


    override fun getXiContextHeader(): String {
        val defaultAccountId = get(
            SharedPrefsInf.PREF_DEFAULT_ACCOUNT_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        val defaultProgramId = get(
            SharedPrefsInf.PREF_DEFAULT_PROGRAM_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        val userId = get(
            SharedPrefsInf.PREF_USER_ID,
            SharedPrefsInf.PREF_STRING_DEFAULT
        )
        return "{\"accountId\":\"${defaultAccountId}\", \"programId\": \"${defaultProgramId}\",\"userId\":\"${userId}\"}"
    }


    override fun setDefaultAccountAndProgramId(
        accountId: String,
        programId: String,
        userProgramId: String
    ) {
        sharedPref.edit().putString(
            SharedPrefsInf.PREF_DEFAULT_ACCOUNT_ID,
            accountId
        ).apply()
        sharedPref.edit().putString(
            SharedPrefsInf.PREF_DEFAULT_PROGRAM_ID,
            programId
        ).apply()
        sharedPref.edit().putString(
            SharedPrefsInf.PREF_USER_PROGRAM_ID,
            userProgramId
        ).apply()
    }
}
