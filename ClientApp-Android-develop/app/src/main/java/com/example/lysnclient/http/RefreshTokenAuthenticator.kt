package com.example.lysnclient.http

import android.content.Context
import android.content.Intent
import com.example.lysnclient.BuildConfig
import com.example.lysnclient.model.UserAuthResponse
import com.example.lysnclient.utils.AppConstants
import com.example.lysnclient.utils.HttpConstants
import com.example.lysnclient.utils.PreferenceUtil
import com.example.lysnclient.view.UserAuthenticateActivity
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * This class is used for getting new token when API response return with code 401
 */
class RefreshTokenAuthenticator(val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val isRefreshTokenExpired: Boolean

        Timber.d("RefreshTokenAuthenticator: Token is expire, getting new token")
        val iHttpService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .build().create(IHttpService::class.java)
        val refreshToken = PreferenceUtil.getInstance(context)
            .getValue(PreferenceUtil.KEY_REFRESH_TOKEN, AppConstants.EMPTY_VALUE)

        if (refreshToken.isEmpty() && !PreferenceUtil.getInstance(context)
                .getValue(PreferenceUtil.KEY_IS_USER_LOGGED_IN, false)
        ) return null

        val mainResponse: retrofit2.Response<UserAuthResponse> =
            iHttpService.callRefreshAccessToken(refreshToken).execute()

        if (mainResponse.code() == HttpConstants.STATUS_CODE_OK_200) {
            val userAuthResponse: UserAuthResponse? = mainResponse.body()
            if (userAuthResponse != null) {
                Timber.d("RefreshTokenAuthenticator: Successfully fetch new token, continue api call")

                PreferenceUtil.getInstance(context).saveValue(
                    PreferenceUtil.KEY_ACCESS_TOKEN,
                    userAuthResponse.access
                )
                PreferenceUtil.getInstance(context).saveValue(
                    PreferenceUtil.KEY_REFRESH_TOKEN,
                    userAuthResponse.refresh
                )
                return response.request().newBuilder()
                    .header(
                        HttpConstants.HEADER_PARAM_AUTHORIZATION,
                        HttpConstants.BEARER + userAuthResponse.access
                    )
                    .build()
            } else {
                isRefreshTokenExpired = true
            }
        } else if (mainResponse.code() == HttpConstants.STATUS_CODE_UNAUTHORIZED_401) {
            isRefreshTokenExpired = true
        } else {
            isRefreshTokenExpired = true
        }
        // As not received new token take user to login screen
        if (isRefreshTokenExpired) {
            PreferenceUtil.getInstance(context).clearAll()
            val intent: Intent? =
                Intent(context, UserAuthenticateActivity::class.java)
            intent?.putExtra(AppConstants.INTENT_KEY_IS_FROM_SESSION_EXPIRED, true)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        return null
    }
}
