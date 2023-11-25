package com.inmoment.moments.framework.persist

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

interface SharedPrefsInf {
    fun put(key: String?, value: Any)
    fun <T : Any> get(key: String, defaultValue: T): T

    fun getDefaultAccountAndProgramId(): Triple<String, String, String>
    fun setDefaultAccountAndProgramId(accountId: String, programId: String, userProgramId: String)

    fun removePref(key: String)
    fun clearLoginDetails()
    fun clearAllSharedPrefs()

    fun isUserLogin(): Boolean
    fun getXiContextHeader(): String

    companion object {
        const val PREF_STRING_DEFAULT = ""

        //Login
        const val PREF_USER_EMAIL_ID = "email"
        const val ACCESS_TOKEN_PREFERENCE_KEY = "access_token"
        const val AUTH_STATE_PREFERENCE_KEY = "auth_state"
        const val PREF_AUTH_TOKEN_SESSION = "token_session"
        const val PREF_LOGIN_TIME = "login_time"

        //Program
        const val PREF_DEFAULT_ACCOUNT_ID = "defaultAccountId"
        const val PREF_DEFAULT_PROGRAM_ID = "defaultProgramId"
        const val PREF_USER_PROGRAM_ID = "userProgramId"

        //Saved Views
        const val PREF_DEFAULT_DATA_SOURCE_ID = "dataSourceId"
        const val PREF_DEFAULT_DATA_SOURCE_NAME = "dataSourceName"
        const val PREF_SAVED_VIEW_ID = "savedViewId"
        const val PREF_SAVED_SELECTED_SAVED_VIEW = "selected_saved_views"
        const val PREF_ACCOUNT_NAME = "accountName"
        const val PREF_MOMENT_TYPE = "momentType"

        //Collection
        const val PREF_DEFAULT_COLLECTION_ID = "collectionId"
        const val PREF_DEFAULT_COLLECTION_NAME = "collectionName"

        // User Profile
        const val PREF_FIRST_NAME = "firstName"
        const val PREF_LAST_NAME = "lastName"
        const val PREF_USER_ID = "userId"
        const val PREF_CONTENT_SHARED = "shared_content"
    }
}