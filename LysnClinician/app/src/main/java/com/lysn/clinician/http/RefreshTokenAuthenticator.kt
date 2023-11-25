package com.lysn.clinician.http

import android.content.Context
import android.content.Intent
import com.lysn.clinician.BuildConfig
import com.lysn.clinician.model.UserAuthResponse
import com.lysn.clinician.utils.PreferenceUtil
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

@lombok.Generated
class RefreshTokenAuthenticator(private val preferenceUtil: PreferenceUtil,val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        var isReLoginRequired:Boolean
        Timber.d("RefreshTokenAuthenticator: Token is expire, getting new token")
        val iHttpService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .build().create(IHTTPService::class.java)
        val refreshToken = preferenceUtil.getValue(PreferenceUtil.REFRESH_TOKEN_PREFERENCE_KEY, "")

        if (refreshToken.isEmpty()) return null
        val mainResponse: retrofit2.Response<UserAuthResponse> =
            iHttpService.callRefreshAccessToken(refreshToken).execute()

        if (mainResponse.code() == 200) {
            val userAuthResponse: UserAuthResponse? = mainResponse.body()
            if (userAuthResponse != null) {
                Timber.d("RefreshTokenAuthenticator: Successfully fetch new token, continue api call")

                preferenceUtil.putValue(
                    PreferenceUtil.ACCESS_TOKEN_PREFERENCE_KEY,
                    userAuthResponse.access
                )
                preferenceUtil.putValue(
                    PreferenceUtil.REFRESH_TOKEN_PREFERENCE_KEY,
                    userAuthResponse.refresh
                )
                return response.request().newBuilder()
                    .header(
                        HttpConstants.HEADER_PARAM_AUTHORIZATION,
                        HttpConstants.BEARER + userAuthResponse.access
                    )
                    .build()
            } else {
                isReLoginRequired = true
            }
        } else {
            isReLoginRequired = true
        }
        if(isReLoginRequired)
        {
            preferenceUtil.clearAll()
            val intent: Intent? = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
        return null
    }
}